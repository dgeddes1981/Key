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
**  $Id: Cd.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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

public class Cd extends Command
{
	public Cd()
	{
		setKey( "cd" );
		usage = "<new context>";
	}

	/**
	  *  Changes the players context.  The context is *always* specified
	  *  as an offset from the player class - regardless of what your current
	  *  reference is.  This is to stop you getting lost ;)
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			p.setContext( p );
			return;
		}
		
		Atom old = p.getContext();
		String toCd = args.nextToken();
		Atom from = p.getContext();
		
		Object m = new Search( toCd, from ).result;
		
		if( m != null && m instanceof Atom )
			p.setContext( (Atom) m );
		else
		{
			ic.sendError( "Could not find '" + toCd + "' from " + from.getId() );
			return;
		}
		
		if( args.hasMoreTokens() )
		{
			p.command( args.nextToken( "" ), ic, false );
			p.setContext( old );
			return;
		}
		
		ic.sendFeedback( "New current directory '" + p.getContext().getId() + "'" );
	}
}
