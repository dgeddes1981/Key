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
**  $Id: EmptyCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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

public final class EmptyCollection implements Collection, key.io.Replaceable
{
	public static final EmptyCollection EMPTYCOLLECTION = new EmptyCollection();
	
	public EmptyCollection()
	{
	}
	
	public EmptyCollection( EmptyCollection c )
	{
	}

	public Object getReplacement()
	{
		return( EMPTYCOLLECTION );
	}
	
	public void link( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
	}
	
	public void conceal( Symbol a )
	{
	}
	
	public void reveal( Symbol a )
	{
	}
	
	public void partialLink( Symbol a ) throws NonUniqueKeyException,BadKeyException
	{
	}

	public void unlink( Symbol a ) throws NoSuchElementException,BadKeyException
	{
	}

	public boolean contains( Symbol o )
	{
		return( false );
	}
	
	public void sort()
	{
	}

	public Object get( Object key )
	{
		throw new LimitationException( "get() not supported by EmptyCollection" );
	}

	public Symbol getExact( String key )
	{
		throw new LimitationException( "getExact() not supported by EmptyCollection" );
	}
	
	public Object getTrieFor( String match )
	{
		return( null );
	}
	
	public Symbol getElementAt( int c )
	{
		throw new LimitationException( "get() not supported by EmptyCollection" );
	}
	
	/**
	  *  aha!, but I reserve the right to make this function more efficient
	  *  this way ;p~  (ie, it isn't very, atm)
	 */
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
	}

	public Enumeration elements()
	{
		return( key.util.EmptyEnumeration.EMPTY );
	}
	
	public int count()
	{
		return( 0 );
	}
	
	public void deallocate()
	{
	}
	
	public void concealable( boolean t )
	{
	}
}
