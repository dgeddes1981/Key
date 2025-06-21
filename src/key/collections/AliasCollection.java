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
**  $Id: AliasCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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

import key.util.LinkedList;
import java.util.Enumeration;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.Hashtable;

/**
  *  A special collection that requires that all its
  *  symbols use AliasListKey's as their key.  WARNING:
  *  this collection should NOT be used by a reference
  *  container, as it has a nasty tendancy to change the
  *  key of everything added to it to AliasListKey's.
  *
  *  This collections very specific purpose at the moment
  *  is for help files. (ie, "help res" and "help resident"
  *  call the same file - the lookup must be equivalent.
 */
public final class AliasCollection implements Collection
{
	private transient Hashtable theHash;
	private LinkedList theList;
	
	public AliasCollection()
	{
		init();
		theList = new LinkedList();
	}
	
	public AliasCollection( Collection c )
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
	
	private void init()
	{
		theHash = new Hashtable();
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		init();
		
		for( Enumeration e = theList.elements(); e.hasMoreElements(); )
		{
			try
			{
				linkToHash( (Symbol) e.nextElement() );
			}
			catch( NonUniqueKeyException ex )
			{
				Log.debug( this, ex.toString() );
				ex.printStackTrace( System.out );
			}
		}
	}
	
	private void linkToHash( Symbol a ) throws NonUniqueKeyException
	{
		Object key = a.getKey();
		AliasListKey newKey;
		
		if( !(key instanceof AliasListKey) )
		{
			newKey = new AliasListKey();
			newKey.setPrimary( key.toString() );
			
			a.setKey( newKey );
			try {((Atom)a).regenReference();} catch( Exception x ){}
			
				//  this is pretty hacky.
			if( a instanceof Reference )
			{
				((Reference)a).get().setKey( newKey );
			}
			
			//System.out.println( "Added non-ALK: " + key.toString() + " to AliasColl" );
		}
		else
		{
			newKey = (AliasListKey) key;
			//System.out.println( "Added ALK: " + key.toString() + " to AliasColl" );
		}
		
		String pk = newKey.getPrimary();
		
		if( theHash.contains( pk ) )
			throw new NonUniqueKeyException( "'" + newKey.getPrimary() + "' is already in this collection." );
		
		theHash.put( pk, a );
		
		addSecondaries( newKey, a );
	}
	
	/**
	  * Add this object to the list of objects
	  * @param p the player to add to the list
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
		linkToHash( a );
		theList.append( a );
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

			//  its not possible that the element be
			//  in this collection unless it has an
			//  aliaslistkey key.
		if( key instanceof AliasListKey )
		{
			AliasListKey alk = (AliasListKey) key;
			
			if( theHash != null )
			{
				theHash.remove( alk.getPrimary() );
				theList.removeEqual( a );
				
				removeSecondaries( alk );
			}
		}
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
		if( key instanceof String )
			return( theHash.get( ((String)key).toLowerCase() ) );
		else
			return( theHash.get( key.toString() ) );
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
		return( (Symbol) theList.getElementAt( c ) );
	}

	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		Symbol s = (Symbol) theList.getElementAt( c );
		
		unlink( s );
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

	public void removeSecondaries( AliasListKey alk )
	{
		for( Enumeration e = alk.secondaryKeys(); e.hasMoreElements(); )
			theHash.remove( e.nextElement() );
	}
	
	public void addSecondaries( AliasListKey alk, Symbol o )
	{
		for( Enumeration e = alk.secondaryKeys(); e.hasMoreElements(); )
		{
			Object n = e.nextElement();
			//System.out.println( "ALK: aliased " + n.toString() );
			theHash.put( n, o );
		}
	}
}
