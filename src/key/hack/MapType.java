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

public class MapType extends Command
{
	public MapType()
	{
		setKey( "maptype" );
		usage = "<FQ java class> <alias>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String classT = nextArgument( args, ic );
		String mapping = nextArgument( args, ic );
		
		try
		{
			Type.newType( classT, mapping );
			ic.send( "Done" );
		}
		catch( Exception e )
		{
			ic.send( e.toString() );
		}
	}
}


