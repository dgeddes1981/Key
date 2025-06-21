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
**  $Id: Repeatable.java,v 1.1 1999/10/11 13:25:07 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.StringTokenizer;
import java.io.IOException;

public interface Repeatable
{
	/**
	  *  p, args, ic and flags are the same as for the normal command run method.
	  * <p>
	  *  r is the repeated from the player (probably, I can't understand why
	  *  it wouldn't be.
	  * <p>
	  *  caller is the command calling this one.  It's name is usually used for
	  *  Format: name <blah> output.
	 */
	public void run( Player p, StringTokenizer args, Repeated r, Command caller, InteractiveConnection ic, Flags flags ) throws IOException;
}
