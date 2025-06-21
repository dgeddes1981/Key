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
**  12Jul97     merlin       created this command
**  21Jul97     subtle       renamed to End
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  This command is intended to return the context of the player
  *  back to the player's own context - the player itself.    
 */
public class End extends Command
{
	public End()
	{
		setKey( "end" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		p.setContext( p );
		ic.sendFeedback( "Now referencing '" + p.getName() + "'" );
	}
}
