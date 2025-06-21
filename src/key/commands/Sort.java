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
**  $Id: Sort.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Sort extends Command
{
	public Sort()
	{
		setKey( "sort" );
		usage = "<identifier>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String id;

		if( args.hasMoreTokens() )
			id = args.nextToken();
		else
			id = "";
		
		Container c = (Container) getSymbolInside( ic, id, Type.CONTAINER, p.getContext() );
		
		if( c != null )
		{
			c.sort();
			ic.sendFeedback( "Sorted " + c.count() + " objects in " + c.getName() );
		}
	}
}
