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
**  $Id: Cancel.java,v 1.1 2000/06/19 20:40:49 subtle Exp $
**
**  $Log: Cancel.java,v $
**  Revision 1.1  2000/06/19 20:40:49  subtle
**  scan timeouts now deletes players
**  temporary atoms are now cleaned up regularly
**  added 'rescan' command to refresh friends lists (bug workaround)
**  added 'cancel' command to cancel a scheduled event
**
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Cancel extends Command
{
	public Cancel()
	{
		setKey( "cancel" );
		usage = "<event>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String eventId = args.nextToken();
			
			Object o = new Search( eventId, p.getContext() ).result;
			
			if( o != null && o instanceof Event )
			{
				Event evt = (Event) o;
				Scheduler s = Key.instance().getScheduler();
				
				s.remove( evt );
				
				ic.sendFeedback( "Event '" + evt.getName() + "' cancelled." );
			}
			else
				ic.sendError( "Could not find '" + eventId + "'" );
		}
		else
			usage( ic );
	}
}
