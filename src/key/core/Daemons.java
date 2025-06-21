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
**  $Id: Daemons.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.NoSuchElementException;

/**
  *  Despite its ultra-trendy name, this is just a
  *  reference container which holds other groups
  *  that a specific group can 'imply'.
  *
  *  In the future, this is probably better done
  *  with a set of custom commands rather than a
  *  container - less resources.
 */
public class Daemons extends Container
{
	public Daemons()
	{
		super( false );
		setConstraint( Type.DAEMON );
		setKey( "daemons" );
	}
	
	protected void addInternal( Reference added ) throws BadKeyException, NonUniqueKeyException
	{
		super.addInternal( added );

			//  if we're running, start the daemon
			//
			//  (ie, _only_ start the daemons if
			//   we're running - otherwise
			//   additional setup information might be
			//   required.)
			//
		if( Key.isRunning() )
			((Daemon)added.get()).start();
	}
	
	protected void noSideEffectRemove( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		super.noSideEffectRemove( removed );
		
		Daemon daemon = (Daemon) removed.get();
		
		if( daemon.isAlive() )
			daemon.stop();
	}
}
