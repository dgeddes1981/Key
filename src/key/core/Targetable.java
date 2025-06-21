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
**  $Id: Targetable.java,v 1.1 1999/10/11 13:25:08 pdm Exp $
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
  *  Only things of class Atom should implement this
  *  class.
 */
public interface Targetable
{
	/**
	  *  default implementation might be something like:
	  *  return( rank.getTargets().contains( this ) );
	  *
	  *  This needs to be overriden because a player isn't
	  *  targetted directly, generally - they're targetted
	  *  by people who have a rank that targets them...
	  *
	  *  Sites, now, aren't targetable, but internet will
	  *  be targetted normally...
	 */
	public boolean isOutRankedBy( Rank rank );
}
