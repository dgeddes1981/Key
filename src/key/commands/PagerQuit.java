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
**  $Id: PagerQuit.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       created this class
**
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class PagerQuit extends Command
{
	public PagerQuit()
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
		
		if( tc.isPaging() )
			tc.quitPager();
		else
			ic.sendError( "You're not in the pager" );
	}
}
