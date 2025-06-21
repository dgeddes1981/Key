/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key.commands;

import key.*;
import java.io.IOException;

import java.util.StringTokenizer;


public class Give extends Verb
{
	private static final long serialVersionUID = -8150826828693357779L;
	
	public Give()
	{
		setKey( "give" );
		usage = "<amount or object> <player>";
		verb = "give";
		method = null;
		checkInventory = true;
		checkRoom = false;
	}
	
	public String getVerb()
	{
		return( "read" );
	}
	
	public void setVerb( String v )
	{
		throw new InvalidArgumentException( "you may not set this property" );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		int amount;
		int savedAmount;
		int targetAmount;
		
		if( args.hasMoreTokens() )
		{
			String item = args.nextToken();
			
				//  try to get the amount
			try
			{
				amount = Integer.parseInt( item );
			}
			catch( NumberFormatException e )
			{
				runWithItem( p, args, fullLine, caller, ic, flags, item );
				return;
			}
			
				//  if the player exists
			if( args.hasMoreTokens() )
			{
				Player targetPlayer = (Player) getPlayer( ic, args.nextToken() );
				
				if( targetPlayer != null )
				{
					savedAmount = p.getFlorins();
					
						//  make sure its positive and the
						//  player has enough to give
					if( amount == 0 )
					{
						ic.sendFeedback( "0! Why bother? (Scrooge Clause)" );
						return;
					}
					else if( amount < 0 )
					{
						ic.sendFeedback( "Hang on - you want to take their florins? That's not very nice." );
						return;
					}
					else if( amount > savedAmount )
					{
						ic.sendFeedback( "Nice try mate... you don't have enough florins to give." );
						return;
					}
					else if( p == targetPlayer )
					{
						ic.sendFeedback( "You juggle with your money. (Tax evasion Clause)" );
						return;
					}
					else
					{
						if( !targetPlayer.isLocation( p.getLocation() ) )
						{
							ic.sendFeedback( "You need to be in the same room as " + targetPlayer.getName() + " to give " + targetPlayer.himHer() + " florins." );
							return;
						}
						
							//  this routine will notify targetPlayer if
							//  he/she is online, as well.
						p.transferFlorins( targetPlayer, amount );
						
						ic.sendFeedback( "Transaction complete.  You now have " + (savedAmount - amount) + " silver florins." );
					}
				}
				else
				{
						// the player does not exist
					ic.sendFeedback( "We're not going to be party to your money laundering schemes ;) (player not found)" );
					return;
				}
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
}
