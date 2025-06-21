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

package key;

import java.util.StringTokenizer;
import java.io.IOException;

public class Credit extends Command
{
	public Credit()
	{
		setKey( "credit" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String pname = nextArgument( args, ic );
		String fcount = nextArgument( args, ic );
		
		Player v = getPlayer( ic, pname );
		
		if( v != null )
		{
			try
			{
				int i = Integer.parseInt( fcount );
				v.florins += i;
				ic.sendFeedback( p.getName() + " now has " + v.florins + " florins." );
			}
			catch( NumberFormatException e )
			{
				ic.sendError( e.toString() );
			}
		}
	}
}
