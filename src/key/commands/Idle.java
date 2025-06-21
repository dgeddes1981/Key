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
**  $Id: Idle.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  04Jul97    exile        added on/off toggle to idle so that the player
**                          can put themself into and take themselves out
**                          of idle mode.
**  21Jul97    subtle       tidied, added recalculation of idleprompt
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Idle extends Command
{
	public static final String IN = "You are now in idle mode.";
	public static final String OUT = "You are no longer in idle mode.";
	
	public Idle()
	{
		setKey( "idle" );
		usage = "[<player>] | [on|off]";
	}

	/**
	  *  checks the idle time of <player>
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String playerName = args.nextToken();
			
			if( playerName.equalsIgnoreCase( "on" ) )
			{
				p.setIdle( true );
				ic.sendFeedback( IN );
				ic.sendFeedback( p.getName() + p.getIdleMsg() );

			}
			else if( playerName.equalsIgnoreCase( "off" ) )
			{
				p.setIdle( false );
				ic.sendFeedback( OUT );
			}
			else
			{
					//  it's a players name and not a toggle option
				Object target = getOnlinePlayer( p, ic, playerName );
				
				if( target instanceof LWPlayerGroup )
				{
					for( Enumeration e = ((LWPlayerGroup)target).players(); e.hasMoreElements(); )
						doIdle( p, (Player)e.nextElement(), ic, flags );
				}
				else if( target instanceof Player )
				{
					doIdle( p, (Player) target, ic, flags );
				}
				else if( target != null )
					ic.sendError( "Don't know what that is to idle it." );
			}
		}
		else
		{
				//  no param means toggle the current idle state
			if( p.isIdle() )
			{
				p.setIdle( false );
				ic.sendFeedback( OUT );
			}
			else
			{
				p.setIdle( true );
				ic.sendFeedback( IN );
				ic.sendFeedback( "When they try to talk to you, people will see:" );
				ic.sendFeedback( p.getName() + " is idle: " + p.getIdleMsg() );
			}
		}
	}
	
	public void doIdle( Player p, Player targetPlayer, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( targetPlayer != null )
		{
			Duration idleTime = (Duration) targetPlayer.getIdle( new DateTime() );
			ic.sendFeedback( targetPlayer.getName() + " has been idle for " + idleTime.toString() );
		}
	}
}
