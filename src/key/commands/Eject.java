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
**  $Id: Eject.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle       created this command
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  Removes someone from an owned room.
 */
public class Eject extends Command
{
	private static final long serialVersionUID=-5426923688822903086L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Eject.class, String.class, "leave",
			AtomicElement.PUBLIC_FIELD,
			"the string sent to the room the player leaves" ),
		AtomicElement.construct( Eject.class, String.class, "enter",
			AtomicElement.PUBLIC_FIELD,
			"the string sent to the room the player enters" ),
		AtomicElement.construct( Eject.class, String.class, "failure",
			AtomicElement.PUBLIC_FIELD,
			"the feedback string if the eject fails" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char playerCode = 'o';
	
	public String leave = "%o vanishes.";
	public String enter = "%o tumbles in.";
	public String failure = "You can't eject them.";
	
	public Eject()
	{
		setKey( "eject" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
		//  this routine is a bit of a mess.  It would be better to
		//  move the permissions checking stuff to room & player,
		//  rather than exit.  But blah, y'know?
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		Player target = getPlayer( ic, args.nextToken() );
		
		if( target == null )
			return;
		
		if( target == p )
		{
			ic.sendError( "That's you.  Isn't it fun looking for stupid things the code will let you do?" );
			return;
		}
		
		Room current = target.getLocation();
		Room wanted = target.getLastPublicRoom();
		
		if( wanted == null )
		{
			wanted = Key.instance().getConnectRoom( target );
		}

		if( current == null )
		{
			ic.sendError( "But " + target.getName() + " isn't here at the moment." );
			return;
		}
		
		if( wanted == current )
		{
			ic.sendError( "But " + target.getName() + " is already in a public room." );
			return;
		}
		
		try
		{
			{
				String e;
				String l;
				
				p.putCode( playerCode, target.getName() );
				
				e = Grammar.substitute( enter, p.getCodes() );
				l = Grammar.substitute( leave, p.getCodes() );
				
				Effect ee = new key.effect.Enter( target, wanted, e );
				Effect le = null;
				
				if( current != null )
					le = new key.effect.Leave( target, current, l );
				
				current.ejectPlayer( target, ee, le );
				p.sendFeedback( "You eject " + target.getName() + " from " + current.getCalled() );
			}
		}
		catch( AccessViolationException e )
		{
			ic.sendFailure( failure );
		}
	}
}
