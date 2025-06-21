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
**  $Id: Msgs.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Jul97    subtle       added additional messages
**
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Msgs extends Command
{
	public Msgs()
	{
		setKey( "msgs" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Player targetPlayer;
		String playerName;
	
		if( args.hasMoreTokens() )
		{
			playerName = args.nextToken();
			targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
		}
		else
		{
			targetPlayer = p;
			playerName = p.getName();
		}
		
			//  if the player is online
		if( targetPlayer != null )
		{
			ic.send( new HeadingParagraph( "messages for " + playerName ) );
			ic.sendFeedback( "idleMsg    : " + targetPlayer.getIdleMsg() );
			ic.sendFeedback( "blockMsg   : " + targetPlayer.getBlockMsg() );
			ic.sendFeedback( "blockingMsg: " + targetPlayer.getBlockingMsg() );
			ic.sendFeedback( "enterMsg   : " + targetPlayer.getName() + " " + targetPlayer.getEnterMsg() );
			ic.sendFeedback( "exitMsg    : " + targetPlayer.getName() + " " + targetPlayer.getExitMsg() );
			ic.sendFeedback( "loginMsg   : " + targetPlayer.getName() + " has connected" + targetPlayer.getLoginMsg() );
			ic.sendFeedback( "logoutMsg  : " + targetPlayer.getName() + " has disconnected" + targetPlayer.getLogoutMsg() );
			ic.sendLine();
		}
	}
}
