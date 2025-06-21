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

public class UpdateSupplementals extends Command
{
	public UpdateSupplementals()
	{
		setKey( "updateSupp" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		new US().start();
	}
	
	public class US extends Thread
	{
		public void run()
		{
			Registry.instance.updateSupplementals();		
		}
	}
}