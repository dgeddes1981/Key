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
import key.primitive.DateTime;
import key.util.Trie;
import java.util.StringTokenizer;
import java.io.*;

public class SetSession extends Command
{
	public SetSession()
	{
		setKey( "setSession" );
		
			// text for command
		usage = "<new session>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String comm = args.nextToken( "" );
			
			p.getRealm().setSessionTitle( p, ic, comm );
		}
		else
		{
			Realm r = p.getRealm();
			
			if( r.sessionCanReset != null )
			{
				ic.sendFeedback( "The current session is '" + r.sessionTitle + "'" );
				DateTime now = new DateTime();
				
				if( now.after( r.sessionCanReset ) )
					ic.sendFeedback( "It was set by " + r.getSessionSetByName() + ", and can be reset any time now." );
				else
				{
					try
					{
						ic.sendFeedback( "It was set by " + r.getSessionSetByName() + ", and can be reset in " + r.sessionCanReset.difference( new DateTime() ).toString() + "." );
					}
					catch( OutOfDateReferenceException e )
					{
						ic.sendFeedback( "It was set by " + r.getSessionSetByName() + ", and can be reset any time now." );
					}
				}
			}
			else
				ic.sendFeedback( "No session currently set, use 'session <session title>' to set one." );
		}
	}
}
