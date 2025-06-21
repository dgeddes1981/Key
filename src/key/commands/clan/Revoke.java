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
**  Class: revoke
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

public class Revoke extends Command
{
	public Revoke()
	{
		setKey( "revoke" );
		usage = "<player> [<rank>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String playerName;
		Player targetPlayer;

		if( args.hasMoreTokens() )
		{
			playerName = args.nextToken();
			targetPlayer = (Player) getPlayer( ic, playerName );
			Clan tempClan = p.getClan();
			Rank targetRank;
			
			if( args.hasMoreTokens() )
			{
					// has tried to use revoke and another rank...
					// this part assigns either the specified rank
					// ( checks to make sure it exists and is valid )
					// otherwise assigns the baserank...
				
				String revokeRank = args.nextToken();
				targetRank = (Rank)tempClan.ranks.getElement( revokeRank );
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
			
			// get the targeted player
			
			if( targetPlayer == p )
			{
				ic.sendFeedback( "You're in it already?? (spoon clause)" );
				return;
			}
			else if( targetPlayer == null )
				return;
			
			Clan targetsClan = targetPlayer.getClan(); 
			Clan currentClan = p.getClan();
			
				//  check to see if the player is not already in a clan
			if( currentClan == null )
			{
				ic.sendError( "But you're not in a clan!!" );
				return;
			}
			
			if( targetsClan != null && targetsClan != currentClan )
			{
				ic.sendFeedback( "But they're already in clan " + targetsClan.getName() );
				return;
			}
			if( targetsClan == null )
			{
				ic.sendFeedback( "They're not in a Clan." );
				return;
			}
			
				// take them out of  the specified rank
			try
			{
				targetRank.remove( targetPlayer );
				if( !targetsClan.containsPlayer( targetPlayer ) )
				{
					targetPlayer.enrolIntoClan( null );
				}
			}
			catch( BadKeyException t )
			{
				throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
			}
			catch( NonUniqueKeyException t )
			{
				throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
			}
			
			(new key.effect.Broadcast( currentClan, targetPlayer, targetPlayer.getName() +" hath had Clan privileges revoked.", "You have had your Clan " + currentClan.getName() + " privileges revoked (" + targetRank.getName() + ")" )).cause();
			ic.sendFeedback( "'" + targetRank.getName() + "' privileges revoked for '" + targetPlayer.getName() + "'" );
			Log.log( "clans/" + p.getClan().getName() + ".records", "'" + p.getName() + "' revoked '" + targetRank.getName() + "' privileges, for '" + targetPlayer.getName() + "'" ); 
		}
		else
			usage( ic );
	}
}
