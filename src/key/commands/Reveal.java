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
**  $Id: Reveal.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import key.util.SeperatedIdentifier;
import java.io.IOException;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class Reveal extends Command
{
	public Reveal()
	{
		setKey( "reveal" );
		usage = "<fully qualified identifier>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;

		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			SeperatedIdentifier full = new SeperatedIdentifier( args.nextToken( " " ) );
			
			String id=full.id;
			String loc=full.location;
		
			Container l = (Container) getSymbolInside( ic, loc, Type.CONTAINER, p.getContext() );
			if( l != null )
			{
				Atom r;
				
				r = getElementInside( ic, id, l );
				if( r != null )
				{
					l.unalias( r );
					ic.sendFeedback( "Revealed atom '" + r.getName() + "' in container " + l.getId() );
				}
			}
		}
	}
}
