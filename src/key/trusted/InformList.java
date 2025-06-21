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
**  $Id: InformList.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
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
  *  Only subclasses so its a different type for qualification,
  *  really.
 */
public final class InformList extends Container
{
	private static final long serialVersionUID = 5886393161496542711L;
	
	public InformList()
	{
		super( true );
	}
	
	/**
	  *  An accessor method that would otherwise be a security risk
	  *  to implement.
	 */
	public void addInternal( Reference added ) throws BadKeyException, NonUniqueKeyException
	{
		super.addInternal( added );
	}
	
	/**
	  *  An accessor method that would otherwise be a security risk
	  *  to implement.
	 */
	public void removeInternal( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		super.removeInternal( removed );
	}
	
	/**
	  *  An accessor method that would otherwise be a security risk
	  *  to implement.
	 */
	public void removeClanInform( Clan c ) throws BadKeyException,NonUniqueKeyException
	{
		removeInternal( c.getThis() );
	}
}
