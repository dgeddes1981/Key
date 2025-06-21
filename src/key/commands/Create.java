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

package key.commands;

import key.*;
import key.util.Trie;
import key.commands.clan.*;

import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

/**
  *  An abstract superclass containing the common functionality
  *  between the creating functions.  (They're all pretty similar)
 */
public abstract class Create extends Command
{
	private static final long serialVersionUID = 2823156582231052536L;
	public Create()
	{
		setKey( "create" );
		usage = "";
	}

	/**
	  * @return true iff the name is okay
	 */
	protected static boolean checkName( String id )
	{
		return( id.indexOf( "/" ) == -1 && id.indexOf( "." ) == -1 && id.indexOf( "~" ) == -1 && id.indexOf( "#" ) == -1 );
	}
	
	public void add( InteractiveConnection ic, Atom created, Container place, String successFeedback ) throws IOException
	{
		try
		{
			place.add( created );
			
			if( successFeedback != null )
				ic.sendFeedback( successFeedback );
		}
		catch( BadKeyException e )
		{
			ic.sendError( "'" + created.getName() + "' should contain only alphabetic characters" );
		}
		catch( NonUniqueKeyException e )
		{
			ic.sendError( "There is already something of that name here" );
		}
		catch( TypeMismatchException e )
		{
			ic.sendError( "I don't quite know how to break this to you, so I'm just going to say it.  It seems that " + place.getName() + " isn't really meant to hold " + Type.typeOf( created ).getName() + "." );
		}
		catch( AccessViolationException e )
		{
			ic.sendError( "You're, uh, not really allowed to do that..." );
		}
	}
	
	public static class player extends Create
	{
		private static final long serialVersionUID = -4018356858163141459L;
		
		public player()
		{
			setKey( "player" );
			usage = "<name> <password>";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Atom created = null;
			Container place = null;
			String successFeedback;
			
			String id = nextArgument( args, ic );

			if( !Create.checkName( id ) )
			{
				ic.sendFailure( "Invalid characters in id.  (You can't use /'s or .'s)" );
				return;
			}
			
			String password = nextArgument( args, ic );
			
			place = Key.instance().getResidents();
			created = (key.Player) Factory.makeAtom( Player.class, id );
			created.setProperty( "canSave", Boolean.TRUE );
			((key.Player)created).setPassword( password );
			
			successFeedback = "Created player " + id + " with password '" + password + "'";
			
			this.add( ic, created, place, successFeedback );
			
				//  we might as well do this here, it has
				//  to be done, or the player will be a
				//  newbie forever. ;0
			created.sync();
		}
	}
	
	public static class object extends Create
	{
		public object()
		{
			setKey( "object" );
			usage = "<number>";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			String num = nextArgument( args, ic );
			int n = 0;
			Atom created = null;
			Container place = null;
			
			if( !num.startsWith( "#" ) )
			{
				place = (Container) new Search( "/realm.objects", null ).result;
				
				try
				{
					n = Integer.parseInt( num );
				}
				catch( NumberFormatException e )
				{
					ic.sendError( "'" + num + "' is not a number." );
					return;
				}
				
				if( place == null )
				{
					ic.sendError( "Could not locate /realm.objects" );
					return;
				}
				
				created = (Atom) place.getElementAt( n );
			}
			else
			{
				created = (Atom) new Search( num, null ).result;
			}
			
			if( created == null || (!(created instanceof Thing)) )
			{
				ic.sendError( "Could not find object number " + n );
				return;
			}
			
			Reference r = ((Thing)created).build( p );
			
			if( r != null )
			{
				Atom a = r.get();
				
				if( a != null )
				{
					try
					{
						p.getInventory().add( a );
					}
					catch( Exception e )
					{
						ic.sendFailure( e.toString() + " during build." );
					}
					
					ic.send( "Built: " + a.getId() );
				}
				else
					ic.sendError( "Build failed" );
			}
			else
				ic.sendError( "Build failed" );
		}
	}
	
	public static class objecttype extends Create
	{
		private static final long serialVersionUID = 522031145694060626L;
		
