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

import key.util.Trie;
import key.util.LinkedList;
import key.util.EmptyEnumeration;

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;

/**
  *  The shortcut container was created so that you could 'shortcut'
  *  the contents of a container on the string.  There are limitations
  *  in the implementation - it uses Trie's to provide the shortcut,
  *  so only [a-z] characters may be used.  (No punctuation)
  *
  *  Obviously, everything in the collection must be adequately (and
  *  uniquely named).
 */
public final class ShortcutCollection implements Collection,LatentlyCached
{
	private static final long serialVersionUID = 2190566778863697841L;
	private transient Trie theTrie;
	private LinkedList theList;
	private LinkedList concealList;
	private boolean concealing=false;
	
	public ShortcutCollection()
	{
		theList = null;
		concealList = null;
	}

	public ShortcutCollection( Collection c )
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
	public synchronized void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		ensureList();
		
		theList.prepend( a );
		linkToSecondary( a );
	}

	public synchronized void conceal( Symbol a )
	{
		ensureList();
		ensureConcealList();
		if( theList.containsEqual( a ) )
		{
			theList.removeEqual( a );
			concealList.prepend( a );
		}
		else
			throw new UnexpectedResult( "Attempting to conceal a symbol which isn't in the linked trie" );
	}
	
	public synchronized void reveal( Symbol a )
	{
		ensureList();
		ensureConcealList();
		if( concealList.containsEqual( a ) )
		{
			concealList.removeEqual( a );
			theList.prepend( a );

			if( concealList.count() == 0 )
				concealList = null;
		}
		else
			throw new UnexpectedResult( "Attempting to conceal a symbol which isn't an alias" );
	}

	public void concealable( boolean t )
	{
		concealing = true;
	}
	
	protected synchronized void linkToSecondary( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		if( theTrie != null )
		{
			try
			{
				theTrie.insert( (String) a.getKey(), a );
			}
			catch( ClassCastException e )
			{
				System.out.println( e.toString() );
				System.out.println( "during SHTCUT: " + a.toString() );
				throw e;
			}
		}
	}

	protected synchronized void unlinkFromSecondary( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		if( theTrie != null )
		{
			try
			{
				theTrie.remove( (String) a.getKey() );
			}
			catch( Exception e )
			{
			}
		}
	}
	
	/**
	  * Adds this atom to the database in such a
	  * way that it can be matched, but not seen
	  * by iterating through the elements.
	 */
	public synchronized void partialLink( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		ensureTrie();
		linkToSecondary( a );
		ensureConcealList();
		concealList.prepend( a );
	}

	/**
	  * Take this player out of the list of players
	  * @param p the player to remove from the list
	  * @exception NoSuchElementException if the player is not in the list
	  * @exception BadKeyException if the players name is malformed somehow
	 */
	public synchronized void unlink( Symbol a ) throws NoSuchElementException,BadKeyException
	{
		try
		{
			unlinkFromSecondary( a );
			
			if( theList != null )
			{
				theList.removeEqual( a );
				if( theList.count() == 0 )
					theList = null;
			}

			if( concealList != null )
			{
				concealList.removeEqual( a );
				if( concealList.count() == 0 )
					concealList = null;
			}
		}
		catch( NonUniqueKeyException e )
		{
			throw new java.util.NoSuchElementException( e.getKey() );
		}
	}

	public boolean contains( Symbol o )
	{
		return( (theList != null && theList.containsEqual( o )) || (concealList != null && concealList.containsEqual( o ) ) );
	}

	private synchronized final void ensureList()
	{
		if( theList == null )
			theList = new LinkedList();
	}

	private synchronized final void ensureConcealList()
	{
		if( concealList == null )
			concealList = new LinkedList();
	}

	public synchronized void ensureTrie()
	{
		if( theTrie == null )
		{
			theTrie = new Trie();
			
			//Log.debug( this, "creating trie for " + count() + " elements" );
			
			if( theList != null )
				linkToSecondary( theList );
			
			if( concealList != null )
				linkToSecondary( concealList );
			
			//Key.getLatencyCache().addToCache( this );
		}

			//  it was called because the trie was used
			//  keep it in the latent cache for another round
		changed = true;
	}

	/**
	  *  Sorts the elements in this linked trie
	 */
	public synchronized void sort()
	{
		if( theList != null )
		{
			ensureTrie();
			LinkedList sorted = theTrie.getSortedList();
			
			for( Enumeration e = sorted.elements(); e.hasMoreElements(); )
			{
				Object o = e.nextElement();
				
				if( theList.containsEqual( o ) )
				{
					theList.removeEqual( o );
					theList.append( o );
				}
			}
		}
	}

	protected synchronized void linkToSecondary( LinkedList ll )
	{
			//  add all the elements of the list
			//  into the Trie, retrospectively
		LinkedList.Iterator l = ll.iterator();
		
		while( l.isValid() )
		{
			Symbol a = (Symbol) l.element();
			
			if( a instanceof Reference )
			{
				if( !((Reference)a).isValid() )
				{
					l.remove();
					continue;
				}
			}
			
			try
			{
				linkToSecondary( a );
				l.next();
			}
			catch( NonUniqueKeyException t )
			{
				Log.debug( this, "while creating Trie: " + t.toString() + " (offending element removed)" );
				l.remove();
			}
			catch( BadKeyException t )
			{
				Log.debug( this, "while creating Trie: " + t.toString() + " (offending element removed)" );
				l.remove();
			}
		}
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
		
		try
		{
			match = (String) key;
		}
		catch( ClassCastException e )
		{
			throw new UnexpectedResult( "non string key passed to shortcut collection" );
		}
		
		ensureTrie();
		
		Object o = theTrie.search( match );
		//System.out.println( " get in shortcutCollection returned " + o );
		return( o );
	}

	public Object getTrieFor( String match )
	{
		ensureTrie();
		return( theTrie.getTrieFor( match ) );
	}

	public Symbol getExact( String match )
	{
		ensureTrie();
		if( match.length() > 0 )
			if( Character.isDigit( match.charAt( 0 ) ) )
				return( (Symbol) get( match ) );
		
		return( (Symbol) theTrie.searchExact( match ) );
	}

	public Symbol getElementAt( int c )
	{
		if( theList != null )
			return( (Symbol) theList.getElementAt( c ) );
		else
			return( null );
	}

	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public synchronized void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		if( theList != null )
			unlink( (Symbol) theList.getElementAt( c ) );
	}

	public Enumeration elements()
	{
		if( theList != null )
			return( theList.elements() );
		else
			return( new EmptyEnumeration() );
	}

	public int count()
	{
		if( theList != null )
			return( theList.count() );
		else
			return( 0 );
	}
	
	/**
	  *  boolean used to indicate modified() state of the
	  *  class (LatentlyCached)
	 */
	private transient boolean changed;
	
	public synchronized void deallocate()
	{
		/*
		if( count() != 0 && theTrie != null )
			Log.debug( this, "trie deallocated for " + count() + " elements" );
		*/
		
		theTrie = null;
	}
	
	public boolean modified()
	{
		return( changed );
	}
	
	public void resetModify()
	{
		changed = false;
	}
}
