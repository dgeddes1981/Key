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
**  $Id: Animate.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/


package key;

/**
  *
 */
public interface Animate extends Runnable
{
	public void stop();
	public void start();
	public boolean isAlive();
	public boolean isRunning();

	public boolean isDaemon();
	public void setPriority( int pri );
	public int getPriority();
}
