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
**  $Id: LimitActions.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       added support for Ranks in permissionlist
**
*/

package key.commands;
import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

/**
  *  Remember to turn your expert bit on if you want to see
  *  everything.
 */
public class LimitActions extends Command
{
	public LimitActions()
	{
		setKey( "limitActions" );
		
		usage = "[<new limit>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom context = p.getContext();
		PermissionList pl = context.getPermissionList();
		int old = pl.getLimit();
		
		if( args.hasMoreTokens() )
		{
			int newLimit = PermissionList.DEFAULT_LIMIT;
			
			try
			{
				newLimit = Integer.parseInt( args.nextToken() );
			}
			catch( NumberFormatException e )
			{
				ic.sendError( "Please use a number for the new limit" );
				return;
			}
			
			if( old != newLimit )
			{
				pl.setLimit( newLimit );
				ic.sendFeedback( "The permissionlist limit for " + context.getName() + " has been changed from " + old + " to " + newLimit + "." );
			}
			else
				ic.sendFeedback( "The permissionlist limit for " + context.getName() + " has not been changed from " + old + "." );
		}
		else
			ic.sendFeedback( "The permissionlist limit for " + context.getName() + " is " + old + "." );
		
	}
}
