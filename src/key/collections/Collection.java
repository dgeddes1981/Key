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
**  $Id: Collection.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;

public interface Collection extends java.io.Serializable
{
	/**
	  *  Adds the supplied atom
	 */
	public void link( Symbol added ) throws BadKeyException, NonUniqueKeyException;
	public void partialLink( Symbol added ) throws BadKeyException, NonUniqueKeyException;

	public void unlink( Symbol removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException;

	public Enumeration elements();
    public int count();
	public boolean contains( Symbol o );
	public Object get( Object key );
	public Symbol getExact( String key );
	
	/**
	  *  Conceal the symbol - if possible
	 */
	public void conceal( Symbol a );

	/**
	  *  Unconceal the symbol, if its concealed
	 */
	public void reveal( Symbol a );

	/**
	  *  While this is false, even conceal'd
	  *  symbols are visible.  It starts out
	  *  as false, is set to true, and can
	  *  never be reset again.
	 */
	public void concealable( boolean t );
	
	/**
	  * Called when anything cached can be 'deallocated'
	 */
	public void deallocate();

	/**
	  *  Called when theres an opportunity to index the
	  *  elements in the collection in some ordered way
	  *  (as opposed to the haphazard way they're normally
	  *  treated ;)
	 */
	public void sort();

	/**
	  *  Get multi-matches.  Only implemented in some
	  *  Collections.  (Not required) Marked for
	  *  removal
	 */
	public Object getTrieFor( String match );
	
		// remove these next two, too
	public Symbol getElementAt( int c );
	public void removeElementAt( int c ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException;
}
