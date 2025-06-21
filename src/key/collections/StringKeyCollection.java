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
**  $Id: StringKeyCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.collections;

import key.*;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;
import java.util.Hashtable;

public final class StringKeyCollection implements Collection
{
	private Hashtable theHash;
	
	public StringKeyCollection()
	{
		theHash = new Hashtable();
	}

	public StringKeyCollection( Collection c )
	{
		this();
		
		for( Enumeration e = c.elements(); e.hasMoreElements(); )
		{
			try
			{
				link( (Symbol) e.nextElement() );
			}
			catch( BadKeyException ex )
			{
			}
			catch( NonUniqueKeyException ex )
			{
			}
		}
	}
	
	/**
	  * Add this object to the list of objects
	  * @param p the player to add to the list
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		Object key = a.getKey();
		
		if( !(key instanceof String) )
			key = key.toString();
		
		String k = ((String)key).toLowerCase();
		
		if( theHash.containsKey( k ) )
			throw new NonUniqueKeyException( "'" + k + "' is already in this collection." );
		
		theHash.put( k, a );
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
		Object key = a.getKey();
		if( !(key instanceof String) )
			key = key.toString();

		if( theHash != null )
			theHash.remove( ((String)key).toLowerCase() );
	}
	
	public boolean contains( Symbol o )
	{
		return( theHash.contains( o ) );
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
		if( !(key instanceof String) )
			key = key.toString();
		
		return( theHash.get( ((String)key).toLowerCase() ) );
	}
	
	public Symbol getExact( String key )
	{
		return( (Symbol) get( key ) );
	}
	
	public Object getTrieFor( String match )
	{
		return( null );
	}
	
	public Symbol getElementAt( int c )
	{
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Object o = e.nextElement();
			
			if( c-- == 0 )
				return( (Symbol) o );
		}

		return( null );
	}
	
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		throw new LimitationException( "removeElementAt() not supported by StringKeyCollection" );
	}
	
	public Enumeration elements()
	{
		return( theHash.elements() );
	}
	
	public int count()
	{
		return( theHash.size() );
	}
	
	public void deallocate()
	{
	}
	
	public void concealable( boolean t )
	{
	}
}
