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
**  class: motd
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle      creation
**
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Footnote extends Command
{
	public Footnote()
	{
		setKey( "footnote" );
		usage = "<number>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Container footnotes = (Container) Key.instance().getOnline().getElement( "footnotes" );
		if( footnotes != null )
		{
			if( args.hasMoreTokens() )
			{
				String arg = args.nextToken();
				
				Memo fn = (Memo) footnotes.getElement( arg );
				
				if( fn != null )
				{
					ic.send( (String) fn.getProperty( "value" ) );
				}
				else
					ic.sendFailure( "There isn't a footnote " + arg + ".  For example, try trying: 'footnote 6'." );
			}
			else
				usage( ic );
		}
		else
			ic.sendFailure( "Someone has stolen the footnotes!" );
	}
}
