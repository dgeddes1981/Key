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

import java.net.Socket;
import java.net.InetAddress;
import java.io.*;
import java.util.Hashtable;

/**
  *  all output from the program is classified
  *  into one of the 'types' of data that is
  *  being fed back.  This not only allows a
  *  great flexibility in the output method
  *  used on a telnet connection, but it allows
  *  for the easy expansion of connections to
  *  support applet clients or other frontends
  *  that require code support.
  *
  *  This class supports several 'codes' within the
  *  strings you pass to output to the program.
  *
  *  <ul>
  *  <li><i>\n</i>:    To mean 'start a new line'
  *  </ul>
 */
public abstract class InteractiveConnection
extends AnimatedAtom
{
	private transient Player player;
	private transient Interactive interacting = null;
	
	/**
	  *  The constructor must take a socket as its sole
	  *  argument
	  *
	  * @param s The socket that the connection is on
	 */
	public InteractiveConnection()
	{
		setKey( "IC" );
	}
	
	public void interactWith( Interactive i )
	{
		interacting = i;
	}

	public void run()
	{
		try
		{
			while( interacting != null && isAlive() && isConnected() )
			{
				interacting.run( this );
			}
		}
		catch( Throwable e )
		{
			if( isConnected() )
			{
				try
				{
					send( "Sorry, an error occurred.  Closing connection.\n" );
				}
				catch( NetworkException ne )
				{
				}
				
				Log.error( e );
			}
		}
		finally
		{
			close();
		}
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		throw new UnexpectedResult( "attempting to de-serialise InteractiveConnection" );
	}
	
	private void writeObject( ObjectOutputStream ois ) throws IOException
	{
		throw new UnexpectedResult( "attempting to serialise InteractiveConnection" );
	}
	
	public String getName()
	{
		if( player != null )
			return( super.getName() + " for " + player.getName() );
		else
			return( super.getName() );
	}
	
	public final void startUnIdling( Player p )
	{
		player = p;
		p.unIdle();
	}
	
	void stopBeingTemporary()
	{
		//  do nothing, we always want to be temporary
	}
	
	public abstract boolean isConnected();
	public abstract boolean newbiesAllowed();
	public abstract void printStackTrace( Throwable t );
	public abstract String getFullSiteName();
	
	public String getPrivateSiteName()
	{
		return( getFullSiteName() );
	}
	
	public abstract String getSiteName();
	
	public void flush()
	{
	}
	
	/**
	  *  The intention is to provide a method that only calls flush()
	  *  on the stream if the socket is waiting for input.
	 */
	public void flushIfWaiting()
	{
		flush();
	}
	
	/**
	  *  Throw away all incoming input.
	 */
	public abstract void discard() throws IOException;
	
	public final void stopUnIdling()
	{
		player = null;
	}
	
	public Player getPlayer()
	{
		return( player );
	}
	
	public Interactive getInteracting()
	{
		return( interacting );
	}
	
	/**
	  *  Specifies that this ic is in 'verbose'
	  *  mode - that is, it will recieve newbie
	  *  help instrctions and so on.
	 */
	public boolean verbose()
	{
		return( true );
	}
	
	/**
	  *  The sub-classes should call this routine everytime
	  *  the player puts some input into the program
	 */
	protected final void unIdle()
	{
		if( player != null )
			player.unIdle();
	}
	
	public synchronized void close()
	{
		interactWith( null );
	}
	
	/**
	  *  draw a line across the screen
	 */
	public abstract void sendLine();
	
	/**
	  *  beeps the terminal, if supported.
	 */
	public void beep()
	{
	}
	
	/**
	  * This function should block until it can
	  * return a complete string of the player's
	  * input.  It can then be used in sequential
	  * code without complications with loops.
	  *
	  * @param prompt The prompt that the player sees (eg "-> ")
	  * @return the string that the player entered.  This string is not \n terminated
	 */
	public abstract String input( String prompt );
	
	/**
	  * This function is very similar to
	  * input(), above, except that it should be
	  * used to *not* echo text as it's entered,
	  * mainly for passwords
	 */
	public abstract String hiddenInput( String prompt );
	
	/**
	  *  leave some blank space (usually the height of the characters
	 */
	public abstract void blankLine();
	
	public abstract void sendSubliminal( String message, int duration, int frequency );
	
	public abstract void sendRaw( String message );
	public abstract void send( String message );
	public abstract void send( char qualifier, String message );
	public abstract void send( Paragraph para );
	public abstract void send( Paragraph para, boolean okayToPage );
	public abstract void send( String message, int li, int ri, int lfi, int rfi, boolean stripLeadingSpaces );
	
	public final void sendSystem( String message )
	{
		send( message );
	}
	
	public void sendEffect( char preChar, String message, Effect e )
	{
		if( preChar == ' ' )
			send( message );
		else
			send( preChar, message );
	}
	
	public void blankLines( int c )
	{
		while( c-- > 0 )
			blankLine();
	}
	
	/**
	  * This function is used to output direct,
	  * *successful* feedback of a players command.
	  * It is important that this feedback is normal
	  * and expected by the player, since it generally
	  * won't be hilited in any way.  Please try
	  * to restrict this output to a single line.
	  * <p>
	  * this command does start a new line
	  * <p>
	  * Examples:
	  * <p>
	  * You tell name 'hi' <br>
	  *
	  * @param message the text to be displayed
	 */
	public void sendFeedback( String message )
	{
		send( message );
	}
	
	/**
	  * This function is used when the user makes some
	  * sort of error in typing - *not* if a command
	  * can't be executed because of a failure.  So,
	  * Command not found would go here, but 'Not enough
	  * privs to nuke snapper' wouldn't.
	  * <p>
	  * this command does start a new line
	 */
	public void sendError( String message )
	{
		send( message );
	}
	
	/** META: remove */
	public void sendFailure( String message )
	{
		send( message );
	}
	
	public void send( Screen s )
	{
		send( s.text );
	}
	
		/**  returns true if we need to output a 'paging' prompt */
	public abstract boolean isPaging();
}
