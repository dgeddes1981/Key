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
**  $Id: Log.java,v 1.2 1999/10/12 16:52:31 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Nov98     subtle       created
**
*/

package key;

import key.primitive.*;
import key.collections.*;

import java.net.*;
import java.io.*;
import java.util.*;

import key.config.BaseConfiguration;

/**
 */
public class Log
{
	public static boolean DEBUG_MODE = false;
	public static Log instance;
	
		//  directories
	protected static transient File logsBasePath;
	protected static transient File clanLogsBasePath;
	
	public Log()
	{
		instance = this;
		
		//logsBasePath = Key.confirmedDirectory( "logs" );
		//clanLogsBasePath = Key.confirmedDirectory( "logs/clans" );
	}
	
	/**
	  *  A generalised debugging log function
	  *
	  * @param message The message to store
	 */
	public static final void debug( Object from, String message )
	{
		if( from instanceof Class )
			debug( ((Class)from).getName(), message );
		else
			debug( Type.typeOf( from ).getName(), message );
	}

	public static final void currentPlayer( String message )
	{
		Player p = Player.getCurrent();
		
		if( p != null )
		{
			InteractiveConnection ic = p.getNullConnection();
			if( ic != null )
			{
				ic.send( '|', "(FYI): " + message );
				return;
			}
		}
		
		debug( "failedCurrentPlayer", message );
	}
	
	private static final String ERROR_PRE =
		"---  ERROR  --------------------------------------";
	private static final String ERROR_POST =
		"--------------------------------------  ERROR  ---";
	
	/**
	  *  A handled-error has occurred.  The message (and a stack
	  *  trace) should be logged for later examination (and output
	  *  to System.err), but no other action need be taken.  This
	  *  is a more serious condition than a 'debug' message (since
	  *  debug messages may occur often and no stack trace is associated
	  *  with them).
	 */
	public static final void error( String message, Throwable e )
	{
		//log( ERROR, ERROR_PRE );
		//log( ERROR, message );
		
		if( e instanceof ThreadDeath )
			throw ((ThreadDeath) e);
		
		System.out.println( ERROR_PRE );
		System.out.println( message );
		
		System.out.println();

		writeExceptionToLog( ERROR, ERROR_PRE + "\n" + message, ERROR_POST, e );
		e.printStackTrace();
		
		System.out.println( ERROR_POST );
	}
	
	public static final void error( Throwable e )
	{
		//log( ERROR, ERROR_PRE );
		
		if( e instanceof ThreadDeath )
			throw ((ThreadDeath) e);
		
		System.out.println( ERROR_PRE );
		
		writeExceptionToLog( ERROR, ERROR_PRE, ERROR_POST, e );
		e.printStackTrace();
		
		//log( ERROR, ERROR_POST );
		System.out.println( ERROR_POST );
	}
	
	public static final void error( String message )
	{
		try
		{
			throw new Exception();
		}
		catch( Exception e )
		{
			error( message, e );
		}
	}
	
	/**
	  *  Use for static routines only, please
	 */
	public static final void debug( String name, String message )
	{
		log( DEBUG, name + " - " + message );
	}
	
	public static void fatal( String from, String message )
	{
		bootLog( Type.typeOf( from ).getName() + " -FATAL- " + message );
		
		if( !Key.isRunning() )
		{
			System.exit( 1 );
		}
		else
		{
				//  This is a more serious error - something has triggered
				//  a fatal failure while the program was running
			
			try
			{
				Key.instance().sync();
			}
			catch( Throwable t )
			{
				debug( t.getClass().getName(), t.getMessage() );
			}
			finally
			{
				System.exit( 1 );
			}
		}
	}
	
	/**
	  *  A generalised fatal error message
	 */
	public static void fatal( Object from, String message )
	{
		fatal( Type.typeOf( from ).getName(), message );
	}
	
	public static final File getLogsBasePath()
	{
		return( logsBasePath );
	}

	/**
	  *  This is a global variable that can be used
	  *  by any command to indicate that _everything_
	  *  on the program should start doing verbose
	  *  logging.  At any given point in time, there
	  *  should be only _one_ section of code that
	  *  can turn this flag on.
	 */
	public static boolean verbose = false;
	
		/**  A log used for global debug messages */
	private static final String DEBUG = "debug";
	
	private static final String ERROR = "error";
	
		/**  A log used for bootup messages */
	private static final String BOOT = "boot";
	
