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
**  $Id: BlockingMsg.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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

public class BlockingMsg extends Command
{
    public static final int MAX_LENGTH = 60;
	
	public BlockingMsg()
	{
		setKey( "idleMsg" );
		usage = "[<idle message>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String blockingmsg = args.nextToken( "" );
		
		    if( blockingmsg.length() <= MAX_LENGTH )
			{
				p.setBlockingMsg( blockingmsg );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as a blockingmsg.  Please limit your messages to " + MAX_LENGTH + " characters." );
				return;
			}
			
			ic.sendFeedback( "When you are blocking tells, people will now see:" );
		}
		else
			ic.sendFeedback( "When you are blocking tells, people will see:" );
		
		ic.sendFeedback( p.getName() + SuppressionList.Entry.GENERAL_DEFAULT + p.getBlockingMsg() );
	}
}
