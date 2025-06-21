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
**  $Id: DumbIC.java,v 1.1 1999/10/11 13:25:05 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.io.*;
import java.util.Stack;
import java.util.Enumeration;

/**
 */
public abstract class DumbIC
extends InteractiveConnection
{
	transient PrintStream ps;
	
	/**
	  *  The constructor must take a socket as its sole
	  *  argument
	  *
	  * @param s The socket that the connection is on
	 */
	public DumbIC()
	{
		setKey( "console" );
	}
	
	protected void setPrintStream( PrintStream p )
	{
		ps = p;
	}
	
	public void stopBeingTemporary()
	{
		//  do nothing, we always want to be temporary
	}
	
	public String getSiteName()
	{
		return( "dumb terminal" );
	}
	
	public final boolean newbiesAllowed()
	{
		return( true );
	}
	
	public final void printStackTrace( Throwable t )
	{
		t.printStackTrace();
	}
	
	public String getFullSiteName()
	{
		return( getSiteName() );
	}
	
	public void discard()
	{
	}

	public synchronized void close()
	{
		super.close();
		
		if( ps != null )
			ps = null;
	}
	
	/**
	  * return true if the socket is still open
	 */
	public boolean isConnected()
	{
		if( ps != null )
			return true;
		else
			return false;
	}
	
	public void sendLine()
	{
		if( ps != null )
			ps.println( "----------------------------------------------------------------------------" );
	}
	
	public void blankLine()
	{
		if( ps != null )
			ps.println();
	}
	
	public void send( String message )
	{
		if( ps != null )
			ps.println( message );
	}
	
	public void send( String message, int li, int ri, int lfi, int rfi, boolean stripLeadingSpaces )
	{
		if( ps != null )
			ps.println( message );
	}
	
		//  failure is always output
	public void sendFailure( String message )
	{
		System.err.println( message );
	}
	
	public void sendRaw( String message )
	{
		if( ps != null )
			ps.println( message );
	}
	
	public void send( char qual, String message )
	{
		if( ps != null )
			ps.println( message );
	}
	
	public void send( Paragraph p )
	{
		send( p, false );
	}
	
	public void send( Paragraph p, boolean j )
	{
		if( ps != null )
		{
			if( p instanceof TextParagraph )
				ps.println( ((TextParagraph)p).getText() );
			else if( p instanceof HeadingParagraph )
				ps.println( "--- " + ((HeadingParagraph)p).getText() + " ---" );
			else if( p instanceof LineParagraph )
				sendLine();
			else if( p instanceof BlankLineParagraph )
				blankLine();
			else if( p instanceof MultiParagraph )
			{
				for( Enumeration e = ((MultiParagraph)p).getParagraphs(); e.hasMoreElements(); )
				{
					Paragraph o = (Paragraph) e.nextElement();
					send( o, j );
				}
			}
			else if( p instanceof ColumnParagraph )
			{
				for( Enumeration e = ((ColumnParagraph)p).elements(); e.hasMoreElements(); )
				{
					ps.println( (String) e.nextElement() );
				}
			}
			else if( p instanceof TableParagraph )
			{
				ps.println( "--- table paragraph type sent ---" );
			}
			else
				ps.println( "--- unknown paragraph type sent ---" );
		}
	}
	
	public boolean isPaging()
		{ return( false ); }
	
	public void sendSubliminal( String message, int duration, int frequency )
	{
	}
}
