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

package key.util;

import java.net.*;
import java.io.*;
import java.util.*;

public class Ident extends Thread
{
	public static final int DEFAULT_PORT = 113;
	
	Socket fromSocket;
	Socket identSocket;
	
	Callback back;
	Writer output;
	PushbackInputStream inputStream;
	
	public Ident( Socket s, Callback cb )
	{
		fromSocket = s;
		back = cb;
	}
	
	public void run()
	{
		Socket is = null;
		
		try
		{
			identSocket = new Socket( fromSocket.getInetAddress(), DEFAULT_PORT );
			
				//  give up after 30 seconds
			identSocket.setSoTimeout( 30 * 1000 );
			
			output = new BufferedWriter( new OutputStreamWriter( identSocket.getOutputStream() ) );
			inputStream = new PushbackInputStream( identSocket.getInputStream() );
			
				//  query the socket
			send( fromSocket.getPort() + "," + fromSocket.getLocalPort() );
			
			String result = input();
			
			StringTokenizer st = new StringTokenizer( result, ":" );
			
				//  skip ports
			if( !st.hasMoreTokens() )
				return;
			st.nextToken();
			
			if( !st.hasMoreTokens() )
				return;
			
			String responseType = st.nextToken().trim();
			
			if( responseType.equalsIgnoreCase( "USERID" ) )
			{
				String opsys = st.nextToken().trim();
				
				if( st.hasMoreTokens() )
					back.identResolvedUserId( opsys.toLowerCase(), st.nextToken("").trim() );
				else
					back.identProtocolError( result );
			}
			else if( responseType.equalsIgnoreCase( "ERROR" ) )
			{
				if( st.hasMoreTokens() )
					back.identResolvedError( st.nextToken().toLowerCase().trim() );
				else
					back.identProtocolError( result );
			}
			else
			{
				back.identProtocolError( result );
			}
		}
		catch( IOException e )
		{
			back.identProtocolError( e.getMessage() );
		}
		finally
		{
			try
			{
				if( output != null )
					output.close();
			}
			catch( Exception e )
			{
			}
			
			try
			{
				if( inputStream != null )
					inputStream.close();
			}
			catch( Exception e )
			{
			}
			
			try
			{
				if( is != null )
					is.close();
			}
			catch( Exception e )
			{
			}
		}
	}
	
	public static final String CRLF = "\r\n";
	public static final int INPUT_BUFFER = 640;
	public static final int MAX_OVERRUN = 640;
	
	private char[] inBuffer = new char[INPUT_BUFFER+2];
	private int lastCharWas = -1;
	
	public String input() throws IOException
	{
		int where=0;
		char b=' ';
		
		do
		{
			int i;
			
				//  continuously skip until a CR or LF
				//  when the buffer runs out
			if( where >= INPUT_BUFFER )
			{
				int count = 0;
				do
				{
					i = inputStream.read();
					count++;
				} while( !( i == '\r' || b == '\n' ) && (count < MAX_OVERRUN) );
				
				if( count == MAX_OVERRUN )
					throw new IOException( "max overrun reached" );
			}
			else
				i = inputStream.read();
			
			if( i != -1 )
				b = (char) i;
			else
				throw new IOException( "connection closed" );
			
			if( b >= 32 && b <= 126 )    //  valid ascii characters only
				inBuffer[where++] = (char) b;
			else if( b == 8 || b == 127 )
			{
				if( where > 0 )
					where--;
			}
		} while( !((b == '\r') && !(lastCharWas == '\n')) && !((b == '\n') && !(lastCharWas == '\r' )) );
		
		lastCharWas = b;
		
		return( new String( inBuffer, 0, where ) );
	}
	
	public void send( String message ) throws IOException
	{
		output.write( message );
		output.write( CRLF );
		output.flush();
	}
	
	public interface Callback
	{
		public void identResolvedError( String error );
		public void identProtocolError( String error );
		public void identResolvedUserId( String machine, String user );
	}
}
