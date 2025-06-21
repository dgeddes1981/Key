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
**  $Id: NoKeyCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  16Sep98     subtle       created
**
*/

package key.collections;

import key.*;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;
import java.util.Vector;

public final class NoKeyCollection implements Collection
{
	private Vector theVector;
	
	public NoKeyCollection()
	{
		theVector = new Vector( 5, 20 );
	}
	
	public NoKeyCollection( Collection c )
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
		theVector.addElement( a );
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
		theVector.removeElement( a );
	}

	public boolean contains( Symbol o )
	{
		return( theVector.contains( o ) );
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
		throw new LimitationException( "getExact() not supported by NoKeyCollection" );
	}
	
	public Symbol getExact( String key )
	{
		throw new LimitationException( "getExact() not supported by NoKeyCollection" );
	}
	
	public Object getTrieFor( String match )
	{
		return( null );
	}
	
	public Symbol getElementAt( int c )
	{
			//  -ve numbers indicate positions from the end of the array
		if( c < 0 )
			c = theVector.size() + c;
		
		return( (Symbol) theVector.elementAt( c ) );
	}
	
	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		theVector.removeElementAt( c );
	}
	
	public Enumeration elements()
	{
		return( theVector.elements() );
	}
	
	public int count()
	{
		return( theVector.size() );
	}
	
	public void deallocate()
	{
	}
	
	public void concealable( boolean t )
	{
	}
}
