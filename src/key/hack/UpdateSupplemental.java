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

public class UpdateSupplemental extends Command
{
	public UpdateSupplemental()
	{
		setKey( "updSup" );
		usage = "<index>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			try
			{
				int index = Integer.parseInt( args.nextToken() );

				Registry.instance.updateSupplemental( index );
			}
			catch( NumberFormatException nfe )
			{
				ic.sendFailure( "That is not a valid index." );
				return;
			}	
		}
		else
			usage( ic );

	}
}