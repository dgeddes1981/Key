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
**  $Id: Sync.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
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

import java.util.StringTokenizer;
import java.io.IOException;

public class Sync extends Command
{
	public Sync()
	{
		setKey( "sync" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom a = null;
		String id = null;
		if( args.hasMoreTokens() )
		{
			a = getSymbolInside( ic, args.nextToken(), p.getContext() );
			
			if( a == null )
				return;
			
			id = a.getId();
		}
		else
		{
			a = Key.instance();
			id = "/";
		}
		
		a.sync();
		
		ic.sendFeedback( "Sync of " + id + " completed." );
	}
}
