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
**  $Id: RoomHistory.java,v 1.1 2000/02/21 22:27:00 subtle Exp $
**
**  $Log: RoomHistory.java,v $
**  Revision 1.1  2000/02/21 22:27:00  subtle
**  added Room history command, untested
**
**
*/

package key.commands;

import key.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class RoomHistory extends Command
{
	public RoomHistory()
	{
		setKey( "history" );
		usage = "";
	}
	
	public static final HeadingParagraph HEADING = new HeadingParagraph( "Last " + PublicRoom.MAX_HISTORY_LINES + " messages in this room", HeadingParagraph.CENTRE );
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Room r = p.getLocation();
		if( r instanceof PublicRoom )
		{
			ic.send( HEADING );
			
			for( Enumeration e = ((PublicRoom)r).getRoomHistory(); e.hasMoreElements(); )
				ic.send( "? " + (String) e.nextElement() );
			
			ic.sendLine();
		}
		else
		{
			ic.sendFeedback( "For your security, this command only works in rooms tagged as 'public'" );
		}
	}
}
