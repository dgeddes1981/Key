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
**  $Id: Aka.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Aka extends Command
{
	public static final int MAX_LENGTH = 60;

	public Aka()
	{
		setKey( "aka" );
		usage = "[<your other name(s)>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String aka = args.nextToken( "" );
		
			if( aka.length() <= MAX_LENGTH )
			{
				p.setAka( aka );
				ic.sendFeedback( "You set your aka so that it now reads:" );
				ic.sendFeedback( aka );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as an aka.  Please limit your aka to " + MAX_LENGTH + " characters." );
			}
		}
		else
		{
			p.setAka( "" );
			ic.sendFeedback( "You reset your aka." );
		}
	}
}
