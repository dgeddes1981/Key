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
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  06Oct97     exile
**              druss        created this command
**  02Aug97     exile        fixed so that it respects the to rooms deny enter 
**                           actions.
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Go extends Command
{
	public Go()
	{
		setKey( "go" );
		usage = "<exit>";
	}
	
	public static void useExit( Player p, Exit e, InteractiveConnection ic, StringTokenizer args, Flags flags )
	{
			//  check to see that the room about to be entered
			//  allows this player (exile 02Aug97)
		Room toRoom = e.getTo();
		
		if( toRoom != null )
		{
			if( toRoom.canMoveInto( p ) )
			{
				e.use( p, args, ic, flags, e, null );
			}
			else
			{
				ic.sendFeedback( "Your ability to enter that room has been denied." );
				return;
			}
		}
		else
			ic.sendFeedback( "That exit doesn't appear to lead anywhere (dud link)." );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String exitName = args.nextToken();
			
			Object o = p.getLocation().getExactElement( exitName ) ;
			if( o == null )
			{
					//  splash 'it aint there' clause 
				ic.sendFeedback( "That exit dosent appear to exist." );
			}
			else
			{
					//  test for the objects 'exitability'
				if( o instanceof Exit )
				{
						//  only use the exit if the object is an exit
					useExit( p, (Exit) o, ic, args, flags );
				} 
				else
				{
						//  otherwise splash idiot tell
					ic.sendFeedback( "You cannot exit through that!! (pancake clause)" );
				}
			}	
		}
		else
			usage( ic );
		
	}
}
