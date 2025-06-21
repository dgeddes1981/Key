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
public class ConnectingTwig
implements LatentlyCached, Interactive
{
	InteractiveConnection ic;
	Player p = null;
	Player created = null;
	
	public ConnectingTwig()
	{
	}
	
	public void run( InteractiveConnection connection )
	{
		ic = connection;
		
		Player p = null;
		
		try
		{
			String name;
			int tries = 0;
			
			Key.getLatencyCache().addToCache( this );
			
			name = getPlayersName( ic );
			
				//  the player probably typed 'quit', this
				//  will clean it up.
			if( name == null )
			{
				ic.flush();
				ic.close();
				return;
			}
			
			p = getPlayerFromName( ic, name );
			
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
			
			ic.send( "SYSMSG:Error connecting:" + e.toString() );
			
			ic.flush();
			ic.close();
		}
		finally
		{
			if( p != null )
			{
				p.endConnect();
				p = null;
			}
			
			Key.getLatencyCache().removeFromCache( this );
		}
	}
	
	protected Player connectResident( InteractiveConnection ic, Player p ) throws IOException
	{
		p.beginConnect();
		
		//  now check if the player has been banished
		if( p.isBanished() )
		{
			ic.sendFeedback( "SYSMSG:Character currently banished." );
			
			p.endConnect();
			p = null;
			ic.flush();
			ic.close();
			return null;
		}
		
		boolean name_okay = false;
		
		String password = ic.input( "SENDPASS:Please send your password" );
		
		name_okay = p.authenticate( password );
		
		if( name_okay )
		{
				//  the endConnect is handled by the
				//  level above.
			//p.endConnect();
			
				//  should return a tag instead?
			return( p );
		}
		else
		{
				//  authentication failed, disconnect
			p.endConnect();
			p = null;
			
			ic.flush();
			ic.close();
			
			return( null );
		}
	}
	
	protected void connectToProgram( InteractiveConnection ic, Player p ) throws IOException
	{
			//  the MOTD
		Screen motd = (Screen) Key.instance().getOnline().getElement( "motd" );
		
		if( motd != null && !motd.isEmpty() && (p.loginStats.lastConnection == null || motd.isModifiedAfter( p.loginStats.lastConnection ) ) )
		{
			ic.send( "MOTD" + (((TextParagraph)motd.text).getText()) );
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
					ic.send( "CLANMOTD" + (((TextParagraph)motd.text).getText()) );
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
					p.getConnection().sendSystem( "SYSMSG:Reconnecting..." );
					
					p.disconnect();
				}
				
				p.connectTo( ic );
			}
		}
		catch( NonUniqueKeyException e )
		{
			ic.send( "SYSMSG:This player is already on the program.  You will need to enter another name. (Internal error)" );
			throw new UnexpectedResult( e.toString() );
		}
		catch( BadKeyException e )
		{
			ic.send( "SYSMSG:Your name must consist of alphabetical characters only.  Please try again. (Internal error)" );
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
		
		if( a != null )
		{
			if( a instanceof Player )
			{
				Player p = (Player) a;
				
				if( p.willSync() )
					return( connectResident( ic, p ) );
				else
					ic.send( "SYSMSG:That name is in use by another new player at the moment, and can't be used." );
			}
			else
				ic.send( "SYSMSG:That name means something else here." );
		}
		
		return( null );
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
			
			name = ic.input( "SENDNAME:Please send your name" );
			
			if( !Grammar.isStringCompletelyAlphabetical( name ) )
			{
				ic.send( "SYSMSG:Your name must only contain alphabetical characters" );
			}
			else if( name.length() < Player.MIN_NAME )
			{
				ic.send( "SYSMSG:We require that your name is at least " + Player.MIN_NAME + " characters long." );
			}
			else if( name.length() > Player.MAX_NAME )
			{
				ic.send( "SYSMSG:We require that your name is less than " + Player.MAX_NAME + " characters long.  You'll need to choose another." );
			}
			else
				name_okay = true;
		} while( !name_okay );
		
		return( name );
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
		ic.send( "SYSMSG:You have taken too long to connect." );
		ic.flush();
		ic.close();
	}
}
