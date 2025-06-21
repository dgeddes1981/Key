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

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;

/**
  *  The fundamental (large) building block of Key.  Most system
  *  accessable objects are based on the Atom.
  * <P>
  *  The code for the Atom is broken up into several sections:
  * <UL>
  * <LI>Structure: dealing with the makeup of the atom, in particular
  *     it's properties.
  * <LI>Owner: dealing with the player who owns this object
  * <LI>References: the other Atoms that are using this one
  * <LI>Symbol: symbolic/reflective matching capabilities
  * <LI>Parent: the container that holds this Atom
  * <LI>Management: creation/destruction/load/unload
  * <LI>Tag: handling of Atom tags
  * <LI>Interaction: splash event handling, help & information
  * <LI>Action: static action information
  * <LI>Statistics: general static statistical information
  * </UL>
  * <HR>
  * <B>Rules for key classes:</B>
  * <BR>
  * <I>All classes in the key heirarchy should follow these rules
  *    in order for user-loaded code to remain secure</I>
  * <P>
  *  All fields shall be either protected or private.  Protected
  *  fields are essentially public through the symbol mechanism,
  *  which is capable of enforcing the additional permissions of
  *  the key system when it is used.  This means that code within
  *  the key package may access it directly, but user-loaded code
  *  needs to use public methods (below), or use the symbolic system.
  * <P>
  *  Methods may be public iff they explicitly call the permission
  *  checking system.  Protected methods do not have this restriction.
 */
public class Atom implements Searchable,Symbol,Serializable,key.sql.SQLStorable
{
	private static final long serialVersionUID = -7706021129639899395L;
	
	public static final int MAX_KEY_LENGTH = 16;
	
