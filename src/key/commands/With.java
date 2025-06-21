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
**  $Id: With.java,v 1.2 1999/11/23 14:13:38 noble Exp $
**
**  $Log: With.java,v $
**  Revision 1.2  1999/11/23 14:13:38  noble
**  added Permission checking
**
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class With extends Command
{
	public With()
	{
		setKey( "with" );
		usage = "<name>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String pName;
		
		pName = nextArgument( args, ic );
		Player targetPlayer = (Player) getOnlinePlayer( p, ic, pName );
		
			//  test to see if the target is online...
		if( targetPlayer != null )
		{
			try {
				Room r = targetPlayer.getLocation();
				ic.sendFeedback( r.with( targetPlayer ) );
			}
			catch ( PermissionDeniedException e ) {
			
				ic.sendFeedback( "You can't even determine where " + pName + " is." );
			}
				
		}
	}
}
