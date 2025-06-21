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
** $Id: SocketIC.java,v 1.9 2000/07/28 13:38:06 pdm Exp $
**
** $Log: SocketIC.java,v $
** Revision 1.9  2000/07/28 13:38:06  pdm
** compile error, whoops
**
** Revision 1.8  2000/07/12 17:06:56  pdm
** fixed: java.lang.NullPointerException:
**     at key.SocketIC$1.identResolvedUserId(SocketIC.java:153)
**     at key.util.Ident.run(Ident.java:71)
**
*/

package key;

import java.net.Socket;
import java.net.InetAddress;
import java.io.*;
import java.util.Hashtable;

/**
 */
public abstract class SocketIC
extends InteractiveConnection
{
	public static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( SocketIC.class, Integer.TYPE, "localPort",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the local port number of this connection" ),
		AtomicElement.construct( SocketIC.class, Integer.TYPE, "remotePort",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the remote port number of this connection" ),
		AtomicElement.construct( SocketIC.class, String.class, "address",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the address that this connection is to" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( InteractiveConnection.STRUCTURE, ELEMENTS );
	public static final int MAXIMUM_CONNECTIONS_OPEN = 200;
	private static int connectionCount = 0;
	
	Socket socket;
	Writer output;
	OutputStream rawOutput;
	PushbackInputStream inputStream;
	String maskedSiteName = "[not connected]";
	
	public int getLocalPort() { return( socket.getLocalPort() ); }
	public int getRemotePort() { return( socket.getPort() ); }
	public String getAddress() { return( socket.getInetAddress().toString() ); }
	
	String identString = "";
	
	/**
	  *  The constructor must take a socket as its sole
	  *  argument
	  *
	  * @param s The socket that the connection is on
	 */
	public SocketIC( Socket s ) throws IOException
	{
		socket = s;
		connectionCount++;
		
		try
		{
			rawOutput = socket.getOutputStream();
			output = new BufferedWriter( new OutputStreamWriter( rawOutput ) );
			inputStream = new PushbackInputStream( socket.getInputStream() );
		}
		catch( IOException e )
		{
			close();
			throw e;
		}
		
		if( connectionCount > MAXIMUM_CONNECTIONS_OPEN )
		{
			close();
			throw new LimitExceededException( "too many open connections" );
		}
		
		InetAddress inetAddress = s.getInetAddress();
		//Log.debug( this, "Connected to: " + inetAddress.getHostName() +":"+ socket.getPort() );
		
		//Internet inet = Key.getInternet();
		byte[] baddress = inetAddress.getAddress();
		int[] address = new int[ baddress.length ];
		StringBuffer sb = null;
		for( int i = 0; i < baddress.length; )
		{
			if( baddress[i] < 0 )
				address[i] = 256 + baddress[i];
			else
				address[i] = baddress[i];
			
			i++;
			if( i < baddress.length )
			{
				if( sb == null )
					sb = new StringBuffer();
				else
					sb.append( '.' );
				
				sb.append( address[i-1] );
			}
		}
		
		maskedSiteName = sb.toString();
		sb = null;
		
		String dns = inetAddress.getHostName();

		if( dns.equalsIgnoreCase( maskedSiteName + "." + address[baddress.length-1] ) )
			dns = "<unresolved>";
		
		{
			int i;
			i = dns.indexOf( '.' );
			if( i != -1 )
			{
				try
				{
					if( !Character.isDigit( dns.charAt( 0 ) ) )
						dns = dns.substring( i+1 );
				}
				catch( Exception e )
				{
					Log.error( "in SocketIC during dns substring, ignoring", e );
				}
			}
		}
		
		maskedSiteName = dns + " [" + maskedSiteName + "]";
		
		if( key.Main.SOCKET_IDENT_LOOKUP )
		{
			new key.util.Ident( socket, new key.util.Ident.Callback()
			{
				public void identResolvedError( String error )
				{
					identString = "<" + error + "> ";
				}
				
		        public void identProtocolError( String error )
				{
					identString = "<" + error + "> ";
				}
				
		        public void identResolvedUserId( String machine, String user )
				{
					String sId;
					if( socket != null )
						sId = socket.getInetAddress().getHostName() + "|" + socket.getPort();
					else
						sId = "CLOSED";
					
					identString = "<" + machine + "> " + user + "@";
					Log.log( "trace", "IDENT|" + identString + sId );
				}
			} ).start();
		}
	}
	
	public String getName()
	{
		return( super.getName() + " " + getFullSiteName() );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public final String getSiteName()
	{
		return( socket.getInetAddress().getHostName() );
	}

	public final boolean newbiesAllowed()
	{
		return( true );
	}
	
	public void flush()
	{
		try
		{
			output.flush();
		}
		catch( IOException e )
		{
			throw new NetworkException( e );
		}
	}
	
	public final String getPrivateSiteName()
	{
		if( socket == null )
			return( "[not connected]" );
		InetAddress ia = socket.getInetAddress();
		if( ia == null )
			return( "[not connected]" );
			
		return( identString + ia.getHostName() + " [" + ia.getHostAddress() + "] /" +socket.getPort() );
	}
	
	public final String getFullSiteName()
	{
		if( socket != null )
		{
			//  the old way:
			//InetAddress ia = socket.getInetAddress();
			//return( ia.getHostName() + " [" + ia.getHostAddress() + "]" );
			//  the new way (with the last IP number dropped from the listing)
			return( maskedSiteName );
		}
		
		return( "[not connected]" );
	}
	
	public final String getSiteIP()
	{
		InetAddress ia = socket.getInetAddress();
		return( ia.getHostAddress() );
	}
	
	public final void discard() throws IOException
	{
		inputStream.skip( inputStream.available() );
	}

	public final void printStackTrace( Throwable t )
	{
		PrintWriter pw = new PrintWriter( output );
		t.printStackTrace( pw );
		pw.flush();
	}
	
	public void finalize() throws Throwable
	{
		super.finalize();
		
		if( socket != null )
		{
			try
			{
				socket.close();
			}
			catch( IOException e )
			{
			}
			finally
			{
				socket = null;
				connectionCount--;
			}
		}
	}
	
	public synchronized void close()
	{
		super.close();
		
		if( socket != null )
		{
			try
			{
				socket.close();
			}
			catch( Exception t )
			{
			}
			finally
			{
				if( socket != null )
				{
					socket = null;
					connectionCount--;
				}
				
				Registry.instance.delete( this );
			}
		}
	}
	
	/**
	  * return true if the socket is still open
	 */
	public boolean isConnected()
	{
		if( socket != null )
			return true;
		else
			return false;
	}
	
	/**
	  *  This function will be called every 'now and
	  *  again', as a 'check' function when no input
	  *  is required (we're not waiting), but some
	  *  is allowed.  This is useful for the telnet
	  *  protocol in particular, which can recieve
	  *  IAC's before the login prompt.  This function
	  *  should be harmless, no matter when its called
	 */
	public abstract boolean check() throws IOException;
}
