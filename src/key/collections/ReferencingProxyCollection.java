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
**  $Id: ReferencingProxyCollection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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

import key.util.FilteredEnumeration;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;

/**
  *  You should not link references into this collection as normal -
  *  only atom's should be used.  It will store references, though.
 */
public class ReferencingProxyCollection
implements Collection, ReferenceEnumeratorFilter.EnumeratedThing
{
	Collection proxy;

	public ReferencingProxyCollection( Collection p )
	{
		proxy = p;
	}
	
	public void noSideEffectRemove( Reference r ) throws BadKeyException, NonUniqueKeyException, NoSuchElementException
	{
		proxy.unlink( r );
	}
	
	public void link( Symbol added ) throws BadKeyException, NonUniqueKeyException
	{
		proxy.link( ((Atom)added).getThis() );
	}
	
	public void partialLink( Symbol added ) throws BadKeyException, NonUniqueKeyException
	{
		proxy.partialLink( ((Atom)added).getThis() );
	}

	public void unlink( Symbol removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		proxy.unlink( ((Atom)removed).getThis() );
	}
	
	public Enumeration elements()
	{
			//  there is no need for a indirection agent here as seen 
			//  in Player.java and Container.java - the  noSideEffectRemove
			//  method is public, since access to instances of this class
			//  can only occur through delegation from a trusted class
			//  (Container, generally)
		return( new FilteredEnumeration( 
				proxy.elements(),
				new ReferenceEnumeratorFilter( this, true ) ) );
	}
	
    public int count()
	{
		return( proxy.count() );
	}
	
	public boolean contains( Symbol o )
	{
		return( proxy.contains( ((Atom)o).getThis() ) );
	}
	
	public Object get( Object key )
	{
		return( ((Reference)proxy.get( key )).get() );
	}
	
	public Symbol getExact( String key )
	{
		return( ((Reference)proxy.getExact( key )).get() );
	}
	
	/**
	  *  Conceal the symbol - if possible
	 */
	public void conceal( Symbol a )
	{
		proxy.conceal( ((Atom)a).getThis() );
	}
	
	/**
	  *  Unconceal the symbol, if its concealed
	 */
	public void reveal( Symbol a )
	{
		proxy.reveal( ((Atom)a).getThis() );
	}

	/**
	  *  While this is false, even conceal'd
	  *  symbols are visible.  It starts out
	  *  as false, is set to true, and can
	  *  never be reset again.
	 */
	public void concealable( boolean t )
	{
		proxy.concealable( t );
	}
	
	/**
	  * Called when anything cached can be 'deallocated'
	 */
	public void deallocate()
	{
		proxy.deallocate();
	}

	/**
	  *  Called when theres an opportunity to index the
	  *  elements in the collection in some ordered way
	  *  (as opposed to the haphazard way they're normally
	  *  treated ;)
	 */
	public void sort()
	{
		proxy.sort();
	}

	/**
	  *  Get multi-matches.  Only implemented in some
	  *  Collections.  (Not required) Marked for
	  *  removal
	 */
	public Object getTrieFor( String match )
	{
		return( proxy.getTrieFor( match ) );
	}
	
		// remove these next two, too
	public Symbol getElementAt( int c )
	{
		return( ((Reference)proxy.getElementAt( c )).get() );
	}
	
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		proxy.removeElementAt( c );
	}
}
