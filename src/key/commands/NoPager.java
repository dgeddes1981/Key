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
**  $Id: NoPager.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       renamed command to NoPager
**
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class NoPager extends Command
{
	public NoPager()
	{
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		TelnetIC tc = null;
		
		try
		{
			tc = (TelnetIC) ic;
		}
		catch( ClassCastException e )
		{
			ic.sendError( "This command only works during a telnet session." );
			return;
		}
		
		boolean v = !tc.canPage();
		
		tc.setCanPage( v );
		p.setCanPage( v );

		if( v )
			ic.sendFeedback( "The pager is now enabled." );
		else
			ic.sendFeedback( "The pager is now disabled." );
	}
}