	/**
	  *  A special type of log call which sends the output to the
	  *  boot log (if available), but will also always send the
	  *  message to System.err
	 */
	public static final void bootLog( String message )
	{
		Key k = Key.instance();
		
		if( !DEBUG_MODE )
			System.out.println( "[" + BOOT + "]: " + message );
		
		if( k != null && logsBasePath != null )
			writeLog( BOOT, message );
	}
	
	/**
	  *  A log routine which will send the message to the
	  *  supplied log file, if available, or System.out
	  *  otherwise.
	 */
	public static void log( String logKey, String message )
	{
		Key k = Key.instance();

		if( k != null && logsBasePath != null )
		{
			writeLog( logKey, message );
		}
		else
			System.out.println( "[" + logKey + "]: " + message );
	}
	
	private static String cachedKey = null;
	private static RandomAccessFile cachedRAF = null;
	
	/**
	  *  Actually write to the log file
	 */
	private static synchronized final void writeLog( String logKey, String message )
	{
		try
		{
			if( logKey != cachedKey )
			{
				if( cachedRAF != null )
					cachedRAF.close();
				
				cachedRAF = new RandomAccessFile( new File( logsBasePath, logKey ), "rw" );
				cachedRAF.seek( cachedRAF.length() );
				cachedKey = logKey;
			}
			
			cachedRAF.writeBytes( new DateTime().toString() + ": " + message + "\n" );
			
			
		}
		catch( IOException e )
		{
			cachedRAF = null;
			cachedKey = null;
			
			System.out.println( "(write to log error: " + e.getMessage() + ") - [" + logKey + "]: " + message );
			
			return;
		}
		
		if( DEBUG_MODE )
			System.out.println( "[" + logKey + "]: " + message );
	}
	
	private static synchronized final void writeExceptionToLog( String logKey, String pre, String post, Throwable exce )
	{
		logKey = "exceptions/" + exce.getClass().getName();
		
		try
		{
			if( logKey != cachedKey )
			{
				if( cachedRAF != null )
					cachedRAF.close();
				
				cachedRAF = new RandomAccessFile( new File( logsBasePath, logKey ), "rw" );
				cachedRAF.seek( cachedRAF.length() );
				cachedKey = logKey;
			}
			
			cachedRAF.writeBytes( pre + "\n" );

			
			PrintWriter pw = new PrintWriter( new FileOutputStream( cachedRAF.getFD() ) );
			
				//  get the current player
			try
			{
				pw.println( "Date: " + ((new Date()).toString()) );
				Player p = Player.getCurrent();
				if( p != null )
				{
					pw.println( "Current player: " + p.getName() );
					pw.println( "Command Line: '" + p.getLastCommandExecuted() + "'" );
					pw.println();
				}
			}
			catch( Throwable t )
			{
				if( t instanceof ThreadDeath )
					throw ((ThreadDeath)t);
				
				t.printStackTrace();
			}
			
			exce.printStackTrace( pw );
			pw.flush();
			cachedRAF.seek( cachedRAF.length() );
			cachedRAF.writeBytes( post + "\n" );
			pw.close();
			cachedRAF.close();
			cachedRAF = null;
			cachedKey = null;
		}
		catch( IOException e )
		{
			cachedRAF = null;
			cachedKey = null;
			
			System.out.println( "(exception write to log error: " + e.getMessage() + ") - [" + logKey + "]: " );
			e.printStackTrace();
			exce.printStackTrace();
			
			return;
		}
	}
	
	public static final FileReader viewLog( String logName ) throws IOException
	{
		if( logName == null || logName.length() == 0 )
			return( null );
		
			//  check permissions: META: Add a permission check in here for
			//  who may read log files at will.
		
		File logFile = new File( logsBasePath, logName );
		String lfc = logFile.getCanonicalPath();
		
			//  check to see if this is a valid path
		if( lfc.startsWith( logsBasePath.getCanonicalPath() ) )
		{
				//  this is okay
			return( new FileReader( logFile ) );
		}
		else
		{
			Player p = Player.getCurrent();
			
			if( p != null )
				Log.log( "security", "ViewLog: Attempt to read file '" + lfc + "' while running as player " + p.getName() + "." );
			else
				Log.log( "security", "ViewLog: Attempt to read file '" + lfc + "' while running as a system process" );
			
			throw new AccessViolationException( Log.class, "trying to read a logfile outside the logs directory" );
		}
	}

	static void sync()
	{
			//  shutdown logging
		if( cachedRAF != null )
		{
			try
			{
				cachedRAF.close();
			}
			catch( IOException e )
			{
			}
			
			cachedRAF = null;
			cachedKey = null;
		}
	}
}
