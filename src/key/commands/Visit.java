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
**  $Id: Visit.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul97     subtle       created this command
**  02Aug97     exile        fix so that a player can't visit through a deny
**                           enter.
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

public class Visit extends Command
{
	private static final long serialVersionUID = 3143960238620407694L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Visit.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player leaves" ),
		AtomicElement.construct( Visit.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player enters" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String leave = "%o fades from view...";
	public String enter = "%o appears.";
	
	public static final char playerCode = 'o';
	
	public Visit()
	{
		setKey( "visit" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String pName = args.nextToken();
		
		Player v = getPlayer( ic, pName );
		
		if( v == null )
			return;
		
		Room wanted = v.getHome();
		Room current = p.getLocation();
		
		if( wanted == null )
			ic.sendFailure( v.getName() + " doesn't seem to have a home." );
		else
		{
			String e;
			String l;
			
			p.putCode( playerCode, p.getName() );
			
			e = Grammar.substitute( enter, p.getCodes() );
			l = Grammar.substitute( leave, p.getCodes() );
			
			Effect ee = new key.effect.Enter( p, wanted, e );
			Effect le = null;
			
				// check to see if the player is able to enter (exile 02Aug97)
			if( wanted.canMoveInto( p ) )
			{
				if( current != null )
					le = new key.effect.Leave( p, current, l );
				
				p.moveTo( wanted, le, ee );
				p.roomLook();
			}
			else
				ic.sendFeedback( "Access to " + v.getName() + "." + wanted.getName() + " is denied." );

		}
	}
}