		public objecttype()
		{
			setKey( "objecttype" );
			usage = "<primary-name> <java-class>";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			String id = nextArgument( args, ic );
			if( !Create.checkName( id ) )
			{
				ic.sendFailure( "Invalid characters in id.  (You can't use /'s or .'s)" );
				return;
			}
			
			Atom created = null;
			Container place = null;
			String successFeedback;
				
			String classType = nextArgument( args, ic );
						
			place = (Container) new Search( "/realm.objects", null ).result;
						
			try
			{
				Class c = Class.forName( "key." + classType );
				
				if( !Thing.class.isAssignableFrom( c ) )
				{
					ic.sendFailure( "That class does not implement Thing" );
					return;
				}
				
				created = (Atom) Factory.makeAtom( c, id );
				
				successFeedback = "Created new " + c.getName();
			}
			catch( Exception e )
			{
				ic.sendError( e.toString() );
				return;
			}
			
			this.add( ic, created, place, successFeedback );
		}
	}
	
	public static class room extends Create
	{
		private static final long serialVersionUID = -3758269531230038487L;
		
		public room()
		{
			setKey( "room" );
			usage = "<id> [<name>]";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			String id = nextArgument( args, ic );
			if( !Create.checkName( id ) )
			{
				ic.sendFailure( "Invalid characters in id.  (You can't use /'s or .'s)" );
				return;
			}
			
			Atom created  = null;
			Container place = null;
			String successFeedback;
			String name;
			
			if( id.length() > Atom.MAX_KEY_LENGTH )
			{
				ic.sendFeedback( "That id is too long for a room." );
				return;
			}
			
				//  check for a name for the room
			if( args.hasMoreTokens() )
				name = args.nextToken( "" );
			else //  set the default room name
				name =  p.getName() + "'s boring room";
			
			Atom a = p.getContext();
			
			try
			{
				place = (Container) a;
			}
			catch( ClassCastException e )
			{
				ic.sendError( a.getName() + " isn't a container of any sort, and can't be used to hold things.  Sorry.  No, really, I am." );
				return;
			}
			
			created = (Room) Factory.makeAtom( Room.class, id );
			
				// set the default room title to <names>'s boring room
			created.setProperty( "called", "^h" + name + "^-" ); 
			
			successFeedback = "Built the room '" + id + "' (in " + place.getName() + ")";
		
			this.add( ic, created, place, successFeedback );
			
			if( place instanceof Player )
			{
				Player pl = (Player) place;
				
				if( pl.countRooms() == 1 )
				{
						//  set it to be their home room
					pl.setHome( (Room) created );
					
					if( pl == p )
						ic.sendFeedback( "This room has also been set to your home, since you don't have any others." );
					else
						ic.sendFeedback( "Since " + pl.getName() + " doesn't have any other rooms, this room has also been set as " + pl.hisHer() + " home." );
				}
			}
		}
	}
	
	public static class exit extends Create
	{
		private static final long serialVersionUID = 3953831862212183512L;

		public exit()
		{
			setKey( "exit" );
			usage = "<id> <destination room>";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			String id = nextArgument( args, ic );
			if( !Create.checkName( id ) )
			{
				ic.sendFailure( "Invalid characters in id.  (You can't use /'s or .'s)" );
				return;
			}
			
			Atom created = null;
			Container place = null;
			String successFeedback;
			String name;
			
			if( id.length() > Atom.MAX_KEY_LENGTH )
			{
				ic.sendFeedback( "That name is too long for an exit." );
				return;
			}
			
			Room dest = null;
			
			if( !args.hasMoreTokens() )
			{
				usage( ic );
				return;
			}
			
			String toId = args.nextToken();
			Atom ctx = null;
			
			if( toId.indexOf( '/' ) == -1 )
				ctx = p.getContext();
			else
				ctx = Key.shortcuts();
			
				//  exits must have a place to go...
			dest = (Room) getSymbolInside( ic, toId, Type.ROOM, ctx );
			
			if( dest != null )
			{
				place = p.getLocation();
				
				if( place == null )
				{
					ic.sendError( "You don't appear to, uh, *be* anywhere" );
					return;
				}
				
				created = (Exit) Factory.makeAtom( Exit.class, id );
				((Exit) created).setTo( (Room)dest );
				successFeedback = "Built the exit " + id + " in " + place.getName() + ", which leads to " + dest.portrait() + " (" + dest.getName() + ")";
			}
			else
				return;
			
			this.add( ic, created, place, successFeedback );
		}
	}
	
