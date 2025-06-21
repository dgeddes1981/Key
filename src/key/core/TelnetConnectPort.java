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
**  $Id: TelnetConnectPort.java,v 1.4 2000/02/25 13:58:25 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.net.*;
import java.io.*;
import java.util.Vector;

/**
  *  This class is a thread which opens and binds a port,
  *  creating new instances of 'InteractiveConnectionion' to
  *  deal with each connection request that comes in.
 */

public class TelnetConnectPort extends Daemon
{
	private static final long serialVersionUID = -9053728784766596365L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( TelnetConnectPort.class, Integer.TYPE, "port",
			AtomicElement.PUBLIC_FIELD | AtomicElement.READ_ONLY,
			"the port number connections are being accepted on" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Daemon.STRUCTURE, ELEMENTS );
	
		//  sleep for 5 minutes when we exceed our limit of connections
	public static final int SLEEP_DURATION = 5 * 60 * 1000;
	
	public int port = 2809;
	private transient ServerSocket socket = null;
	
	public TelnetConnectPort()
	{
	}
	
	public TelnetConnectPort( int portNumber )
	{
		port = portNumber;
	}
	
	public TelnetConnectPort( Object key, int portNumber )
	{
		port = portNumber;
		setKey( key );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void argument( String args )
	{
		if( args.length() > 0 )
		{
			try
			{
				port = Integer.parseInt( args );
			}
			catch( NumberFormatException e )
			{
				throw new IllegalArgumentException( "'" + args + "' is not an integer port number" );
			}
		}
	}
	
	public void run()
	{
		while( true )
		{
			try
			{
				serveRequests();
			}
			catch( Throwable t )
			{
				if( t instanceof ThreadDeath )
					throw (ThreadDeath) t;
				else
					Log.error( t );
			}
		}
	}
	
	private static final String CONNECTION_LOG = "connection";
	
	private void serveRequests()
	{
		if( socket != null )
		{
			try
			{
				socket.close();
			}
			catch( Exception e )
			{
			}
			
			socket = null;
		}
		
		try
		{
			socket = new ServerSocket( port );
		}
		catch( java.io.IOException e )
		{
			Log.error( e );
			stop();
		}
		
		InetAddress ourAddress = socket.getInetAddress();
		
		String hn = ourAddress.getHostName();
		Log.log( CONNECTION_LOG, "'" + hn + " " + port + "' initialised" );
		
		TelnetIC tc = null;
		ConnectingPlayer cp = null;
		Socket thisConnection = null;
		
		try
		{
			while( true )
			{
				try
				{
					thisConnection = socket.accept();
				}
				catch( java.net.SocketException e )
				{
						//  this almost certainly means we've been killed
					socket.close();
					Log.error( e );
					break;
				}
				
				//  not all these options are supported on all platforms,
				//  but we want to try each of them in case:
				
				try
				{
						//  linger for up to three seconds
					thisConnection.setSoLinger( true, 300 );
				}
				catch( java.net.SocketException e )
				{
				}
				
				try
				{
						//  timeout in an hour of no data
					thisConnection.setSoTimeout( 60 * 60 * 1000 );
				}
				catch( java.net.SocketException e )
				{
				}
					
				try
				{
						//  ensure we're waiting for packets
					thisConnection.setTcpNoDelay( false );
				}
				catch( java.net.SocketException e )
				{
				}
				
				try
				{
					tc = new TelnetIC( thisConnection );
				}
				catch( NetworkException e )
				{
					Log.log( "network", e.toString() );
					continue;
				}
				catch( IOException e )
				{
					Log.log( "network", e.toString() );
					continue;
				}
				catch( LimitExceededException e )
				{
						//  generally means we've exceeded our
						//  limit of sockets, or something
						//  really funny has happened
					try
					{
						Thread.sleep( SLEEP_DURATION );
					}
					catch( InterruptedException ex )
					{
					}
					
					continue;
				}
				catch( Exception e )
				{
					e.printStackTrace();
					Log.debug( this, e.toString() + " during accept" );
					continue;
				}
				
				cp = new ConnectingPlayer();
				cp.setPortFrom( port );
				tc.interactWith( cp );
				tc.start();
				
					//  clean up our references so that they
					//  can be garbage collected
				cp = null;
				tc = null;
				thisConnection = null;
				
				yield();
			}
		}
		catch( Exception e )
		{
			if( Key.isRunning() )
			{
				Log.error( "during scan", e );
			}
		}
		catch( Error e )
		{
			Log.error( e );
			throw e;
		}
	}
}
