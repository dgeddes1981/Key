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
**  $Id: Event.java,v 1.1 1999/10/11 13:25:05 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;
import key.primitive.*;

import java.util.StringTokenizer;

/**
  *  Event
  *
  *
 */
public abstract class Event extends Atom
{
	DateTime scheduledFor;
	
	public Event()
	{
	}

	public void schedule( DateTime at )
	{
		scheduledFor = at;
	}

	public boolean isBefore( Event e )
	{
		return( scheduledFor.before( e.scheduledFor ) );
	}

	public final boolean runNow()
	{
		//Key.instance().debug( this, "comparing scheduledTime of " + ((scheduledFor.getTime()/1000)%10000) + " to current time of " + ((System.currentTimeMillis()/1000)%10000) );
		return( scheduledFor.getTime() <= System.currentTimeMillis() );
	}
	
	public abstract void run( Daemon scheduler );
}
