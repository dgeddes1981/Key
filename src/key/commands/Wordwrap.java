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
**  $Id: Wordwrap.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
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

public class Wordwrap extends Command
{
	public Wordwrap()
	{
		setKey( "wordwrap" );
		usage = "[on | off]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			if( args.nextToken().equalsIgnoreCase( "off" ) )
			{
				p.setWordwrap( false );
				ic.sendFeedback( "Automatic wordwrap turned ^hoff^-." );
			}
			else
			{
				p.setWordwrap( true );
				ic.sendFeedback( "Automatic wordwrap turned ^hon^-." );
			}
		}
		else
		{
			if( p.getWordwrap() )
				ic.sendFeedback( "Automatic wordwrap is currently ^hon^-.  Turn it off using 'wordwrap off'." );
			else
				ic.sendFeedback( "Automatic wordwrap is currently ^hoff^-.  Turn it on using 'wordwrap on'." );
		}
	}
}
