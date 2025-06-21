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
**  $Id: Where.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  05Jul97     exile        Hide functionality implemented
**  20Jul97     exile        Grammar correction
**  24Aug98     subtle       new element update
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Where extends Command
{
	public Where()
	{
		setKey( "where" );
		usage = "<name>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String playerName = args.nextToken();
			Player targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
			
				//  test to see if player online
			if( targetPlayer != null )
			{
				try
				{
					Room r = targetPlayer.getLocation();
					String portrait = r.getFullPortrait();
					ic.sendFeedback( targetPlayer.getName() + " is " + portrait + "  (" + r.getContainedId() + ")" );
				}
				catch( PermissionDeniedException e )
				{
					ic.sendFeedback( targetPlayer.getName() + " doesn't want to be found.  (Hiding)" );
				}
			}
		}
		else
			usage( ic );
	}
}
