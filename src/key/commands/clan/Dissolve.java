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
**  Class: dissolve
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  20Jul97     snapper      finally added this header
**  20Jul97     snapper      added logging
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

/**
  *  Just an advanced create command intended for those
  *  intended to make clans, of such...
 */
public class Dissolve extends Command
{
	public Dissolve()
	{
		setKey( "dissolve" );
		usage = "<rank>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String dissolveRank = args.nextToken();
		Clan currentClan = (Clan) p.getClan();
		Rank targetRank = (Rank)currentClan.ranks.getElement( dissolveRank );
		if( targetRank == null )
		{
			ic.sendError( "Your clan does not have that rank." );
			return;
		}
		try
		{
			currentClan.ranks.remove( targetRank );
		}
		catch( BadKeyException t )
		{
			throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
		}
		catch( NonUniqueKeyException t )
		{
			throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
		}
		
		ic.sendFeedback( "You dissolve the '" + dissolveRank + "' rank." );
		Log.log( "clans/" + p.getClan().getName() + ".notes" , "'" + p.getName() + "' dissolved rank '" + dissolveRank + "'" );
	}
}
