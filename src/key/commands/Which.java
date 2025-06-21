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
**  $Id: Which.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
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

public class Which extends Command
{
	public Which()
	{
		setKey( "which" );
		usage = "<command>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
				//  just run the command with trace output turned on
			p.command( args.nextToken( "" ), ic, true );
		}
		else
			usage( ic );
	}
}
