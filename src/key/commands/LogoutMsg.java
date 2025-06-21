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
import java.io.IOException;
import java.util.StringTokenizer;

public class LogoutMsg extends Command
{
    public static final int MAX_LENGTH = 40;
	
	public LogoutMsg()
	{
		setKey( "logoutMsg" );
		usage = "[<message>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String msg = args.nextToken( "" );
			
			if( msg.length() <= MAX_LENGTH )
			{
					//  basically, if the first character isn't in the provided set
				p.setProperty( "logoutMsg", ": " + msg );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as a logout message.  Please limit it to " + MAX_LENGTH + " characters." );
				return;
			}
		}
		else
			p.getContext().setProperty( "logoutMsg", "" );
		
		ic.sendFeedback( "Your logout informs will now appear:" );
		ic.sendFeedback( "{ " + p.getName() + " has disconnected^@" + (String) p.getProperty( "logoutMsg" ) + "^$ [relation]" );
	}
}
