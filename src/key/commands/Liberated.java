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
**  Class: liberated
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  08Jul97     snapper      Creation
**  11Jul97     snapper      Added clan checking etc.
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Liberated extends Command
{
	public Liberated()
	{
		setKey( "liberated" );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
			// check to see if their in a clan first...
		if( p.getClan() != null )
		{
			ic.sendFeedback( "Perhaps you should leave Clan " + p.getClan().getName() + " before you liberate yourself? " );
			return;
		}
			// get the flag, and if its true, make it false
			// if false, make it true!
		if( p.isLiberated() )
		{
			p.setLiberated( false );
			ic.sendFeedback( "You are no longer liberated and can join clans." );
		}
		else
		{
			p.setLiberated( true );
			ic.sendFeedback( "You liberate yourself from Clan rule." );
		}
	}
}
