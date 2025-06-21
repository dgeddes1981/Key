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
**  Class: take
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  15Jul97     snapper      creation
**  20Jul97     snapper      added logging
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class Take extends Command
{
	public Take()
	{
		setKey( "take" );
		usage = "<command>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		Object o = p.getContext();
		
		if( o != null )
		{
			if( o instanceof Rank )
			{
				Rank rank = (Rank) o;
				//CommandList cl = (CommandList) rank.getProperty( "commands" );
				CommandList cl = (CommandList) new Search( ".commands/clan.commands", rank ).result;
				if( cl == null )
				{
					ic.sendFeedback( "The command list is empty!  No commands to take!" );
					return;
				}
				
				String takeCommand = args.nextToken();
				Command taken = (Command) cl.getElement( takeCommand );	
				if( taken == null )
				{
					ic.sendFeedback( "That command does not exist inside '" + rank.getName() + "'" );
					return;
				}
				try
				{
					cl.remove( taken );
				}
				catch( BadKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " on removing a matched atom." );
				}
				catch( NonUniqueKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
				}
				ic.sendFeedback( "Command '" + takeCommand + "' taken from '" + rank.getName() + "'" );
					// logging
				Log.log( "clans/" + p.getClan().getName() + ".notes", "'" + p.getName() + "' took the command '" + takeCommand + " from " + rank.getName() + "'" );
			}
			else
				ic.sendError( "You are not referencing a rank.  Use: rank <rank> first!" );
		}
		else
			ic.sendError( "Could not find that rank?" );
	}
}
