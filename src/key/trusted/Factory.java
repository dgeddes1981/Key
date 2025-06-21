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

import key.io.KeyOutputStream;
import key.io.KeyInputStream;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import com.groovyj.filesystem.*;

public final class Factory
{
	public static class PersistLocation
	{
		File systemFile;
		Filesystem.File fsFile;
		
		public PersistLocation( File sf, Filesystem.File fs )
		{
			systemFile = sf;
			fsFile = fs;
		}
	}
	
	public static void storeObject( Object object, PersistLocation to )
	{
		storeObject( object, to, false );
	}
	
	public static Object loadObject( PersistLocation pl )
	{
		try
		{
			if( pl.fsFile != null && Main.USE_FILESYSTEM )
			{
				//System.out.println( "Loading from fs, " + pl.fsFile.getOffset() );
				return( loadObject( new key.io.FileSystemInputStream( pl.fsFile ), Main.COMPRESS_FS_FILES ) );
			}
			else
				return( loadObject( new FileInputStream( pl.systemFile ), Main.COMPRESS_DISK_FILES ) );
		}
		catch( IOException e )
		{
			Log.debug( Factory.class, "while opening file '" + pl.systemFile.getPath() + "'" );
			return( null );
		}
	}
	
	private static Object loadObject( InputStream from, boolean compress )
	{
		ObjectInputStream ds = null;
		Object result = null;
		InputStream fis = from;
		
		try
		{
			if( compress )
				fis = new java.util.zip.GZIPInputStream( fis );
			
			ds = new KeyInputStream( fis );
		}
		catch( IOException e )
		{
			Log.debug( Factory.class, "while loading" );
			e.printStackTrace();
			return( null );
		}
		
		try
		{
			result = ds.readObject();
		}
		catch( java.io.InvalidClassException e )
		{
//			if( Key.isRunning() )
//				Log.error( "while loading file '" + from.getPath() + "'", e );
//			else
			{
				/*
				e.printStackTrace();
				Log.fatal( "Key", e.toString() + " while loading from file '" + from.getPath() + "'" );
				*/
				try
				{
				handleInvalidClassException( e );
				}
				catch( Exception ex )
				{
					e.printStackTrace();
					Log.fatal( "Key", e.toString() + " while loading from file" );
				}
			}
		}
		catch( ClassNotFoundException e )
		{
			Log.error( "while loading from file", e );
			return( null );
		}
		catch( IOException e )
		{
			Log.error( "while loading from file", e );
			return( null );
		}
		finally
		{
			try
			{
				ds.close();
				fis.close();
			}
			catch( IOException except )
			{
			}
		}
		
			//  this is done by the Factory on construction, but
			//  here on load, obviously
		if( result instanceof Atom )
			Factory.incrementImplicitReferenceCounts( (Atom) result );
		
		return( result );
	}
	
	public static void handleInvalidClassException( InvalidClassException e ) throws InvalidClassException, IOException
	{
					//  vTODO: REMOVE ALL THIS CODE!
		//Log.error( "Registry", e );
		String err = e.getMessage();
		int start = err.indexOf( "=" );
		int end = err.indexOf( " ", start );
		int endClass = err.indexOf( ";" );
		if( start == -1 || end == -1 || endClass == -1 )
		{
			System.out.println( "\n\n\nMessage not found, message is: '" + err + "'\n\n\n\n\n\n\n" );
			throw e;
		}
		else
		{
			String fileName = "source\\" + err.substring( 0, endClass ).replace( '.', '\\' ) + ".java";
			String path = fileName.substring( 0, fileName.lastIndexOf( '\\' ) );
			fileName = fileName.substring( fileName.lastIndexOf( '\\' ) );
						
						
			String addLine = "\tprivate static final long serialVersionUID = " + err.substring( start+1, end ) + "L;";
						
			System.err.println( e.getMessage() );
			System.err.println( "Add: " + addLine );
		
			//try
			{
				File sFile = new File( path, fileName );
				
				if( !sFile.exists() )
				{
						//  lets see if we can find it
					File p = new File( path );
					String[] fl = p.list();
					if( fl != null )
					{
						for( int i = 0; i < fl.length; i++ )
						{
							p = new File( path, fl[i] );
							if( p.isDirectory() )
							{
								sFile = new File( p, fileName );
								if( sFile.exists() )
								{
									System.out.println( "Found wayward source file in " + sFile.getAbsolutePath() );
									path = p.getAbsolutePath();
									break;
								}
							}
						}
					}
				}
				
				File nFile = new File( path, fileName.substring( 0, fileName.lastIndexOf( "." ) ) + ".new" );
				File backupFile = new File( path, fileName.substring( 0, fileName.lastIndexOf( "." ) ) + ".backup" );
							
				System.out.println( "path=" + path );
				System.out.println( "sfile=" + sFile.toString() );
				System.out.println ("nFile=" + nFile.toString() );
				System.out.println( "backupFile=" + backupFile.toString() );
				throw e;
/*
				DataInputStream fis = new DataInputStream( new FileInputStream( sFile ) );
				PrintStream fos = new PrintStream( new FileOutputStream ( nFile ) );
				String l = null;
				boolean done = false;
				
				do
				{
					l = fis.readLine();
					if( l != null )
					{
						fos.println( l );
						if( !done && l.equals( "{" ) )
						{
							fos.println( addLine );
							done = true;
						}
					}
				} while( l != null );
							
				fis.close();
				fos.close();
							
				if( !backupFile.delete() )
					System.out.println( "Could not delete backup file" );
				if( !sFile.renameTo( backupFile ) )
					System.out.println( "Could not rename original source file to backup" );
				if( !nFile.renameTo( sFile ) )
					System.out.println( "Could not rename changed source to proper name" );
				System.out.println( "Recompiling..." );
				Runtime.getRuntime().exec( "javac -d D:\\Projects\\key\\classes " + sFile.getAbsolutePath() );
				System.out.println( "Done..." );
*/
			}
			/*
			catch( FileNotFoundException ex )
			{
				System.err.println( "Cannot locate source file to do automatic substitution!" );
				ex.printStackTrace();
			}
			
			System.exit( -1 );
			*/
		}
	}
	
