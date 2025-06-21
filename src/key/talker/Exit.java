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
*/

package key;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

/**
  *  Exits connect rooms.
 */
public class Exit extends Material implements Thing
{
	private static final long serialVersionUID = -7925959907727440220L;
	public static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( Exit.class, Room.class, "to",
			AtomicElement.PUBLIC_FIELD,
			"the room this exit goes to" ),
		AtomicElement.construct( Exit.class, String.class, "departRoom",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when someone exits" ),
		AtomicElement.construct( Exit.class, String.class, "arriveRoom",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when someone arrives" ),
		AtomicElement.construct( Exit.class, Paragraph.class, "through",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the person who walks through the exit" ),
		AtomicElement.construct( Exit.class, Paragraph.class, "description",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to a person who looks at the exit" ),
		AtomicElement.construct( Exit.class, Boolean.TYPE, "autoLook",
			AtomicElement.PUBLIC_FIELD,
			"whether an automatic 'look' command is performed when someone walks through the exit" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Material.STRUCTURE, ELEMENTS );
	
	public Reference to = Reference.EMPTY;
	public String departRoom = "%o walks out";
	public String arriveRoom = "%o walks in";
	public Paragraph through = new TextParagraph( "You walk through the exit" );
	public Paragraph description = new TextParagraph( "A rent in the fabric of reality" );

	/**
	  *  determines if a 'look' command is automatically
	  *  invoked when you step through the exit.  If true,
	  *  the rooms description is displayed.  Otherwise, just
	  *  a listing of the other people here is displayed.
	 */
	public boolean autoLook = true;
	
	public static final char originatorCode = 'o';
	public static final char hesheCode = 'h';
	public static final char hisHerCode = 'i';
	
	public Exit()
	{
			//  exits are not getable, generally
			//  speaking.
		getPermissionList().deny( Atom.moveAction );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public final String getDepartRoom()
		{ return( departRoom ); }
	
	public final Paragraph getThrough()
		{ return( through ); }
	
	public final String getArriveRoom()
		{ return( arriveRoom ); }
	
	public final Paragraph getDescription()
		{ return( description ); }
	
	public final Room getTo()
	{
		try
		{
			return( (Room)to.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			to = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( getId() + ".to", e );
			to = Reference.EMPTY;
			return( null );
		}
	}
	
	public final boolean getAutoLook()
		{ return( autoLook ); }
	
	public void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container orig )
	{
		ic.send( description );
	}
	
	public void get( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container orig )
	{
		ic.sendFailure( "You can't pick up a direction!" );
	}
	
	public void drop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Atom item, Container orig )
	{
		ic.sendFailure( "You can't drop a direction!" );
	}
	
	public static final Paragraph throughParagraph = new HeadingParagraph( "through" );
	public static final Paragraph descriptionParagraph = new HeadingParagraph( "description" );
	public static final Paragraph throughHelpParagraph = new TextParagraph( TextParagraph.CENTERALIGNED, "(the message a player sees when going through this exit is shown below)" );
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		defaultInspect( p, args, ic, flags, item );
		
		putCodes( p );
		
		ic.send( "Autolook: " + Grammar.onOff( autoLook ) );
		ic.send( "The arriveRoom message is: " + Grammar.substitute( arriveRoom, p.getCodes() ) );
		ic.send( "The departRoom message is: " + Grammar.substitute( departRoom, p.getCodes() ) );
		
		ic.blankLine();
		
		Room room = getTo();
		if( room == null )
			ic.send( "This exit doesn't lead anywhere, by the looks of it." );
		else
			ic.send( "This exit leads to " + room.portrait() + ", designated by " + room.getId() + "." );
		
		ic.blankLine();
		
		ic.send( descriptionParagraph );
		ic.send( description );
		
		ic.blankLine();
		
		ic.send( throughHelpParagraph );
		
		ic.send( throughParagraph );
		ic.send( through );
		ic.sendLine();
	}
	
	public String getFullPortrait( Player p )
	{
		Room r = getTo();
		
		if( r != null )
			return( "exit to the " + r.portrait );
		else
			return( "exit to nowhere" );
	}
	
	public void putCodes( Player p )
	{
		p.putCode( originatorCode, p.getName() );
		p.putCode( hesheCode, p.heShe() );
		p.putCode( hisHerCode, p.hisHer() );
	}
	
	public void use( Player p, StringTokenizer _args, InteractiveConnection _ic, Flags _flags, Thing item, Container originating )
	{
			//  the process:
			//    1. splash the exit messages to the players current location
			//    2. move the player to the new room (toProperty)
			//    3. splash the enter message to the players current location
			//       (to room)
		
		String enter;
		String leave;
		
		putCodes( p );
		
			// exile: swapped the exit events so that they splash correctly
		enter = Grammar.substitute( arriveRoom, p.getCodes() );
		leave = Grammar.substitute( departRoom, p.getCodes() );
		
		Effect le = null;
		Effect ee = null;
		
		Room wanted = (Room) to.get();
		Room current = p.getLocation();
		InteractiveConnection ic = p.getConnection();
		
		if( ic == null )
			throw new PlayerNotConnectedException();
		
		if( wanted == null )
		{
			ic.sendFailure( "You can't go that way.  (We were lying about the exit)" );
			return;
		}
		else
			ee = new key.effect.Enter( p, wanted, enter );
		
		if( current != null )
			le = new key.effect.Leave( p, current, leave );
		
		p.moveTo( wanted, le, ee );
		
		ic.send( through );
		
		if( autoLook )
			p.roomLook();
		else
		{
			if( wanted.called != null )
				ic.send( wanted.called );
			else
				ic.send( "[" + wanted.getId() + "]" );
			
			ic.send( wanted.who( p ) );
			
			Paragraph exits = wanted.exits();
			if( exits != null )
				ic.send( exits );
		}
	}
	
	public void setTo( Room r )
	{
		to = r.getThis();
	}
	
	protected void checkAdd_imp( Atom a )
	{
		if( a instanceof PublicRoom )
		{
			Player p = Player.getCurrent();
			if( p != null && !p.isBeyond() )
				throw new PermissionDeniedException( this, "adding exit to system room" );
		}
	}
}
