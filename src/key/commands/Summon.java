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
**  $Id: Summon.java,v 1.2 1999/11/17 13:50:19 noble Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  23Jul97     subtle       created this command
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Summon extends Command
{
	private static final long serialVersionUID = -5001453991446325408L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Summon.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player leaves" ),
		AtomicElement.construct( Summon.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the room when a player enters" ),
		AtomicElement.construct( Summon.class, String.class, "failure",
			AtomicElement.PUBLIC_FIELD,
			"the feedback if you're not allowed to summon them" ),
		AtomicElement.construct( Summon.class, String.class, "notAnywhere",
			AtomicElement.PUBLIC_FIELD,
			"the feedback if you don't have a current location" ),
		AtomicElement.construct( Summon.class, String.class, "alreadyHere",
			AtomicElement.PUBLIC_FIELD,
			"the feedback if the player is already in the same room" ),
		AtomicElement.construct( Summon.class, String.class, "target",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when grabbed" ),
		AtomicElement.construct( Summon.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player grabbing" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String leave = "%t is summoned by %p.";
	public String enter = "%t appears, having been summoned by %p.";
	public String target = "^hYou have been summoned by %p.^-";
	public String feedback = "You successfully summon %t.";
	public String failure = "You can't summon them.";
	public String notAnywhere = "You don't appear to be anywhere";
	public String alreadyHere = "They're, uh, already here?";
	
	public static final char playerCode = 't';
	public static final char summonerCode = 'p';
	
	public Summon()
	{
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void doSummon( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags, Player v )
	{
		p.putCode( playerCode, v.getName() );
		p.putCode( summonerCode, p.getName() );
		
		if( !v.permissionCheck( Player.summonAction, true, true ) )
		{
			ic.sendError( Grammar.substitute( failure, p.getCodes() ) );	
			return;
		}
		
		Room wanted = p.getLocation();
		Room current = v.getLocation();
		
		if( wanted == null )
			ic.sendFailure( Grammar.substitute( notAnywhere, p.getCodes() ) );	
		else if( wanted == current )
			ic.sendFailure( Grammar.substitute( alreadyHere, p.getCodes() ) );	
		else
		{
			String e;
			String l;
			
			e = Grammar.substitute( enter, p.getCodes() );
			l = Grammar.substitute( leave, p.getCodes() );
			
			Effect ee = new key.effect.Enter( v, wanted, e );
			Effect le = null;
			
			if( current != null )
				le = new key.effect.Leave( v, current, l );
			
			v.moveTo( wanted, le, ee );
			v.roomLook();
			
			v.send( Grammar.substitute( target, p.getCodes() ) );	
			ic.send( Grammar.substitute( feedback, p.getCodes() ) );	
		}
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String pName = args.nextToken();
		
		Object o = getOnlinePlayer( p, ic, pName );
		
		if( o == null )
			return;
		
		if( o instanceof Player )
		{
			doSummon( p, args, fullLine, caller, ic, flags, (Player) o );
		}
		else if( o instanceof PlayerGroup )
		{
			PlayerGroup pg = (PlayerGroup) o;
			
			for( Enumeration e = pg.players(); e.hasMoreElements(); )
				doSummon( p, args, fullLine, caller, ic, flags, (Player) e.nextElement() );
		}
		
	}
}
