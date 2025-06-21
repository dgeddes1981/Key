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
**  Class: accept
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
import java.io.IOException;

import java.util.StringTokenizer;

public class Accept extends Command
{
	public Accept()
	{
		setKey( "accept" );
		usage = "<player> [<rank>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String playerName;
		Player targetPlayer;

		if( args.hasMoreTokens() )
		{
			playerName = args.nextToken();
			targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
			Clan tempClan = (Clan)p.getClan();
			Rank targetRank;

			if( args.hasMoreTokens() )
			{
					// has tried to use accept and another rank...
					// this part assigns either the specified rank
					// ( checks to make sure it exists and is valid )
					// otherwise assigns the baserank...
			
				String acceptRank = args.nextToken();
				targetRank = (Rank)tempClan.ranks.getElement( acceptRank );
			}
			else
			{
				targetRank = (Rank)tempClan.getProperty( "baseRank"  );
			}
			
			if( targetRank == null )
			{
				ic.sendError( "Your clan does not have that rank." );
				return;
			}
			
			//  get the targeted player
		
			if( targetPlayer == p )
			{
				ic.sendFeedback( "You're in it already?? (spoon clause)" );
				return;
			}
			else if( targetPlayer == null )
				return;

				// if targeted player has liberated set, abort!
	     	if( targetPlayer.isLiberated() )
			{
				ic.sendFeedback( "Nup!  " + targetPlayer.getName() + " is liberated!" );
				return;
			}
			
			Clan targetsClan = (Clan) targetPlayer.getClan(); 
			Clan currentClan = (Clan) p.getClan();
			
				//  check to see if the player is not already in a clan
			if( currentClan == null )
			{
				ic.sendError( "But you're not in a clan!!" );
				return;
			}
			
			if( targetsClan != null && targetsClan != currentClan )
			{
				ic.sendFeedback( "But they're already in clan " + targetsClan.getName() );
				if( targetPlayer.connected() )
					targetPlayer.sendFailure( p.getName() + " just tried to accept you into Clan " + currentClan.getName() );
				
				return;
			}
			
			if( targetRank.contains( targetPlayer ) )
			{
				ic.sendFeedback( "But they're already in that rank." );
				return;
			}
			
				// set them up in the specified rank
			try
			{
				targetRank.add( targetPlayer );
			}
			catch( BadKeyException t )
			{
				throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
			}
			catch( NonUniqueKeyException t )
			{
				ic.sendFailure( "I tried, but I think they might already be in this rank, in a roundabout way." );
			}
			
				// they weren't in any clan...
			if( targetsClan == null )
			{
				targetPlayer.enrolIntoClan( currentClan );
			}
			
			(new key.effect.Broadcast( currentClan, targetPlayer, "(" + currentClan.getPrefix() + "): " + targetPlayer.getName() + " has been accepted into " + targetRank.getContainedId(), "You have been accepted into Clan " + currentClan.getName() + " (" + targetRank.getContainedId() + ")" )).cause();
			ic.sendFeedback( "'" + targetPlayer.getName() + "' accepted into rank '" + targetRank.getContainedId() + "'" );
			
				// logging
			Log.log( "clans/" + p.getClan().getName() + ".records" , "'" + p.getName() + "' accepted '" + targetPlayer.getName() + "' into '" + targetRank.getName() + "'" );
		}
		else
			usage( ic );
	}
}
