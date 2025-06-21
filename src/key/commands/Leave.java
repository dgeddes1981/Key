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
**  19Jul97     subtle       created this command
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  Leave returns you to the last public room
  *  you were in.  It will mainly be used to
  *  leave someone's rooms (including your own)
 */
public class Leave extends ChangeRoom
{
	public Leave()
	{
	}

	public static final String CANNOT = "Cannot find that place";

	/**
	  *  Pick a random public room
	 */
	protected Room noSuchRoom( Player p, InteractiveConnection ic )
	{
		if( Key.instance() instanceof Forest )
		{
			Forest f = (Forest) Key.instance();
			
			return( f.getConnectRoom( p ) );
		}
		
		ic.sendFailure( CANNOT );
		return null;
	}
}
