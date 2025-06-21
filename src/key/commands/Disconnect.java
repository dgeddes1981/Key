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
**  $Id: Disconnect.java,v 1.2 2000/03/02 22:23:36 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.*;

import java.util.StringTokenizer;

public class Disconnect extends Command
{
	private static final long serialVersionUID = -6762182701016534703L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Disconnect.class, String.class, "spoonClause",
			AtomicElement.PUBLIC_FIELD,
			"the string sent if someone tries to disconnect themself" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String spoonClause = "Theres a good idea!  Can I suggest 'quit' in the future?";
	
	public Disconnect()
	{
		setKey( "disconnect" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String playerName;
		Player targetPlayer;

		if( args.hasMoreTokens() )
		{
			playerName = args.nextToken();
			
			if( playerName.equalsIgnoreCase( "me" ) )
			{
				ic.sendFeedback( spoonClause );
				p.disconnect();
			}
			else
			{
				// get the targeted player
				targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
				
				if( targetPlayer == p )
				{
					ic.sendFeedback( spoonClause );
					p.disconnect();
				}
				else if( targetPlayer != null)
				{
					targetPlayer.disconnect();
					ic.sendFeedback( "You dc " + playerName );
				}
			}
		}
		else
			usage( ic );
	}
}
