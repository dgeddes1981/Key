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
**  $Id: LatentlyCached.java,v 1.1 1999/10/11 13:25:06 pdm Exp $
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
  *  When the class is modified, it should ensure that
  *  'modified' returns true until 'resetModify' is called.
 */
public interface LatentlyCached
{
	/**
	 */
	public void deallocate();

	/**
	  *  This routine returns the state of the
	  *  modified boolean
	 */
	public boolean modified();

	/**
	  *  This routine is called to indicate that the
	  *  modified boolean should be reset
	 */
	public void resetModify();
}
