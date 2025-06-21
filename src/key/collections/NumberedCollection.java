/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key.collections;
import key.*;
import key.util.LinkedList;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;
import java.util.Hashtable;

/**
  *  The numberedCollection is based on a hashtable of
  *  integers.  Every symbol thats added to it is required
  *  to be an instance of 'Numbered', and it will be
  *  automatically numbered and added to the container.
  *
  *  The numbered collection is mainly intended for
  *  mail and news and so forth, and is generally
  *  reverse ordered
 */
public final class NumberedCollection implements Collection
{
	private LinkedList theList;
	
	public NumberedCollection()
	{
		theList = new LinkedList();
	}
	
	/**
	  * Add this object to the list of objects
	  * @param p the player to add to the list
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
			//  scan through to find the highest number
		int highest = -1;
		
		for( Enumeration e = theList.elements(); e.hasMoreElements(); )
		{
			int t = ((Integer)((Symbol)e.nextElement()).getKey()).intValue();
			
			if( t > highest )
				highest = t;
		}
		
		/*
		try
		{
			a = (Symbol) a.clone();
		}
		catch( CloneNotSupportedException ex )
		{
		}
		*/
		
		a.setKey( new Integer( highest+1 ) );
		
		theList.prepend( a ); 
	}
	
	public void conceal( Symbol a )
	{
	}
	
	public void reveal( Symbol a )
	{
	}
	
	/**
	  * Adds this atom to the database in such a
	  * way that it can be matched, but not seen
	  * by iterating through the elements.
	 */
	public void partialLink( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		link( a );
	}
	
	/**
	  * Take this player out of the list of players
	  * @param p the player to remove from the list
	  * @exception NoSuchElementException if the player is not in the list
	  * @exception BadKeyException if the players name is malformed somehow
	 */
	public void unlink( Symbol a ) throws NoSuchElementException,BadKeyException
	{
		theList.removeEqual( a );
	}
	
	public boolean contains( Symbol o )
	{
		return( theList.containsEqual( o ) );
	}
	
	/**
	  *  Sorts the elements in this linked trie
	 */
	public void sort()
	{
	}
	
	/**
	  * Returns the atom matched, or, possibly, an instance of
	  * a Trie object that contains all the matching atoms.
	  * <p>
	  * A null is returned if no matches were found at all.  The
	  * match string is searched until the end of the string or
	  * a non-alphabetical character is found.
	  *
	  * @param match the start or whole string to match from
	  * @return An atom object, referring to the sole match, or a Trie
	 */
	public Object get( Object key )
	{
		Integer ikey = null;
		
		if( key instanceof String )
		{
			try
			{
				ikey = new Integer( Integer.parseInt( (String) key ) );
			}
			catch( NumberFormatException e )
			{
				return null;
			}
		}
		else if( key instanceof Integer )
			ikey = (Integer) key;
		else
			return( null );
		
		for( Enumeration e = theList.elements(); e.hasMoreElements(); )
		{
			Symbol a = (Symbol) e.nextElement();
			
			if( ikey.equals( a.getKey() ) )
				return( a );
		}
		
		return( null );
	}
	
	public Symbol getExact( String key )
	{
		return( (Symbol) get( key ) );
	}
	
	public Object get( Integer match )
	{
		return( get( match ) );
	}
	
	public Object getTrieFor( String match )
	{
		return( null );
	}
	
	public Symbol getElementAt( int c )
	{
		return( (Symbol) theList.getElementAt( c ) );
	}
	
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		theList.removeElementAt( c );
	}
	
	public Enumeration elements()
	{
		return( theList.elements() );
	}
	
	public int count()
	{
		return( theList.count() );
	}
	
	public void deallocate()
	{
	}
	
	public void concealable( boolean t )
	{
	}
}
