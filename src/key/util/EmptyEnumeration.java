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
**  $Id: EmptyEnumeration.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumeration implements Enumeration
{
	public static final EmptyEnumeration EMPTY = new EmptyEnumeration();
	
	public EmptyEnumeration()
	{
	}

	public boolean hasMoreElements()
	{
		return( false );
	}

	public Object nextElement()
	{
		throw new NoSuchElementException( "iterating in empty enumeration" );
	}
}

