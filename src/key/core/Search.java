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
**  $Id: Search.java,v 1.2 2000/01/14 19:03:27 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Feb98     subtle       created
**
*/

package key;

import java.util.StringTokenizer;

/**
  *  This class is used to contain search parameters for the routines
  *  which iterate through a string to produce a symbol.  For instance,
  *  while matching '/players/subtle.name', a Search object will be
  *  created for the duration of the search, to hold transient information
  *  required for abstraction purposes.
  *
  * @see key.Searchable
 */
public final class Search
{
	public StringTokenizer st;
	
	/**
	  *  Holds the last atom we passed through.  This is
	  *  necessary in case we wish to return an AtomicElement
	  *  from the search, in which case the atom that the
	  *  element is on is very important if we wish to get
	  *  or set the actual value.
	 */
	public Atom lastAtom;
	
	/**
	  *  holds the final result of our search, properties & transient
	  *  stepped through
	 */
	public Object result;
	
	/**
	  *  holds the first matched result of our search, useful for set() routines
	  *  that need to access the low-level provider.  specifically, this is
	  *  the object before it has been notified that it is the end result
	  *  of a search
	 */
	public Object matchedResult;
	
	public boolean dirty;
	public boolean resolveReference = true;
	public boolean resolve = true;
	
	public Search( String s, Atom relativeTo )
	{
		doSearch( s, relativeTo );
	}
	
	public Search( String s, Atom relativeTo, boolean resolveReferences, boolean resolve )
	{
		this.resolveReference = resolveReferences;
		this.resolve = resolve;
		doSearch( s, relativeTo );
	}
	
	public void doSearch( String s, Atom relativeTo ) throws InvalidSearchException
	{
		int count = 0;
		int len = s.length();
		
		if( len <= 0 )
		{
			result = relativeTo;
			st = new StringTokenizer( s, "./@", true );
		}
		else
		{
			char c = s.charAt( 0 );
			Object oldResult;
			
			switch( c )
			{
				case '/':
					if( len > 1 && s.charAt( 1 ) == '.' )
						s = s.substring( 1 );
					else
						;
						//  don't strip the leading slash, it's
						//  needed to show that we want an element,
						//  not a property
					
					relativeTo = Key.instance();
					break;
				case '~':
					s = s.substring( 1 );
					relativeTo = Key.shortcuts();
					break;
				case '#':
					s = s.substring( 1 );
					
					String number = null;
					
						//  try to strip the number off the front
					for( int i = 0; i < s.length(); i++ )
					{
						if( !Character.isDigit( s.charAt( i ) ) )
						{
							number = s.substring( 0, i );
							s = s.substring( i );
							break;
						}
					}
					
					if( number == null )
					{
						number = s;
						s = "";
					}
					
					int index = 0;
					
					try
					{
						index = Integer.parseInt( number );
					}
					catch( NumberFormatException e )
					{
						throw new InvalidSearchException( e.toString() + " while matching id #" );
					}
					
					relativeTo = Registry.instance.get( index );
					
					break;
			}
			
			st = new StringTokenizer( s, "./@", true );
			lastAtom = relativeTo;
			result = relativeTo;
			
			while( result instanceof Searchable && st.hasMoreTokens() && count < 20 )
			{
				((Searchable)result).search( this );
				
				count++;
			}
			
			if( count >= 20 )
				throw new InvalidSearchException( "search path too deep (> 20)" );
		}
		
		Object lastResult = null;
		count = 0;
		
		matchedResult = result;
		
			//
			//  one last time notifies result of our intention
			//  to use it as the search results.  this permits transient
			//  matches to resolve, or offline atoms with stubs to be
			//  loaded and returned.
			//
			//  if one of these atoms returns a different result, we need
			//  to notify that different result, as well, hence the while
			//
			//  again, we use 'count' to bound the limit of these to 20, just
			//  to stop circular searches
			//
		if( resolve )
		{
			if( !resolveReference )
			{
				while
				(
					(result instanceof Searchable) &&
					!(
						(result instanceof Reference) &&
						!(((Reference)result).isLoaded())
					) &&
					(lastResult != result) &&
					(count < 20)
				)
				{
					lastResult = result;
					((Searchable)result).search( this );
					count++;
				}
			}
			else
			{
				while( (result instanceof Searchable) && (lastResult != result) && (count < 20) )
				{
					lastResult = result;
					((Searchable)result).search( this );
					count++;
				}
			}
			
			if( count >= 20 )
				throw new InvalidSearchException( "search resolve path too deep (> 20)" );
		}
	}
}
