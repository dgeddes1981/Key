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
**  $Id: Expirable.java,v 1.1 1999/10/11 13:25:05 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.Date;

/**
  *  To be implemented by atoms that expect to be
  *  deleted automatically - players, mail &
  *  news, etc, etc.
 */
public interface Expirable
{
	/**
	  *  Returns the expiry date of the atom - after
	  *  this date it is not guaranteed to exist -
	  *  it isn't necessarily killed right at this
	  *  time, though - some expiry's won't be
	  *  checked incredibly regularly.
	  *
	  *  Returning null is okay.
	 */
	public Date expireAt();

	/**
	  *  Routine to call which actually expires
	  *  the atom
	 */
	public void expire();
}
