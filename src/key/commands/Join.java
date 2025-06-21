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
**  21Jul97     subtle       created this command
**  29Jul97     exile        fixed the "fading into view" message.
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
public class Join extends Command
{
	private static final long serialVersionUID = -1163937509448863652L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Join.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player leaves" ),
		AtomicElement.construct( Join.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player enters" ),
		AtomicElement.construct( Join.class, String.class, "failure",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the move fails" ),
		AtomicElement.construct( Join.class, String.class, "noFind",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the room cannot be found" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String leave = "%o slowly fades from view...";
	public String enter = "%o materialises before you...";
	public String failure = "But you are already with %t.";
	public String noFind = "But %g is hiding.";

	public static final char playerCode = 'o';
	public static final char genderCode = 'g';
	public static final char targetCode = 't';
	
	public Join()
	{
		setKey( "join" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Room current = p.getLocation();
		
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		Player in = null;
		
		try
		{
			in = (Player) getOnlinePlayer( p, ic, args.nextToken() );
		}
		catch( ClassCastException e )
		{
			ic.sendError( "It doesn't make much sense to join more than one person.  Pick one." );
			return;
		}
		
		if( in != null )
		{
			p.putCode( playerCode, p.getName() );
			p.putCode( targetCode, in.getName() );
			p.putCode( genderCode, in.heShe() );
			
			Room wanted = null;
			
			try
			{
				wanted = in.getLocation();
			}
			catch( AccessViolationException e )
			{
			}
			
			if( wanted == null )
			{
				String nf;
				
				nf = Grammar.substitute( noFind, p.getCodes() );
				ic.sendFailure( nf );
				return;
			}
			
			if( current == wanted )
			{
				String nf;
				
				nf = Grammar.substitute( failure, p.getCodes() );
				ic.sendFailure( nf );
			}
			else if( wanted.canMoveInto( p ) )
			{
				String e;
				String l;
				
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
	}
}