	public static class clan extends Command
	{
		private static final long serialVersionUID = -5538407593459767621L;
		
		public static final int MAX_RANKS = 7;
		public static final String COUNCIL_ID = "/online/channels/council";
		
		public clan()
		{
			setKey( "clan" );
			usage = "<founder> <clan name> ";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			String type;
			String playerName = nextArgument( args, ic );
			String clanName = nextArgument( args, ic );
			if( !Create.checkName( clanName ) )
			{
				ic.sendFailure( "Invalid characters in clan name.  (You can't use /'s or .'s)" );
				return;
			}
			
			
				//  get the targeted player
			Player targetPlayer = (Player) getPlayer( ic, playerName );
			
			if( targetPlayer == null )
				return;
			
			if( clanName.length() > Clan.MAX_NAME )
			{
				ic.sendFeedback( "That is too long for a Clan name, sheesh!" );
				return;
			}
			
				// Check to see if the person who a clan is being made
				// for is already in a clan, we *don't* want clans
				// created for those who are in one already!!
			
			Clan currentClan = (Clan) targetPlayer.getClan();
			if( currentClan != null )
			{
				ic.sendError( "'" + targetPlayer.getName() + "' is already in a Clan!" );
				return;
			}
			
			Container sc = Key.shortcuts();
			Object o = sc.getExactElement( clanName );
			if( o != null && !(o instanceof Trie) )
			{
				ic.sendError( "There is already something of this name." );
				return;
			}
			
				// has a verified player and the next string,
				// time to form the clan!
				
				// equivalent to instant Clan, it also puts it 
				// under the player who issued the Create_Clan command.
			
			Clan clan = (Clan) Factory.makeAtom( Clan.class, clanName );
			
				// we do this, to catch BadKeyException errors
				// and NonUniqueKeyException errors
			
			try
			{
				((Container)Key.instance().getElement( "clans" )).add( clan );	
			}
			catch( BadKeyException e )
			{
				ic.sendError( "'" + clanName + "' should contain only alphabetic characters" );
				return;
			}
			catch( NonUniqueKeyException e )
			{
				ic.sendError( "There is already something of that name here" );
				return;
			}
			
				// set the founder of the clan to the name specified
				// in the command (player existence already verified!)
			clan.setFounder( targetPlayer );
			
				// and set player field clan entry to their clan
			targetPlayer.enrolIntoClan( clan );
			
			clan.setRecursiveOwner( targetPlayer );
			
				// all done, send some feedback about the clan created and log it
			ic.sendFeedback( "Clan '" + clan.getName() + "' formed, with founder " + targetPlayer.getName() );
			
			if( targetPlayer != p && targetPlayer.connected() )
				targetPlayer.send( "Clan '" + clan.getName() + "' has been created for you." );
			
			String fm = p.getName() + " formed Clan '" + clan.getName() + "' for " + targetPlayer.getName();
			
			Group g = null;
			
			try
			{
				g = (Group) new Search( COUNCIL_ID, null ).result;
			}
			catch( ClassCastException e )
			{
			}
			
			if( g == null )
			{
				ic.sendFailure( "Could not find " + COUNCIL_ID );
				Log.log( "clan", "'" + COUNCIL_ID + "' not found looking for council" );
			}
			else
			{
				try
				{
					g.add( targetPlayer );
				}
				catch( Exception e )
				{
					String err = "Could not add " + targetPlayer.getName() + " to council: " + e.toString();
					ic.sendFailure( err );
					Log.log( "clan", err );
				}
			}
			
			Log.log( "clans/" + clan.getName() + ".records", fm );
			Log.log( "clan" , fm );
		}
	}
}
