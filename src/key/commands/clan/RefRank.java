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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  12Jul97     snapper      created this command
**
*/

package key.commands.clan;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  This command is intended to change the players context to
  *  their clan.
 */
public class RefRank extends Command
{
	public RefRank()
	{
		setKey( "rank" );
		usage = "[<rank>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Clan currentClan = p.getClan();
		
		if( currentClan == null )
		{
			ic.sendFeedback( "No Clan to reference, join one today!" );
			return;
		}
		
		if( args.hasMoreTokens() )
		{
				// the next argument should be a rank
			String refRank = args.nextToken();
			Rank targetRank = (Rank) currentClan.ranks.getElement( refRank );
			if( targetRank == null )
			{
				ic.sendFeedback( "Your Clan does not have that rank!" );
				return;
			}
			if( args.hasMoreTokens() )
			{
				Atom old = p.getContext();
				p.setContext( targetRank );
				p.command( args.nextToken( "" ), ic, false );
				p.setContext( old );
				return;
			}
			else
			{
				p.setContext( targetRank );
				ic.sendFeedback( "Now referencing '" + p.getContext().getId() + "'" );
			}
		}
		else
		{
			usage( ic );
		}
	}
}
