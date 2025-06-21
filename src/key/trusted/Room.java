/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
**
**  $Id: Room.java,v 1.7 2000/07/11 14:13:45 noble Exp $
**
**  $Log: Room.java,v $
**  Revision 1.7  2000/07/11 14:13:45  noble
**  PublicRoom's history no longer Grammar substituted
**
**  Revision 1.6  2000/03/02 22:23:38  subtle
**  first checkin of personal objects - objects owned by someone aren't accessible to other people or outside that persons rooms.  there are probably security flaws, this needs testing.
**
**  Revision 1.5  2000/01/05 12:14:56  noble
**  Colour codes/Method calls
**
**  Revision 1.4  1999/10/18 13:00:08  pdm
**  moved object splashes to rooms only
**
**  Revision 1.3  1999/10/15 14:37:14  pdm
**  added $ tags
**
*/

package key;

import key.collections.*;
import key.util.MultiEnumeration;

import java.util.Enumeration;
import java.io.*;
import java.util.StringTokenizer;

public class Room extends Landscape
{
	private static final long serialVersionUID = 2178840055236700139L;
	
	public static final int MAX_DESCRIPTION_LINES = Player.MAX_DESCRIPTION_LINES;
	public static final int MAX_DESCRIPTION_BYTES = Player.MAX_DESCRIPTION_BYTES;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Room.class, String.class, "called",
			AtomicElement.PUBLIC_FIELD,
			"the title of this room" ),
		AtomicElement.construct( Room.class, TextParagraph.class,
			"description", "description",
			AtomicElement.PUBLIC_FIELD,
			"a description of this room",
				//  these constants should be put elsewhere
			AtomicSpecial.TextParagraphLengthLimit( MAX_DESCRIPTION_BYTES,
			                                        MAX_DESCRIPTION_LINES ) ),
		AtomicElement.construct( Room.class, String.class, "portrait",
			AtomicElement.PUBLIC_FIELD,
			"a short description of this room" ),
		AtomicElement.construct( Room.class, String.class, "relation",
			AtomicElement.PUBLIC_FIELD,
			"the type of room, such as 'in', 'on' or 'at'" ),
		AtomicElement.construct( Room.class, String.class, "fullPortrait",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"relation followed by portrait" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Landscape.STRUCTURE, ELEMENTS );
	
	/**
	  *  what the room is called and the player
	  *  sees as a short description
	 */
	public String called;
	
	public TextParagraph description;
	public String portrait;
	public String relation;
	
	public static final int MAX_IN_ROOM = 20;
	
	public Room()
	{
		description = new TextParagraph( "" );
		portrait = "somewhere";
		called = "";
		relation = "in";
		
		setLimit( MAX_IN_ROOM );
		
			//  generally, its okay to go into a room
		permissionList.allow( enterAction );

			//  these would be permission problems if
			//  we had user-coding: some system needs
			//  to be set up here.
		permissionList.allow( addToAction );
		permissionList.allow( removeFromAction );
		
		setConstraint( Type.THING );
		contained = new ObjectCollection();
	}
	
	public Room( Object key )
	{
		this();
		setKey( key );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void clearTransient()
	{
		Room backup = (Room) new Search( "landscape/void", Key.instance() ).result;
		
		if( backup == null || backup == this )
		{
			Log.debug( this, "No voidroom in Room::clearTransient" );
			throw new UnexpectedResult( "Cannot clear rooms when there is no void room." );
		}
		
		String msg = getLoginMessage();
		
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			try
			{
				Player p = (Player) e.nextElement();
				if( p.connected() )
				{
					Room r = (Room) p.getProperty( "lastPublicRoomLocation" );
					
					if( r == null || r == this )
						r = backup;
					
					p.sendSystem( "\n\nThe room around you vanishes!\n\n" );
					p.putCode( 'o', p.getName() );
					p.moveTo( r, null, new key.effect.Enter( p, r, Grammar.substitute( msg, p.getCodes() ) ) );
				}
			}
			catch( PlayerNotConnectedException ex )
			{
			}
		}
		
		super.clearTransient();
	}
	
	public void setCalled( String nc )
	{
		permissionList.check( modifyAction );
		called = nc;
	}
	
		/**  A players aspect is its description when looked at */
	public Paragraph aspect( Player l )
	{
		MultiParagraph.Generator p = new MultiParagraph.Generator();
		
		p.append( BlankLineParagraph.BLANKLINE );
		
		if( called != null && called.length() > 0 )
			p.append( new TextParagraph( TextParagraph.CENTERALIGNED, called ) );
		
		p.append( description );
		p.append( BlankLineParagraph.BLANKLINE );
		p.append( exits() );
		p.append( who( l ) );
		
		return( Grammar.substitute( p.getParagraph() ) );
	}
	
		//  returns, for eg: "the courtyard", or
		//  "a place belonging to subtle", or
		//  "a small hill".
	public String portrait()
	{
		return( portrait );
	}
	
	public String relation()
	{
		return( relation );
	}
	
		//  returns, for eg: "in the courtyard", where
		//  'in' is the prop, and 'the courtyard' is the portrait.
		//  META: used to be called 'fullPortrait'
	public String getFullPortrait()
	{
		return( relation + " " + portrait );
	}

	public Paragraph getDescription()
	{
		return( description );
	}
	
	public String getLoginMessage()
	{
		return( (String) Key.instance().getConfig().getProperty( "loginMessage" ) );
	}
	
	public String getLogoutMessage()
	{
		return( (String) Key.instance().getConfig().getProperty( "logoutMessage" ) );
	}
	
	public String getCalled()
	{
		return( called );
	}

	public String countPlayers( int np, boolean here )
	{
		if( np == 0 )
			return( "There is no-one in " + getId() );
		else if( np == 1 && here )
			return( "There is nobody here but you" );
		else
		{
			StringBuffer w = new StringBuffer();
			w.append( "There " );

			if( here )
				np--;

			w.append( Grammar.isAreCount( np ) );
			if( here )
				w.append( " other " );
			else
				w.append( " " );
			w.append( Grammar.personPeople( np ) );
			w.append( " here" );

			return( w.toString() );
		}
	}
	
	public Paragraph who( Player l )
	{
		boolean here=isCurrentHere();
		
		int np = numberPlayers();
		
		String cString = countPlayers( np, here );
		
		if( np == 0 || (np == 1 && here) )
			return( new TextParagraph( cString + "." ) );
		else
		{
				// see how tolerate the user is to the titles they see
			if( np < l.titleTolerance )	// remember the orginator is included in the count
			{
				StringBuffer w = new StringBuffer( cString );
				w.append( ":\n" );
			
				for( Enumeration e = players(); e.hasMoreElements(); )
				{
					Player p = (Player) e.nextElement();
					if( l != p )
						w.append( p.getTitledName() + "\n" );
				}
				return( new TextParagraph( w.toString() ) );
			}
			else
			{
				int i = 0;
					// allplayers is the total count of people in the scape
				String[] allplayers = new String[ np ];
				
					// generate a string array of all the players
				for( Enumeration e = players(); e.hasMoreElements(); )
				{
					Player p = (Player) e.nextElement();
					if( l != p ) // don't include the observer in the list
						allplayers[i++] = p.getName();
				}
						//  META very inefficient: replace with bounded comma seperate
						//  in grammar.
					// other players is all players - 1
				String[] otherplayers = new String[ i ];
				
					// copy from all players to other players (don't include the observer
				for( int j = 0; j < i; j++ )
						otherplayers[j] = allplayers[j];
				
				return( new TextParagraph( cString + ": " + Grammar.commaSeperate( otherplayers ) + ".\n" ) );
				
			}

		}
	}

	public String withPlayers( int np, boolean here, Player o, Player targetPlayer )
	{
		if( np == 0 )
			return( "That's strange..." );
		else if( np == 1 && !here )
			return( targetPlayer.getFullName() + " is alone" );
		else if( np == 1 && here )
			return( "It's just you here." );
		else if( np == 2 && here && targetPlayer != o )
			return( "Well, its just you and " + targetPlayer.getFullName() );
		else
		{
			StringBuffer w = new StringBuffer();
			w.append( "There " );
			
			// take one off so the target player is not counted in the total...
			np--;
			
			w.append( Grammar.isAreCount( np ) );
			if( here )
				w.append( " other " );
			else
				w.append( " " );
			
			w.append( Grammar.personPeople( np ) );
			w.append( " with " );
			w.append( targetPlayer.getFullName() );
			
			return( w.toString() );
		}
	}
	
	private boolean isCurrentHere()
	{
			//  start out by determining if observer is
			//  a player, and then if the player is *in*
			//  the room
		Player l = Player.getCurrent();
		if( l != null && l.location == this )
			return( true );
		
		return( false );
	}
	
	public String with( Player targetPlayer )
	{
		Player l=null;
		boolean here=isCurrentHere();

		int np = numberPlayers();
		String cString = withPlayers( np, here, l, targetPlayer );

		if( np == 0 || (np == 1 && !here) || (np == 1 && here) || (np == 2 && here) )
			return( cString + "." );
		else
		{
			StringBuffer w = new StringBuffer( cString );
			w.append( ":\n" );

			for( Enumeration e = players(); e.hasMoreElements(); )
			{
				Player p = (Player) e.nextElement();
				if( targetPlayer != p )
				{
					if( p.hidden )
						w.append( "Someone [hiding]" + "\n" );
					else
						w.append( p.getFullName() + "\n" );
				}
			}
			
			return( w.toString() );
		}
	}
	
		/**  eventually make this a property that can change */
	public Realm getRealm()
	{
		return( Key.instance().getDefaultRealm() );
	}
	
	public Paragraph exits()
	{
		String[] exits = new String[ this.count() ];
		String[] other = new String[ this.count() ];
		int i = 0;
		int k = 0;
		
			// the first iteration is basically to count the number of exits
			// and create an array of exits only.
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Object o = e.nextElement();
			if( o instanceof Exit )
				exits[i++] = ((Exit)o).getName();
			else if( o instanceof Thing )
			{
				if( ((Thing)o).isAvailableTo( null, this ) )
					other[k++] = ((Atom)o).getName();
			}
			else
			{
				Log.debug( this, "Strange object (" + ((Atom)o).getId() + ") in " + getId() );
				other[k++] = ((Atom)o).getName();
			}
		}
		
			// i is the number of objects that are actually exits
		if( i >= 1 || k >= 1 )
		{
			MultiParagraph.Generator mp = new MultiParagraph.Generator();
			
			if( i >= 1 )
			{
				/*
				String[] actual = new String[ i ];
				
				for( int j = 0; j < i; j++ )
					actual[j] = exits[j];
				*/
				
				mp.append( new TextParagraph( "Exits: " + Grammar.enumerate( exits, i  ) + "." ) );
			}
			
			if( k >= 1 )
			{
				/*
				String[] actual = new String[ k ];
				
				for( int j = 0; j < k; j++ )
					actual[j] = other[j];
				*/
				
				mp.append( new TextParagraph( "Objects: " + Grammar.enumerate( other, k ) + "." ) );
			}

			return( mp.getParagraph() );
		}
		else
			return( null );
	}
	
	public void ejectPlayer( Player p, Effect enter, Effect leave )
	{
		permissionList.check( ejectFromAction );
		p.ejected( this, enter, leave );
	}
	
	/**
	  *  Only stops a direct trans - not using an exit
	 */
	public boolean canMoveInto( Player p )
	{
		try
		{
			permissionList.check( enterAction );
			return true;
		}
		catch( PermissionDeniedException e )
		{
			return( false );
		}
		catch( AccessViolationException e )
		{
			return( false );
		}
	}
	
	//---  actions  ---//
	
	protected static StringKeyCollection staticActions;
	public static Action ejectFromAction;
	public static Action enterAction;
	public static Action setHomeAction;

	static
	{
		staticActions = new StringKeyCollection();
		ejectFromAction = newAction( Room.class, staticActions, "ejectFrom", false, false );
		enterAction = newAction( Room.class, staticActions, "enter", false, false );
		setHomeAction = newAction( Room.class, staticActions, "setHome", false, false );
	}
	
	public Enumeration getActions()
	{
		return( new MultiEnumeration( staticActions.elements(), super.getActions() ) );
	}
	
	public boolean containsAction( Action a )
	{
		return( staticActions.contains( a ) || super.containsAction( a ) );
	}
	
	/**
	  *  Returns the action corresponding to the
	  *  supplied name.  This routine will need to
	  *  be overriden by sub-classes using actions.
	 */
	public Action getAction( String name )
	{
		Action a = (Action) staticActions.get( name );

		if( a == null )
			return( super.getAction( name ) );
		else
			return( a );
	}
	
	/**
	  *  A general way of saying that the people in
	  *  this scape should be notified of the
	  *  supplied Effect.
	 */
	public void splash( Effect t, SuppressionList s )
	{
		Atom oldsplasher = t.getSplasher();
		t.setSplasher( this );
		
		playergroup.splash( t, s ); //splash all Players in the scape
		
		for( Enumeration e = elements(); e.hasMoreElements(); ) //splash all Things in the scape. (Noble 09Oct99)
		{
			Atom a = (Atom) e.nextElement();
			
			if( a instanceof Thing )
				a.splash( t , s );
		}
		
		t.setSplasher( oldsplasher );
	}
	
	/**
	  *  Sends the splash to everyone except the listed
	  *  atom
	 */
	public void splashExcept( Effect t, Splashable except, SuppressionList sl )
	{
		Atom oldsplasher = t.getSplasher();
		t.setSplasher( this );
		
		playergroup.splashExcept( t, except, sl ); //splash all Players in the scape
		
		for( Enumeration e = elements(); e.hasMoreElements(); ) //splash all Things in the scape. (Noble 09Oct99)
		{
			Atom a = (Atom) e.nextElement();
			
			if( a instanceof Thing && a != except)
				a.splash( t , sl );
		}		
		
		t.setSplasher( oldsplasher );
	}
	
		//  here we prevent things from being added to the room
	protected void checkAdd_imp( Atom a )
	{
		super.checkAdd_imp( a );
		
		if( a instanceof Thing )
		{
				//  check to see if this object is available in this room
				//  if not, don't allow it to be added
			if( !((Thing)a).isAvailableTo( Player.getCurrent(), this ) )
			{
				throw new PermissionDeniedException( this, "This is a personal (non-approved) object, and can only be dropped in one of your own rooms" );
			}
		}
	}
}
