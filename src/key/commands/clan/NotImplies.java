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
**  $Id: NotImplies.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  20Jul97     snapper      creation ( mutter, mutter, mutter )
**  20Jul97     subtle       changed to use current rank context
**
*/

package key.commands.clan;

import key.*;

import java.util.StringTokenizer;
import java.io.IOException;
import java.util.Enumeration;

public class NotImplies extends Command
{
	public NotImplies()
	{
		setKey( "notImplies" );
		usage = "<implied rank>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;

		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
			//  first variable is the rank you wish to remove the imply from.
			//  eg. founder ( to remove the leader implies )
		Clan currentClan = (Clan) p.getProperty( "clan" );
		
		Rank firstRank;
		
		try
		{
			firstRank = (Rank) p.getContext();
		}
		catch( ClassCastException e )
		{
			ic.sendFeedback( "Please use 'rank <rank> " + getName() + " <implied rank>'" );
			return;
		}
		
        if( firstRank == null )
		{
			ic.sendFeedback( "Your clan does not contain this rank." );
			return;
		}

			//  now for the implied rank...
		String targetRank = args.nextToken();
		Rank secondRank = (Rank) currentClan.ranks.getElement( targetRank );
		if( secondRank == null )
		{
			ic.sendFeedback( "Your clan does not contain the rank '" + targetRank + "'" );
			return;
		}

		Container cFirstRank = (Container) firstRank.getProperty( "implies" );
		if( !cFirstRank.contains( secondRank ) )
		{
			ic.sendFeedback( "The rank '" + firstRank.getName() + "' does not link to '" + secondRank.getName() + "'" );
			return;
		}
		
			//  now remove the implies
		try
		{
			cFirstRank.remove( secondRank );
		}
		catch( BadKeyException e )
		{
			ic.sendError( "It should only contain alphabetic characters" );
			return;
		}
		catch( NonUniqueKeyException e )
		{
			ic.sendError( "There is already something of that name here" );
			return;
		}
		
		ic.sendFeedback( "You unlink the rank '" + firstRank.getName() + "' to imply '" + secondRank.getName() + "'" );
		
		Log.log( "clans/" + p.getClan().getName() + ".notes" , "'" + p.getName() + "' removed the implies from '" + firstRank.getName() + "' to '" + secondRank.getName() + "'" );
	}
}
