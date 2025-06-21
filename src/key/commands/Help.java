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
**  $Id: Help.java,v 1.2 2000/01/28 17:03:50 noble Exp $
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

public class Help extends Command
{
	public Help()
	{
		setKey( "help" );
		usage = "[<keyword>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String keyword = "general";
		
		if( args.hasMoreTokens() )
			keyword = args.nextToken();
		
		Container helpFiles = (Container) Key.instance().getOnline().getElement( "help" );
		Object found = helpFiles.getElement( keyword );
		
		if( found instanceof Screen )
		{
			ic.send( Grammar.substitute( ((Screen) found).aspect() ) );
		}
		else
		{
			ic.sendError( "No help on the subject '" + keyword + "' found." );
			
			Log.log( "help", p.getName() + " requested help on '" + keyword + "'" );
		}
	}
}
