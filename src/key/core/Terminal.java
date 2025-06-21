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
*/

package key;

import key.util.LinkedList;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.Writer;
import java.io.IOException;

/**
  *  This class defines the basic abstract implementation of
  *  a terminal type.  It's specific to a TelnetConnection,
  *  since the other (custom) terminals will need changes at
  *  a much higher level.
 */
public class Terminal
{
	/**
	  *  The maximum expected colours on a line.
	  * <br>
	  *  If this limit is exceeded, then the ^-
	  *  operator won't work correctly - for
	  *  efficiency reasons we're simply going
	  *  to write over the top part of the stack.
	  * <br>
	  *  If this becomes a problem in the future,
	  *  (all those F&$#*(&#$ gits who set their
	  *  emails "all pretty colours" for instance,
	  *  just increase this stack size.
	 */
	public static final int STACK_SIZE = 15;
	
	/**
	  *  The position that was marked.  If no-where
	  *  has been marked, this will be -1.  If you
	  *  mark, you can't remark something until
	  *  the terminal has been reset (like what
	  *  happens when there's a new paragraph)
	  * <br>
	  *  Its meant to be used in, for example, the
	  *  'say' command.  You don't want the users
	  *  stuffed up colour codes wrecking your
	  *  colouring for the end quote, so right after
	  *  the open quote you mark, then restore mark
	  *  just before the end quote.  This means
	  *  no matter *what* happens, you can always
	  *  get back to where you marked.
	  * <br>
	  *  Don't mark when you're > the stack size.
	  * <br>
	  *  Again - if you run out of stack, bad things *will
	  *  happen*. ;)
	 */
	protected int marked;
	
	/**
	  *  The actual stack variable, holds all the
	  *  previous colour codes.
	 */
	protected char[] stack;
	
	/**
	  *  This always points at the next place to put
	  *  an increasing colour code.  (ie, one past)
	 */
	protected int stackValue; 
	
	/**
	  *  If this boolean is true, then the stream is
	  *  in an uncoloured state (the last code sent
	  *  down reset it completely).
	 */
	protected boolean normal;

	/**
	  *  It is the responsibility of the subclass to
	  *  set this array.
	 */
	protected String names[];

	protected Terminal()
	{
		names = new String[2];
		names[0] = "none";
		names[1] = "dumb";
		marked = -1;
		stackValue = 1;
		stack = new char[ STACK_SIZE ];
		normal = true;
		
			//  see, here's the thing - the first entry
			//  on the stack is *always* normal, since
			//  without it, we've got no way to restore.
		stack[0] = 'N';
	}
	
	/**
	  *  Should be called at the end of every section
	  *  of input - will reset the stream to its
	  *  proper state
	 */
	public void reset( Writer outputStream ) throws IOException
	{
		stackValue = 1;
		marked = -1;

		if( !normal )
		{
			outputStream.write( stringForCode( 'N' ) );
			//Log.debug( this, "terminal reset" );
			normal = true;
		}
		//else
			//Log.debug( this, "terminal NOT reset (already normal)" );
	}

	public boolean nameMatches( String test )
	{
		for( int i = 0; i < names.length; i++ )
		{
			if( test.equalsIgnoreCase( names[i] ) )
				return true;
		}

		return false;
	}
	
	public String getName()
	{
		if( names != null && names.length > 0 )
			return( names[0] );
		else
			throw new UnexpectedResult( "names isn't valid in " + Type.typeOf( this ).getName() );
	}
	
	/**
	  *  Does all the necessary coding to support
	  *  the newly returned colour code for input
	  *  into the stream.
	 */
	public String stringForCode( char code )
	{
		String result="";
		
		switch( code )
		{
			case '@':  // set mark if its not already
				if( marked == -1 )
					marked = stackValue;
				
				return( "" );
			
			case '$':
				if( marked != -1 )
					stackValue = marked - 1;
				
					//  restore the previous colour
				return( stringForCode( stack[ stackValue ] ) );

			case '.':
				if( stackValue > 0 )
					return( stringForCode( stack[ stackValue-1 ] ) );
				else
					return( stringForCode( 'n' ) );
			
			case '-':
				if( stackValue > 1 )
					stackValue-=2;
				else
					return( stringForCode( 'n' ) );
				
					//  technically speaking, the stackValue
					//  could here be invalid, but.. I mean..
					//  nothings going to go wrong, right?
					//  *arrogent smile*
				
				return( stringForCode( stack[ stackValue ] ) );
		}
		
		normal = false;
		
		result = codeLookup( code );
		
		if( doPushCode( code ) )
		{
				//  push this value onto the stack
			if( stackValue < STACK_SIZE )
				stack[ stackValue++ ] = code;
			else
				stack[ STACK_SIZE-1 ] = code;
		}
		
		return( result );
	}
	
	public boolean doPushCode( char code )
	{
		switch( code )
		{
			case '(':
			case '<':
			case '{':
			case '[':
			case ')':
			case '>':
			case '}':
			case ']':
				return false;
			default:
				return true;
		}
	}
	
	public String codeLookup( char code )
	{
		switch( code )
		{
			case 'n':
			case 'N':
				normal = true;
				return( "" );
				
			case '{':
				return( null );
			
			case '<':
				return( "<" );
		}
		
		return( "" );
	}

	private final static String[] until = { "^}", "^]", "^|" };
	public String[] getUntil()
	{
		return( until );
	}
	
	public String clearScreen()
	{
		return( "" );
	}
	
	public String beep()
	{
		return( "" );
	}
	
	protected static LinkedList terminalTypes;

	public static Terminal createTerminal( String terminalName ) throws IllegalAccessException, InstantiationException
	{
		if( terminalTypes == null )
		{
			terminalTypes = new LinkedList();
			
			terminalTypes.append( new key.terminals.Dumb() );
			terminalTypes.append( new key.terminals.ISO6429() );
			terminalTypes.append( new key.terminals.Applet() );
			terminalTypes.append( new key.terminals.HTML() );
		}
		
		for( Enumeration e = terminalTypes.elements(); e.hasMoreElements(); )
		{
			Terminal t = (Terminal) e.nextElement();
			if( t.nameMatches( terminalName ) )
				return( (Terminal) t.getClass().newInstance() );
				//return( (Terminal) Type.typeOf( t ).newInstance() );
		}

		return null;
	}
}
