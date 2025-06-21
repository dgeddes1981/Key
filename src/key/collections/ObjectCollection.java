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

/**
  *  The shortcut container was created so that you could 'shortcut'
  *  the contents of a container on the string.  There are limitations
  *  in the implementation - it uses Trie's to provide the shortcut,
  *  so only [a-z] characters may be used.  (No punctuation)
  *
  *  Obviously, everything in the collection must be adequately (and
  *  uniquely named).
 */
public final class ObjectCollection implements Collection
{
	private static final long serialVersionUID = -340343914054921047L;
	private LinkedList theList;
	
	public ObjectCollection()
	{
		theList = null;
	}
	
	public ObjectCollection( Collection c )
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
		
		/*
		try
		{
			a = (Symbol) a.clone();
		}
		catch( CloneNotSupportedException ex )
		{
		}
		*/
		
		Object key = a.getKey();
		AliasListKey newKey;

		if( key == null )
		{
			throw new UnexpectedResult( "cannot add atom without key to ObjectCollection" );
		}
		
		if( !(key instanceof AliasListKey) )
		{
			newKey = new AliasListKey();
			newKey.setPrimary( key.toString() );
			
			a.setKey( newKey );
			
				//  this is pretty hacky.
			if( a instanceof Reference )
			{
				((Reference)a).get().replaceKey( newKey );
			}
			else if( a instanceof Atom )
			{
				((Atom)a).regenReference();
			}
		}
		
		theList.prepend( a );
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
		if( theList != null )
		{
			theList.removeEqual( a );
			if( theList.count() == 0 )
				theList = null;
		}
	}
	
	public boolean contains( Symbol o )
	{
		return( (theList != null && theList.containsEqual( o )) );
	}
	
	public void rekey( Symbol o )
	{
		if( theList != null )
			theList.replaceEqual( o );
	}
	
	private final void ensureList()
	{
		if( theList == null )
			theList = new LinkedList();
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
		
		if( theList == null )
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
		
		for( Enumeration e = theList.elements(); e.hasMoreElements(); )
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
	
	public void deallocate()
	{
	}
	
	public void partialLink( Symbol added )
	{
		throw new UnexpectedResult( "partialLink not available in this collection" );
	}
}
