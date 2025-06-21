/*
**  $Id: RecursiveEnumeration.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
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
import java.util.EmptyStackException;
import java.util.Stack;

/**
  *  This is a special type of enumerator filter that will
  *  unroll other enumerations that are encountered as
  *  elements.
  *
  *  For instance, using this on a Vector enumerator,
  *  where the vector contains Enumerations, would
  *  cause a loop through all of each of those enumerations
  *  in turn.
  *
  *  At the moment, only a shallow unroll is done - to
  *  unroll deeply, each of the sub enumerations should
  *  also be wrapped in this class.
 */
public final class RecursiveEnumeration implements Enumeration
{
	Enumeration e;
	Enumeration recurse;
	Object next;
	boolean goDeep;
	Stack st = null;
	Observer teo = null;
	
	/**
	  * @param enum the enumeration to walk through
	  * @param deep recurse deeply through the tree (more than just one level?)
	  * @param o a callback that can be notified as we step into different
	  *          enumerations (may be null)
	 */
	public RecursiveEnumeration( Enumeration enum, boolean deep, Observer o )
	{
		e = enum;
		goDeep = deep;
		teo = o;
		
		scanNext();
	}
	
	public boolean hasMoreElements()
	{
		return( next != null );
	}
	
	private void scanNext()
	{
		if( recurse != null && recurse.hasMoreElements() )
		{
			next = recurse.nextElement();
				
			if( goDeep && next instanceof Enumeration )
			{
				if( st == null )
					st = new Stack();
				
				st.push( recurse );
				recurse = (Enumeration) next;
				if( teo != null )
					teo.notify( recurse, st.size() );
				scanNext();
			}
		}
		else
		{
			if( st != null )
			{
				if( !st.empty() )
				{
					recurse = (Enumeration) st.pop();
					scanNext();
					return;
				}
				else
					st = null;
			}
			
			if( e.hasMoreElements() )
			{
				next = e.nextElement();
				
				if( next instanceof Enumeration )
				{
					recurse = (Enumeration) next;
					if( teo != null )
						teo.notify( recurse, (st != null) ? st.size() : 0 );
					scanNext();
				}
				else
					recurse = null;
			}
			else
				next = null;
		}
	}
	
	public Object nextElement()
	{
		if( next != null )
		{
			Object n = next;
			scanNext();
			return( n );
		}
		else
			throw new java.util.NoSuchElementException();
	}
	
	public static interface Observer
	{
		/**
		  * @param steppingInto the enumeration we're about to
		  *        walk through
		  * @param depth the depth that this enumeration is at.  top
		  *        level enumerations are at depth 0.
		 */
		public void notify( Enumeration steppingInto, int depth );
	}
}
