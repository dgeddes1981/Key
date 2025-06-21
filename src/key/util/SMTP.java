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
**  $Id: SMTP.java,v 1.7 2000/06/26 16:33:46 subtle Exp $
**  
**    This code was originally written by
**    James Driscoll, maus@io.com
**
**    I've 'tidied' it up a little.  The
**    copy I had didn't work. *shrug*
**  
**  $Log: SMTP.java,v $
**  Revision 1.7  2000/06/26 16:33:46  subtle
**  removed some more printlns
**
**  Revision 1.6  2000/03/29 15:43:56  subtle
**  Removed System.out.println(), whoops
**
**  Revision 1.5  2000/02/24 18:21:48  subtle
**  http://pobox.com/~djb/docs/smtplf.html problem FIXED. Yay
**
**
*/

package key.util;

import java.net.*;
import java.io.*;
import java.util.*;

public class SMTP
{
	public static final int DEFAULT_PORT = 25;
	
	public static final String CRLF = "\r\n";

	protected DataInput reply = null;
	protected BufferedWriter send = null;
	protected Socket sock = null;

	public SMTP( String hostid ) throws UnknownHostException, IOException
	{
		this( hostid, DEFAULT_PORT );
	}

	public SMTP( String hostid, int port ) throws UnknownHostException, IOException
	{
		sock = new Socket( hostid, port );
		reply = new DataInputStream( sock.getInputStream() );
		send = new BufferedWriter( new OutputStreamWriter( sock.getOutputStream() ) );
		
		String recv = readLine();
		if( !recv.startsWith( "2" ) )
			throw new ProtocolException( recv );

		while( recv.indexOf( '-' ) == 3 )
		{
			recv = readLine();
			if( !recv.startsWith( "2" ) )
			throw new ProtocolException( recv );
		}
	}

	public SMTP( InetAddress address ) throws IOException
	{
		this( address, DEFAULT_PORT );
	}

	public SMTP( InetAddress address, int port ) throws IOException
	{
		sock = new Socket( address, port );
		reply = new DataInputStream( sock.getInputStream() );
		send = new BufferedWriter( new OutputStreamWriter( sock.getOutputStream() ) );

		String recv = readLine();

		if( !recv.startsWith( "2" ) )
			throw new ProtocolException( recv );
		
		while( recv.indexOf( '-' ) == 3 )
		{
			recv = readLine();
			if( !recv.startsWith( "2" ) )
			throw new ProtocolException( recv );
		}
	}

	public void send( String from_address, String to_address, String subject, String message ) throws IOException, ProtocolException
	{
		String recv;

		println( "HELO smtp" );
		recv = readLine();
		if( !recv.startsWith( "250" ) )
			throw new ProtocolException(recv);

		println( "MAIL FROM: " + from_address );

		recv = readLine();
		if( !recv.startsWith( "250" ) )
			throw new ProtocolException(recv);

		println( "RCPT TO: " + to_address );
		recv = readLine();
		if( !recv.startsWith( "250" ) )
			throw new ProtocolException(recv);
		
		println("DATA");
		recv = readLine();
		if( !recv.startsWith( "354" ) )
			throw new ProtocolException( recv );
	
		println( "From: " + from_address );
		println( "To: " + to_address );
		println( "Subject: " + subject );
		println( "Date: " + msgDateFormat( new Date() ) );
		println( "Comment: Automated message" );
		println( "X-Mailer: Key Smtp" );
		
			// Sending a blank line ends the header part.
		println();
		
			// Now send the message proper
		StringTokenizer lines = new StringTokenizer( message, "\n" );
		
		while( lines.hasMoreTokens() )
		{
			println( lines.nextToken() );
		}
		println();
		println( "." );
		recv = readLine();
		if( !recv.startsWith( "250" ) )
			throw new ProtocolException( recv );
	}

	protected void finalize() throws Throwable
	{
		println( "QUIT" );
		sock.close();
		super.finalize();
	}

	private static String Day[] =
	{
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
	};
	
	private static String Month[] =
	{
		"Jan", "Feb", "Mar", "Apr", "May", "Jun",
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};

	private final String msgDateFormat( Date senddate )
	{
		StringBuffer formatted = new StringBuffer();
		
		formatted.append( Day[ senddate.getDay() ] );
		formatted.append( ", " );
		formatted.append( String.valueOf( senddate.getDate() ) );
		formatted.append( " " );
		formatted.append( Month[ senddate.getMonth() ] );
		formatted.append( " " );
		
		if( senddate.getYear() > 99 )
			formatted.append( String.valueOf( senddate.getYear() + 1900 ) );
		else
			formatted.append( String.valueOf( senddate.getYear() ) );

		formatted.append( " " );

		appendLeadingZeroNumber( senddate.getHours(), formatted );
		formatted.append( ":" );
		
		appendLeadingZeroNumber( senddate.getMinutes(), formatted );
		formatted.append( ":" );
		
		appendLeadingZeroNumber( senddate.getSeconds(), formatted );
		formatted.append( " " );
		
		if( senddate.getTimezoneOffset() < 0 )
			formatted.append( "+" );
		else
			formatted.append( "-" );
		
		appendLeadingZeroNumber( senddate.getTimezoneOffset()/60, formatted );
		appendLeadingZeroNumber( senddate.getTimezoneOffset()%60, formatted );
		
		return( formatted.toString() );
	}

	private final void appendLeadingZeroNumber( int num, StringBuffer sb )
	{
		if( num < 10 )
			sb.append( "0" );

		sb.append( String.valueOf( num ) );
	}

	private void println() throws IOException
	{
		//System.out.println();
		send.write( CRLF );
		send.flush();
	}

	private String readLine() throws IOException
	{
		String r = reply.readLine();

		//System.out.println( r );
		return r;
	}
	
	private void println( String message ) throws IOException
	{
		send.write( message );
		send.write( CRLF );
		send.flush();
	}
}
