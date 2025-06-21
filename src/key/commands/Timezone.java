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
**  15Jul97    merlin       created this command
**  17Jul97    merlin       amended for completion
**  26Jul97    subtle       made a little easier to use
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.Date;

public class Timezone extends Command
{
	public Timezone()
	{
		setKey( "timezone" );
		usage = "<time difference>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
   			String timezoneString = args.nextToken( "" );
			Duration timezoneOffset = null;
			StringBuffer message = new StringBuffer();
			
			try
			{
				long dp = Duration.parse( timezoneString );
				
				if( dp > Duration.SID || dp < -Duration.SID )
				{
					ic.sendError( "Use a value of less than a day for a timezone offset, please." );
					return;
				}
				
				timezoneOffset = new Duration( dp );
			}
			catch( NumberFormatException n )
			{
				usage( ic );
				return;
			}
			
			p.setTimezone( timezoneOffset );
			
			long time = timezoneOffset.getTime();
			if( time == 0 )
				message.append( "Your time is " + Key.instance().getName() + " time" );
			else
			{
				message.append( "Your timezone offset is " + timezoneOffset );
				if( time < 0 )
					message.append( " behind " );
				else if( time > 0 )
					message.append( " ahead of " );
				message.append( Key.instance().getName() ); 
				message.append( " time" );
			}
			message.append( "\n" );

			DateTime now = new DateTime();

			message.append( Key.instance().getName() );
			message.append( " time is " );
			message.append( now.toString() );
			message.append( "\n" );
			  
			if( time != 0 )
			{
				message.append( "We think time where you are is " );
				message.append( DateTime.nowPlus( timezoneOffset ).toString() );
			}
			
			ic.sendFeedback( message.toString() );
		}
		else
			usage( ic );
		
	}

	public void usage( InteractiveConnection ic )
	{
		super.usage( ic );
		
		ic.sendFeedback( "\nSuppose, when you look at your watch, the time it said was 3 hours ahead of the time you see when you type 'time' on the program.  In order to set up your character so that it knows which timezone you are in, you would type '" + getName() + " 3h', to mean 'my time is 3 hours ahead'.  To do negative timezones, say if you were half an hour behind, you could use '" + getName() + " -30m', for 'my time is 30 minutes behind'.  You get the idea." );
	}
}