	public static void storeObject( Object object, PersistLocation pl, boolean distinct )
	{
		File temp = null;
		
		try
		{
			if( pl.fsFile != null && Main.USE_FILESYSTEM )
			{
				//System.out.println( "Storing into fsFile" );
				storeObject( object, new key.io.FileSystemOutputStream( pl.fsFile ), distinct, Main.COMPRESS_FS_FILES );
				
					//  Delete the distinct file when we get it into the FS
				if( Main.CLEANUP_DISTINCT_ON_FS_WRITE )
				{
					pl.systemFile.delete();
				}
			}
			else
			{
				temp = new File( pl.systemFile.getPath() + tempFileExtension );
				
				storeObject( object, new FileOutputStream( temp ), distinct, Main.COMPRESS_DISK_FILES );
				
				pl.systemFile.delete();
				temp.renameTo( pl.systemFile );
			}
		}
		catch( IOException e )
		{
			if( temp != null )
				temp.delete();
			
			throw new UnexpectedResult( e.toString() + " while trying to saveObject to file '" + pl.systemFile.getName() + "'" );
		}
	}
	
	private static void storeObject( Object object, OutputStream fos, boolean distinct, boolean compress ) throws IOException
	{
		if( compress )
			fos = new java.util.zip.GZIPOutputStream( fos );
		
		KeyOutputStream oos = new KeyOutputStream( fos );
		
		if( distinct )
			oos.doSwapping();
		
		oos.writeObject( object );
		oos.close();
		fos.close();
	}
	
	public static Atom makeAtom( Class cl, Object key )
	{
		Atom a;
		a = createAtom( cl );
		a.setKey( key );
		
		a.constructed();
		
		return( a );
	}
	
	public static Atom makeAtom( Class cl )
	{
		Atom a;
		a = createAtom( cl );
		a.constructed();
		return( a );
	}
	
	private static Atom createAtom( Class cl )
	{
		Atom a;
		
		try
		{
			a = (Atom) cl.newInstance();
		}
		catch( Exception e )
		{
			e.printStackTrace( System.out );
			throw new UnexpectedResult( e );
		}
		
		postCreateProcess( a );
		
		return( a );
	}
	
	public static void postCreateProcess( Atom a )
	{
		processFields( a.getClass(), a, CONSTRUCTER );
	}
	
	public static void decrementImplicitReferenceCounts( Atom a )
	{
		processFields( a.getClass(), a, DECREMENTER );
	}
	
	public static void incrementImplicitReferenceCounts( Atom a )
	{
		processFields( a.getClass(), a, INCREMENTER );
	}
	
	public static void distinctSyncFields( Atom a )
	{
		processFields( a.getClass(), a, DISTINCT_SYNCER );
	}
	
	public static void nonTemporaryFields( Atom a )
	{
		processFields( a.getClass(), a, NON_TEMPORARY );
	}
	
