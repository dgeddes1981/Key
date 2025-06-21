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
**  $Id: MultiEnumeration.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle      created this class
**
*/

package key.util;

import java.util.Enumeration;

public class MultiEnumeration implements Enumeration
{
	Enumeration first;
	Enumeration second;
	
	public MultiEnumeration( Enumeration a, Enumeration b )
	{
		first = a;
		second = b;
	}

	public boolean hasMoreElements()
	{
		return( first.hasMoreElements() || second.hasMoreElements() );
	}

	public Object nextElement()
	{
		if( first.hasMoreElements() )
			return( first.nextElement() );
		else
			return( second.nextElement() );
	}
}
