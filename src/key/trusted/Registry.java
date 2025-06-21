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

import key.util.Bits;
import key.util.LinkedList;
import key.primitive.DateTime;
import key.primitive.Duration;

import java.net.*;
import java.io.*;
import java.util.*;

import com.groovyj.filesystem.*;

public final class Registry
	implements Externalizable
{
	private static final long serialVersionUID = 8124646714892406403L;
	
	public static final int FILES_PER_DIRECTORY = 100;
	public static final int INITIAL_SIZE = 1000;
	public static final int CAPACITY_INCREMENT = 500;

		//  if latency sleep time is 5 minutes, a value
		//  of 10 here will checkpoint atoms every 5m * 10 =
		//  50 minutes, or once an hour.
	public static final int CHECKPOINT_EVERY_N_LATENCY_TICKS = 10;
	
		//  this field initialised by Key during startup
	public static Registry instance;
	
	protected static File baseDirectory;
	
	protected Filesystem filesystem;
	
	protected transient Object[] elementData;
	protected transient Object[] supplemental;
	protected transient int[] timestamps;
	
		//  to save us initializing offsets, we always use it "-1".  So 
		//  if we have to store a 0 in here, we actually store a 1.  This
		//  allows us to have the default 0 valid as "invalid" (where invalid
		//  probably should be -1
	protected transient long[] offsets;
	protected transient int highestNewId;
	protected transient int timestampUpto;
	
		//  This cache probably isn't going to happen:
	//protected java.lang.ref.WeakReference[] onlineReferences;
	//protected long elementFlags[];
	
	/**
	  *  Free indexes in the registry.  A bit is set to 1 if
	  *  that index is not being used.  (Therefore, we initially
	  *  assume that every index is being used)
	 */
	protected transient Bits freeSpace;
	
	public Registry()
	{
		init();
		freeSpace = new Bits( INITIAL_SIZE );
	}
	
	public String statistics()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append( "Registry\n\n" );
		
		sb.append( "current timestamp: " );
		sb.append( timestampUpto );
		sb.append( "\n\n" );
		
		sb.append( highestNewId );
		sb.append( " allocated slots, " );
		sb.append( (elementData.length-highestNewId) );
		sb.append( " slots remaining.\n" );
		
		sb.append( "freespace: " );
		sb.append( freeSpace.toString() );
		sb.append( "\n\n" );
		
		return( sb.toString() );
	}
	
	public void readExternal( ObjectInput from ) throws IOException
	{
		try
		{
			int revision = 0;
			int count = from.readInt();
			
			if( count == -1 )
			{
				revision = from.readInt();
				count = from.readInt();
			}
			
			timestampUpto = from.readInt();
			
			ensureCapacity( count );
			
			highestNewId = count;
			
			for( int i = 0; i < count; i++ )
				timestamps[i] = from.readInt();
			
			if( revision > 1 )
			{
				for( int i = 0; i < count; i++ )
				{
					try
					{
						supplemental[i] = from.readObject();
					}
					catch( ClassNotFoundException e )
					{
						Log.error( "Registry", e );
					}
				}
				
				if( revision > 2 )
				{
					for( int i = 0; i < count; i++ )
						offsets[i] = from.readLong();
				}
			}
			
			for( int i = 0; i < count; i++ )
			{
				try
				{
					Atom a = (Atom) from.readObject();
					
					if( a != null )
					{
							//  if you're getting this exception a lot and need
							//  to recover the db, just take out this check,
							//  and the system will still load, even if the
							//  references might be -everywhere-.
						if( a.index != i )
							throw new UnexpectedResult( "database corrupted" );
						
						setElementAt( a, i );
					}
				}
				catch( ClassNotFoundException e )
				{
					Log.error( "Registry", e );
				}
				catch( InvalidClassException e )
				{
					Factory.handleInvalidClassException( e );
				}
			}
			
			freeSpace = (Bits) from.readObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
	}
	
	public void writeExternal( ObjectOutput to ) throws IOException
	{
		Bits bs = (Bits) freeSpace.clone();
		
		to.writeInt( -1 );
			
		to.writeInt( 3 );			//  REVISION:  Increment this number to change it
		to.writeInt( highestNewId );
		to.writeInt( timestampUpto );
		
		for( int i = 0; i < highestNewId; i++ )
		{
				//  if this is a temporary atom, we should
				//  clear the timestamp in the saved file.
				//  this way might be inefficient - we could
				//  clone this array, preprocess the objects
				//  below, and write it out last, instead.
			if( elementData[ i ] instanceof TemporaryAtom )
				to.writeInt( -1 );
			else
				to.writeInt( timestamps[i] );
		}
		
			//  revision 2 modification: write out supplemental
			//  data.
		for( int i = 0; i < highestNewId; i++ )
		{
			to.writeObject( supplemental[i] );
		}
			//  end revision 2
		
			//  revision 3 modification: write out supplemental
		for( int i = 0; i < highestNewId; i++ )
		{
			to.writeLong( offsets[i] );
		}
			//  end revision 3
		
		for( int i = 0; i < highestNewId; i++ )
		{
			Object o = elementData[ i ];
			
			if( o instanceof Atom )
				to.writeObject( o );
			else
			{
					//  we don't write anything about distinct atoms
				to.writeObject( null );
				
				if( o instanceof TemporaryAtom )
				{
						//  mark this as free space in the saved file
					bs.set( i );
				}
			}
		}
		
		to.writeObject( bs );
	}
	
	private void init()
	{
		elementData = new Object[ INITIAL_SIZE ];
		supplemental = new Object[ INITIAL_SIZE ];
		timestamps = new int[ INITIAL_SIZE ];
		offsets = new long[ INITIAL_SIZE ];
		
		//onlineReferences = new java.lang.ref.WeakReference[ INITIAL_SIZE ];
		highestNewId = 0;
		timestampUpto = 0;
		
		try
		{
			filesystem = new Filesystem( "filesystem" );
		}
		catch( IOException e )
		{
			throw new UnexpectedResult( e );
		}
		
		if( instance == null )
			instance = this;
		else
			throw new UnexpectedResult( "Attempting to create an additional Registry" );
		
		idleTicks = IDLE_TICKS_BEFORE_TEMP_CLEANUP;
	}
	
	public void start()
	{
		if( latentCacheHook == null )
		{
			latentCacheHook = new LatentlyCached()
			{
					//  don't you dare add anything to this routine to stop
					//  certain players idling out: it needs to be as small
					//  and fast as possible.  Put it in 'resetModify'
					//  if you must.
				public final boolean modified()
				{
					return( idleTicks > 0 );
				}
				
				public void resetModify()
				{
					idleTicks--;
				}
				
				public synchronized void deallocate()
				{
						//  do the hit
					idleTicks = IDLE_TICKS_BEFORE_TEMP_CLEANUP;
					
					Log.log( "registry", "temporary atom cleanup started" );
					
					{
						long now = System.currentTimeMillis();
						
						try
						{
							for( int i = 0; i < highestNewId; i++ )
							{
								if( elementData[ i ] instanceof TemporaryAtom )
								{
									TemporaryAtom ta = (TemporaryAtom)elementData[i];
									if( now < (ta.lastAccess + MILLISECONDS_BEFORE_TEMP_CLEANUP) )
									{
										deleteIfTemporary( get( i ) );
									}
								}
								
								if( i % 1000 == 0 && i != 0 )
								{
									try
									{
										Thread.sleep( 1000 );
									}
									catch( Exception e )
									{
									}
								}
							}
						}
						catch( Exception t )
						{
							Log.error( t );
						}
					}
				}
			};
		}
		
		Key.getLatencyCache().addToCache( latentCacheHook );
	}
	
    private void ensureCapacity( int minCapacity )
	{
		if( elementData == null )
			init();
		
		int oldCapacity = elementData.length;
		
		if( minCapacity < oldCapacity )
			return;
		
		Object oldData[] = elementData;
		Object oldSupp[] = supplemental;
		int oldTS[] = timestamps;
		long oldOf[] = offsets;
		
		int newCapacity = oldCapacity + CAPACITY_INCREMENT;
		
		if( newCapacity < minCapacity )
		{
			newCapacity = minCapacity;
		}
		
		elementData = new Object[ newCapacity ];
		supplemental = new Object[ newCapacity ];
		timestamps = new int[ newCapacity ];
		offsets = new long[ newCapacity ];
		
		if( highestNewId > 0 )
		{
			System.arraycopy( oldData, 0, elementData, 0, (highestNewId-1) );
			System.arraycopy( oldSupp, 0, supplemental, 0, (highestNewId-1) );
			System.arraycopy( oldTS, 0, timestamps, 0, (highestNewId-1) );
			System.arraycopy( oldOf, 0, offsets, 0, (highestNewId-1) );
		}
    }
	
		/**  ensure that the timestamp is valid before calling this routine */
    private final Object elementAt( int index )
	{
		if( index >= highestNewId )
		{
			throw new ArrayIndexOutOfBoundsException(index + " >= " + highestNewId);
		}
		
		return elementData[ index ];
    }
	
    private final Object supplementalElementAt( int index )
	{
		if( index >= highestNewId )
		{
			throw new ArrayIndexOutOfBoundsException(index + " >= " + highestNewId);
		}
		
		return supplemental[ index ];
    }
		
    private final void setElementAt( Object obj, int index )
	{
		if( index >= highestNewId )
		{
			throw new UnexpectedResult( "index >= highestNewId in registry" );
			/*
			ensureCapacity( index );
			highestNewId = index+1;
			*/
		}
		
		elementData[index] = obj;
		freeSpace.clear( index );
	}
	
	private final int getNewIndex()
	{
			//  scan to see if we have any free
		if( freeSpace != null )
		{
			int lowestFree = freeSpace.firstSet();
			
			if( lowestFree != Integer.MAX_VALUE )
			{
				if( freeSpace.get( lowestFree ) )
				{
					freeSpace.clear( lowestFree );
					//System.out.println( "REGISTRY: re-using slot #" + lowestFree + " (old ts=" + timestamps[lowestFree] );
					return( lowestFree );
				}
				else
					throw new UnexpectedResult( "firstSet not behaving correctly in Bits" );
			}
			//else
				//System.out.println( "REGISTRY: freeSpace.firstSet() couldn't find any free space" );
		}
		//else
			//System.out.println( "REGISTRY: freeSpace is null" );
		
		//System.out.println( "REGISTRY: allocating new id #" + highestNewId );
		
		int s = highestNewId;
		
		highestNewId++;
		
		if( highestNewId > elementData.length )
			ensureCapacity( highestNewId );
		
		return( s );
	}
	
	/**
	  *  for newly constructed atoms, generates a new index for them,
	  *  sets it on the atom, sets it in the registry, and returns it.
	 */
	synchronized int allocateNewIndex( Atom a )
	{
		int newIdx = getNewIndex();
		int ts = timestampUpto++;
		
		elementData[ newIdx ] = a;
		timestamps[ newIdx ] = ts;
		offsets[ newIdx ] = 0;
		supplemental[ newIdx ] = a.createSupplemental();
		
		a.setIndex( newIdx, ts );
		
		return( newIdx );
	}
	
	synchronized int allocateTemporaryIndex( Atom a )
	{
		int newIdx = getNewIndex();
		int ts = timestampUpto++;
		
		elementData[ newIdx ] = new TemporaryAtom( a );
		supplemental[ newIdx ] = a.createSupplemental();
		timestamps[ newIdx ] = ts;
		offsets[ newIdx ] = 0;
		
		a.setIndex( newIdx, ts );
		
		return( newIdx );
	}
	
	synchronized void upgradeTemporaryIndex( int index, int ts )
	{
		ensureValidReference( index, ts );
		
		Object o = elementData[ index ];
		
		if( o instanceof TemporaryAtom )
		{
			TemporaryAtom ta = (TemporaryAtom) o;
			elementData[ index ] = ta.actual;
		}
	}
	
	/**
	  *  Makes this Atom temporary (if it is in the main db at the moment)
	 */
	void makeTemporary( Atom a )
	{
		int index = a.index;
		
		Object n = elementData[ index ];
		
		if( n instanceof TemporaryAtom )
			return;
		
		elementData[ index ] = new TemporaryAtom( a );
		
		if( n == null || n instanceof LoadedAtom )
			deleteFileFor( index );
		
		if( n instanceof LoadedAtom )
		{
			LoadedAtom la = (LoadedAtom) n;
			Key.getLatencyCache().removeFromCache( la );
			la.actual = null;
		}
	}
	
	void deleteIfTemporary( Atom a )
	{
		int index = a.index;
		int ts = a.timestamp;
		
		if( isReferenceValid( index, ts ) )
		{
			Object n = elementData[ index ];
			
			if( n instanceof TemporaryAtom )
			{
				if( a == ((TemporaryAtom)n).actual )
				{
					a.dispose();
				}
				else
					throw new UnexpectedResult( "deleteIfTemporary found unmatching index/timestamp -> atom pairs" );
			}
		}
	}

	/**
	  *  Used to write a loaded atom to disk (actually write it
	  *  now, as opposed to waiting for it to be swapped out)
	 */
	synchronized void write( int index, int ts )
	{
		ensureValidReference( index, ts );
		Object o = elementAt( index );
		
		if( o instanceof LoadedAtom )
		{
			writeLoadedAtom( (LoadedAtom) o );
			//System.out.println( "write forced for #" + ((LoadedAtom)o).actual.toString() );
		}
	}
	
	void syncDistinct( int index, int ts )
	{
		synchronized( Key.getLatencyCache() )
		{
			synchronized( this )
			{
				ensureValidReference( index, ts );
				
				Object o = elementAt( index );
				LoadedAtom la = null;
				
				if( o instanceof LoadedAtom )
				{
					la = ((LoadedAtom)o);
				}
				else
				{
					Atom a = null;
					
					if( o instanceof Atom )
						a = (Atom)o;
					else if( o instanceof TemporaryAtom )
						a = ((TemporaryAtom)o).actual;
					
					if( a == null )
						return;
					
						//  build a new loaded atom and write it in...
					la = buildLoadedAtom( a, index );
					
						//  now, if we sync something distinctly, we should
						//  also sync all of it's part atoms distinctly (otherwise
						//  we're storing part of the atom in the main database)
					Factory.distinctSyncFields( a );
					
						//  if we're a container, sync children
					if( a instanceof Container )
					{
						Container c = (Container) a;
						
						for( Enumeration e = c.elements(); e.hasMoreElements(); )
						{
							Atom child = (Atom) e.nextElement();
							
							syncDistinct( child.index, child.timestamp );
						}
					}
				}
				
					//  now write the loaded atom
				writeLoadedAtom( la );
			}
		}
	}
	
	/**
	  *  All Atom's should call this when they are loaded from disk
	 */
	synchronized void registerIndex( Atom a, int idx, int ts )
	{
		ensureValidReference( idx, ts );
		
		if( idx > elementData.length )
		{
			throw new UnexpectedResult( "index >= elementData.length in registry" );
			/*
			highestNewId = idx+1;
			ensureCapacity( highestNewId );
			*/
		}
		
		elementData[ idx ] = a;
	}
	
	public final void ensureValidReference( int index, int ts )
	{
		try
		{
			if( timestamps[ index ] != ts )
				throw new OutOfDateReferenceException( "index " + index + " has a timestamp of " + timestamps[ index ] + ", not " + ts );
		}
		catch( IndexOutOfBoundsException e )
		{
			throw new OutOfDateReferenceException( "index " + index + " is out of bounds" );
		}
	}
	
	public final boolean isReferenceValid( int index, int ts )
	{
		try
		{
			return( timestamps[ index ] == ts );
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	private final void clearIndex( int index )
	{
		freeSpace.set( index );
		elementData[ index ] = null;
		supplemental[ index ] = null;
		timestamps[ index ] = -1;
		offsets[ index ] = 0;
	}
	
	/**
	  *  for when we're deleting an atom forever - removes it from the
	  *  registry.  Call a.dispose(), not registry.delete() to delete
	  *  an object.
	 */
	void delete( Atom a )
	{
		synchronized( Key.getLatencyCache() ) { synchronized( this )
		{
				//  already been deleted
			if( !isReferenceValid( a.index, a.timestamp ) )
			{
				Log.log( "registry", "Error deleting " + a.getName() + ": already deleted" );
				return;
			}
			
				//  check if locked after decrement
			if( !a.willbeLockedAfterDecrement() )
			{
				Log.log( "registry", "Deleting " + a.getName() + ", #" + a.index );
				
					//  clean up transient:
					//    - take out of scapes & notify lists
					//    - disconnect if player
				a.clearTransient();
				
				LoadedAtom la = null;
				int index = a.index;
				
				{
					Object o = null;
					o = elementData[ a.index ];
					
					if( o instanceof LoadedAtom )
					{
						la = (LoadedAtom) o;
							
							//  remove it from the latent cache
						Key.getLatencyCache().removeFromCache( la );
					}
				}
				
					//  make the atom unavailable
				if( index < elementData.length )
					clearIndex( index );
				
					//  decrement implicit reference counts
					//  important for the next step!
				a.prepareForSwap();
				
					//  delete fields if they're atomic, delete
					//  contents if it is a non-reference container
				a.delete();
				
					//  delete distinct file
				if( la != null )
					deleteFileFor( index );
			}
			else
			{
				throw new UnexpectedResult( "Could not delete " + a.getKey() + ": Atom is implicitly locked" );
			}
		}}
	}
	
	private void deleteFileFor( int index )
	{
		if( Main.USE_DISTINCT )
		{
			File f = getFileFor( index );
			f.delete();
			
			Log.debug( this, "REGISTRY: Deleted distinct file " + f.toString() );
		}
		
		if( Main.USE_FILESYSTEM )
		{
			if( offsets[ index ] != 0 )
			{
				try
				{
					Filesystem.File fsf = filesystem.open( offsets[index] - 1 );
					fsf.delete();
					Log.debug( this, "REGISTRY: Deleted filesystem file " + index );
				}
				catch( IOException e )
				{
					Log.error( "io error deleting file", e );
				}
			}
		}
	}
	
	/**
	  *  used to swap an atom out of memory.  doesn't need
	  *  to remove the item from the latent cache, since
	  *  the LC itself handles this when swapout is called,
	  *  which is only ever from deallocate() in LoadedAtom.
	 */
	void swapout( LoadedAtom la )
	{
		if( la.actual == null )
			return;
		
		synchronized( Key.getLatencyCache() ) { synchronized( this )
		{
			Atom a = la.actual;
			
			boolean notrunning = !Key.isRunning();
			
			//System.out.println( "trying to swap out " + a.getKey() );
			if( a.canSwap() )
			{
					//  check if locked after decrement
				if( !a.willbeLockedAfterDecrement() )
				{
					//System.out.println( "Swapping out atom " + a.toString() );
					
						//  clean up transient:
						//    - take out of scapes & notify lists
						//    - disconnect if player
					a.clearTransient();
					
						//  decrement implicit reference counts
					a.prepareForSwap();
					
						//  sync
					writeLoadedAtom( la );
					
						//  make the atom unavailable
					if( la.index < elementData.length )
						elementData[ la.index ] = null;
					
						//  clear all references to it
					la.actual = null;
					a = null;
				}
			}

			if( a != null && notrunning )
			{
					//  sync
				writeLoadedAtom( la );
			}
		}}
	}
	
	/**
	  *  Attempts to reduce the amount of memory in use by
	  *  swapping out all loaded atoms (that can be swapped)
	  *  to disk.
	  * <BR>
	  *  <B>For use by players with the beyond bit only.</B>
	 */
	public void compress()
	{
		Player p = Player.getCurrent();
		if( p != null )
		{
			if( !p.isBeyond() )
				throw new AccessViolationException( this, "You may not attempt to compress the registry." );
		}
		
		LatentCache lc = Key.getLatencyCache();
		int c = 0;
		
		synchronized( lc )
		{
			synchronized( this )
			{
				for( int i = 0; i < elementData.length; i++ )
				{
					Object o = elementData[i];
					if( o instanceof LoadedAtom )
					{
						//lc.resetModify();
						//lc.interrupt();
						lc.deallocateNow( (LatentlyCached) o );
						c++;
					}
				}
			}
		}
		
		/*
		System.out.println( "registry compress: " + c + " items deallocated.  " + lc.countEntries() + " latency entries" );
		
		int atoms, containers, scapes, players, lists;
		
		atoms = Atom.getTotalAtoms();
		containers = Container.getTotalContainers();
		atoms -= containers;

		scapes = Scape.getTotalScapes();
		containers -= scapes;

		players = Player.getTotalPlayers();
		containers -= players;
		
		lists = LinkedList.getTotalLists();
		
		System.out.println( "Totals: " + atoms + " atoms, " + containers
			+ " containers, " + scapes + " scapes, " + players + " players, "
			+ lists + " lists" );
		*/
	}
	
	void swapout( Atom a )
	{
		LatentCache lc = Key.getLatencyCache();
		
		synchronized( lc )
		{
			synchronized( this )
			{
				Object o = elementData[ a.index ];
				if( o instanceof LoadedAtom )
				{
					//lc.resetModify();
					//lc.interrupt();
					lc.deallocateNow( (LatentlyCached) o );
				}
			}
		}
	}
	
	void writeLoadedAtom( LoadedAtom la )
	{
		Filesystem.File f = null;
		
		if( Main.USE_FILESYSTEM )
		{
			try
			{
				if( offsets[ la.index ] == 0 )
				{
					f = filesystem.open();
				}
				else
					f = filesystem.open( offsets[ la.index ] - 1 );  //  if it's 1, make it 0
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		Factory.storeObject( la.actual, new Factory.PersistLocation( getFileFor( la.index ), f ), true );
		
		if( f != null )
		{
			offsets[ la.index ] = f.getOffset() + 1;  // if it's 0, make it 1
			//System.out.println( "Wrote fs file, offset = " + f.getOffset() );
			
			if( Main.CLEANUP_DISTINCT_ON_FS_WRITE )
			{
				File df = getFileFor( la.index );
				if( df.exists() )
				{
					df.delete();
					Log.log( "registry", "Cleaned up #" + la.index + ": " + df.getPath() );
				}
			}
		}
		//System.out.println( "Wrote #" + la.index );
		//key.sql.Storage.storeObject( la.actual );
	}
	
	LoadedAtom buildLoadedAtom( Atom a, int idx )
	{
		LoadedAtom la = new LoadedAtom( idx, a );
		Key.getLatencyCache().addToCache( la );
		
		setElementAt( la, idx );
		
		return( la );
	}
	
	public static final int STORAGE_UNLOADED = 0;
	public static final int STORAGE_DB = 1;
	public static final int STORAGE_LOADED = 2;
	public static final int STORAGE_TEMPORARY = 3;
	public static final int STORAGE_UNKNOWN = 3;
	
	int getStorageTypeIndex( int index, int ts )
	{
		ensureValidReference( index, ts );
		
		Object o = elementAt( index );
		
		if( o == null )
			return( STORAGE_UNLOADED );
		else if( o instanceof Atom )
			return( STORAGE_DB );
		else if( o instanceof LoadedAtom )
			return( STORAGE_LOADED );
		else if( o instanceof TemporaryAtom )
			return( STORAGE_TEMPORARY );
		else
			return( STORAGE_UNKNOWN );
	}
	
	String getStorageType( int index, int ts )
	{
		ensureValidReference( index, ts );
		
		Object o = elementAt( index );
		
		if( o == null )
			return( "Not loaded (Distinct)" );
		else if( o instanceof Atom )
			return( "Database" );
		else if( o instanceof LoadedAtom )
			return( "Distinct" );
		else if( o instanceof TemporaryAtom )
			return( "Temporary" );
		else
			return( "Unknown" );
	}
	
	/**
	  *  Returns the atom iff it is in memory already
	 */
	Atom getIfInDatabase( int index, int ts )
	{
		ensureValidReference( index, ts );
		
		Object o = elementAt( index );
		
		if( o instanceof Atom )
			return( (Atom) o );
		else if( o instanceof LoadedAtom )
			return( ((LoadedAtom)o).actual );
		else
			return null;
	}
	
	/**
	  *  A special 'get' routine that is used by Search.java in order
	  *  to match user entered id #'s.
	 */
	synchronized Atom get( int idx )
	{
		if( idx >= 0 && idx < elementData.length )
		{
			int ts = timestamps[ idx ];
			
			if( ts != -1 )
				return( get( idx, ts ) );
		}
		
		return( null );
	}

	int getTimestamp( int idx )
	{
		return( timestamps[ idx ] );
	}
	
	public synchronized Object getSupplemental( int idx, int timestamp )
	{
		ensureValidReference( idx, timestamp );
		return( supplementalElementAt( idx ) );
	}
	
	/**
	  *  retrieves this atom from disk
	 */
	public synchronized Atom get( int idx, int timestamp )
	{
		ensureValidReference( idx, timestamp );
		
		/*
		 * - throw an exception, don't return null
		if( !isReferenceValid( idx, timestamp ) )
			return null;
		*/
		
		Object o = elementAt( idx );
		
		if( o instanceof Atom )
			return( (Atom)o );
		else if( o instanceof LoadedAtom )
		{
			LoadedAtom la = (LoadedAtom) o;
			
			la.active = true;
			return( la.actual );
		}
		else if( o instanceof TemporaryAtom )
		{
			TemporaryAtom ta = (TemporaryAtom)o;
			ta.lastAccess = System.currentTimeMillis();
			return( ta.actual );
		}
		else
		{
			Atom a;
			File f = getFileFor( idx );
			Filesystem.File fsf = null;
			
			if( Main.USE_FILESYSTEM )
			{
				if( offsets[ idx ] != 0 )
				{
					try
					{
						fsf = filesystem.open( offsets[ idx ] - 1 );
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
			
			//System.out.println( "--- Loading #" + idx );
			
				//  a read atom will call 'makeTemporaryAvailable'
				//  during this call, which is a little bit of overhead,
				//  but not drastic.  we limit it by 
			allowTemporaryAvailability = true;
			a = (Atom) Factory.loadObject( new Factory.PersistLocation( f, fsf ) );
			allowTemporaryAvailability = false;
			
			if( a == null )
			{
				Log.debug( this, "REGISTRY: Could not load #" + idx + ", clearing..." );
				clearIndex( idx );
				throw new OutOfDateReferenceException( "index " + idx + " could not be loaded from disk (file corrupt, missing, class not found?) and has been cleared" );
			}
			
			LoadedAtom la = buildLoadedAtom( a, idx );
			
			//System.out.println( "Loaded #" + idx + " (" + a.getKey() + ")" );
			
			return( a );
		}
	}
	
	/**
	  *  This code is called from Atom.readObject() in order
	  *  to make this atom available to the registry as soon
	  *  as possible.  This is necessary when circular
	  *  stored implicit references occur.  See the comments
	  *  in KeyInputStream.resolveObject(), as well as Atom.readObject()
	  *  also.
	  *
	  *  This method must be called only after a.timestamp and a.index
	  *  have been correctly loaded.
	 */
	synchronized void makeTemporarilyAvailable( Atom a )
	{
		if( allowTemporaryAvailability )
		{
			ensureValidReference( a.index, a.timestamp );
			setElementAt( new TemporaryAtom( a ), a.index );
		}
	}
	
	private transient boolean allowTemporaryAvailability;
	
	private File getFileFor( int index )
	{
		File directory = new File( baseDirectory, Integer.toString( index / 150 ) );
		
		if( !directory.exists() )
			directory.mkdir();
		
		return( new File( directory, Integer.toString( index ) ) );
	}
	
	boolean indexLoaded( int idx, int timestamp )
	{
		if( !isReferenceValid( idx, timestamp ) )
			return( false );
		
		if( idx >= highestNewId )
			return( false );
		else
			return( elementData[ idx ] != null );
	}
	
	final class TemporaryAtom
	{
		Atom actual;
		long lastAccess;
		
		public TemporaryAtom( Atom a )
		{
			actual = a;
			lastAccess = System.currentTimeMillis();
		}
	}
	
	final class LoadedAtom implements LatentlyCached
	{
		int index;
		boolean active;
		Atom actual;
		int sinceSyncCount = 0;
		
		public LoadedAtom( int idx, Atom a )
		{
			index = idx;
			actual = a;
			active = true;
		}
		
		public void deallocate()
		{
				//  this call will set 'actual' to true
			Registry.instance.swapout( this );
		}
		
		public boolean modified()
		{
			//System.out.println( "Registry: " + actual.getName() + " active=" + active + " canSwap=" + actual.canSwap() + " willbeLocked=" + actual.willbeLockedAfterDecrement() );

				//  the shortcutting here prevents the expensive willbe operation
				//  from being called every-time: it only happens when it is needed
			if( actual == null )
				return( false );
			else
				return( active || !actual.canSwap() || actual.willbeLockedAfterDecrement() );
		}
		
		public void resetModify()
		{
			active = false;
			sinceSyncCount++;
			
			if( sinceSyncCount > CHECKPOINT_EVERY_N_LATENCY_TICKS )
			{
				sinceSyncCount = 0;
				//Log.debug( "Registry", "checkpointing " + actual.getId() );
				Registry.instance.writeLoadedAtom( this );
			}
		}
		
		public String toString()
		{
			return( "LoadedAtom #" + index + " (" + actual.getKey() + ")" );
		}
	}

	/*
	static Hashtable commonAtomNames;
	
	static
	{
		commonAtomNames = new Hashtable();
		commonAtomNames.put( "loginStats", "loginStats" );
		commonAtomNames.put( "connectionStats", "connectionStats" );
		commonAtomNames.put( "friends", "friends" );
		commonAtomNames.put( "prefer", "prefer" );
		commonAtomNames.put( "inform", "inform" );
		commonAtomNames.put( "inventory", "inventory" );
		commonAtomNames.put( "objects", "objects" );
		commonAtomNames.put( "mailbox", "mailbox" );
		commonAtomNames.put( "land", "land" );
		commonAtomNames.put( "ranks", "ranks" );
		commonAtomNames.put( "commandSets", "commandSets" );
		commonAtomNames.put( "motd", "motd" );
		commonAtomNames.put( "founder", "founder" );
		commonAtomNames.put( "leader", "leader" );
		commonAtomNames.put( "member", "member" );
		commonAtomNames.put( "commands", "commands" );
		commonAtomNames.put( "who", "who" );
		commonAtomNames.put( "cl", "cl" );
		commonAtomNames.put( "ce", "ce" );
		commonAtomNames.put( "ct", "ct" );
		commonAtomNames.put( "c", "c" );
		commonAtomNames.put( "rank", "rank" );
		commonAtomNames.put( "proxydata", "proxydata" );
	}
	*/

	/**
	  *  A routine that scans all Atom's and ensures that
	  *  we have created all the supplementals for them.
	  */
	void updateSupplemental( int i )
	{
		if( supplemental[i] == null && timestamps[i] != -1 )
		{
			Atom a = get( i, timestamps[i] );
			if( a != null )
			{
				if( supplemental[i] == null )
				{
					supplemental[i] = a.createSupplemental();
					Log.log( "registry", "Converting #" + i + " (is a " + a.getClass().getName() + " @ " + a.getName() );
				}
				
				swapout( a );
				a = null;
			}
			else
				Log.log( "registry", "Converting #" + i );
		}
	}
	
	void updateSupplementals()
	{
		try
		{
			for( int i = 0; i < highestNewId; i++ )
			{
				try
				{
					updateSupplemental( i );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
				
				if( i % 1000 == 0 && i != 0 )
				{
					Log.log( "registry", "syncing db" );
					Key.instance().sync();
					Log.log( "registry", "...complete" );
					
					try
					{
						Thread.sleep( 10000 );
					}
					catch( Exception e )
					{
					}
				}
			}
		}
		catch( Throwable t )
		{
			Log.error( t );
		}
	}

	public void scanTimeouts()
	{
		Vector topEntries = new Vector( 100, 100 );
		//long currentSmallest = Long.MAX_VALUE;
		long t;
		long month = 1000*60*60;
		month *=24;
		month *=31;
		//month *=6;
		long now = System.currentTimeMillis();
		
			//  timeouts are set to 1 months
		long normalTimeoutAt = now - month;
		long twoHourTimeoutAt = now - (month*6);
		
			//  citizens time out in 12 months
		long citizenTimeoutAt = now - (month*12);
		
		for( int i = 0; i < highestNewId; i++ )
		{
			if( supplemental[i] != null && timestamps[i] != -1 )
			{
				if( supplemental[i] instanceof PlayerSupplemental )
				{
					PlayerSupplemental ps = ((PlayerSupplemental)supplemental[i]);
					t = ps.lastTouch;
					
					if( t == -1 || t == 0 )
					{
						Log.log( "timeouts", "lastTouch==-1 for #" + i + " (skipping)" );
						continue;
					}
					
					if( t > now )
					{
						Log.log( "timeouts", "lastTouch > now (=" + t + ") for #" + i );
						continue;
					}
					
					if( t < normalTimeoutAt )
					{
						if( (ps.timeoutFlags & PlayerSupplemental.TIMEOUT_CLAN) == PlayerSupplemental.TIMEOUT_CLAN ) continue;
						
						if( (ps.timeoutFlags & PlayerSupplemental.TIMEOUT_NOTIMEOUT) == PlayerSupplemental.TIMEOUT_NOTIMEOUT ) continue;
						
						if( (ps.timeoutFlags & PlayerSupplemental.TIMEOUT_CITIZEN) == PlayerSupplemental.TIMEOUT_CITIZEN )
						{
							if( t > citizenTimeoutAt )
							{
								Log.log( "timeouts", "Spared citizen #" + i + " until 12 months" );
								continue;
							}
						}
						
						if( (ps.timeoutFlags & PlayerSupplemental.TIMEOUT_TWOHOUR) == PlayerSupplemental.TIMEOUT_TWOHOUR )
						{
								//  check for two hour timeout time
							if( t > twoHourTimeoutAt )
							{
								Log.log( "timeouts", "Spared > 2 hours #" + i + " until 6 months" );
								continue;
							}
						}
						
						topEntries.addElement( new Integer( i ) );
					}
				}
			}
		}
		
		Log.log( "timeouts", "-----------------------------------------------------" );
		Log.log( "timeouts", "Timeout scan complete, " + topEntries.size() + " timeouts" );
		
		int totalCount = 0;
		boolean noAnon = false;
		
		//if( currentSmallest == Long.MAX_VALUE )
			//Log.log( "timeouts", "No player supplementals found, at all." );
		//else
		{
			//Log.log( "timeouts", "Earliest value found is: " + currentSmallest );
			Log.log( "timeouts", "Scan started: " + (new Date()).toString() + " (" + System.currentTimeMillis() + ")" );
			Log.log( "timeouts", "Players, normal timeout @ " + normalTimeoutAt + ":" );
			Log.log( "timeouts", "Citizens, timeout @ " + citizenTimeoutAt + ":" );
			
			for( Enumeration e = topEntries.elements(); e.hasMoreElements(); )
			{
				int i = ((Integer)e.nextElement()).intValue();
				
				PlayerSupplemental ps = ((PlayerSupplemental)supplemental[i]);
				Player p = null;
				
				try
				{
					p = (Player) get(i);
				}
				catch( Exception ex1 )
				{
					Log.log( "timeouts", "invalid player supplemental for " + i + ": " + ex1.toString() );
					continue;
				}

				if( p == null )
				{
					Log.log( "timeouts", "get( " + i + ") returned null during scanTimeouts" );
					continue;
				}
				
				Thread.yield();
				
					//  Since we're loading the player anyway, double check
					//  the last touch result just before we time them out.
				if( p.loginStats != null )
				{
					DateTime dt = p.loginStats.getLastDisconnection();
					if( dt != null && dt.getTime() > ps.lastTouch )
					{
						Log.log( "timeouts", "fixed lastTouch for " + p.getName() + " (#"+ i + ")" );
						ps.lastTouch = dt.getTime();
					}
				}
				
				if( p.notimeout )
				{
					Log.log( "timeouts", "SKIP (notimeout):" + p.getName() );
					ps.timeoutFlags |= PlayerSupplemental.TIMEOUT_NOTIMEOUT;
					swapout( p );
					continue;
				}
				else
				{
					if( p.getName().toLowerCase().startsWith( "clan" ) )
					{
						Log.log( "timeouts", "Skipping clan + " + p.getName() );
						ps.timeoutFlags |= PlayerSupplemental.TIMEOUT_CLAN;
						swapout( p );
						continue;
					}

					try
					{
						if( !noAnon && p == Key.instance().getAnonymous() )
						{
							Log.log( "timeouts", "SKIP (auto anonymous notimeout):" + p.getId() );
							p.notimeout = true;
							ps.timeoutFlags |= PlayerSupplemental.TIMEOUT_NOTIMEOUT;
							swapout( p );
							
								//  shortcut in the future
							noAnon = true;
							continue;
						}
					}
					catch( Exception ex3 )
					{
						Log.error( "during check anonymous in timeout scan", ex3 );
						noAnon = true;
					}
					
					StringBuffer sb = new StringBuffer( p.getName() );
					
					sb.append( " " );
					sb.append( ((PlayerSupplemental)supplemental[p.index]).lastTouch );
					
					if( p.isCitizen() )
					{
						sb.append( " [citizen]" );
						if( ps.lastTouch > citizenTimeoutAt )
						{
								//  don't time out this citizen yet
							Log.log( "timeouts", "Spared citizen " + p.getName() + " (#" + i + ") until 12 months w/ " + p.loginStats.getTotalConnectionTime().toLimitedString(3) );
							ps.timeoutFlags |= PlayerSupplemental.TIMEOUT_CITIZEN;
							swapout( p );
							continue;
						}
						else
						{
						}
					}
					else
					{
						try
						{
							if( p.loginStats.getTotalConnectionTime().getTime() > 1000*60*60*2 )
							{
								if( ps.lastTouch > twoHourTimeoutAt )
								{
									Log.log( "timeouts", "Spared > 2 hours " + p.getName() + " (#" + i + ") until 6 months w/ " + p.loginStats.getTotalConnectionTime().toLimitedString(3) );
									ps.timeoutFlags |= PlayerSupplemental.TIMEOUT_TWOHOUR;
									swapout( p );
									continue;
								}
							}
						}
						catch( Exception ex2 )
						{
							Log.error( "during scanTimeouts twoHour (disposing anyway)", ex2 );
						}
					}
					
					DateTime dateTime = p.loginStats.getLastDisconnection();
					if( dateTime == null )
					{
						sb.append( " [no logins, lasttouch was " );
						sb.append( Duration.toString( now - ps.lastTouch, 3 ) );
						sb.append( " ago]" );
					}
					else
					{
						sb.append( " [lastseen " );
						sb.append( dateTime.toString( p ) );
						
						sb.append( " = " );
						sb.append( dateTime.getTime() );
						sb.append( "] [totallogin " );
						sb.append( p.loginStats.getTotalConnectionTime().toLimitedString(3) );
						sb.append( "]" );
					}
					
					Log.log( "timeouts", sb.toString() );
				}
				
				p.dispose();
				totalCount++;
			}
		}
		
		Key.instance().sync();
		Log.log( "timeouts", "Timeout run complete, total = " + totalCount );
		Log.log( "timeouts", "-----------------------------------------------------" );
	}
	
		//  about 5 hours
	public static final int IDLE_TICKS_BEFORE_TEMP_CLEANUP = 60;
	public static final int MILLISECONDS_BEFORE_TEMP_CLEANUP = 1000*60*5*IDLE_TICKS_BEFORE_TEMP_CLEANUP;
	
	private transient int idleTicks;
	
		//  for detecting the player idling out in the latencycache
	private transient LatentlyCached latentCacheHook;
}
