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
**  Class: sethall
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Jul97     snapper      creation
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class SetHall extends Command
{
	public SetHall()
	{
		setKey( "sethall" );
		usage = "<name> ";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String targetHall = args.nextToken();
		
		Clan currentClan = p.getClan();
		Landscape Land = (Landscape)currentClan.getProperty( "land" );
		Room hall = (Room) Land.getElement( targetHall );
		
        if( hall == null )
        {
            ic.sendError( "That room does not exist." );
            return;
        }
		
		currentClan.setProperty( "hall", hall );
		ic.sendFeedback( "You set the clan hall to be '" + targetHall + "'" );
			
			// logging
		 Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName()+ "' set the clan hall to '" + targetHall + "'" );
	}
}
