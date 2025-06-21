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
**  $Id: Term.java,v 1.2 2000/03/03 21:44:35 subtle Exp $
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

public class Term extends Command
{
	public Term()
	{
		setKey( "term" );
		usage = "[<terminal-type> | reset ]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String ttype = args.nextToken();
			boolean force = args.hasMoreTokens() && args.nextToken().equalsIgnoreCase( "force" );
			p.setTerminal( ttype, force, ic );
		}
		else
		{
			if( p.getWillForceTerminal() )
				ic.sendFeedback( "Your terminal is currently set to '" + p.getTerminal().getName() + "' (forced)" );
			else
				ic.sendFeedback( "Your terminal is currently set to '" + p.getTerminal().getName() + "'" );
			
			ic.sendFeedback( "Terminal LocalEcho? : " + p.getLocalecho() );
			
			ic.sendFeedback( "\nCommon usage:\n" );
			ic.send( "  term reset    -    Use the autodetected terminal defaults" );
			ic.send( "  term ansi     -    Use ANSI colour terminal codes (popular)" );
			ic.send( "  term none     -    Turn off terminal code processing" );
		}
	}
}
