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
**  $Id: BlockMsg.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Jul97    subtle       created
**  24Aug98    subtle       converted to new element system
**
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class BlockMsg extends Command
{
    public static final int MAX_LENGTH = 60;
	
	public BlockMsg()
	{
		setKey( "idleMsg" );
		usage = "[<block message>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String blockmsg = args.nextToken( "" );
		
		    if( blockmsg.length() <= MAX_LENGTH )
			{
				p.setBlockMsg( blockmsg );
			}
			else
			{
				ic.sendFeedback( "That is too long to be set as a blockmsg.  Please limit your messages to " + MAX_LENGTH + " characters." );
				return;
			}
			
			ic.sendFeedback( "When you are blocking them, people will now see:" );
		}
		else
			ic.sendFeedback( "When you are blocking them, people will see:" );
		
		ic.sendFeedback( p.getName() + SuppressionList.Entry.SPECIFIC_DEFAULT + p.getBlockMsg() );
	}
}
