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
**  $Id: Schedule.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import key.primitive.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Schedule extends Command
{
	public Schedule()
	{
		setKey( "schedule" );
		usage = "<event> <in>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String eventId = args.nextToken();

			if( args.hasMoreTokens() )
			{
				String dateStr = args.nextToken();
				
				Duration duration = new Duration( Duration.parse( dateStr ) );
				DateTime when = DateTime.nowPlus( duration );
				
				Object o = new Search( eventId, p.getContext() ).result;
				
				if( o != null && o instanceof Event )
				{
					Event evt = (Event) o;
					Scheduler s = Key.instance().getScheduler();
					
					evt.schedule( when );
					s.add( evt );
					
					ic.sendFeedback( "Event '" + evt.getName() + "' scheduled for " + when.toString() );
				}
				else
					ic.sendError( "Could not find '" + eventId + "'" );
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
}
