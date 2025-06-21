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
public class RefClanLand extends Command
{
	public RefClanLand()
	{
		setKey( "clanland" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Clan currentClan = p.getClan();
		
		if( currentClan == null )
		{
			ic.sendFeedback( "No Clan to reference, join one today!" );
			return;
		}
		
		Landscape targetClanLand = (Landscape) currentClan.getProperty( "land" );
		if( targetClanLand == null )
		{
			ic.sendFeedback( "Your Clan does not have any land!" );
			return;
		}
		
		if( args.hasMoreTokens() )
		{
			Atom old = p.getContext();
			p.setContext( targetClanLand );
			p.command( args.nextToken( "" ), ic, false );
			p.setContext( old );
			return;
		}
		
		p.setContext( targetClanLand );
		ic.sendFeedback( "Now referencing '" + p.getContext().getId() + "'" );
	}
}
