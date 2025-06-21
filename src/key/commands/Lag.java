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
**  $Id: Lag.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;

import java.util.StringTokenizer;

public class Lag extends Command
{
	public Lag()
	{
		setKey( "lag" );
		usage = "<player> <duration> [ always | discard ]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String playerName = args.nextToken();
			
			if( args.hasMoreTokens() )
			{
				String amount = args.nextToken();
				boolean continual = false;
				boolean discard = false;
				long time = 0;
				
				if( args.hasMoreTokens() )
				{
					String type = args.nextToken();

					if( type.equalsIgnoreCase( "always" ) )
						continual = true;
					else if( type.equalsIgnoreCase( "discard" ) )
						discard = true;
					else
					{
						usage( ic );
						return;
					}
				}
				
				time = Duration.parse( amount );

					//  get the targeted player
				Player targetPlayer = (Player) getPlayer( ic, playerName );
			
				if( targetPlayer != null )
				{
					targetPlayer.setLag( (int) time/1000, !continual );

					if( time == 0 )
						ic.sendFeedback( targetPlayer.getName() + " has been unlagged" );
					else
					{
						if( continual )
							ic.sendFeedback( targetPlayer.getName() + " will find that his commands will be delayed for " + Duration.toString( time ) );
						else
							ic.sendFeedback( targetPlayer.getName() + " has been paused for " + Duration.toString( time ) );
					}
				}
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
}
