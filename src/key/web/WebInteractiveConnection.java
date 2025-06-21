package key.web;

import key.*;
import java.io.PrintWriter;

public class WebInteractiveConnection extends InteractiveConnection
{
	PrintWriter output;
	public static final String CRLF = "\r\n";
	
	public WebInteractiveConnection( PrintWriter os )
	{
		output = os;
	}
	
	public void send( Paragraph p )
	{
		output.write( Converter.convert( p ) );
		output.flush();
	}
	
	public boolean isConnected()
	{
		return( true );
	}
	
	public boolean newbiesAllowed()
	{
		return( true );
	}
	
	public final void printStackTrace( Throwable t )
	{
		t.printStackTrace( output );
	}
	
	public String getSiteName()
	{
		return( "web" );
	}
	
	public final String getFullSiteName()
	{
		return( getSiteName() );
	}
	
	public final void discard()
	{
	}
	
	public String input()
	{
		return( "" );
	}
	
	public void parseToHTML( String message, boolean replaceCR )
	{
		output.write( Converter.convert( message, replaceCR ) );
	}
	
	public void check()
	{
	}
	
	public void run()
	{
	}
	
	public final void sendLine()
	{
		output.write( "<HR>" + CRLF );
	}
	
	public final String hiddenInput( String prompt )
	{
		return( input() );
	}
	
	public final String input( String prompt )
	{
		return( input() );
	}
	
	public final void blankLine()
	{
		output.write( "<BR>" + CRLF );
	}
	
	public final void sendSubliminal( String message, int duration, int frequency )
	{
	}

	public final void send( String message )
	{
		parseToHTML( message, true );
	}
	
	public final void send( String message, int a, int b, int c, int d, boolean r )
	{
		parseToHTML( message, true );
	}
	
	public final void sendRaw( String message )
	{
		parseToHTML( message, true );
	}

	public final void println( String message )
	{
		parseToHTML( message, true );
	}
	
	public final void send( char qualifier, String message )
	{
		parseToHTML( qualifier + message, true );
	}
	
	public final void send( Paragraph para, boolean okayToPage )
	{
		send( para );
	}
	
	public final boolean isPaging()
	{
		return( false );
	}
}
