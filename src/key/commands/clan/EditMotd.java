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
**  Class: editmotd
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  18Jul97     snapper      creation
**  20Jul97     snapper      added logging
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

public class EditMotd extends Command
{
	public static final int MAX_LINES = 20;
	public static final int MAX_BYTES = 80 * MAX_LINES;
	
	public EditMotd()
	{
		setKey( "editmotd" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Screen motd = null;
		
		Clan c = p.getClan();
		
		motd = (Screen) c.getProperty( "motd" );
		Paragraph mp = motd.getParagraph();
		
		Paragraph para = Editor.edit( p, mp, ic, MAX_LINES, MAX_BYTES );
		
		if( para == mp )
			ic.sendFeedback( "No changes made to the clan motd." );
		else
		{
			motd.setParagraph( para );
			ic.sendFeedback( "You change the clan motd." );
			Log.log( "clans/" + c.getName() + ".notes" , "'" + p.getName() + "' altered the Clan MOTD." );
		}
	}
}
