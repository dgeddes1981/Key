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
**  $Id: As.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class As extends Command
{
	public As()
	{
		setKey( "as" );
		usage = "<player> <command>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String playerName = args.nextToken();
			if( args.hasMoreTokens() )
			{
				String line = args.nextToken( "" );
				Player t = getPlayer( ic, playerName );
				if( t != null )
				{
					Log.log( "as", p.getName() + ", as " + t.getName() + ": " + line );
					t.command( line, ic, false );
				}
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
}
