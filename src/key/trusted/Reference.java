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
**  $Id: Reference.java,v 1.2 2000/01/14 19:03:27 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.util.SeperatedIdentifier;

import java.io.*;
import java.util.StringTokenizer;

/**
  *  A reference is an immutable pointer to an object, stored in a
  *  manner that allows it to be maintained through swaps of the
  *  referenced atom back to disk, as well as system resets.
 */
public abstract class Reference
	implements Serializable, Searchable, Symbol, Cloneable, 
	           key.io.Replaceable
{
	private static final long serialVersionUID = -1279870814310783156L;
	public static final Reference to( Atom a )
	{
		return( new NamedDirectReference( a ) );
	}
	
	public Object clone()
	{
		try
		{
			return( super.clone() );
		}
		catch( CloneNotSupportedException e )
		{
			throw new UnexpectedResult( e.toString() + " in reference::clone()" );
		}
	}
	
	public static final Reference to( String id, Atom context )
	{
		if( id.equals( "$" ) )
			return( Reference.EMPTY );
		
		Search s = new Search( id, context );
		
		if( s.dirty || s.result == null )
			return( new IndirectReference( id ) );
		else
			return( ((Atom)s.result).getThis() );
	}
	
	public static final Reference to( String id )
	{
		if( id.equals( "$" ) )
			return( Reference.EMPTY );
		
		Search s = new Search( id, Key.instance() );
		
		if( s.dirty || s.result == null )
			return( new IndirectReference( id ) );
		else
			return( ((Atom)s.result).getThis() );
	}
	
	public static final Reference to( int index, int timestamp )
	{
		return( new DirectReference( index, timestamp ) );
	}
	
	public static final Reference to( Atom a, boolean named )
	{
		if( named )
			return( new NamedDirectReference( a ) );
		else
			return( new DirectReference( a ) );
	}
	
	/**
	  *  @param id the full path (leading with a '/' or '~') of the reference
	  *  @param named true if the name of the atom should be stored in the
	  *               reference
	  *  @param noDirect true if a indirect reference is required - no lookup
	  *                  translation should be performed.  This is useful if
	  *                  a property can change or move and we still wish to
	  *                  point to it.  This is the equivalent of a unix softlink.
	 */
	public static final Reference to( String id, boolean named, boolean noDirect )
	{
		if( id.equals( "$" ) )
			return( Reference.EMPTY );
		
		if( noDirect )
			return( new IndirectReference( id ) );
		
		Search s = new Search( id, Key.instance() );
		
		if( s.dirty || (s.result == null) )
			return( new IndirectReference( id ) );
		else
			return( new DirectReference( (Atom)s.result ) );
	}
	
	public abstract Atom get();
	
	public abstract Atom getType( Class c );
	
	/**
	  * @return true iff these two references point to the same
	  *         atom.
	 */
	public abstract boolean equals( Object o );
	
	public int hashCode()
	{
		return( get().hashCode() );
	}
	
	/**
	  * @return true iff this reference points to this atom
	 */
	public abstract boolean contains( Atom a );
	
	/**
	  * @return false if this atom is definately not in memory.  May
	  *         return true if the atom is not in memory, particularly
	  *         if this is a symbol reference.  Use this routine as a
	  *         guide, not as a definitive result.  should work for
	  *         getThis() references, though.
	 */
	public abstract boolean isLoaded();

	public int getIndex()
	{
		return( get().index );
	}

	public int getTimestamp()
	{
		return( get().timestamp );
	}

	public String getName()
	{
		return( "" );
	}

	public void search( Search s ) throws InvalidSearchException
	{
		s.result = get();
	}
	
	public Object getKey()
	{
		return( get().getKey() );
	}
	
	public void setKey( Object k )
	{
		throw new UnexpectedResult( "setKey::called on Reference" );
	}
	
	/**
	  *  Return false if this reference can be replaced by EMPTY
	 */
	public boolean isValid()
	{
		return( true );
	}
	
	public Object getReplacement()
	{
		return( this );
	}
	
	public static final EmptyReference EMPTY = new EmptyReference();
}

class EmptyReference
	extends Reference
{
	private static final long serialVersionUID = -5345429757003014274L;
	
	public EmptyReference()
	{
	}
	
	public Object getReplacement()
	{
		return( Reference.EMPTY );
	}
	
	public Object clone()
	{
		return( Reference.EMPTY );
	}
	
	public int hashCode()
	{
		return( 0 );
	}
	
	public boolean isLoaded()
	{
		return false;
	}

	public boolean equals( Object o )
	{
		return false;
	}

	public boolean contains( Atom a )
	{
		return false;
	}

	public Atom get()
	{
		return( null );
	}
	
	public Object getKey()
	{
		return( null );
	}
	
	public Atom getType( Class c )
	{
		return( null );
	}
	
	public boolean isValid()
	{
		return( false );
	}
}

/**
  *  A reference that is stored with a string id that is looked up
  *  at each get() operation.
  *
  *  This is useful for references that need to travel through transit
  *  atoms (references that need to change).
 */
