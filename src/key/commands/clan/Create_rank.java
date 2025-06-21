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

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

/**
  *  Just an advanced create command intended for those
  *  intended to make clans, of such...
 */
public class Create_rank extends key.commands.Create
{
	private static final long serialVersionUID = -3921411822723531600L;
	
	public Create_rank()
	{
		setKey( "rank" );
		usage = "<rank>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;
		String targetRank = nextArgument( args, ic );
		
		Clan currentClan = p.getClan();
		
		Rank newRank = (Rank) currentClan.ranks.getElement( targetRank );
		CommandList cList = (CommandList) currentClan.commandSets.getElement( targetRank );
		
		if( newRank == null )
		{
			newRank = (Rank) Factory.makeAtom( Rank.class, targetRank );
			add( ic, newRank, currentClan.ranks, "You make the new rank '" + newRank.getName() + "'" );
			Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName() + "' made new rank '" + newRank.getName() + "'" );
		}
		else
			ic.sendFeedback( "Using the rank called '" + newRank.getName() + "' that already exists" );
		
		if( cList == null )
		{
			cList = (CommandList) Factory.makeAtom( CommandList.class, targetRank );
			add( ic, cList, currentClan.commandSets, "You make a new commandList for '" + cList.getName() + "'" );
			Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName() + "' made new commandlist '" + newRank.getName() + "'" );
		}
		else
			ic.sendFeedback( "Using the commandList called '" + newRank.getName() + "' that already exists" );
		
		newRank.commands = Reference.to( cList, false );
		ic.sendFeedback( "Ensuring that the rank and commandList know about each other..." );
		
			//  create the 'clan' command
		CommandCategory refClan = (CommandCategory) cList.getElement( "clan" );

		if( refClan == null )
		{
			try
			{
				refClan = (CommandCategory) Factory.makeAtom( key.CommandCategoryContainer.class );
				refClan.setKey( "clan" );
				cList.add( refClan );
				ic.sendFeedback( "Adding a clan command to the rank commandList" );
				Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName() + "' made new clan command in '" + newRank.getName() + "'" );
			}
			catch( Exception e )
			{
				ic.sendFailure( "Error while creating clan command: " + e.toString() );
				Log.error( "while creating clan command", e );
			}
		}
		else
			ic.sendFeedback( "The clan command is already in the commandList... good!" );
		
			// logging
		Log.log( "clans/" + currentClan.getName() + ".notes", "'" + p.getName() + "' made new rank '" + newRank.getName() + "'" );
	}
}
