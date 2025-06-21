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

import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class Trans extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Trans.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player leaves" ),
		AtomicElement.construct( Trans.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player enters" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String enter = "%o appears with a loud *POP*";
	public String leave = "%o disappears with a loud *POP*";
	
	public static final char playerCode = 'o';
	
	public Trans()
	{
		setKey( "trans" );
		usage = "<location>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			String id = args.nextToken();
			Atom a = p.getContext();
			
			if( a instanceof Container )
			{
				Object l = ((Container)a).getElement( id );
				
				if( l == null )
					ic.sendError( "Could not locate '" + id + "'" );
				else if( l instanceof Room )
				{
					Room wanted = (Room) l;
					Room current = p.getLocation();
					
						//  set the codes
					p.putCode( playerCode, p.getName() );
					
						//  substitute name for %o
					String e;
					String lm;
					
					p.putCode( playerCode, p.getName() );
					
					e = Grammar.substitute( enter, p.getCodes() );
					lm = Grammar.substitute( leave, p.getCodes() );
					
					Effect ee = new key.effect.Enter( p, wanted, e );
					Effect le = null;
					
					if( current != null )
						le = new key.effect.Leave( p, current, lm );
					
					p.moveTo( wanted, le, ee );
					
						//  show them where they are
					p.roomLook();
					
						//  tell them
					ic.sendFeedback( "You have transported yourself to " + ( (Room) l).getId() + "." );
				}
				else if( l instanceof Atom )
					ic.sendError( "'" + ((Atom)l).getId() + "' is not a room." );
				else
					ic.sendError( "Object of type " + Type.typeOf( l ).getName() + " is not a room" );
			}
			else
				ic.sendError( "You have to be referencing a container for this command to work" );
		}
	}
}
