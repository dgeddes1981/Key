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
**  $Id: IdleMsg.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Jul97    subtle       modified to make it work ;)
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class IdleMsg extends Command
{
    public static final int MAX_LENGTH = 60;
	
	public IdleMsg()
	{
		setKey( "idleMsg" );
		usage = "[<idle message>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String idlemsg = args.nextToken( "" );
		
		    if( idlemsg.length() <= MAX_LENGTH )
			{
				p.setProperty( "idlemsg", idlemsg );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as a idlemsg.  Please limit your messages to " + MAX_LENGTH + " characters." );
				return;
			}
			
			ic.sendFeedback( "When you are idle or afk, people will now see:" );
		}
		else
			ic.sendFeedback( "When you are idle or afk, people will see:" );
		
		ic.sendFeedback( p.getName() + SuppressionList.Entry.IDLING_DEFAULT + p.getIdleMsg() );
	}
}
