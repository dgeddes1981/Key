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
**  $Id: Title.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
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

public class Title extends Command
{
    public static final int MAX_LENGTH = 60;
	
	public Title()
	{
		setKey( "title" );
		usage = "[<title>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		try
		{
			if( args.hasMoreTokens() )
			{
				String title = args.nextToken( "" );
			
				if( title.length() <= MAX_LENGTH )
				{
						//  basically, if the first character isn't in the provided set
					if( ",.:;".indexOf( title.charAt( 0 ) ) == -1 )
						p.getContext().setProperty( "title", " " + title );
					else
						p.getContext().setProperty( "title", title );
				}
				else
				{
					ic.sendFeedback( "That is too long to be set as a title.  Please limit titles to " + MAX_LENGTH + " characters." );
					return;
				}
			}
			else
				p.getContext().setProperty( "title", "" );
			
			ic.sendFeedback( "You set the title of " + p.getContext().getName() + " so that it now reads:" );
			ic.sendFeedback( (String) p.getContext().getProperty( "titledName" ) );
		}
		catch( NoSuchPropertyException e )
		{
			ic.sendError( p.getContext().getName() + " does not have a title." );
		}
	}
}
