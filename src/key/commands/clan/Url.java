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
**  $Id: Url.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands.clan;

import key.*;
import key.primitive.Webpage;
import java.io.IOException;
import java.util.StringTokenizer;

public class Url extends Command
{
	public static final int MAX_LENGTH = 60;

	public Url()
	{
		setKey( "url" );
		usage = "[<WWW homepage address>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String url = args.nextToken( "" );
			
			if( p.getClan() == null )
			{
				ic.sendError( "You aren't in a clan." );
				return;
			}
			
			if( url.length() > MAX_LENGTH )
			{
				ic.sendFeedback( "That is too long to be set as a URL.  Please limit your url to " + MAX_LENGTH + " characters." );
				return;
			}
			else
			{
				Webpage page = (Webpage) p.getClan().getProperty( "homepage" );

				page.set( url, ic );
			}
		}
		else
		{
			usage( ic );
		}
	}
}
