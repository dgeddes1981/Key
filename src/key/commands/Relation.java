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
**  $Id: Relation.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  07Oct98     subtle      created this command
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  Allows a player to write the rooms portrait
 */
public class Relation extends Command
{
	public Relation()
	{
		setKey( "relation" );
		usage = "<relation>";
	}

    public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String portraitText = args.nextToken( "" );
		Room r = p.getLocation();
		
		try
		{
			r.setProperty( "relation", portraitText );
			ic.sendFeedback( "The portrait now reads: [" + p.getName() + " is " + r.relation() + "] " + r.portrait() );
		}
		catch( ClassCastException e )
		{
			ic.sendError( r.getName() + " doesn't appear to be a room." );
		}
		catch( AccessViolationException e )
		{
			ic.sendError( "You are not allowed to set the portrait for " + r.getName() + "." );
		}
	}
}
