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

package key;

import key.collections.*;
import key.util.FilteredEnumeration;
import key.util.MultiEnumeration;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
  *  A container is capable of holding Atoms.
 */
public class Container
extends Atom
{
	private static final long serialVersionUID = 4938263978050622486L;
	
	public static final AtomicElement[] ELEMENTS =
	{
			//  boolean getReferencesOnly();
		AtomicElement.construct( Container.class, Boolean.class,
		      "referencesOnly",
			  AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			  "true if this container only holds references" ),
		
			//  int getCountContents();
		AtomicElement.construct( Container.class, Integer.TYPE,
		      "countContents",
			  AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			  "the number of things inside this container" ),
		
		AtomicElement.construct( Container.class, String.class,
		      "collectionType",
			  AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			  "the type of the collection" ),
		
			//  Integer limit;
		AtomicElement.construct( Container.class, Integer.class,
		      "limit",
			  AtomicElement.PUBLIC_FIELD,
			  "the maximum number of things that may be in this container" ),
		AtomicElement.construct( Container.class, String.class,
		      "constrainedType",
			  AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			  "the enforced type of items in this container" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	/**
	  * @see key.AtomicElement.PARENT_TYPE
	 */
	public static final int PARENT_TYPE = 0;
	
		//  proper container objects have rooms off them
		//  normal small rooms have exits
		//  I *guess* its possible to have both
	transient Collection contained;
	Reference joined = Reference.EMPTY;
	
	protected boolean reference;

		//  initially no limit (null)
	public Integer limit = null;
	
	protected Type constrainedType;
	
	public Container()
	{
		this( false );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Container( boolean ref )
	{
		totalContainers++;
		
		reference = ref;
		contained = new StringKeyCollection();
		constrainedType = null;
	}
	
	public Container( boolean ref, Object key, Collection initialCollection )
	{
		totalContainers++;
		
		setKey( key );
		reference = ref;
		contained = initialCollection;
		constrainedType = null;
	}
	
	public Container( Object key, Type constraint )
	{
		this( false );
		setKey( key );

		if( constraint != null )
			setConstraint( constraint );
	}
	
	private void writeObject( ObjectOutputStream oos ) throws IOException
	{
		removeInvalidReferences();
		oos.defaultWriteObject();
		writeContents( oos );
	}

	public String getCollectionType()
	{
		return( contained.getClass().getName() );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
			readContents( ois );
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		totalContainers++;
	}
	
	void setRecursiveOwner_imp( Atom newOwner )
	{
		super.setRecursiveOwner_imp( newOwner );
		
		if( !reference )
		{
			for( Enumeration e = elements(); e.hasMoreElements(); )
			{
				Atom a = (Atom) e.nextElement();
				
				if( a.isParent( this ) )
					a.setRecursiveOwner( newOwner );
			}
		}
	}
	
	/**
	  *  The argument to a container is the class to constrain
	  *  contained objects to
	 */
	public void argument( String args ) throws IllegalArgumentException
	{
		if( args.length() != 0 )
		{
			try
			{
				setConstraint( Type.forName( args ) );
			}
			catch( ClassNotFoundException e )
			{
				throw new IllegalArgumentException( "Could not find class '" + args + "' to constraint the container." );
			}
		}
	}
	
	public Type getConstraint()
	{
		return( constrainedType );
	}
	
	public String getConstrainedType()
	{
		if( constrainedType != null )
			return( constrainedType.getName() );
		else
			return( Key.nullString );
	}
	
	protected void setConstraint( Type c )
	{
		if( (contained.count() == 0) )
		{
			constrainedType = c;
		}
		else
			throw new UnexpectedResult( "trying to constrain a class with " + contained.count() + " elements already in it" );
	}
	
	public final boolean isReference()
	{
		return( reference );
	}
	
	public boolean canAdd()
	{
		if( limit != null && (contained.count() >= limit.intValue()) )
			return( false );
		
		return( true );
	}
	
	/**
	  *  This method is called after the permission checking has
	  *  been done to actually do the work of adding the atom.
	  *
	  *  If this is a reference container, the atom is never
	  *  actually loaded.
	 */
	protected void addInternal( Reference added ) throws BadKeyException, NonUniqueKeyException
	{
		Atom addedAtom = null;
		
			//  check the limit on this container hasn't been exceeded
		if( limit != null )
		{
			if( contained.count() >= limit.intValue() )
			{
				Player p = Player.getCurrent();
				if( !p.isBeyond() )
					throw new LimitExceededException( "limit of " + limit.toString() + " exceeded on " + getName() );
			}
		}
		
		addedAtom = added.get();
		
		if( addedAtom == null )
		{
			System.out.println( "null reference " + added.getClass().getName() + " passed to addInternal." );
		}
		
		if( constrainedType != null )
		{
			Type addedType = Type.typeOf( addedAtom );
			if( !addedType.isA( constrainedType ) )
				throw new TypeMismatchException( added.getName() + " is a " + addedType.getName() + ", which is not a " + constrainedType.getName() + ", the required class for " + getName() );
		}
		
		Container j = (Container) joined.get();
		if( j != null )
		{
			j.addInternal( added );
		}
		
		if( !reference )
			addedAtom.addParent( this, PARENT_TYPE );
		else
			addedAtom.addSymbolicParent( this );
		
		contained.link( (Symbol) added );
	}
	
	/**
	  *  Adds the supplied atom and sets its parent to this
	 */
	public void add( Atom added ) throws BadKeyException, NonUniqueKeyException
	{
		permissionList.check( addToAction );
		added.checkAdd( this );
		addInternal( added.getThis() );
	}
	
	public void alias( Atom toAlias )
	{
		permissionList.check( concealInAction );
		contained.conceal( (Symbol) toAlias.getThis() );
	}
	
	public void unalias( Atom toNormal )
	{
		permissionList.check( revealInAction );
		contained.reveal( (Symbol) toNormal.getThis() );
	}
	
		/** Used primarily in the 'Many' command. */
	public void rekey( Atom toRekey, AliasListKey newAlk )
	{
		removeInvalidReferences();
		
			//  remove the current keys
		AliasListKey alk;
		
		try
		{
			alk = (AliasListKey) toRekey.getKey();
		}
		catch( ClassCastException e )
		{
			alk = new AliasListKey();
			alk.setPrimary( toRekey.getKey().toString() );
			toRekey.setKey( alk );
			toRekey.regenReference();
			//throw new UnexpectedResult( "You need an aliaslist key atom in order to rekey." );
		}
		
		if( contained instanceof AliasCollection )
		{
			AliasCollection ac;
			
			ac = (AliasCollection) contained;
			
			ac.removeSecondaries( alk );
			
				//  rekey the atom
			alk.copySecondaries( newAlk );
			
			ac.addSecondaries( alk, (Symbol) toRekey.getThis() );
		}
		else if( contained instanceof ObjectCollection )
		{
				//  rekey the atom
			alk.copySecondaries( newAlk );
			
			((ObjectCollection)contained).rekey( toRekey.getThis() );
		}
		else
		{
			toRekey.setKey( newAlk );
			//throw new UnexpectedResult( "You need an aliasable container in order to rekey (" + contained.getClass().getName() + ": " + (contained instanceof ObjectCollection) );
		}
	}
	
	public final void sort()
	{
		permissionList.check( sortAction );
		removeInvalidReferences();
		contained.sort();
	}
	
	protected void delete()
	{
		super.delete();
		
		if( !reference )
		{
			try
			{
				for( Enumeration e = elements(); e.hasMoreElements(); )
				{
					Atom a = (Atom)e.nextElement();
					a.removeParent( this );
					Registry.instance.deleteIfTemporary( a );
				}
			}
			catch( NoSuchElementException e )
			{
				//  this is okay - it happens when we dispose atoms that are
				//  already disposed - during this scan.  this exception is
				//  just because we ran out of things to delete in this 
				//  circumstance.
			}
		}
	}
	
	void stopBeingTemporary()
	{
		super.stopBeingTemporary();
		
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Atom a = (Atom) e.nextElement();
			
			a.stopBeingTemporary();
		}
	}

	public final void checkRemoveFromPermission( Reference r )
	{
			//  this is a slightly more complicated permissions
			//  model.  we can remove if we've got access to remove
			//  from this group, OR if we've got access to move the
			//  player in question.
		try
		{
			permissionList.check( removeFromAction );
		}
		catch( AccessViolationException e )
		{
			Atom removed = r.get();
			if( !reference )
				removed.checkPermissionList( Atom.deleteAction );
			else
			{
					//  we can also remove from a container if
					//  we own the atom being removed - we always
					//  have rights over our own things.
				Player p = Player.getCurrent();
				
				//System.out.println( "checking to see if we own us: p = " + p.getId() + " removed = " + removed.getId() );
				
					//  this way is easier to read, rather than
					//  negating the expression.
				if( p != null && removed.isOwner( p ) )
				{
					; //  fall through
					//System.out.println( "remove okay anyway" );
				}
				else
					throw e;
			}
		}
	}
	
	/**
	  * removes the atom.  if this is its parent container, specifies that the
	  * atom should be disposed of (deleted)
	 */
	public void remove( Atom removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		Reference r = removed.getThis();
		checkRemoveFromPermission( r );
		removeInternal( r );
	}
	
	protected void removeInternal( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		if( reference )
		{
			noSideEffectRemove( removed );
			
				//  this is a little hacky - it should
				//  really just get() and "removeParent"
				//  like the other one, but that would
				//  require loading all the time, which
				//  wouldn't be much fun.  besides, what
				//  are reference containers for, if not
				//  efficiency hacks like this.
			if( removed.isLoaded() )
				removed.get().removeSymbolicParent( this );
		}
		else
		{
			Atom a = removed.get();
			
				//  will call 'noSideEffectRemove' to remove 
			a.removeParent( this );
		}
	}
	
	protected void noSideEffectRemove( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		contained.unlink( (Symbol) removed );
		
			//  this will occasionally throw an exception - generally for the joined
			//  classes - I figure its our responsibility to remove the element
			//  if it gets removed from here.  If you're going to step through
			//  reverseReferences and unlink them all, and then unlink the atom
			//  from its parent, unlink it from its parent first, since that will
			//  tidy up and make it one less reverse reference to deal with.
		Container j = (Container) joined.get();
		if( j != null )
		{
			try
			{
				j.removeInternal( removed );
			}
			catch( NonUniqueKeyException e )
			{
				Log.debug( this, e.toString() + " while removing from joined" );
			}
			catch( NoSuchElementException e )
			{
					//  If you get:
					//  key.Residents - java.util.NoSuchElementException: 
					//      joe while removing from joined
					//
					//  or similar - it just means the person was a
					//  newbie when the server was shut down.
				Log.debug( this, e.toString() + " while removing from joined" );
			}
			catch( BadKeyException e )
			{
				Log.debug( this, e.toString() + " while removing from joined" );
			}
		}
	}
	
	public void removeByNumber( int c ) throws NonUniqueKeyException,BadKeyException
	{
			//  this is a slightly more complicated permissions
			//  model.  we can remove if we've got access to remove
			//  from this group, OR if we've got access to move the
			//  player in question.
		try
		{
			permissionList.check( removeFromAction );
		}
		catch( AccessViolationException e )
		{
			if( !reference )
			{
				Reference t = (Reference) contained.getElementAt( c );
				Atom a = t.get();
				a.getPermissionList().check( Atom.deleteAction );
			}
			else
				throw e;
		}
		
			//  TODO: this code below has been added & not
			//  tested to ensure it was really required.
		if( !reference )
		{
			Reference t = (Reference) contained.getElementAt( c );
			Atom a = t.get();
			a.removeParent( this );
		}
		else
			contained.removeElementAt( c );
	}
	
	public final Symbol getElementAt( int n )
	{
		Reference r = (Reference) contained.getElementAt( n );
		
		if( r != null )
			return( getReference( r ) );
		else
			return( null );
	}
	
	public final Symbol getElementAt( String p )
	{
		try
		{
			return( getElementAt( Integer.parseInt( p ) ) );
		}
		catch( NumberFormatException e )
		{
			throw new InvalidSearchException( "a number must follow the @ symbol" );
		}
	}
	
		//  used to be limit( int )
	public void setLimit( int amount )
	{
		limit = new Integer( amount );
	}
	
	public final Object getTrieFor( String m )
	{
		removeInvalidReferences();
		
		return( contained.getTrieFor( m ) );
	}
	
	public void removeInvalidReferences()
	{
		for( Enumeration e = contained.elements(); e.hasMoreElements(); )
		{
			Reference r = (Reference) e.nextElement();
			
			if( !r.isValid() )
			{
				try
				{
					noSideEffectRemove( r );
				}
				catch( Exception x )
				{
					Log.error( "during removeInvalidReferences()", x );
				}
			}
		}
	}
	
	/**
	  *  Adds all the symbols in this
	  *  container to the provided Container
	  *
	  *  This container retains a reference to
	  *  its elements, but full ownership (the
	  *  elements parents) is given to the new
	  *  container.  (ie, the parents of all
	  *  elements in this container are passed
	  *  to the parent container)
	 */
	public void addTopLevel( Container addTo )
	{
		joined = addTo.getThis();
		
		for( Enumeration e = referenceElements(); e.hasMoreElements(); )
		{
			try
			{
				Reference l;
				l = (Reference) e.nextElement();
				addTo.addInternal( l );
			}
			catch( BadKeyException t )
			{
				Log.error( "invalid key while trying to add to top level", t );
			}
			catch( NonUniqueKeyException t )
			{
				Log.error( "non unique key while trying to add to top level", t );
			}
		}
	}
	
	/**
	  *  This function returns Tags?
	 */
	public final Enumeration elements()
	{
		return( new FilteredEnumeration( 
				contained.elements(),
				new ReferenceEnumeratorFilter(
					new ReferenceEnumeratorFilter.EnumeratedThing()
					{
						public void noSideEffectRemove( Reference r )
							throws NonUniqueKeyException, java.util.NoSuchElementException,BadKeyException
						{
							Container.this.noSideEffectRemove( r );
						}
					}
				, true ) ) );
	}
	
	public final Enumeration referenceElements()
	{
		return( new FilteredEnumeration( 
				contained.elements(),
				new ReferenceEnumeratorFilter(
					new ReferenceEnumeratorFilter.EnumeratedThing()
					{
						public void noSideEffectRemove( Reference r )
							throws NonUniqueKeyException, java.util.NoSuchElementException,BadKeyException
						{
							Container.this.noSideEffectRemove( r );
						}
					}
				, false ) ) );
	}
	
	public void clearAllElements()
	{
		permissionList.check( removeFromAction );
		
		for( Enumeration e = referenceElements(); e.hasMoreElements(); )
		{
			Reference t = (Reference) e.nextElement();
			
			try
			{
				removeInternal( t );
			}
			catch( NonUniqueKeyException except )
			{
				throw new UnexpectedResult( except.toString() );
			}
			catch( BadKeyException except )
			{
				throw new UnexpectedResult( except.toString() );
			}
		}
		
		contained.deallocate();
	}
	
	public void search( Search s ) throws InvalidSearchException
	{
		StringTokenizer st = s.st;
		
		if( !st.hasMoreTokens() )
			return;
		
			//  s.result is already == this
		s.lastAtom = this;
		
		String p = st.nextToken();
		
		//System.out.println( "container::search recvd '" + p + "'" );
		
		Reference o = null;
		
		switch( p.charAt( 0 ) )
		{
			case '.':
				if( !st.hasMoreElements() )
				{
					s.result = this;
					return;
				}
				
				p = st.nextToken();
				//System.out.println( "(.)               recvd '" + p + "'" );
				
				{
					AtomicElement ae = structure.getElement( p );
					
					if( ae == null )
						throw new InvalidSearchException( "no such property '" + p + "' in " + getId() );
					
					s.result = ae;
				}
				
				return;
			case '@':
					//  if it has a '@' in it, get elementAt
				if( !st.hasMoreElements() )
					throw new InvalidSearchException( "search string cannot end with a @" );
				
				p = st.nextToken();
				
				try
				{
					o = (Reference) contained.getElementAt( Integer.parseInt( p ) );
				}
				catch( NumberFormatException e )
				{
					throw new InvalidSearchException( "a number must follow the @ symbol" );
				}
				
				break;
				
			case '/':
					//  if it ends with a '/', ignore the trailing slash
				if( !st.hasMoreElements() )
					return;
				
				p = st.nextToken();

				if( p.charAt(0) == '.' )
				{
					if( !st.hasMoreTokens() )
						return;  //  ignore it, as above
					
						//  sometimes people write 'myname/.property',
						//  and this is permitted, but we need a hack
						//  for it.
					p = st.nextToken();
					
					{
						AtomicElement ae = structure.getElement( p );
						
						if( ae == null )
							throw new InvalidSearchException( "no such property '" + p + "' in " + getId() );
						
						s.result = ae;
					}
					return;
				}
				
				//  deliberate fall through
				
			default:
				
				if( p.charAt( 0 ) == Key.nullEntry )
				{
						//  special case for matching an element without a name
					p = "";
				}
				
					//  look up the atom here
				o = (Reference) contained.getExact( p );
				
				break;
		}
		
		if( o == null )
			throw new InvalidSearchException( "no such element '" + p + "' in " + getId() );
		if( !o.isValid() )
		{
			try
			{
				noSideEffectRemove( o );
			}
			catch( Exception x )
			{
				throw new UnexpectedResult( x );
			}
			
			throw new InvalidSearchException( "no such element (invalid) '" + p + "' in " + getId() );
		}
		
		s.result = o;
	}
	
	protected void readContents( ObjectInputStream from ) throws IOException
	{
		try
		{
			contained = (Collection) from.readObject(); 
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( "Incompatible change: " + e.toString() + " during container::readContents" );
		}
	}
	
	protected void writeContents( ObjectOutputStream to ) throws IOException
	{
		to.writeObject( contained ); 
	}
	
	protected String getSecondLevelId( int c )
	{
		Atom p = getParent();
		
		if( p == null || getParentType() == PARENT_TYPE )
			return( getName() + ((c==0)?'/':':') );
		else
			return( p.getSecondLevelId( c + 1 ) );
	}
	
    public final int count()
	{
		return( contained.count() );
	}
	
	public final boolean getReferencesOnly()
	{
		return( reference );
	}

	public final int getCountContents()
	{
		return( count() );
	}
	
		//  if this was false, the container would
		//  use a . match if there aren't any leading
		//  tokens on a getSymbol
	protected boolean assumedContents = true;
	
	public final Object getReferenceElement( String s )
	{
		Object o = contained.get( s );
		if( o instanceof Reference )
		{
			Reference r = (Reference) o;
			
			if( r == null || r.isValid() )
				return( r );
			else
			{
				try
				{
					noSideEffectRemove( r );
				}
				catch( Exception x )
				{
					throw new UnexpectedResult( x );
				}
				
				return( null );
			}
		}
		else
			return( o );
	}
	
	public final Object getElement( String s )
	{
		try
		{
			if( s.charAt( 0 ) == '@' )
				return( getElementAt( s.substring( 1 ) ) );
		}
		catch( StringIndexOutOfBoundsException e )
		{
		}
		
		Object o = contained.get( s );
		
		if( o instanceof Reference )
		{
			Reference r = (Reference) o;
			
			if( r != null )
				return( getReference( r ) );
			else
				return( null );
		}
		else
			return( o );
	}
	
	private final Atom getReference( Reference r )
	{
		try
		{
			return( r.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			System.out.println( "CONTAINER: out of date reference to " + r.getName() + " removed" );
			try
			{
				noSideEffectRemove( r );
			}
			catch( Exception x )
			{
				throw new UnexpectedResult( x );
			}
			
			return( null );
		}
	}
	
	public final Object getExactElement( String s )
	{
		try
		{
			if( s.charAt( 0 ) == '@' )
				return( getElementAt( s.substring( 1 ) ) );
		}
		catch( StringIndexOutOfBoundsException e )
		{
		}
		
		Reference o = (Reference) contained.getExact( s );
		
		if( o != null )
			return( getReference( o ) );
		else
			return( null );
	}

	public void noLongerReferencing( Atom a )
	{
		if( a.getParentType() == PARENT_TYPE )
		{
			try
			{
				noSideEffectRemove( a.getThis() );
			}
			catch( NonUniqueKeyException e )
			{
				Log.error( "noLongerReferencing called for an atom that we're not referencing", e );
			}
			catch( BadKeyException e )
			{
				Log.error( "noLongerReferencing called for an atom that we're not referencing", e );
			}
		}
		else
			super.noLongerReferencing( a );
	}
	
	public final boolean contains( Atom a )
	{
		return( contained.contains( (Symbol) a.getThis() ) );
	}

	public final boolean containsReference( Reference r )
	{
		return( contained.contains( r ) );
	}

	//---  actions  ---//

	protected static StringKeyCollection staticActions;

		/**  is allowed to add objects to this container */
	public static Action addToAction; 

		/**  is allowed to remove objects from this container */
	public static Action removeFromAction;

		/**  can conceal atoms in this container */
	public static Action concealInAction;

		/**  can reveal atoms in this container */
	public static Action revealInAction;

		/**  can sort atoms in this container */
	public static Action sortAction;
	
	static
	{
		staticActions = new StringKeyCollection();
		
		addToAction = newAction( Container.class, staticActions, "addTo", true, true );
		removeFromAction = newAction( Container.class, staticActions, "removeFrom", true, true );
		concealInAction = newAction( Container.class, staticActions, "concealIn", true, true );
		revealInAction = newAction( Container.class, staticActions, "revealIn", true, true );
		sortAction = newAction( Container.class, staticActions, "sort", true, true );
	}
	
	public Enumeration getActions()
	{
		return( new MultiEnumeration( staticActions.elements(), super.getActions() ) );
	}
	
	public boolean containsAction( Action a )
	{
		return( staticActions.contains( a ) || super.containsAction( a ) );
	}
	
	/**
	  *  Returns the action corresponding to the
	  *  supplied name.  This routine will need to
	  *  be overriden by sub-classes using actions.
	 */
	public Action getAction( String name )
	{
		Action a = (Action) staticActions.get( name );

		if( a == null )
			return( super.getAction( name ) );
		else
			return( a );
	}
	
	private static int totalContainers;
	
	public static int getTotalContainers()
	{
		return( totalContainers );
	}
	
	protected void finalize() throws Throwable
	{
		super.finalize();
		
		totalContainers--;
	}
}