	public static void partDeleteFields( Atom a )
	{
		processFields( a.getClass(), a, PART_DELETER );
	}
	
	/**
	  *  Makes sure that the owner of this atom also
	  *  owns all of this atom's final atomic fields.
	 */
	public static void syncOwnerFields( Atom a )
	{
		processFields( a.getClass(), a, SYNC_OWNER );
	}
	
	public static void syncOwnerRecursiveFields( Atom a )
	{
		processFields( a.getClass(), a, SYNC_OWNER_R );
	}
	
	private final static void processFields( Class start, Atom parent, FieldScanner fs )
		throws SecurityException
	{
		{
			Field[] fields;
			
			fields = start.getDeclaredFields();
			
			for( int i = 0; i < fields.length; i++ )
			{
				Field f = fields[i];
				
				if( Atom.class.isAssignableFrom( f.getType() ) )
				{
						//  this is an atom
					try
					{
						int mod = f.getModifiers();
						
						if( !Modifier.isTransient( mod ) && !Modifier.isStatic( mod ) )
						{
								//  The field must be final, then, since if
								//  re-assignments are made we would have to update
								//  reference counts here, there, and everywhere, and
								//  that can't be guaranteed.
							/*
							**  We're going to permit this from now on in, 
							**  and just assume the coder knows what they're doing
							**  and doesn't replace these fields at random.
							**  Nov 11, 1999: pdm
							**
							if( !Modifier.isFinal( mod ) )
							{
									//  replace with exception
								System.err.println( "ERROR: Atomic Field " + f.toString() + " is not final or transient." );
							}
							*/
							
							try
							{
								Atom sub = (Atom) f.get( parent );
							
								if( sub == null )
								{
										//  TODO: replace with exception
									if( Modifier.isFinal( mod ) )
									{
										Log.error( "WARNING: Atomic Field " + f.toString() + " has a value of null, which isn't useful" );
										continue;
									}
									else
									{
										//  This is bad, but fix it
										Log.log( "factory", "WARNING: Atomic Field " + f.toString() + " had a value of null. (repaired)" );
										Atom a = Factory.makeAtom( f.getType(), f.getName() );
										f.set( parent, a );

										//  & restore sub
										sub = a;
									}
								}
								
								fs.processField( f, parent, sub );
							}
							catch( OutOfDateReferenceException e )
							{
								Log.error( "during field scan of " + parent.getId(), e );
							}
						}
					}
					catch( Exception e )
					{
						Log.error( "factory", e );
						throw new UnexpectedResult( e );  //  this line temporary
					}
				}
			}
		}
		
		{
			Class root;
			
			root = start.getSuperclass();
			
			if( root != Atom.class && root != null )
			{
				processFields( root, parent, fs );
			}
		}
	}

	public static final OnConstruct CONSTRUCTER = new OnConstruct();
	public static final DecrementImplicitRefs DECREMENTER = new DecrementImplicitRefs();
	public static final IncrementImplicitRefs INCREMENTER = new IncrementImplicitRefs();
	public static final DistinctSyncer DISTINCT_SYNCER = new DistinctSyncer();
	public static final NonTemporary NON_TEMPORARY = new NonTemporary();
	public static final PartDeleter PART_DELETER = new PartDeleter();
	public static final SyncOwner SYNC_OWNER = new SyncOwner();
	public static final SyncOwnerRecursive SYNC_OWNER_R = new SyncOwnerRecursive();
	
	public static final String tempFileExtension = ".tmp";
}

interface FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub );
}

class OnConstruct implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		sub.owner = parent.owner;
		sub.setKey( f.getName() );
		sub.setParent( parent, AtomicElement.PARENT_TYPE );
		sub.addReference( parent );
	}
}

class DecrementImplicitRefs implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		sub.removeReference( parent );
	}
}

/**
  *  the opposite of decrementImplicitRefs, but not to be used for
  *  creation, only for loading.  Typically called from Key.loadObject
 */
class IncrementImplicitRefs implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		if( sub != null )
			sub.addReference( parent );
	}
}

class DistinctSyncer implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		Registry.instance.syncDistinct( sub.index, sub.timestamp );
	}
}

class NonTemporary implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		sub.stopBeingTemporary();
	}
}

class PartDeleter implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		Registry.instance.delete( sub );
	}
}

class SyncOwner implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		sub.setOwner( parent.getOwner() );
	}
}

class SyncOwnerRecursive implements FieldScanner
{
	public void processField( Field f, Atom parent, Atom sub )
	{
		sub.setRecursiveOwner( parent.getOwner() );
	}
}
