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
**  $Id: Recap.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  06Oct96    exile        created
**  24Jul97    subtle       made it so that your name can't be all upper
**                          case
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Recap extends Command
{
    public static final int MAX_LENGTH = 60;
	
	public Recap()
	{
		setKey( "recap" );
		usage = "<recapped name>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String newName = args.nextToken();
			
		    if( newName.equalsIgnoreCase( p.getName() ) )
			{
				if( newName.equals( newName.toUpperCase() ) )
				{
					ic.sendError( "Your name may not be all capitals." );
					return;
				}
				
				p.setKey( newName );
				ic.sendFeedback( "You recap your name to " + newName );
			}
			else
			{
				ic.sendError( "You cannot use recap to change your name." );
				return;
			}
		}
		else
			usage( ic );
	}
}
