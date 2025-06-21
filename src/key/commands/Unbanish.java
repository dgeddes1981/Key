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
**  22Jul97     merlin       created this command
**
*/

package key.commands;

import key.*;
import key.primitive.DateTime;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  This command is intended to unban a player after
  *  receiving a sneeze, splat, or banish
 */
public class Unbanish extends Command
{
private static final long serialVersionUID = -5934761482059675039L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Unbanish.class, String.class, "commandType",
			AtomicElement.PUBLIC_FIELD,
			"the default type of the banish to undo" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );

	public String commandType = "";
	
	public Unbanish()
	{
		setKey( "unbanish" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			Player causePlayer = null;
			String name = args.nextToken();
			
			causePlayer = (Player) getPlayer( ic, name );
			
			if( causePlayer == null )
				return;
			
			if( p == causePlayer )
			{
				ic.sendFeedback( "Try using a name other than your own" );
				return;
			}
			
			String playerBanType = (String) causePlayer.getProperty( "banishType" );
			if( !playerBanType.equals( (String) getProperty( "commandType" ) ) )
			{
				ic.sendFeedback( name + " is not currently " + getCommandType() );
				return;
			}
			
			DateTime until = (DateTime) causePlayer.getProperty( "banishedUntil" );
			
			causePlayer.setProperty( "banishedUntil", null );
			causePlayer.setProperty( "banishType", "" );
			causePlayer.sync();

			ic.sendFeedback( causePlayer.getName() + " unbanned..." );
		}
		else
			usage( ic );
	}
	
	private String getCommandType()
	{
		return( new String( getName() + "ed" ) );
	}
}
