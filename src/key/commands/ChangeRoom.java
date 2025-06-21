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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Jul97     subtle       created this command
**  28Jul97     exile        fixed the "fades from view" thing
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  ChangeRoom returns you to the last public room
  *  you were in.  It will mainly be used to
  *  leave someone's rooms (including your own)
 */
public class ChangeRoom extends Command
{
	private static final long serialVersionUID = 566814661709401352L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( ChangeRoom.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player leaves" ),
		AtomicElement.construct( ChangeRoom.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player enters" ),
		AtomicElement.construct( ChangeRoom.class, String.class, "failure",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the move fails" ),
		AtomicElement.construct( ChangeRoom.class, String.class, "noFind",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the room cannot be found" ),
		AtomicElement.construct( ChangeRoom.class, Atom.class, "referenceIn",
			AtomicElement.PUBLIC_FIELD,
			"the place to look for the room" ),
		AtomicElement.construct( ChangeRoom.class, String.class, "roomProperty",
			AtomicElement.PUBLIC_FIELD,
			"the property within the reference to search for the room (eg, 'home', if the reference is to a player)" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char playerCode = 'o';
	
	public String leave = "%o slowly fades from view...";
	public String enter = "%o materialises before you...";
	public String roomProperty = "";
	public String failure = "But you are already there.";
	public String noFind = "Cannot find that place.";
	public Reference referenceIn = Key.shortcuts().getThis();
	
	public ChangeRoom()
	{
		setKey( "changeRoom" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Room current = p.getLocation();
		
		Atom in = (Atom) referenceIn.get();
		
		if( in != null )
		{
			Room wanted = null;
			
			try
			{
				wanted = (Room) in.getProperty( roomProperty );
			}
			catch( NoSuchPropertyException e )
			{
				ic.sendFailure( noFind );
				return;
			}
			catch( ClassCastException e )
			{
				ic.sendFailure( noFind );
				return;
			}
			
			if( wanted == null )
			{
				wanted = noSuchRoom( p, ic );

				if( wanted == null )
					return;
			}
			
			if( current == wanted )
				ic.sendFailure( failure );
			else if( wanted.canMoveInto( p ) )
			{
				String e;
				String l;
				
				p.putCode( playerCode, p.getName() );
				
				e = Grammar.substitute( enter, p.getCodes() );
				l = Grammar.substitute( leave, p.getCodes() );
				
				Effect ee = new key.effect.Enter( p, wanted, e );
				Effect le = null;
				
				if( current != null )
					le = new key.effect.Leave( p, current, l );
				
				p.moveTo( wanted, le, ee );
				p.roomLook();
			}
			else
				ic.sendFailure( "You may not use this mode of travel to go there." );
		}
		else
			ic.sendFailure( "The command is temporarily unavailable" );
	}

	protected Room noSuchRoom( Player p, InteractiveConnection ic )
	{
		ic.sendFailure( noFind );
		return null;
	}
}
