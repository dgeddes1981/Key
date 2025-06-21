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
**  Class: hide
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  05Jul97     GJW         History created
*/

package key.commands;

import key.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class History extends Command
{
	public History()
	{
		setKey( "history" );
		usage = "";
	}

	public static final HeadingParagraph HEADING = new HeadingParagraph( "Last " + Player.MAX_HISTORY_LINES + " direct messages", HeadingParagraph.CENTRE );
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		ic.send( HEADING );
		
		for( Enumeration e = p.getTellHistory(); e.hasMoreElements(); )
			ic.send( "? " + (String) e.nextElement() );
		
		ic.sendLine();
	}
}
