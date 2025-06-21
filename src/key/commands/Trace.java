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
import java.io.IOException;

import java.util.StringTokenizer;

public class Trace extends Command
{
	public Trace()
	{
		setKey( "trace" );
		usage = "<player>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String playerName = args.nextToken();
			
				//  get the targeted player
			Player targetPlayer = (Player) getPlayer( ic, playerName );
			
			if( targetPlayer != null )
			{
				ic.sendFeedback( playerName + " is from " + targetPlayer.getLastConnectFrom() );
			}
		}
		else
			usage( ic );
	}
}
