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
**  $Id: Pwd.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;

import java.util.StringTokenizer;

public class Pwd extends Command
{
	public Pwd()
	{
		setKey( "pwd" );
		usage = "";
	}

	/**
	  *  Changes the players context.  The context is *always* specified
	  *  as an offset from the player class - regardless of what your current
	  *  reference is.  This is to stop you getting lost ;)
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom a = p.getContext();
		if( a == Key.instance() )
			ic.sendFeedback( "/" );
		else
			ic.sendFeedback( p.getContext().getId() );
	}
}
