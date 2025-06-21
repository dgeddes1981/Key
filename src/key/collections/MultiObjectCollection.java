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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jun97     subtle       modified loadFrom so that the order of the
**                           elements in the linked list is preserved.
**  22Jun97     subtle       added a constructor to clone the contents of
**                           another collection.
**
*/

package key.collections;
import key.*;
import key.util.LinkedList;
import key.util.EmptyEnumeration;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 *  This class has not been fully implemented, pending a decision on how we're actually planning on
 *  implementing multiples.  (Whether it will be done at the collection level, or done above that level)
 */
public final class MultiObjectCollection implements Collection
{
	private Vector theContents;
	
	private final class CountedContent
	{
		int count;
		Object content;
		
		public CountedContent( Object c )
		{
			count = 1;
			content = c;
		}
		
		public void addItem()
		{
			count++;
		}
		
		public boolean removeItem()
		{
			count--;
			return( count == 0 );
		}
		
		public boolean isEmpty()
		{
			return( count == 0 );
		}
		
		public void addItems( int number )
		{
			count += number;
		}
		
		public boolean removeItems( int number )
		{
			count -= number;
			
			if( count < 0 )
				throw new UnexpectedResult( "Attempting to remove more items then there are in container" );
			
			return( count == 0 );
		}
		
		public boolean equals( Object o )
		{
			if( o instanceof CountedContent )
				return( content.equals( ((CountedContent)o).content ) );
			else
				return( content.equals( o ) );
		}
	}
	
	public MultiObjectCollection()
	{
		theContents = null;
	}
	
	public MultiObjectCollection( Collection c )
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
	  * Add this object to the list of objects.
	  *
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public synchronized void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		ensureContents();
		
		Object key = a.getKey();
		
		if( key == null )
		{
			throw new UnexpectedResult( "cannot add atom without key to ObjectCollection" );
		}
		
			//  If it's already in this container, just add a new one
		for( int i = 0; i < theContents.size(); i++ )
		{
			Object o = theContents.elementAt( i );
			
				//  This is very clever.  Since the CountedContent class will return that it does
				//  indeed equal the symbol being passed, this will work nicely.  We still need to check
				//  and make sure we're adding the right type, though.
			if( o.equals( a ) )
			{
				if( o instanceof CountedContent )
				{
					((CountedContent)o).addItem();
					return;
				}
				else
				{
					CountedContent cc = new CountedContent( o );
					cc.addItem();
					theContents.setElementAt( cc, i );
				}
			}
		}
		
		if( !(key instanceof AliasListKey) )
		{
			AliasListKey newKey;
			newKey = new AliasListKey();
			newKey.setPrimary( key.toString() );
			
			a.setKey( newKey );
			try {((Atom)a).regenReference();} catch( Exception x ){}
			
				//  this is pretty hacky.
			if( a instanceof Reference )
			{
				((Reference)a).get().setKey( newKey );
			}
		}
		
		theContents.addElement( a );
	}
	
	public synchronized void conceal( Symbol a )
	{
		throw new UnexpectedResult( "conceal not supported on this collection" );
	}
	
	public synchronized void reveal( Symbol a )
	{
		throw new UnexpectedResult( "reveal not supported on this collection" );
	}
	
	public void concealable( boolean t )
	{
	}
	
	/**
	  * Take this player out of the list of players
	  * @param p the player to remove from the list
	  * @exception NoSuchElementException if the player is not in the list
	  * @exception BadKeyException if the players name is malformed somehow
	 */
	public synchronized void unlink( Symbol a ) throws NoSuchElementException,BadKeyException
	{
		if( theContents != null )
		{
			theContents.removeElement( a );
			if( theContents.size() == 0 )
				theContents = null;
		}
	}
	
	public boolean contains( Symbol o )
	{
		return( (theContents != null && theContents.contains( o )) );
	}
	
	public void rekey( Symbol o )
	{
		if( theContents != null )
		{
			int i = theContents.indexOf( o );
			if( i != -1 )
				theContents.setElementAt(o, i );
		}
	}
	
	private final void ensureContents()
	{
		if( theContents == null )
			theContents = new Vector(10, 5);
	}
	
	public synchronized void sort()
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
		String match;
		int hc;
		
		if( theContents == null )
			return( null );
		
		try
		{
			match = (String) key;
			hc = match.toLowerCase().hashCode();
		}
		catch( ClassCastException e )
		{
			throw new UnexpectedResult( "non string key passed to shortcut collection" );
		}
		
			//  this match, this match strength
		Symbol tm = null;
		int tms = Integer.MAX_VALUE;

		//System.out.println( "Scanning object collection for '" + match + "'" );
		
		for( Enumeration e = theContents.elements(); e.hasMoreElements(); )
		{
			Symbol o = (Symbol) e.nextElement();
			Object k = o.getKey();
			
			try
			{
				int s = ((AliasListKey)k).getMatchStrength( hc, match );
				//System.out.println( "  list key '" + k.toString() + "' strength " + s );
				
				switch( s )
				{
					case 0:
						break;
					case 1:
						return( o );
					default:
						if( s < tms )
						{
							tms = s;
							tm = o;
						}
						break;
				}
			}
			catch( ClassCastException ex )
			{
				Log.debug( this, ex.toString() + " in ObjectCollection::get()" );
				
				if( k.equals( key ) )
				{
					//System.out.println( "  key '" + k.toString() + "' matched, returning" );
					return( o );
				}
				//else
					//System.out.println( "  key '" + k.toString() + "' didn't match" );
			}
		}
		
		return( tm );
	}
	
	public Object getTrieFor( String match )
	{
		throw new UnexpectedResult( "getTrieFor not available in this collection" );
	}
	
	public Symbol getExact( String match )
	{
		return( (Symbol) get( match ) );
	}
	
	public Symbol getElementAt( int c )
	{
		if( theContents != null )
			return( (Symbol) theContents.elementAt( c ) );
		else
			return( null );
	}
	
	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public synchronized void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		if( theContents != null )
			unlink( (Symbol) theContents.elementAt( c ) );
	}

	public Enumeration elements()
	{
		if( theContents != null )
			return( theContents.elements() );
		else
			return( new EmptyEnumeration() );
	}

	public int count()
	{
		if( theContents != null )
			return( theContents.size() );
		else
			return( 0 );
	}
	
	public void deallocate()
	{
	}
	
	public void partialLink( Symbol added )
	{
		throw new UnexpectedResult( "partialLink not available in this collection" );
	}
}
