/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key;

import key.primitive.Gender;
import key.primitive.EmailAddress;
import key.primitive.Password;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

import key.config.*;

import key.util.Trie;

/**
  * Is the main instance of a player on the program.  The
  * authenticate() routine has been changed in player to
  * attempt to stop a player being swapped out while they
  * connect, but no guarantee's... we should probably
  * be using tags in here somewhere.
  *
  *  It is the responsibility of this class to send the opening
  *  logo, and retrieve a player from the name entered.  (Authenticating
  *  the player or creating a suitable player as necessary).  No further
  *  action need be taken.
 */
public class ConnectingPlayer
implements LatentlyCached, Interactive
{
	InteractiveConnection ic;
	Player p = null;
	Player created = null;
	int portFrom = -1;
	
	public ConnectingPlayer()
	{
	}

	public void setPortFrom( int p )
	{
		portFrom = p;
	}
	
	public void run( InteractiveConnection connection )
	{
		//Key key = Key.instance();
		
		ic = connection;
		
		Player p = null;
		
		try
		{
				//  wait for terminal type handling...
			if( connection instanceof TelnetIC )
				((TelnetIC)connection).waitForIAC();
			
			outputLogo();
			
			outputQuote();
			
			String name;
			int tries = 0;
			
			Key.getLatencyCache().addToCache( this );
			
				//  loop this continually until they
				//  managed to get a name correct
			do
			{
				name = getPlayersName( ic );
				
					//  the player probably typed 'quit', this
					//  will clean it up.
				if( name == null )
					break;
				
				ic.blankLine();
				ic.blankLine();
				
				p = getPlayerFromName( ic, name );
			} while( p == null && ic.isConnected() && ++tries < 3 );
			
			if( p == null || !ic.isConnected() || tries >= 3 )
			{
				ic.flush();
				ic.close();
				return;
			}
			else
			{
				connectToProgram( ic, p );
				
					//  kick off the player (they'll look at the motd
					//  & link themselves in when they're ready)
				ic.interactWith( p );
				
				p.endConnect();
				p = null;
			}
		}
		catch( java.io.IOException e )
		{
			Log.debug( this, "disconnect from login" );
			
			ic.close();
		}
		catch( NetworkException e )
		{
			if( ic instanceof SocketIC )
				Log.debug( this, "disconnect from login [" + ((SocketIC)ic).getAddress() + "]" );
			else
				Log.debug( this, "disconnect from login" );
			
			ic.close();
		}
		catch( Exception e )
		{
			Log.debug( this, e.toString() + " at login prompt" );
			e.printStackTrace();
			
			ic.send( "\n\nWe apologise, but an error has occurred attempting to connect you.  Please mail forest@realm.progsoc.uts.edu.au if you are continually having problems.  Detail: " + e.toString() );
			
			ic.flush();
			ic.close();
		}
		finally
		{
			if( p != null )
			{
				p.endConnect();
				Registry.instance.deleteIfTemporary( p );
				p = null;
			}
			
			Key.getLatencyCache().removeFromCache( this );
		}
	}
	
	/**
	  *  If a players name is not matched to a character on
	  *  the program, this routine leads them through the
	  *  operations in creating a character of their own.
	  *
	  * @return null if an error or cancellation occurs
	 */
	protected Player connectGuest( InteractiveConnection ic, String name ) throws IOException
	{
		Container online = Key.instance().getOnline();
		Container shortcuts = (Container) Key.shortcuts();
		ConnectionConfiguration connectConfig = (ConnectionConfiguration) Key.instance().getConfig().getElement( "connection" );
		Container players = Key.instance().getResidents();
		
			//  check to ensure there isn't a newbie ban on this
			//  site.
		if( !ic.newbiesAllowed() )
		{
			Screen newbieban = (Screen) online.getElement( "newbieban" );
			
			if( newbieban == null )
				ic.sendFeedback( "New players are not currently permitted to connect from this site" );
			else
				ic.send( newbieban.text, false );
			
			ic.flush();
			ic.close();
			return null;
		}
		
            //  check that the name is not in the reserved list
			//  (names that aren't permitted to log in)
		StringSet ss = (StringSet) online.getElement( "reservedNames" );
		if( ss != null && ss.get( name ) != null )
		{
			ic.send( "'" + name + "' means something else here, please choose another." );
			ic.blankLine();
			ic.blankLine();
			
			return( null );
		}
		
            //  check that the name is not in the reserved list
			//  (names that aren't permitted to log in)
		ss = (StringSet) online.getElement( "bannedNames" );
		if( ss != null && ss.get( name ) != null )
		{
			ic.send( "You may not use the name '" + name + "', please choose another." );
			ic.blankLine();
			ic.blankLine();
			
			return( null );
		}
		
			//  need to create a new player & link it in
		ic.send( "  '" + name + "' not found in player database...\n" );
		
		if( guestConfirmName( ic, name ) )
		{
			Player created = null;
			
			try
			{
				created = (Player) Factory.makeAtom( Player.class, name );
				created.setKey( name );
				created.beginConnect();
				players.add( created );
				
				Rank nr = Key.instance().getInitialRank();
				if( nr != null )
					nr.add( created );
				
				Atom a = (Atom) shortcuts.getExactElement( name );
				if( a != null )
				{
					Paragraph disclaimer = (Paragraph) connectConfig.getProperty( "disclaimer" );
					
					if( disclaimer != null && !disclaimer.isEmpty() )
					{
						ic.send( disclaimer, false );

						if( !Grammar.getYesNo( "Do you accept these conditions? ", true, ic ) )
						{
							ic.flush();
							ic.close();
							
							if( created != null )
							{
								players.remove( created );
								Registry.instance.deleteIfTemporary( created );
								created.endConnect();
								return null;
							}
						}
					}
					
					Paragraph gender = (Paragraph) connectConfig.getProperty( "genderRequest" );
					if( gender != null && !gender.isEmpty() )
					{
						ic.blankLine();
						ic.send( gender, false );
						ic.blankLine();
						
						boolean genderOk;
						
						do
						{
							genderOk = true;
							
							String in = ic.input( "Enter 'm' or 'f': " );
							if( in.equalsIgnoreCase( "m" ) )
								created.setGender( Gender.MALE_GENDER );
							else if( in.equalsIgnoreCase( "f" ) )
								created.setGender( Gender.FEMALE_GENDER );
							else
							{
								genderOk = false;
								
								if( in.startsWith( "\'" ) )
									ic.sendFeedback( "\nDo not type the quotes, just the letter m or f.\n" );
								else
									ic.sendFeedback( "\nPlease enter, literally: 'm' if you are male, or 'f' if you are female.\n" );
							}
						} while( !genderOk );
					}
					
					Paragraph newbieScreen = (Paragraph) connectConfig.getProperty( "newbieA" );
					if( newbieScreen != null && !newbieScreen.isEmpty() )
					{
						ic.send( newbieScreen, false );
						Grammar.getReturn( ic );
					}
					
					newbieScreen = (Paragraph) connectConfig.getProperty( "newbieB" );
					if( newbieScreen != null && !newbieScreen.isEmpty() )
					{
						ic.send( newbieScreen, false );
						Grammar.getReturn( ic );
					}
					
					newbieScreen = (Paragraph) connectConfig.getProperty( "newbieAskEmail" );
					if( newbieScreen != null && !newbieScreen.isEmpty() )
					{
							//  At this point we diverge - if this
							//  screen is non-null, then we ask the 
							//  newbie for their email, set up their
							//  character, but DO NOT let them on
							//  until it has been verified.  This is
							//  kind of like a 'cool-off' period to
							//  stop people making characters for
							//  no reason.
						ic.send( newbieScreen, false );
						
						String email = null;
						
						do
						{
							boolean valid;
							
							do
							{
								valid = true;
								email = ic.input( "Enter your email address: " );
								if( email.length() < 3 )
								{
									valid = false;
									ic.sendFeedback( "\nThat is too short to be an email address.\n" );
								}
								else if( email.length() > 60 )
								{
									valid = false;
									ic.sendFeedback( "\nThat is too long to be an email address.\n" );
								}
							} while( !valid );
							
							ic.sendFeedback( "The address we can send an email to is: " + email + "\n" );
						} while( !Grammar.getYesNo( "Is this correct? (y/n): ", true, ic ) );
						
						ic.send( "" );
						
						EmailAddress address = (EmailAddress) created.getProperty( "email" );
						
						if( address == null )
						{
							address = new EmailAddress();
							created.setProperty( "email", address );
							Log.debug( this, "EmailAddress was null in ConnectingPlayer::connectGuest, fixed" );
						}
						
						address.set( email, ic, created );
						
						//ic.send( "Email address set.  A confirmation email with your password wll be sent to you shortly (within about 15 minutes).  When you receive it you can come back and log into " + Key.instance().getName() + "." );
						ic.sendFeedback( "\n\nTo reset your email address or enter your verify code, just try to log back on with this name." );
						created.sync();
						created.endConnect();
						ic.input( "\n\nPress return to disconnect: " );
						ic.close();
						return( null );
					}
					
					Log.log( "newbie", name + " from " + ((SocketIC)ic).getAddress() );
					
					return( created );
				}
				else
					ic.send( "Cannot create a player with this name" );
			}
			catch( BadKeyException e )
			{
				ic.send( "Your name must only contain alphabetical characters" );
			}
			catch( NonUniqueKeyException e )
			{
				ic.send( "There is already another player using this name" );
			}
			catch( IOException e )
			{
				if( created != null )
				{
					try
					{
						players.remove( created );
					}
					catch( NonUniqueKeyException ex )
					{
					}
					catch( BadKeyException ex )
					{
					}
					
					created.endConnect();
					Registry.instance.deleteIfTemporary( created );
					created = null;
				}
			}
		}
		else
		{
			ic.blankLine();
			ic.blankLine();
			ic.send( "  Perhaps someone else, then..." );
			ic.blankLine();
		}
		
		return( null );
	}
	
	protected Player connectResident( InteractiveConnection ic, Player p ) throws IOException
	{
		p.beginConnect();
		
		//  now check if the player has been banished
		if( p.isBanished() )
		{
			Screen banished = (Screen) Key.instance().getOnline().getElement( "playerBanished" );
			if ( banished == null )
			{
				ic.sendFeedback( "Your character is currently banished." );
			}
			else
				ic.send( banished.text, false );
			
			Log.log( "banish", "Denied connection for " + p.getName() + " from " + ic.getPrivateSiteName() + " at " + (new Date().toString()) );
			
			p.endConnect();
			p = null;
			ic.flush();
			ic.close();
			return null;
		}
		
		boolean name_okay = false;
		
		try
		{
			name_okay = p.authenticate( ic );
		}
		catch( PasswordEntryCancelled except )
		{
			ic.send( "  Perhaps someone else, then..." );
			
			p.endConnect();
			p = null;
			return( null );
		}
		catch( IOException e )
		{
			p.endConnect();
			p = null;
			throw e;
		}
		catch( NetworkException e )
		{
			p.endConnect();
			p = null;
			throw e;
		}
		catch( Exception e )
		{
			p.endConnect();
			p = null;
			throw new UnexpectedResult( e );
		}
		
		if( name_okay )
		{
				//  the endConnect is handled by the caller when we return a player
			//p.endConnect();
			
			Log.log( "site", "Connect|" + (new Date().toString()) + "|" + p.getName() + "|" + ic.getPrivateSiteName() );
			
				//  should return a tag instead?
				//  maybe, but it's locked in any case, by the beginConnect() endConnect() semaphore
			return( p );
		}
		else
		{
				//  authentication failed, disconnect
			Log.log( "password", "Wrong Password for " + p.getName() + " from " + ic.getPrivateSiteName() + " at " + (new Date().toString()) );
			p.endConnect();
			p = null;
			
			ic.flush();
			ic.close();
			
			return( null );
		}
	}
	
	protected void connectToProgram( InteractiveConnection ic, Player p ) throws IOException
	{
			//  copyright messages
		ic.send( Key.instance().getCopyright() );
		ic.sendLine();
		
			//  this is so we know which port the player connected into
		p.setPortFrom( portFrom );
		
			//  the MOTD
		Screen motd = (Screen) Key.instance().getOnline().getElement( "motd" );
		
		if( motd != null && !motd.isEmpty() && (p.loginStats.lastConnection == null || motd.isModifiedAfter( p.loginStats.lastConnection ) ) )
		{
			ic.send( motd.text, false );
			Grammar.getReturn( ic );
		}
		
			//  the clan motd
		{
			Clan c;
			c = p.getClan();

			if( c != null )
			{
				motd = c.motd;
				
				if( motd != null && !motd.isEmpty() && (p.loginStats.lastConnection == null || motd.isModifiedAfter( p.loginStats.lastConnection ) ) )
				{
					ic.send( motd.text, false );
					Grammar.getReturn( ic );
				}
			}
		}
		
			// link this player into the program
		try
		{
			synchronized( p )
			{
				if( p.connected() )
				{
					p.getConnection().sendSystem( "Reconnecting..." );
					
					p.disconnect();
				}
				
				p.connectTo( ic );
			}
		}
		catch( NonUniqueKeyException e )
		{
			ic.send( "This player is already on the program.  You will need to enter another name. (Internal error)" );
			throw new UnexpectedResult( e.toString() );
		}
		catch( BadKeyException e )
		{
			ic.send( "Your name must consist of alphabetical characters only.  Please try again. (Internal error)" );
			throw new UnexpectedResult( e.toString() );
		}
	}
	
	/**
	  *  Retrieves a player structure from the entered name.  Should
	  *  the name not be acceptable for some reason, null is returned.
	  *  Should a serious error occur, the connection will be closed,
	  *  also.
	  *
	  * @return null on failure
	 */
	protected Player getPlayerFromName( InteractiveConnection ic, String name ) throws IOException
	{
		Object a = Key.shortcuts().getExactElement( name );
		
		if( a == null )
		{
			a = Key.instance().getResidents().getExactElement( name );
			
			if( a != null )
			{
				try
				{
					Key.shortcuts().add( (Atom) a );
				}
				catch( Exception e )
				{
					Log.error( "while re-adding to shortcuts after the name disappeared", e );
					ic.send( "Something is wrong with that name.  Please contact a mage (or email " + Key.instance().getConfig().returnAddress + ") as soon as possible.\n" );
					return( null );
				}
			}
			
			return( connectGuest( ic, name ) );
		}
		else
		{
			if( a instanceof Player )
			{
				Player p = (Player) a;
				
				if( p.willSync() )
					return( connectResident( ic, p ) );
				else
					ic.send( "That name is in use by another new player at the moment, and can't be used." );
			}
			else
				ic.send( "That name means something else here." );
		}
		
		return( null );
	}
	
	/**
	  *  Puts the initial logo on the players screen.
	 */
	protected void outputLogo()
	{
		Screen logo = (Screen) Key.instance().getOnline().getElement( "logo" );
		
		if( logo != null )
			ic.send( logo.text, false );
	}
	
	/**
	  * Randomly chooses a loginquote and displays it and the total player population
	  * Noble 29Jul99
	 */
	 protected void outputQuote()
	 {
	 	Container loginquotes = (Container) Key.instance().getOnline().getElement( "loginquotes" );
	 	
	 	if( loginquotes != null )
	 	{
	 		
		 	int max = (int) loginquotes.getCountContents();
			
			int choice = (int) (Math.random() * max);
			
			Memo lq = (Memo) loginquotes.getElementAt( choice );
			
			if( lq != null )
			{
				StringBuffer sb = new StringBuffer();
				
				sb.append( lq.value );
				
				Container pop = (Container) Key.instance().getResidents();
				if( pop != null)
				{
					sb.append( "              population " );
					sb.append( pop.getCountContents() );
				}
				else
					sb.append( "              ghost town" );
				
				ic.send( new TextParagraph( TextParagraph.CENTERALIGNED, sb.toString() ) );
			}
			else
				ic.send( insert );
		}
		else
			ic.send( stolen );
			
		ic.blankLine();
								
	}
	
	/**
	  *  This function returns the player's name, as entered at
	  *  the 'Please enter your name:' prompt.
	 */
	protected String getPlayersName( InteractiveConnection ic ) throws IOException
	{
		boolean name_okay;
		String name;
		
		do
		{
			name_okay = false;
			
			name = ic.input( "Please enter your name: " );
			
			if( name.equalsIgnoreCase( "quit" ) )
			{
				ic.flush();
				ic.close();
				return( null );
			}
			else if( name.equalsIgnoreCase( "who" ) )
			{
				ic.blankLine();
				ic.blankLine();
					//  the old way:
				//key.commands.Who.displayTable( ic, Key.instance().players() );
					//  the new way:
				executeCommand( "swho", ic );
				ic.blankLine();
			}
			else if( name.equalsIgnoreCase( "version" ) )
			{
				ic.blankLine();
				ic.blankLine();
					//  the new way:
				executeCommand( "version", ic );
				ic.blankLine();
			}
			else if( name.toLowerCase().startsWith( "finger " ) )
			{
				ic.blankLine();
				ic.blankLine();
				executeCommand( name, ic );
				ic.blankLine();
			}
			else if( !Grammar.isStringCompletelyAlphabetical( name ) )
			{
				ic.send( "Your name must only contain alphabetical characters" );
			}
			else if( name.length() < Player.MIN_NAME )
			{
				ic.send( "We require that your name is at least " + Player.MIN_NAME + " characters long.  You'll need to choose another." );
			}
			else if( name.length() > Player.MAX_NAME )
			{
				ic.send( "We require that your name is less than " + Player.MAX_NAME + " characters long.  You'll need to choose another." );
			}
			else
				name_okay = true;
		} while( !name_okay );

		return( name );
	}
	
	/**
	  *  Matches a command from /commandsets/base and tries to
	  *  execute it with a null player.
	 */
	protected void executeCommand( String commandLine, InteractiveConnection ic ) throws IOException
	{
		StringTokenizer st = new StringTokenizer( commandLine );
		
		if( st.hasMoreTokens() )
		{
			String cmdname = st.nextToken();
			
			try
			{
				Object o = (new Search( "/commandsets/base/" + cmdname, null )).result;
			
				if( o != null && o instanceof Command )
				{
						((Command)o).run( null, st, commandLine, null, ic, null );
				}
				else
					ic.sendFailure( "Command not found." );
			}
			catch( Exception e )
			{
				Log.error( e );
			}
		}
	}
	
	/**
	  *  This function outputs the 'are you sure you want to use this
	  *  name' prompt, when someone starts to create a new player.
	  *
	  * @param ic the connection
	  * @param name the name the player originally typed
	  * @return true if the person replied 'yes'
	 */
	protected boolean guestConfirmName( InteractiveConnection ic, String name ) throws IOException
	{
		ConnectionConfiguration connectConfig = (ConnectionConfiguration) Key.instance().getConfig().getElement( "connection" );
		
		ic.send( (Paragraph) connectConfig.getProperty( "newbieConfirm" ) );
		ic.blankLine();
		
			//  output the multiple matches screen
		Object o;
		o = Key.instance().getResidents().getTrieFor( name.substring( 0, 3 ) );
		
		if( (o != null) )
		{
			if( o instanceof Trie )
				ic.send( new TextParagraph( TextParagraph.CENTERALIGNED, ((Trie)o).contents(), 10, 10, 0, 0 ), false );
			else if( o instanceof Reference )
				ic.send( new TextParagraph( TextParagraph.CENTERALIGNED, ((Reference)o).getName(), 10, 10, 0, 0 ), false );
		}
		else
			ic.send( new TextParagraph( TextParagraph.CENTERALIGNED, "<no close names found>", 10, 10, 0, 0 ), false );
		
		ic.blankLine();
		
		return( Grammar.getYesNo( "Do you still wish to create a new character of the name '" + name + "'? ", false, ic ) );
	}
	
	public InteractiveConnection getInteractiveConnection()
	{
		return( ic );
	}
	
	private boolean mod = true;
	
	public boolean modified()
	{
		return( mod );
	}
	
	public void resetModify()
	{
		mod = false;
	}
	
	public synchronized void deallocate()
	{
		ic.send( "\n\nYou have taken too long to connect." );
		ic.flush();
		ic.close();
	}
	
	public static final TextParagraph stolen = new TextParagraph( TextParagraph.CENTERALIGNED, "Someone has stolen the loginquotes!" );
	public static final TextParagraph insert = new TextParagraph( TextParagraph.CENTERALIGNED, "<Insert your own quote here>" );
}
