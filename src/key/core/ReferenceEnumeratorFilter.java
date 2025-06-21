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
**  $Id: ReferenceEnumeratorFilter.java,v 1.1 1999/10/11 13:25:06 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.util.FilteredEnumeration;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public final class ReferenceEnumeratorFilter implements key.util.FilteredEnumeration.Filter
{
	EnumeratedThing container;
	boolean resolve;
	
	public ReferenceEnumeratorFilter( EnumeratedThing c, boolean r )
	{
		container = c;
		resolve = r;
	}
	
	public boolean isValid( Object element, Enumeration enum )
	{
		Reference r = (Reference)element;
		boolean v = r.isValid();
		
		if( !v )
		{
			try
			{
				container.noSideEffectRemove( r );
			}
			catch( Exception e )
			{
				Log.error( "during isValid() in ReferenceEnumeratorFilter", e );
			}
		}
		
		return( v );
	}
	
	public Object replace( Object element, Enumeration enum )
	{
		if( resolve )
		{
			try
			{
				return( ((Reference)element).get() );
			}
			catch( OutOfDateReferenceException e )
			{
					//  this only occurs when the atom is
					//  deleted -during- our scan through
					//  this container, which is rare.  In
					//  any case, start up again...
					//  (RECURSIVE code, beware)
				return( enum.nextElement() );
			}
		}
		else
			return( element );
	}
	
	public static interface EnumeratedThing
	{
		void noSideEffectRemove( Reference r ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException; 
	}
}
