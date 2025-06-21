/*
**  $Id: FilteredEnumeration.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  15Nov98     subtle       created
**
*/

package key.util;

import java.util.Enumeration;

public final class FilteredEnumeration implements Enumeration
{
	Enumeration e;
	Filter filter;
	Object next;
	
	public FilteredEnumeration( Enumeration enum, Filter f )
	{
		e = enum;
		filter = f;
		
		scanNext();
	}
	
	public boolean hasMoreElements()
	{
		return( next != null );
	}
	
	private void scanNext()
	{
		boolean loopAgain;
		do
		{
			if( e.hasMoreElements() )
			{
				next = e.nextElement();
				
				if( filter.isValid( next, this ) )
					loopAgain = false;
				else
					loopAgain = true;
			}
			else
			{
				next = null;
				loopAgain = false;
			}
			
		} while( loopAgain );
	}
	
	public Object nextElement()
	{
		if( next != null )
		{
			Object c = next;
			
			scanNext();

			c = filter.replace( c, this );
			
			if( c == null )
				return( nextElement() );
			else
				return( c );
		}
		else
			throw new java.util.NoSuchElementException();
	}
	
	public static interface Filter
	{
		/**
		  *  This routine does the real work of the filtered enumerator.
		  *
		  * @param element the element being scanned
		  * @param enum the enumeration that is being filtered
		  * @return true iff this object can be returned
		 */
		public boolean isValid( Object element, Enumeration enum );
		
		/**
		  *  This routine allows the filter to replace elements with
		  *  proxies or other elements.
		  *
		  *  There is a delay between the call of isValid() and 
		  *  replace() for each object - objects are validated
		  *  when the object before it is replaced.  In some cases
		  *  this has repercussions.  For instance, one of our
		  *  filtered enumerations removes invalid objects from
		  *  the source container as they are scanned (isValid).
		  *  Because an object may become invalid after the isValid
		  *  and before the replace, it is necessary to re-check
		  *  the validity in this routine as well.
		  *
		  *  If you replace an element with 'null', it is skipped.
		  *
		  * @param element the element being scanned
		  * @param enum the enumeration that is being filtered
		  * @return element, or the proxy or other replacement object.
		 */
		public Object replace( Object element, Enumeration enum );
	}
}
