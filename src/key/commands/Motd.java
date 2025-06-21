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
**  $Id: Motd.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  17Jul97     snapper      creation
**  21Jul97     subtle       added failure message
**
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Motd extends Command
{
	public Motd()
	{
		setKey( "motd" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Screen motd = (Screen) Key.instance().getOnline().getElement( "motd" );
		if( motd != null )
		{
			ic.send( motd.getParagraph() );
		}
		else
			ic.sendFailure( "There doesn't seem to be a message at the moment" );
	}
}
