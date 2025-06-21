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
**  $Id: Rescan.java,v 1.1 2000/06/19 20:40:49 subtle Exp $
**
**  $Log: Rescan.java,v $
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
import key.primitive.DateTime;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Rescan extends Command
{
	public Rescan()
	{
		setKey( "rescan" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Group g = p.friends;
		g.rescan();
		ic.sendFeedback( "Rescan complete, everything should behave well, now" );
	}
}
