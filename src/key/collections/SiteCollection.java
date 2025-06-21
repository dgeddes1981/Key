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
**  $Id: SiteCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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

/**
  *  The numberedCollection is based on a hashtable of
  *  integers.  Every symbol thats added to it is required
  *  to be an instance of 'Site', and it will be
  *  automatically numbered and added to the container.
  *
  *  The numbered collection is mainly intended for
  *  mail and news and so forth, and is generally
  *  reverse ordered
 */
public class SiteCollection implements Collection
{
	private Hashtable theHash;
	
	public SiteCollection()
	{
		theHash = new Hashtable();
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
		if( key != null && key instanceof Integer )
		{
				//  this is primarily for atoms which extend a
				//  numeric atom and don't want to be 'just a number'.
				//  (sorry about that.  Too much TV & NRMA ad's).
				//  A good example is key.Internet
			theHash.put( key, a );
		}
		else
			throw new UnexpectedResult( "adding a non-integer (" + a.getClass().getName() + ") keyed symbol to a sitecollection" );
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
		if( key != null && key instanceof Integer )
		{
			if( theHash != null )
				theHash.remove( key );
		}
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
		if( key instanceof String )
		{
			key = new Integer( Integer.parseInt( (String) key ) );
		}
		
		if( key instanceof Integer )
		{
			return( get( (Integer) key ) );
		}
		
		throw new UnexpectedResult( "invalid key while attempting to match a numberic symbol." );
	}
	
	public Symbol getExact( String key )
	{
		return( (Symbol) get( key ) );
	}

	public Object get( Integer match )
	{
		return( theHash.get( match ) );
	}

	public Object getTrieFor( String match )
	{
		return( null );
	}

	public Symbol getElementAt( int c )
	{
		throw new LimitationException( "getElementAt() not supported by SiteCollection" );
	}

	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		throw new LimitationException( "removeElementAt() not supported by SiteCollection" );
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