class IndirectReference
	extends Reference
	implements Symbol
{
	String id;
	
	public IndirectReference( String path )
	{
		id = path;
	}
	
	public Atom get()
	{
		Search s = new Search( id, Key.instance() );
		
		return( (Atom) s.result );
	}
	
	public Atom getType( Class c )
	{
		Atom a = get();

		if( a == null || c.isInstance( a ) )
			return( a );
		else
			return( null );
	}
	
	public int hashCode()
	{
		return( id.hashCode() );
	}
	
	public Object getKey()
	{
		return( new SeperatedIdentifier( id ).id );
	}
	
	public String getName()
	{
		return( (String) getKey() );
	}
	
	public void setKey( Object k )
	{
		SeperatedIdentifier si = new SeperatedIdentifier( id );
		
		StringBuffer sb = new StringBuffer( si.location );
		
		if( si.property )
			sb.append( '.' );
		else
			sb.append( '/' );
		
		sb.append( k.toString() );
		
		id = sb.toString();
	}

	public boolean equals( Object o )
	{
		if( o instanceof IndirectReference )
			return( id.equalsIgnoreCase( ((IndirectReference)o).id ) );
		else if( o instanceof Reference )
			return( get().equals( ((Reference)o).get() ) );
		else
			return( false );
	}
	
	public boolean contains( Atom a )
	{
		return( get() == a );
	}

	public boolean isLoaded()
	{
		return( true );
	}
}

/**
  *  A reference that stores the integer index of this atom in the
  *  system registry.  This allows faster lookups than the string
  *  reference, but does not permit changing references to be stored.
  *
  *  (Such as those through a transit symbol match, such as #me)
 */
class DirectReference extends Reference
{
	private static final long serialVersionUID = -1786646569320699878L;
	final int index;
	final int timestamp;
	
	public DirectReference( int registry_index, int registry_timestamp )
	{
		index = registry_index;
		timestamp = registry_timestamp;
	}
	
	public DirectReference( Atom a )
	{
		index = a.index;
		timestamp = a.timestamp;
	}
	
	public Atom get()
	{
		return( Registry.instance.get( index, timestamp ) );
	}
	
	/*
	private final Atom.Supplemental getSupplemental()
	{
		return( (Atom.Supplemental) Registry.instance.getSupplemental( index, timestamp ) );
	}
	*/
	
	/*
	public Object getKey()
	{
		Atom.Supplemental o = (Atom.Supplemental) getSupplemental();
		
		if( o == null )
		{
			Registry.instance.updateSupplemental( index );
			o = getSupplemental();
		}
		
		return( o.getKey() );
	}
	
	public void setKey( Object k )
	{
		get().setKey( k );
	}
	
	public String getName()
	{
		Object o = getKey();
		if( o instanceof String )
			return( (String) o );
		else
			return( o.toString() );
	}
	*/
	
	public int getIndex()
	{
		return( index );
	}
	
	public int getTimestamp()
	{
		return( timestamp );
	}
	
	public Atom getType( Class c )
	{
		try
		{
			Atom a = get();
			
			if( a == null || c.isInstance( a ) )
				return( a );
			else
				return( null );
		}
		catch( OutOfDateReferenceException e )
		{
			return( null );
		}
	}
	
	public int hashCode()
	{
		return( timestamp );
	}
	
	public boolean isValid()
	{
		return( Registry.instance.isReferenceValid( index, timestamp ) );
	}
	
	public Object getReplacement()
	{
		try
		{
			Atom a = Registry.instance.getIfInDatabase( index, timestamp );
			if( a != null )
				return( a.getThis() );
			else
				return( this );
		}
		catch( OutOfDateReferenceException e )
		{
			Log.debug( this, "replacing reference " + toString() + " with empty" );
			return( Reference.EMPTY );
		}
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof DirectReference )
		{
			DirectReference dr = (DirectReference) o;
			return( (index == dr.index) && (timestamp == dr.timestamp) );
		}
		else if( o instanceof Reference )
		{
			Atom a = ((Reference)o).get();
			if( a != null )
				return( (a.index == index) && (a.timestamp == timestamp) );
			else
				return( false );
		}
		else
			return( false );
	}
	
	public boolean contains( Atom a )
	{
		return( (a.index == index) && (a.timestamp == timestamp) );
	}
	
	public boolean isLoaded()
	{
		return( Registry.instance.indexLoaded( index, timestamp ) );
	}
}

class NamedDirectReference
	extends DirectReference
	implements Symbol
{
	private static final long serialVersionUID = 6458100074905379530L;
	Object key;
	
	public NamedDirectReference( Atom a )
	{
		super( a.index, a.timestamp );
		key = a.getKey();
	}
	
	private NamedDirectReference( int index, int timestamp, Object key )
	{
		super( index, timestamp );
		this.key = key;
	}
	
	public Object getKey()
	{
		return( key );
	}

	public void setKey( Object k )
	{
		key = k;
	}
	
	public String getName()
	{
		Object o = getKey();
		if( o instanceof String )
			return( (String) o );
		else
			return( o.toString() );
	}
	
	public Object getReplacement()
	{
		Reference r = (Reference) super.getReplacement();
		
		if
		(
			(r != this) &&
		    (r instanceof NamedDirectReference) &&
			(key.equals( ((NamedDirectReference)r).key ))
		)
		{
			return( r );
		}
		
		return( this );
	}
	
	public String toString()
	{
		return( "NamedDirectReference to #" + index + ", " + key.toString() );
	}
}
