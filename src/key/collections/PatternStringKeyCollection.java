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

import key.primitive.StringSymbol;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;
import java.util.Vector;

public final class PatternStringKeyCollection implements Collection
{
	private Vector the;
	
	public PatternStringKeyCollection()
	{
		the = new Vector();
	}
	
	/*
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
	*/
	
	/**
	  * Add this object to the list of objects
	  * @param p the player to add to the list
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		String k = getKeyFromSymbol( a );
		
		if( get( k ) != null )
			throw new NonUniqueKeyException( "'" + k + "' would already be matched in this collection." );
		
		the.addElement( new StringSymbol( k ) );
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
		the.removeElement( new StringSymbol( getKeyFromSymbol( a ) ) );
	}
	
	public boolean contains( Symbol o )
	{
		return( get( o ) != null );
	}
	
	private static final String getKeyFromSymbol( Symbol o )
	{
		return( o.getKey().toString().trim().toLowerCase() );
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
		String k = key.toString().trim().toLowerCase();
		
		for( Enumeration e = the.elements(); e.hasMoreElements(); )
		{
			StringSymbol symbol = (StringSymbol) e.nextElement();
			String t = symbol.getName();
			if( k.indexOf( t ) != -1 )
				return( symbol );
		}
		
		return( null );
	}
	
	public Symbol getExact( String key )
	{
		String k = key.toString().trim().toLowerCase();
		
		for( Enumeration e = the.elements(); e.hasMoreElements(); )
		{
			StringSymbol symbol = (StringSymbol) e.nextElement();
			String t = symbol.getName();
			if( k.equals( t ) )
				return( symbol );
		}
		
		return( null );
	}
	
	public Object getTrieFor( String match )
	{
		return( null );
	}
	
	public Symbol getElementAt( int c )
	{
		throw new LimitationException( "getElementAt() not supported by StringKeyCollection" );
	}
	
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		throw new LimitationException( "removeElementAt() not supported by StringKeyCollection" );
	}
	
	public Enumeration elements()
	{
		return( the.elements() );
	}
	
	public int count()
	{
		return( the.size() );
	}
	
	public void deallocate()
	{
	}
	
	public void concealable( boolean t )
	{
	}
}
