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

public class LoginMsg extends Command
{
    public static final int MAX_LENGTH = 40;
	
	public LoginMsg()
	{
		setKey( "loginMsg" );
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
				p.setProperty( "loginMsg", ": " + msg );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as a login message.  Please limit it to " + MAX_LENGTH + " characters." );
				return;
			}
		}
		else
			p.getContext().setProperty( "loginMsg", "" );
		
		ic.sendFeedback( "Your login informs will now appear:" );
		ic.sendFeedback( "} " + p.getName() + " has connected^@" + (String) p.getProperty( "loginMsg" ) + "^$ [relation]" );
	}
}
