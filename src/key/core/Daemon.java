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
**  $Id: Daemon.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
  *  A class which is essentially a key thread (system - connectingplayers
  *  and players don't count
 */
public abstract class Daemon extends AnimatedAtom
{
	public Daemon()
	{
	}

	public Daemon( boolean realDaemon )
	{
		super( realDaemon );
	}
}
