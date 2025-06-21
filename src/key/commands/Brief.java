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
**  17Jul97     druss       created this command
**  24Aug98     subtle      converted to new element system
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Brief extends Command
{
	public Brief()
	{
		setKey( "brief" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.brief() )
		{
			p.setBrief( false );
			ic.sendFeedback( "Ok, you will now see the full room descriptions" );
		}
		else
		{
			p.setBrief( true );
			ic.sendFeedback( "Don't look at our room descriptions, then" );
		}
	}
}
