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
import java.util.Enumeration;

public class Travel extends Command
{
	private static final long serialVersionUID=-5287840842123499345L;
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
	
	public static final char playerCode = 'o';
	public static final char enterMessageCode = 'e';
	public static final char leaveMessageCode = 'l';
	
	//public String leave = "A shimmering rift appears, and %o steps into it as it closes.";
	//public String enter = "A shimmering rift appears, and %o steps out of it as it closes.";
	
	public String leave = "%o %l";
	public String enter = "%o %e";
	
	public Travel()
	{
		setKey( "travel" );
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
			Object l = null;
			
			if( id.indexOf( "/" ) == -1 )
			{
				l = new Search( id, p.getContext() ).result;

				if( l == null )
				{
					ic.sendError( "Could not locate '" + id + "' in " + p.getContext().getId() );
					return;
				}
			}
			else
			{
				l = new Search( id, Key.shortcuts() ).result;
				
				if( l == null )
				{
					ic.sendError( "Could not locate '" + id + "'" );
					return;
				}
			}
			
			if( l instanceof Room )
			{
				Room wanted = (Room) l;
				Room current = p.getLocation();
				
					//  we're nice: if there is an exit
					//  to this room in the current room,
					//  we can use that instead.
				{
					Reference r = wanted.getThis();
					
					for( Enumeration e = current.elements(); e.hasMoreElements(); )
					{
						Object o = e.nextElement();
						
						if( o instanceof Exit )
						{
							Exit x = ((Exit)o);
							
							if( x.to.equals( r ) )
							{
								x.use( p, args, ic, flags, null );
								return;
							}
						}
					}
				}
				
					//  set the codes
				p.putCode( playerCode, p.getName() );
				
					//  substitute name for %o
				String e;
				String lm;
				
				p.putCode( enterMessageCode, p.getEnterMsg() );
				p.putCode( leaveMessageCode, p.getExitMsg() );
				
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
				ic.sendFeedback( "You have travelled to " + ( (Room) l).getId() + "." );
			}
			else if( l instanceof Atom )
				ic.sendError( "'" + ((Atom)l).getId() + "' is not a room." );
			else
				ic.sendError( "Object of type " + Type.typeOf( l ).getName() + " is not a room" );
		}
	}
}