	static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( Atom.class, String.class, "name",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the name of an atom is a unique identifier within its container" ),
		
		AtomicElement.construct( Atom.class, String.class, "keyType",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the type of key this atom has" ),
		
			//  Atom getParent();
		AtomicElement.construct( Atom.class, Atom.class, "parent", ".",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the container (or atomic property) which holds this atom" ),
		
			//  Atom getOwner();
		AtomicElement.construct( Atom.class, Atom.class, "owner",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the atom which owns this atom.  generally a player or null" ),
		
			//  String getId();
		AtomicElement.construct( Atom.class, String.class, "id",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the fully qualified path of this atom" ),
		
			//  int getIndex();
		AtomicElement.construct( Atom.class, Integer.TYPE, "index",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the index of this atom" ),
		
			//  int getTimestamp();
		AtomicElement.construct( Atom.class, Integer.TYPE, "timestamp",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the timestamp of this atom" ),
		
			//  String getStorageType();
		AtomicElement.construct( Atom.class, String.class, "storageType",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the way this atom is stored" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( null, ELEMENTS );
	
	transient AtomicStructure structure;
	
	protected PermissionList permissionList;
	
	private transient Reference referenceToMe;
	
	Reference owner = Reference.EMPTY;
	private Object tag;
	private Reference parent;
	private int parent_type;
	//protected transient Vector reverseReferences;
	
		//  these two are stored explicitly
	transient int index;
	transient int timestamp;
	
		/** referenceCount for implicit references */
	private transient int referenceCount;
	
	/**
	  *  Made available for use by objects and their subclasses.
	 */
	protected transient byte state;
	
	protected Atom()
	{
		index = Registry.instance.allocateTemporaryIndex( this );
		
			//  this line is standard for almost all atoms;
		init();
		
			// deliberately not using setTag() - doing so
			// would break the one reassignment thats
			// permitted (for a loadFrom)
			//
			// This problem is caused because when an Atom
			// is constructed, it _must_ be ready to use,
			// (ie, having a tag), but it also must be loadable
			// (ie, acknowledging that the tag might already be
			// in the system).  On top of this, we want to prevent
			// anyone assigning the tag in any circumstances *other*
			// than these.
		//tag = new Tag( this );
		//initialTag = true;
		
		assignInitialOwner();
		
		permissionList = new PermissionList( this );
		parent = Reference.EMPTY;
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		index = ois.readInt();
		timestamp = ois.readInt();
		
		Registry.instance.makeTemporarilyAvailable( this );
		
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		//initialTag = true;
		//Registry.instance.registerIndex( this, index );
		init();
	}
	
	private void writeObject( ObjectOutputStream to ) throws IOException
	{
		to.writeInt( index );
		to.writeInt( timestamp );
		
		to.defaultWriteObject();
	}
	
	/**
	  *  Called after all constructors and everything has been set up -
	  *  but not after the atom is loaded, only created.
	 */
	protected void constructed()
	{
	}
	
	public Object createSupplemental()
	{
		return( null );
	}

	/**
	  *  Called after all initialisation has been done after the
	  *  atom has been loaded.  It is important not to try and use
	  *  readObject(), since that is called before sub-classes
	  *  have been initialised.
	 */
	public void loaded()
	{
	}
	
	void setIndex( int i, int ts )
	{
		index = i;
		timestamp = ts;
	}
	
	public final int getIndex()
	{
		return( index );
	}
	
	public final int getTimestamp()
	{
		return( timestamp );
	}
	
	public String getStorageType()
	{
		return( Registry.instance.getStorageType( index, timestamp ) );
	}
	
	/**
	  *  Subclasses should override this method to return their
	  *  own derived structure.
	 */
	protected AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	/**
	  *  This method returns a reference to this Atom, which
	  *  prevents the continual creation of new references.
	  *
	  *  This created reference will always be named, that is
	  *  a Symbol.
	  *
	  *  This will not be the only reference to this atom, there
	  *  may be many, so use .equals to compare them, not ==
	 */
	public final Reference getThis()
	{
		return( referenceToMe );
	}
	
	/**
	  *  Private to prevent it being overridden by other init()
	  *  methods.
	 */
	private void init()
	{
		totalAtoms++;
		structure = getDeclaredStructure();
		
		referenceToMe = Reference.to( this, true );
	}

	public final void regenReference()
	{
		referenceToMe = Reference.to( this, true );
	}
	
	void stopBeingTemporary()
	{
		Registry.instance.upgradeTemporaryIndex( index, timestamp );
		
		Factory.nonTemporaryFields( this );
	}
	
//---  STRUCTURE  ----------------------------------------------------------
//
//  This section of an Atom is reserved for those methods dealing with
//  the structure of the Atom.  Included are general routines to look up
//  and change the various properties.
//
	
	/**
	  *  Returns an enumeration of all of the properties on this
	  *  Atom (the Atom's structure).  Used by the 'Dump' command,
	  *  for instance.  Classes within the atom's heirarchy may
	  *  use the protected 'structure' variable instead.
	  */
	public final AtomicStructure getStructure()
	{
		return( structure );
	}

//---  OWNER  --------------------------------------------------------------
//
//  This section of an Atom contains those methods dealing with the Atom's
//  owner.
//
	/**
	  *  Sets the owner to a player (if this is being
	  *  executed as a player), or as null, otherwise
	 */
	void assignInitialOwner()
	{
		Player p = Player.getCurrent();
		
		if( p != null )
			owner = p.getThis();
		else
			owner = Reference.EMPTY;
	}
	
	/**
	  *  A public accessor to a method that can only be overridden
	  *  in the local (trusted) package.
	 */
	public final void setRecursiveOwner( Atom newOwner )
	{
		setRecursiveOwner_imp( newOwner );
	}
	
	void setRecursiveOwner_imp( Atom newOwner )
	{
			//  anyone can setOwner an
			//  atom before its been added
			//  to a parent
		if( getParent() != null )
			permissionList.check( possessAction );
		
		if( newOwner != null )
		{
			owner = Reference.to( newOwner );
			
				//  also set the owner for all final properties
				//  to this owner.
			Factory.syncOwnerRecursiveFields( this );
		}
		else
			owner = Reference.EMPTY;
	}
	
	public synchronized final void setOwner( Atom newOwner )
	{
			//  anyone can setOwner an
			//  atom before its been added
			//  to a parent
		if( getParent() != null )
			permissionList.check( possessAction );
		
		if( newOwner != null )
		{
			owner = Reference.to( newOwner );
			
				//  also set the owner for all final properties
				//  to this owner.
			Factory.syncOwnerFields( this );
		}
		else
			owner = Reference.EMPTY;
	}
	
	/**
	  *  Returns true if potential owns this atom
	  *  (either directly or indirectly)
	 */
	public final boolean isOwner( Atom potential )
	{
		Atom ourOwner = getOwner();
		
		if( ourOwner == null )
			return false;
		else if( potential == ourOwner )
			return true;
		else if( ourOwner == this )
			return false;
		else
			return( ourOwner.isOwner( potential ) );
	}
	
	public final Atom getOwner()
	{
		try
		{
			return( owner.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			owner = Reference.EMPTY;
			return( null );
		}
	}
	
	public final Reference getOwnerReference()
	{
		return( owner );
	}
	
//---  SYMBOL  -------------------------------------------------------------
//
//  This section of an Atom contains those methods permitting an Atom to
//  be accessed symbolically.
//
	public final Object getKey()
	{
		return( tag );
	}
	
	public final void setKey( Object newkey )
	{
		setKey_imp( newkey );
	}
	
	void setKey_imp( Object newkey )
	{
		if( newkey.toString().indexOf( '^' ) != -1 )
			throw new LimitationException( "Colour codes not permitted in object id's" );
		
		if( newkey instanceof String && ((String)newkey).length() > MAX_KEY_LENGTH )
		{
			Log.error( "key length too long: " + ((String)newkey) );
			throw new LimitExceededException( "Key length too long. (>" + MAX_KEY_LENGTH + " characters): '" + ((String) newkey) + "'" );
		}
		
		permissionList.check( modifyAction );
		
		tag = newkey;
		((Symbol)referenceToMe).setKey( newkey );
		//((Supplemental)Registry.instance.getSupplemental( index, timestamp )).tag = newkey;
	}
	
	public final void replaceKey( AliasListKey alk )
	{
		alk.setPrimary( tag.toString() );
		tag = alk;
		regenReference();
	}
	
	/**
	  *  Returns a printable representation of the name of this
	  *  atom.  It can contain funky characters not normally
	  *  present (for instance, non-resident players are preceeded
	  *  with 'VISITOR:')
	 */
	public String getName()
	{
		if( tag != null )
			return( tag.toString() );
		else
			return( "" );
	}
	
	public final String getKeyType()
	{
		if( tag != null )
			return( tag.getClass().getName() );
		else
			return( Key.nullString );
	}
	
	/**
	  *  Gets the value of a property by the property name.  This is
	  *  an efficient function that won't accept more advanced
	  *  paths - the exact property name must be given.
	  * <P>
	  *  For instance, 'name' is fine, but 'owner.name' will fail, since
	  *  this is a path rather than a property name.
	  *
	  * @return the value of the property 'id'
	  * @throws NoSuchPropertyException if that property does not exist
	 */
	public final Object getProperty( String id ) throws NoSuchPropertyException
	{
		AtomicElement ae = structure.getElement( id );
		
		if( ae != null )
		{
			try
			{
				return( ae.getValue( this ) );
			}
			catch( IllegalAccessException e )
			{
				throw new NoSuchPropertyException( e.toString() );
			}
			catch( IllegalArgumentException e )
			{
				throw new UnexpectedResult( e.toString() + " during getProperty( " + id + " );" );
			}
			catch( NullPointerException e )
			{
				throw new UnexpectedResult( e.toString() + " during getProperty( " + id + " );" );
			}
		}
		else
			throw new NoSuchPropertyException( "no property '" + id + "' in " + getName() + ":" + Type.typeOf( this ).getName() );
	}
	
	/**
	  *  Sets the value of a property by property name.
	  *
	  *  Similar to getProperty, this function will only match exact
	  *  property names, as opposed to being able to match pathnames.
	  *
	  *  This function also performs checks to ensure that the caller
	  *  is permitted to modify this Atom.
	  *
	  * @param id the name of the property
	  * @param value the value to set it to.
	  * @throws NoSuchPropertyException if the property does not exist
	  * @throws TypeMismatchException if the value's type does not match
	  *         the type for the property
	 */
	public final void setProperty( String id, Object value ) throws NoSuchPropertyException,TypeMismatchException
	{
		permissionList.check( modifyAction );
		
		AtomicElement ae = structure.getElement( id );
		
		if( ae != null )
		{
			try
			{
				ae.setValue( this, value, null );
			}
			catch( IllegalAccessException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
		}
		else
			throw new NoSuchPropertyException( "no property '" + id + "' in " + getName() + ":" + Type.typeOf( this ).getName() );
	}
	
	public void search( Search s ) throws InvalidSearchException
	{
		StringTokenizer st = s.st;
		
		if( !st.hasMoreTokens() )
			return;
		
			//  s.result is already == this
		s.lastAtom = this;
		
		String p = st.nextToken();
		
		switch( p.charAt( 0 ) )
		{
			case '.':
				if( !st.hasMoreElements() )
				{
					s.result = this;
					return;
				}
				else
					p = st.nextToken();
				
				break;
			case '/':
				if( !st.hasMoreElements() )
				{
					s.result = this;
					return;
				}
				else
				{
					String n = st.nextToken();
					
						//  they can't write 'myname/something' if
						//  we're not a container, although we'll
						//  let them get away with 'myname/.property'.
					if( n.charAt( 0 ) == '.' )
					{
							//  duplicate the code above,
							//  but don't loop it since if someone
							//  writes "///////////." they're obviously
							//  deranged ;)
						if( st.hasMoreElements() )
							p = st.nextToken();
						else
						{
							s.result = this;
							return;
						}
					}
					else
						throw new InvalidSearchException( getId() + " is not a container" );
				}
		}
		
			//  look up the element here
		AtomicElement ae = structure.getElement( p );
		
		if( ae == null )
			throw new InvalidSearchException( "no such property '" + p + "' in " + getId() );
		
		s.result = ae;
	}
	
//---  PARENT  -------------------------------------------------------------
//
//  This section of an Atom contains those methods dealing with the Atom's
//  parent container.
//
	/**
	  *  This routine not only sets the parent, but ensures
	  *  that this atom is removed from its previous parent,
	  *  as well.  (after all, there can only be one parent)
	  *
	  *  TODO: perhaps make this private, or obsolete altogether, 
	  *  with the new addParent()
	 */
	synchronized final void setParent( Atom newParent, int type )
	{
			//  META: no permissions on setParent?
		/* META
		if( parent != null )
		{
			if( !(parent instanceof AtomicProperty && ((Atom)((AtomicProperty)parent).getParent()).disposing) )
			{
				if( newParent == null )
					permissionList.check( deleteAction );
				else
					permissionList.check( moveAction );
			}
		}
		*/
		
		try
		{
			Atom p = parent.get();
			if( p != null )
				p.noLongerReferencing( this );
		}
		catch( OutOfDateReferenceException e )
		{
				//  doesn't matter, we're wiping it anyway
			parent = Reference.EMPTY;
		}
		
		if( newParent != null )
		{
			Reference r = newParent.getThis();
			
			if( r.equals( parent ) )
			{
				if( newParent != null )
					throw new UnexpectedResult( "setParent with our parent called" );
				return;
			}
			
			parent = r;
			parent_type = type;
			
				//  check the parent's storage type & copy that.
			switch( Registry.instance.getStorageTypeIndex( parent.getIndex(), parent.getTimestamp() ) )
			{
				case Registry.STORAGE_LOADED:
				case Registry.STORAGE_UNLOADED:
					try
					{
							//  might not be necessary to sync
							//  straight away, but blah.
						sync();
					}
					catch( IOException e )
					{
						e.printStackTrace( System.out );
					}
					break;
				case Registry.STORAGE_DB:
					stopBeingTemporary();
					break;
				case Registry.STORAGE_TEMPORARY:
					Registry.instance.makeTemporary( this );
					break;
				default:
					Log.error( "invalid container load status on add" );
					break;
			}
		}
		else
		{
			resetParentToNull();
		}
	}

	/**
	  *  Used by setParent( null ) and getParent() when parent
	  *  points to an invalid reference
	 */
	private void resetParentToNull()
	{
		parent = Reference.EMPTY;
		Registry.instance.makeTemporary( this );
	}
	
	/**
	  *  Called on an Atom to erase it
	 */
	public final void dispose()
	{
		permissionList.check( deleteAction );
		
			//  atom being erased
		Registry.instance.delete( this );
	}
	
	/**
	  *  Called by registry.delete to clean up this atom
	 */
	void delete()
	{
			//  delete parts: will call delete() on each
			//  atomic part.
		Factory.partDeleteFields( this );
	}
	
	void noLongerReferencing( Atom a )
	{
			//  the name of the atom is the name of the property -
			//  we can look it up based on this
		AtomicElement ae = structure.getElement( a.getName() );
		
		if( ae != null )
		{
			try
			{
				ae.setValue( this, null, this );
			}
			catch( Exception e )
			{
				System.out.println( e.toString() + " during Atom::noLongerReferencing" );
			}
		}
		else
			System.out.println( "FAIL: noLongerReferencing " + a.getName() + " from " + getName() + ": no field of that name" );
	}
	
	public final Atom getParent()
	{
		return( parent.get() );
	}

	/**
	  *  Determines if a specified atom is the parent of this
	  *  atom.  This is more efficient than this.getParent() == a,
	  *  as it does not load the parent from disk if it isn't
	  *  in memory.
	 */
	public final boolean isParent( Atom a )
	{
		return( a.getThis().equals( parent ) );
	}
	
	public final boolean hasParent()
	{
		return( parent.isValid() );
	}
	
	public final void removeParent( Atom old_parent )
	{
		if( isParent( old_parent ) )
			setParent( null, AtomicElement.PARENT_TYPE );
		else if( old_parent instanceof Container )  //  this if() probably not necessary
		{
			old_parent.noLongerReferencing( this );
			
			removeSymbolicParent( (Container) old_parent );
		}
		else
			Log.debug( this, "atom " + old_parent.toString() + " passed into RemoveParent - unexpected" );
	}
	
	public final void addParent( Atom new_parent, int parent_type )
	{
		if( !parent.isValid() )
		{
				//  set this parent to be our
				//  real parent
			setParent( new_parent, parent_type );
			
		}
		else if( !isParent( new_parent ) && new_parent instanceof Container ) //  final instanceof probably not required
			addSymbolicParent( (Container) new_parent );
	}
	
	public final int getParentType()
	{
		return( parent_type );
	}

//---  MANAGEMENT  ---------------------------------------------------------
//
//  This section of an Atom contains methods used when loading Atoms in
//  and out of memory, as well as the creation and destruction of them.
//
	/**
	  *  This method should be overridden if
	  *  this command takes arguments.
	 */
	public void argument( String args ) throws InvalidArgumentException
	{
		throw new InvalidArgumentException( "no arguments to type '" + Type.typeFor( getClass() ).getName() + "'" );
	}
	
	boolean canSwap()
	{
		return( true );
	}
	
	/**
	  *  If this is called for a non-distinct atom, it makes this
	  *  atom distinct.  If called for a distinct atom, it will
	  *  do a hard-write to disk.
	 */
	public void sync() throws IOException
	{
		Registry r = Registry.instance;
		
		if( r.getStorageTypeIndex( index, timestamp ) == Registry.STORAGE_LOADED )
			r.write( index, timestamp );
		else
			r.syncDistinct( index, timestamp );
	}
	
	/**
	  *  This method is called to notify an Atom that it is being
	  *  swapped out of memory.  This method should clear any
	  *  transient information (such as references from Scapes
	  *  to this player, or disconnecting the player).  There is
	  *  no need for this method to attempt to sync the atom,
	  *  this will be done automatically.
	 */
	void clearTransient()
	{
	}
	
	/**
	  *  Decrements implicit references
	 */
	final void prepareForSwap()
	{
		//System.out.println( "decrementing implicit refs on '" + getKey() + "'..." );
		
		Factory.decrementImplicitReferenceCounts( this );
	}
	
	boolean willbeLockedAfterDecrement()
	{
			//  META: make this much more advanced,
			//  scan through implicit references and
			//  try to decrement those, as well...
		if( referenceCount <= 0 )
			return false;
		
		return true;
	}
	
	/**
	  *  Used to lock the atom into memory by the implicit referencing
	  *  system.  The from parameter exists in case we want
	  *  accountability later.
	 */
	final void addReference( Atom from )
	{
		referenceCount++;
		//System.out.println( "added reference in " + getKey() + " from " + from.getKey() + " (now " + referenceCount + ")" );
	}
	
	/**
	  *  Used to lock the atom into memory by the implicit referencing
	  *  system.  The from parameter exists in case we want
	  *  accountability later.
	 */
	final void removeReference( Atom from )
	{
		if( referenceCount > 0 )
			referenceCount--;
		
		//System.out.println( "removed reference in " + getKey() + " from " + from.getKey() + " (now " + referenceCount + ")" );
		
		if( referenceCount <= 0 )
		{
				//  trigger a swapout of dependant objects
			Registry.instance.swapout( this );
		}
	}
	
	/**
	  *  This is a notification routine that is called when a reference
	  *  container adds this atom to it.  (A non-reference container
	  *  calls addParent).  The default implementation does nothing,
	  *  but some atoms (such as Rank or Player) may be interested.
	 */
	void addSymbolicParent( Container c )
	{
		//System.out.println( getKey() + ".addSymbolicParent( " + c.getKey() + " )" );
	}
	
	/**
	  *  This is a notification routine that is called when a reference
	  *  container removes this atom from it.  (A non-reference container
	  *  calls set_parent).  The default implementation does nothing,
	  *  but some atoms (such as Rank or Player) may be interested.
	 */
	void removeSymbolicParent( Container c )
	{
		//System.out.println( getKey() + ".removeSymbolicParent( " + c.getKey() + " )" );
	}
	
	public final String getId()
	{
		return( getId_kickoff() );
	}
	
	String getId_kickoff()
	{
		return( getId_imp() );
	}
	
	/**
	  *  Another implementation that can only be overridden
	  *  in this package.
	 */
	String getId_imp()
	{
		Atom p = null;
		
		try
		{
			p = parent.get();
			
			if( p != null )
			{
				switch( parent_type )
				{
					case AtomicElement.PARENT_TYPE:
						return( p.getId_imp() + "." + getName() );
					case Container.PARENT_TYPE:
						return( p.getId_imp() + "/" + getName() );
				}
			}
		}
		catch( OutOfDateReferenceException e )
		{
				//  emulates setParent( null )
			resetParentToNull();
		}
		
		return( Key.unlinkedString + getName() );
	}
	
	public final String getContainedId()
	{
		Atom p = parent.get();

		if( p != null )
			return( p.getSecondLevelId( 0 ) + getName() );
		else
			return( getName() );
	}
	
	protected String getSecondLevelId( int c )
	{
		Atom p = parent.get();
		
		if( p != null )
			return( p.getSecondLevelId( c+1 ) );
		else
			return( "" );
	}
	
//---  INTERACTION  --------------------------------------------------------
	/**
	  *  When an effect occurs, people within its
	  *  "splash" range are notified (that is, they
	  *  can choose to notice or ignore it).  This
	  *  routine defaults to ignoring any effect,
	  *  but you may override it at will to recieve
	  *  any effect that it recieves.
	 */
	public void splash( Effect e, SuppressionList s )
	{
		//  do nothing - ignore this
	}
	
	public String toString()
	{
		if( parent.isLoaded() && (getParent() instanceof Container) )
			return( "(" + Type.typeOf( this ).getName() + ") " + getId() );
		else
			return( "(" + Type.typeOf( this ).getName() + ") " + getKey() );
	}
	
	/**
	  *  An atoms aspect is what you see when you look at it
	  *  META: can be removed 05Nov98, we have object code now.
	public Paragraph aspect()
	{
		return( new TextParagraph( "infinitely small, this can hardly be seen" ) );
	}
	 */

	/**
	  *  Returns *class* help about this atom.  That is, help on the properties
	  *  that are available to be set on this atom.  Ideally, for every type of
	  *  atom created this would be overridden (added to) so that help could be
	  *  recieved on every property of an atom
	  *
	 */
	public Paragraph help()
	{
		return( new TextParagraph( "No help available" ) );
	}
	
	/**
	  *  Returns the permission list for this atom.  Requires modify
	  *  permission - if you don't need it, use "getImmutablePermissionList"
	  *  instead.
	  *
	  * @see getImmutablePermissionList
	 */
	public final PermissionList getPermissionList()
	{
		permissionList.check( modifyAction );
		return( permissionList );
	}
	
		//  useful parameter names, hey?
	public final boolean permissionCheck( Action a, boolean b, boolean c )
	{
		return( permissionList.permissionCheck( a, b, c ) );
	}
	
	public final boolean remotePermissionCheck( Player a, Action b, boolean c, boolean d )
	{
		return( permissionList.remotePermissionCheck( a, b, c, d ) );
	}
	
	public final PermissionList.Immutable getImmutablePermissionList()
	{
			//  this is not a security risk because ImmutableQL isn't changeable
		return( permissionList.getImmutable() );
	}
	
	public final void checkPermissionList( Action a )
	{
		permissionList.check( a );
	}
	
	/**
	  *  Check if we're permitted to add this atom to the
	  *  container (or whatever) a.
	 */
	public final void checkAdd( Atom a )
	{
			//  it's only a "move" if we do not already
			//  have a valid parent.  (Otherwise we're
			//  just adding it to another container as
			//  a reference, which doesn't count.  Move
			//  operations need to be remove-then-add, and
			//  if the add fails, it will undo the first
			//  remove.  That makes this permission check
			//  style valid.
		if( !parent.isValid() )
			checkPermissionList( moveAction );
		
		checkAdd_imp( a );
	}
	
	protected void checkAdd_imp( Atom a )
	{
	}
	
//---  ACTION  -------------------------------------------------------------
//
//  This section of an Atom (present in many Atom's) contains information
//  specific to each type of Atom detailing the actions that may be
//  performed upon it.
//
	/**
	  *  The list of actions for Atom's.  Each sub-class
	  *  that wants to have its own possible actions will
	  *  need to have their own collection, as well as
	  *  override the routines getActions(), containsAction(),
	  *  and getAction().
	 */
	protected static StringKeyCollection staticActions;
	
	/**
	  *  is allowed to change the parent of this atom - ie,
	  *  move it around.
	 */
	public static Action moveAction;
	
	/**
	  *  is allowed to modify the properties of this atom.
	 */
	public static Action modifyAction;
	
	/**
	  *  is allowed to setOwnership of this atom.
	 */
	public static Action possessAction;

	/**
	  *  is allowed to remove this atom from its parent
	 */
	public static Action deleteAction;
	
		//  initialise the actions list
	static
	{
		staticActions = new StringKeyCollection();
		
		moveAction = newAction( Atom.class, staticActions, "move", true, false );
		modifyAction = newAction( Atom.class, staticActions, "modify", true, true );
		possessAction = newAction( Atom.class, staticActions, "possess", true, true );
		deleteAction = newAction( Atom.class, staticActions, "delete", true, true );
	}
	
	/**
	  *  Returns a list of the actions supported by
	  *  this class.  This routine will need to be
	  *  overridden by sub-classes using actions
	 */
	public Enumeration getActions()
	{
		return( staticActions.elements() );
	}

	/**
	  *  Returns true if the action is supported by
	  *  this class.  This routine will need to be
	  *  overridden by sub-classes using actions
	 */
	public boolean containsAction( Action a )
	{
		return( staticActions.contains( a ) );
	}

	/**
	  *  Returns the action corresponding to the
	  *  supplied name.  This routine will need to
	  *  be overriden by sub-classes using actions.
	 */
	public Action getAction( String name )
	{
		return( (Action) staticActions.get( name ) );
	}

	/**
	  *  Adds another action to the available actions in the
	  *  collection provided.
	 */
	protected static final Action newAction( Class in, Collection actions, String name, boolean expert, boolean serious )
	{
		Action a = new Action( in, name, expert, serious );
		
		try
		{
			actions.link( a );
		}
		catch( BadKeyException e )
		{
			e.printStackTrace( System.out );
			throw new UnexpectedResult( e.toString() + " while initialising action " + name );
		}
		catch( NonUniqueKeyException e )
		{
			e.printStackTrace( System.out );
			throw new UnexpectedResult( e.toString() + " while initialising action " + name );
		}
		
		return( a );
	}
	
//---  STATISTICS  ---------------------------------------------------------
//
//  This section contains some static functions that keep some statistical
//  information about Atom's in general.
//
	private static int totalAtoms;
	
	public static int getTotalAtoms()
	{
		return( totalAtoms );
	}
	
	protected void finalize() throws Throwable
	{
		super.finalize();
		totalAtoms--;
		
		//System.out.println( "finalizing atom " + getKey() + ":" + getClass().getName() );
	}

	/**
	  *  Just to prevent others from trying to do it.  You can't clone
	  *  an Atom, as it has a unique identifier, etc.  It should be duplicated
	  *  through the Factory and field by field.
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	public Object retrieveField( java.lang.reflect.Field f )
		throws IllegalAccessException
	{
		return( f.get( this ) );
	}

	public final int getSQLIndex()
	{
		return( index );
	}

	public final void setSQLIndex( int i )
	{
		throw new UnexpectedResult( "Trying to set the SQL index of an Atom" );
	}
}
