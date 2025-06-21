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
**  $Id: Implies.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  20Jul97     snapper      finally added this header
**  20Jul97     snapper      added logging
**  20Jul97     subtle       changed to use current rank context
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
public class Implies extends Command
{
	public Implies()
	{
		setKey( "implies" );
		usage = "<implied rank> ";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;

		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
			//  first variable is the rank you wish to imply to something else.
			//  eg. founder ( to imply leaders... )
		Clan currentClan = (Clan) p.getClan();
		Rank firstRank = null;
		
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
		
			//  okay, two verified clan ranks... now check for circular 
			//  references.
		if( firstRank == secondRank )
		{
			ic.sendFeedback( "No circular references allowed." );
			return;
		}
		
			//  now set firstRank to imply secondRank
		Container cFirstRank = (Container) firstRank.getProperty( "implies" );
		
		try
		{
			cFirstRank.add( secondRank );
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
		catch( CircularException e )
		{
			ic.sendError( "No circular implications allowed: " + e.getMessage() );
			return;
		}
		
		ic.sendFeedback( "You make the rank '" + firstRank.getName() + "' imply '" + secondRank.getName() + "'" );
		
		Log.log( "clans/" + p.getClan().getName() + ".notes" , "'" + p.getName() + "' set rank '" + firstRank.getName() + "' to imply '" + secondRank.getName() + "'" );
	}
}
