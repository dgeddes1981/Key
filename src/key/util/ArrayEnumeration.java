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
**  $Id: ArrayEnumeration.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
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

public class ArrayEnumeration implements Enumeration
{
	Object[] objects;
	int c;
	
	public ArrayEnumeration( Object[] os )
	{
		objects = os;
		c = 0;
	}

	public boolean hasMoreElements()
	{
		return( c < objects.length );
	}

	public Object nextElement()
	{
		return( objects[ c++ ] );
	}
}
