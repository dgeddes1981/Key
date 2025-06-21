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
import key.primitive.*;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  This command is intended to completely ban a site for a period
  *  of 10 minutes because of a particular reason
  *
  *   Two Types of Commands
  *        "B" - Banish - both offline and online
  *        "S" - Banish with site ban
 */

public class TimedBanish extends Command
{
private static final long serialVersionUID = 1159952265124121960L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( TimedBanish.class, Duration.class, "banTime",
			AtomicElement.PUBLIC_FIELD,
			"the default amount of time the banish is for" ),
		AtomicElement.construct( TimedBanish.class, String.class, "commandType",
			AtomicElement.PUBLIC_FIELD,
			"the type of banish (B or S)" ),
	};

	public Duration banTime = null;
	public String commandType = "";
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public TimedBanish()
	{
		setKey( "timedDisconnect" );
		usage = "<player> [<duration>]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String c = (String) getProperty( "commandType" ); 

		if( args.hasMoreTokens() )
		{
			Player causePlayer = null;
			String name = args.nextToken();
			
			causePlayer = getPlayer( ic, name );
			if( causePlayer == null )
				return;
			
				//  make sure we aint being dumb
			if( p == causePlayer )
			{
				ic.sendFeedback( "Try using a player other than yourself" );
				return;
			}
			
			Duration d = banTime;
			if( args.hasMoreTokens() )
			{
				d = new Duration( Duration.parse( args.nextToken() ) );
			}
			
			if( d.getTime() <= 0 )
			{
				ic.sendFeedback( "How about entering a position duration" );
				return;
			}

			DateTime now = new DateTime();
			DateTime until = new DateTime( now.getTime() + d.getTime() );
			
			causePlayer.setProperty( "banishedUntil", until );
			causePlayer.setProperty( "banishType", new String( c ) ); 
			
			if( causePlayer.connected() )
				causePlayer.disconnect();
		}
		else
			usage( ic );
	}
}
