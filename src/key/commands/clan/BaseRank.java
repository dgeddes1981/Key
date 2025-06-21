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
**  Class: baserank
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  17Jul97     snapper      creation
**  20Jul97     snapper      added logging
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class BaseRank extends Command
{
	public BaseRank()
	{
		setKey( "baserank" );
		usage = "<rank> ";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String targetRank = args.nextToken();
		
		Clan currentClan = p.getClan();
		Rank tempRank = (Rank) currentClan.ranks.getElement( targetRank );
        if( tempRank == null )
        {
            ic.sendError( "That rank does not exist." );
            return;
        }
		
			// to set a new base rank we have to turn off the joinedTo
			// field so that the basic communication commands belong to
			// the new base rank.
		Rank currentBaseRank = (Rank)currentClan.getProperty( "baseRank" );
		currentBaseRank.setProperty( "joinedTo", null );
			// set the new baserank up...
		currentClan.setProperty( "baseRank", tempRank );
			// now set up the joinedTo field again...
		tempRank.setProperty( "joinedTo", currentClan );
		
		ic.sendFeedback( "You set the new base rank of Clan " + currentClan.getName() + " to '" + targetRank + "'" );
		Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName() + "' changed the base rank to '" + targetRank + "'" );
	}
}
