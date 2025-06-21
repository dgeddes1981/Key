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

import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
  *  Works closely with the 'Terminal' class
  *  to achieve different terminal emulations
  *
  *  Implements RFCs: 1091, 858, 857, 854, 885, 1143,
  *  855, 1073 
 */
public final class TelnetIC
extends SocketIC
{
		//  telnet commands
	static final int IAC=255;
	static final int DONT=254;
	static final int DO=253;
	static final int WONT=252;
	static final int WILL=251;
	static final int SB=250;
	static final int GA=249;
	static final int EL=248;
	static final int EC=247;
	static final int AYT=246;
	static final int AO=245;
	static final int IP=244;
	static final int BREAK=243;
	static final int DM=242;
	static final int NOP=241;
	static final int SE=240;
	static final int EOR=239;
	static final int ABORT=238;
	static final int SUSP=237;
	static final int xEOF=236;
	
	public static final int SMALLEST_BELIEVABLE_WIDTH = 20;
	public static final int SMALLEST_BELIEVABLE_HEIGHT = 6;
	public static final int LARGEST_BELIEVABLE_WIDTH = 200;
	public static final int LARGEST_BELIEVABLE_HEIGHT = 90;
	
	public static final int DEFAULT_WIDTH = 79;
	public static final int DEFAULT_HEIGHT = 25;
	
	/**
	  *  The amount of additional space the pager reserves
	  *  at the bottom of the screen.  3 allows one blank
	  *  line and a message line, without forcing a scroll.
	 */
	public static final int PAGER_VERTICAL_SPACE = 5;
	
	/**
	  *  The number of lines less than the terminal height
	  *  which is the border between paging and not paging
	 */
	public static final int PAGER_THRESHOLD = 2;
	
	public static final String PAGER_LACKS_PAGES = "No more pages to view.\n";
	
	/**
	  *  A vector of strings containing lines that are
	  *  waiting to be output by the pager.
	 */
	private Vector pageBuffer;
	
	/**
	  *  The top of the _next_ screen to be displayed,
	  *  according to the pager.
	 */
	private int topOfScreen;
	
	private char[] inBuffer;
	
		//  size of the window
	int width;
	int height;
	
	private transient String[] until = null;
	
	private Hashtable options;
	private TelnetOption teloptEcho;
	private TelnetOption teloptEOR;
	private TelnetOption teloptNAWS;
	private TelnetOption teloptLinemode;
	private TelnetOption teloptSubliminal;
	private TelnetOption teloptTerminalType;
	
	private Terminal terminal;
	
	/**
	  *  This is used to hold the autodetected
	  *  terminal when the terminal is 'forced'
	  *  by the player
	 */
	private Terminal detectedTerminal;
	
		/**  true iff the user allows us to use the pager */
	boolean canPageEver;
	
	/**
	  *  True iff the user allows us to word wrap.  Turning
	  *  this off means that newlines will not be manually
	  *  inserted in paragraphs.
	 */
	boolean wordwrap = true;
	
	/**
	  *-1 if disabled, a value if the user has forced his
	  *  terminal to a particular width.
	 */
	int forced_width = -1;
	
	/**
	  *-1 if disabled, a value if the user has forced his
	  *  terminal to a particular height.
	 */
	int forced_height = -1;
	
	public static final String CRLF = "\r\n";
	public static final int INPUT_BUFFER = 640;
	public static final int MAX_OVERRUN = 640;
	
	/**
	  * Constructor
	  *
	  * @param s The socket that the connection is on
	 */
	public TelnetIC( Socket s ) throws IOException
	{
		super( s );
		inBuffer = new char[INPUT_BUFFER+2];
		
		options = new Hashtable( 5 );
		//terminal = new key.terminals.Dumb();
		terminal = new Terminal();
		canPageEver = true;
		
		teloptEcho = new EchoTelnetOption( TelnetOption.TELOPT_ECHO, false, true );
		options.put( new Integer( TelnetOption.TELOPT_ECHO ), teloptEcho );
		
		teloptEOR = new TelnetOption( TelnetOption.TELOPT_EOR, false, false );
		options.put( new Integer( TelnetOption.TELOPT_EOR ), teloptEOR );
		
		teloptNAWS = new NAWSTelnetOption( TelnetOption.TELOPT_NAWS, true, false );
		options.put( new Integer( TelnetOption.TELOPT_NAWS ), teloptNAWS );

		teloptSubliminal = new TelnetOption( TelnetOption.TELOPT_SUBLIMINAL, true, false );
		options.put( new Integer( TelnetOption.TELOPT_SUBLIMINAL ), teloptSubliminal );
		
		teloptTerminalType = new TerminalTypeTelnetOption( this, TelnetOption.TELOPT_TTYPE, true, false );
		options.put( new Integer( TelnetOption.TELOPT_TTYPE ), teloptTerminalType );
		
		try
		{
			teloptNAWS.sendDo( rawOutput );
			teloptTerminalType.sendDo( rawOutput );
			//teloptEcho.sendWill( rawOutput );
			//teloptEcho.sendDo( rawOutput );
		}
		catch( IOException e )
		{
			close();
			throw e;
		}
		
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		
			//  since we don't have a null constructor, we can't
			//  be created by the factory: this is our concession.
		Factory.postCreateProcess( this );
	}
	
	public void setLocalEcho( boolean v ) throws IOException
	{
		if( v )
		{
			teloptEcho.sendWill( rawOutput );
			output.write( "## Told client to turn local echo on" );
		}
		else
		{
			teloptEcho.sendWont( rawOutput );
			output.write( "## Told client to turn local echo off" );
		}
		
		output.write( CRLF );
		output.flush();
	}

	public boolean getLocalEcho()
	{
		return( teloptEcho.enabledUs() );
	}
	
	/**
	  *  beeps the terminal, if supported.
	 */
	public void beep()
	{
		try
		{
			output.write( terminal.beep() );
			output.flush();
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	/**
	  *  Returns true if sucessful
	 */
	private boolean setTerminal( String terminalName )
	{
		Terminal newTerm;
		
		try
		{
			newTerm = Terminal.createTerminal( terminalName );
		}
		catch( IllegalAccessException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		catch( InstantiationException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		until = null;
		
		if( newTerm != null )
		{
			terminal = newTerm;
			return( true );
		}
		
		return( false );
	}
	
	public boolean setLoginTerminal( String name )
	{
		if( detectedTerminal == null )
			return( setTerminal( name ) );
		else
			return( false );
	}
	
	public boolean setDetectedTerminal( String name )
	{
		boolean r = setTerminal( name );
		
		if( r )
			detectedTerminal = terminal;
		return( r );
	}

	public boolean hasDetectedTerminal()
	{
		return( detectedTerminal != null );
	}
	
	/**
	  *  Terminals can be autodetected, or overridden by 'forcing'
	  *  them (using, incidently, this routine)
	 */
	public boolean forceTerminal( String name )
	{
		return( setTerminal( name ) );
	}
	
	public void setWordwrap( boolean v )
	{
		wordwrap = v;
	}
	
	public boolean getWordwrap()
	{
		return( wordwrap );
	}
	
	public void resetTerminal()
	{
		if( detectedTerminal != null )
			terminal = detectedTerminal;
		
		until = null;
	}
	
	public Terminal getTerminal()
	{
		return( terminal );
	}
	
	private final void ensureUntil()
	{
		if( until == null )
			until = terminal.getUntil();
	}
	
	public Terminal getDetectedTerminal()
	{
		return( detectedTerminal );
	}
	
	/**
	  *  Reads a full (16-bit) integer
	  *  from the inputStream, taking
	  *  doubled IAC's into account
	 */
	private void write16Int( int t ) throws IOException
	{
		output.flush();
		rawOutput.write( t >> 8 );
		if( t >> 8 == 255 )
			rawOutput.write( 255 );
		t = t & 0x00ff;
		rawOutput.write( t );
		if( t == 255 )
			rawOutput.write( 255 );
	}
	
	//  META: recently changed >> 16's to >> 8's in this protocol
	
	/**
	  *  Reads a full (16-bit) integer
	  *  from the inputStream, taking
	  *  doubled IAC's into account
	 */
	private int read16Int() throws IOException,IACRecievedException
	{
		int number=0;
		int i;
		int j;
		
		for( int k=0; k<2; k++ )
		{
			i = inputStream.read();
			if( i == TelnetIC.IAC )
			{
				j = inputStream.read();
				if( j == IAC )
				{ //  its the number 255
					number = number << 8;
					number += j;
				}
				else
				{ //  authentic IAC code - now we're in trouble
					throw new IACRecievedException( j );
				}
			}
			else
			{
				number = number << 8;
				number += i;
			}
		}

		return( number );
	}

	private String readIACSETerminatedString() throws IOException
	{
		int where=0;
		char[] buffer = new char[ 40 ];
		char b=' ';
		boolean cont = true;
		
		do
		{
			int i;
			i = inputStream.read();
			if( i != -1 )
				b = (char) i;
			else
				throw new IOException( "connection closed" );
			
			if( b == IAC )
			{  //  handle IAC codes
				switch( handleIAC() )
				{
					case SE:
						cont = false;
						break;
				}
			}
			else if( b == '\n' || b == '\r' )
				cont = false;
			else
				buffer[where++] = (char) b;
		} while( cont );
		
		return( new String( buffer, 0, where ) );
	}

	
	/**
	  * An IAC code was just recieved down the input stream, and we've been
	  * called to 'take care' of it. ;)
	 */
	private int handleIAC() throws IOException
	{
		int i;
		char b;
		i = inputStream.read();
		if( i != -1 )
			b = (char) i;
		else
			throw new IOException( "connection closed" );
		
		switch( b )
		{
			case IAC:
				//  a doubled IAC code should be sent direct, but
				//  we should filter it anyway
				return 0;
			
			case SB:
				//  start of a subnegotiation
				i = inputStream.read();
				if( i != -1 )
					b = (char) i;
				else
					throw new IOException( "connection closed" );

				switch( i )
				{
					case TelnetOption.TELOPT_NAWS:
						teloptNAWS.negotiation( this );
						break;
					case TelnetOption.TELOPT_TTYPE:
						teloptTerminalType.negotiation( this );
					default:
						break;
				}
				break;
			
			case SE:
				return( SE );
			
			case WILL:
			case WONT:
			case DO:
			case DONT:
				//  regardless of what it is, its still specifying an option next
				int oc;		//  the option code
				oc = inputStream.read();
				if( oc == -1 )
					throw new IOException( "connection closed" );
				else
				{
					TelnetOption to = null;
					switch( oc )
					{
						case TelnetOption.TELOPT_ECHO:
							to = teloptEcho;
							break;
						case TelnetOption.TELOPT_EOR:
							to = teloptEOR;
							break;
						case TelnetOption.TELOPT_NAWS:
							to = teloptNAWS;
							break;
						case TelnetOption.TELOPT_TTYPE:
							to = teloptTerminalType;
							break;
						default:
							Integer toOptCode = new Integer( oc );
							to = (TelnetOption) options.get( toOptCode );
							if( to == null )
							{	//  need to create this option
								to = new TelnetOption( oc, false, false );
								options.put( toOptCode, to );
							}
					}
					
					switch( b )
					{
						case WILL:
							to.rcptWill( rawOutput );
							break;
						case WONT:
							to.rcptWont( rawOutput );
							break;
						case DO:
							to.rcptDo( rawOutput );
							break;
						case DONT:
							to.rcptDont( rawOutput );
					}
				}
				break;
			case EC:
				return( EC );
			case EL:
				return( EL );
			default:
				break;
		}
		
		return 0;
	}
	
	private boolean pushbacked = false;
	
	/**
	  *  This function will be called every 'now and
	  *  again', as a 'check' function when no input
	  *  is required (we're not waiting), but some
	  *  is allowed.  This is useful for the telnet
	  *  protocol in particular, which can recieve
	  *  IAC's before the login prompt.  This function
	  *  should be harmless, no matter when its called
	 */
	public boolean check() throws IOException
	{
		boolean r = false;
		
		if( !pushbacked )
		{
			int a = inputStream.available();
			if( a > 1 )
			{
				int i = inputStream.read();
				
				if( i == IAC )
				{
					handleIAC();
					r = true;
				}
				else
				{
					inputStream.unread( i );
					pushbacked = true;
				}
			}
		}

		return( r );
	}
	
	public void waitForIAC() throws IOException
	{
		int i = 0;
		
		do
		{
			while( i < 20 && check() ) i++;
			
			try
			{
				Thread.sleep( 500 );
			}
			catch( InterruptedException e )
			{
			}
		} while( i < 20 && check() );
	}
	
	public void flushIfWaiting()
	{
		try
		{
			if( isWaiting > 0 )
				output.flush();
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	private boolean hiddenMode = false;
	private boolean hiddenStatus = false;
	private int isWaiting = 0;
	private int lastCharWas = -1;
	
	/**
	  * This function blocks until a line of input is
	  * recieved.  Think of it as being much like the
	  * input statement in BASIC of old. *shudder*
	  *
	  * @param prompt The prompt to give the player
	 */
	public String input( String prompt )
	{
		try
		{
			isWaiting++;
			int where=0;
			char b=' ';
			
			if( hiddenStatus && !hiddenMode )
			{
				teloptEcho.sendWont( rawOutput );
				hiddenStatus = false;
			}
			
			paragraph( prompt, 0, 0, 0, 0, true, false, false, wordwrap );
			output.flush();
			rawOutput.write( (byte) IAC );
			rawOutput.write( (byte) GA );
			
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
						close();
				}
				else
					i = inputStream.read();
				
				if( i != -1 )
					b = (char) i;
				else
					throw new IOException( "connection closed" );
				
				if( b >= 32 && b <= 126 )    //  valid ascii characters only
				{
					inBuffer[where++] = (char) b;
					if( !hiddenMode && teloptEcho.enabledUs() )
					{
						output.write( b );
						output.flush();
					}
				}
				else if( b == 8 || b == 127 )
				{
					if( where > 0 )
						where--;
					if( !hiddenMode && teloptEcho.enabledUs() )
					{
						output.write( 8 );
						output.flush();
					}
				}
				else if( b == IAC )
				{  //  handle IAC codes
					switch( handleIAC() )
					{
						case EL:
							//  erase the line
							where = 0;
							break;
						case EC:
							//  erase last character
							if( where > 0 )
								where--;
							break;
						case SE:
							Log.log( "telnet", "ERROR: unexpected SE" );
							break;
					}
				}
			} while( !((b == '\r') && !(lastCharWas == '\n')) && !((b == '\n') && !(lastCharWas == '\r' )) );

			lastCharWas = b;
			
			//Log.log( "telnet", "exit loop ( new lastChar is " + (char)b + " )" );
		
			unIdle();
			pushbacked = false;
			
			return( new String( inBuffer, 0, where ) );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
		finally
		{
			isWaiting--;
		}
	}

	/**
	  * This function is very similar to
	  * input(), above, except that it should be
	  * used to *not* echo text as it's entered,
	  * mainly for passwords
	 */
	public String hiddenInput( String prompt )
	{
		if( !hiddenStatus )
		{
			try
			{
				teloptEcho.sendWill( rawOutput );
				hiddenStatus = true;
			}
			catch( IOException e )
			{
				close();
				throw new NetworkException( e );
			}
		}
		
		hiddenMode = true;
		
		String i = input( prompt );

		hiddenMode = false;
		
			//  this is an unavoidable little hack.
			//  since, when they hit return while not echo'ing,
			//  the telnet program *won't* echo their return, and
			//  it won't start a new line.  However, we always want
			//  it to do this.  What we can't detect is that TinyFugue,
			//  a popular client, is linemode based, and will start
			//  a new line on its own accord.  This means that, when
			//  using hiddenInput under the TF client, you'll get
			//  double spacing after each call.  This isn't really
			//  our fault, but it might seem to be, so here's my
			//  disclaimer. ;)
		blankLine();
		
		return( i );
	}

	public void sendLine()
	{
		try
		{
			output.write( dashes( width ) );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}

	public void sendRaw( String message )
	{
		try
		{
			output.write( message );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void send( String message )
	{
		try
		{
			paragraph( message, 3, 0, -3, 0, true, false, true, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}

	public void sendFailure( String message )
	{
		try
		{
			paragraph( message, 0, 0, 0, 0, true, false, true, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void sendError( String message )
	{
		try
		{
			paragraph( message, 0, 0, 0, 0, true, false, true, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void sendFeedback( String message )
	{
		try
		{
			paragraph( message, 0, 0, 0, 0, true, false, true, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void send( String message, int li, int ri, int lfi, int rfi, boolean stripLeadingSpaces )
	{
		try
		{
			paragraph( message, li, ri, lfi, rfi, stripLeadingSpaces, false, false, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void send( char qualifier, String message )
	{
		try
		{
			paragraph( String.valueOf( qualifier ) + " " + message, 0, 0, 0, 0, false, false, true, wordwrap );
			terminal.reset( output );
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	protected void sendTextParagraph( int li, int ri, int lfi, int rfi, String message, int alignment, boolean appending, boolean okayToPage ) throws IOException
	{
		if( message.length() > 0 )
		{
			switch( alignment )
			{
				case TextParagraph.CENTERALIGNED:
				{
						//  this option doesn't use these - set to
						//  0 for the paragraph call
						
					lfi = 0;
					rfi = 0;
					
						//  calculate the longest line
					int longestLine=0;
					
					StringTokenizer st = new StringTokenizer( message, "\n" );
					while( st.hasMoreTokens() )
						longestLine = Math.max( longestLine, stringLength( st.nextToken() ) );
					
						//  the width of the screen is made smaller by the indents
					int i = (( (width - li - ri) - longestLine) / 2)-1;
					if( i <= 0 )
					{
							//  try to center it on the normal screen...
						i = ((width - longestLine) / 2);
						
						if( i <= 0 )
							li = 0;
						else
							li = i;
					}
					else
						li += i;
					
						//  center aligned things aren't word wrapped
					paragraph( message, li, ri, lfi, rfi, false, appending, okayToPage, false );
					break;
				}
				case TextParagraph.CENTERED:
				{
					int virtual_width = width - li - ri;
					StringBuffer sb = new StringBuffer();
					
					StringTokenizer st = new StringTokenizer( message, "\n", true );
					while( st.hasMoreTokens() )
					{
						String this_line = st.nextToken();

						if( this_line.equals( "\n" ) )
							sb.append( '\n' );
						else
						{
							int this_line_length = stringLength( this_line );
							
								//  try to center this line
							int i = ((virtual_width - this_line_length) / 2) - 1;
							
								//  only center it if we can
							if( i > 0 )
								sb.append( spaces( i ) );
							
							sb.append( this_line );
						}
					}
					
					paragraph( sb.toString(), li, ri, lfi, rfi, false, appending, okayToPage, false );
					break;
				}
				default:
					paragraph( message, li, ri, lfi, rfi, true, appending, okayToPage, wordwrap );
			}
			
			if( !isPaging() )
			{
				terminal.reset( output );
				output.write( CRLF );
			}
		}
	}
	
	protected void sendColumnParagraph( ColumnParagraph tp, boolean appending, boolean okayToPage ) throws IOException
	{
			//  first, calculate the number of columns to display on this
			//  width screen.
		int numCols = width / (tp.maxEntryWidth + tp.spaceBetween);
		int indent = (width - (numCols * tp.maxEntryWidth)) / 2;
		int pos = 0;
		
		StringBuffer sb = new StringBuffer();
		
		for( Enumeration e = tp.elements(); e.hasMoreElements(); )
		{
			String ts = (String) e.nextElement();
			int tsl = stringLength( ts );
			int trailer = tp.spaceBetween;
			
			if( tsl <= tp.maxEntryWidth )
			{
				sb.append( ts );
				trailer += tp.maxEntryWidth - tsl;
			}
			else
				sb.append( ts.substring( 0, tp.maxEntryWidth ) );
			
			pos = (++pos) % numCols;
			
			if( pos == 0 )
				sb.append( '\n' );
			else
				sb.append( spaces( trailer ) );
		}
		
		paragraph( sb.toString(), indent, 0, 0, 0, false, appending, okayToPage, false );
		if( !isPaging() )
		{
			terminal.reset( output );
			output.write( CRLF );
		}
	}
	
	protected void sendTableParagraph( TableParagraph tp, boolean appending, boolean okayToPage ) throws IOException
	{
			//  first, calculate the number of columns to display on this
			//  width screen.
		
			//  start at 1, there's a | on the left side
		int currentWidth = 1;
		int lastColumn = -1;
		
			//  add lastColumn to allow for the | seperators
		while( currentWidth + lastColumn < width )
		{
			lastColumn++;
			
			if( lastColumn >= tp.columns.length )
				break;
			
			currentWidth += tp.columns[ lastColumn ].getWidth();
		}
		
		if( lastColumn < tp.columns.length )
			currentWidth -= tp.columns[ lastColumn ].getWidth();
		
			//  add lastColumn to allow for the | seperators
		currentWidth += lastColumn;
		
		lastColumn--;
		
		if( lastColumn < 0 )
		{		//  not enough room to display anything
			return;
		}

			//  now generate the text itself
		StringBuffer sb = new StringBuffer();
		StringBuffer secondRow = new StringBuffer( "|" );
		
			//  add the top heading rows
		for( int i = 0; i <= lastColumn; i++ )
		{
			String cheading = tp.columns[ i ].getHeading();
			int cwidth = tp.columns[ i ].getWidth();
			int chl = stringLength( cheading );
			
			secondRow.append( dashes( cwidth ) );
			secondRow.append( '|' );
			sb.append( ' ' );
			
			if( cwidth > chl )
			{
				sb.append( ' ' );
				sb.append( cheading );

				if( i != lastColumn )
					sb.append( spaces( cwidth - chl - 1 ) );
			}
			else if( cwidth == chl )
			{
				sb.append( cheading );
			}
			else
			{
				sb.append( cheading.substring( 0, cwidth ) );
			}
		}

		sb.append( '\n' );
		sb.append( secondRow.toString() );
		secondRow = null;

			//  now add the rows for the data
		for( Enumeration e = tp.elements(); e.hasMoreElements(); )
		{
			String[] rd = (String[]) e.nextElement();
			
			sb.append( '\n' );
			sb.append( " ^@" );
			
			for( int i = 0; i <= lastColumn; i++ )
			{
				if( rd[i] == null )
				{
					sb.append( spaces( tp.columns[i].getWidth() ) );
				}
				else
				{
					int rdl = stringLength( rd[i] );
					int cwidth = tp.columns[i].getWidth();
					
					if( cwidth >= rdl )
					{
						sb.append( rd[i] );
						sb.append( spaces( cwidth - rdl ) );
					}
					else
						sb.append( rd[i].substring( 0, cwidth ) );
				}
				
				sb.append( "^$ " );
			}
		}
		
		sb.append( '\n' );
		
			//  now add the footer
		String tm = tp.getFooter();

		if( tm != null )
		{
			int tl = stringLength( tm );

			if( tl > 0 )
			{
				if( tl+10 < currentWidth )
				{
						//  right justify it
					sb.append( '|' );
					sb.append( dashes( currentWidth - tl - 7 ) );
					sb.append( " ^@" );
					sb.append( tm );
					sb.append( "^$ ---|" );
				}
				else
					sb.append( dashes( currentWidth ) );
			}
			else
				sb.append( dashes( currentWidth ) );
		}
		else
			sb.append( dashes( currentWidth ) );
		
		paragraph( sb.toString(), 0, 0, 0, 0, false, appending, okayToPage, false );
		if( !isPaging() )
		{
			terminal.reset( output );
			output.write( CRLF );
		}
	}
	
	protected void sendHeadingParagraph( String message, int alignment, boolean appending, boolean okayToPage ) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		int ml = stringLength( message );
		if( ml + 2 >= width )
		{
			sb.append( dashes( 3 ) );
			sb.append( "  " );
			sb.append( message );
			sb.append( "  " );
			sb.append( dashes( 3 ) );
		}
		else
		{
			if( alignment == HeadingParagraph.LEFT )
			{
				sb.append( dashes( 3 ) );
				sb.append( "  " );
				sb.append( message );
				sb.append( "  " );
				sb.append( dashes( width - (ml + 7) ) );
			}
			else if( alignment == HeadingParagraph.RIGHT )
			{
				sb.append( dashes( width - (ml + 7) ) );
				sb.append( "  " );
				sb.append( message );
				sb.append( "  " );
				sb.append( dashes( 3 ) );
			}
			else
			{
				int a = (width - (ml + 4)) / 2;
				sb.append( dashes( a ) );
				sb.append( "  " );
				sb.append( message );
				sb.append( "  " );
				sb.append( dashes( (width - (ml + 4)) - a ) );
			}
		}
		
		paragraph( sb.toString(), 0, 0, 0, 0, false, appending, okayToPage, false );
		if( !isPaging() )
		{
			terminal.reset( output );
			output.write( CRLF );
		}
	}
	
	public final void send( Paragraph para )
	{
		send( para, true );
	}
	
	public void send( Paragraph para, boolean okayToPage )
	{
		try
		{
			okayToPage = okayToPage && canPageEver;
			
			if( para instanceof TextParagraph )
			{
				TextParagraph tp = (TextParagraph) para;
				
				sendTextParagraph( tp.getLeftMargin(), tp.getRightMargin(), tp.getLeftFirstMargin(), tp.getRightFirstMargin(), tp.getText(), tp.getAlignment(), false, okayToPage );
			}
			else if( para instanceof BlankLineParagraph )
			{
				blankLine();
			}
			else if( para instanceof HeadingParagraph )
			{
				HeadingParagraph hp = (HeadingParagraph) para;
				sendHeadingParagraph( hp.getText(), hp.getAlignment(), false, okayToPage );
			}
			else if( para instanceof LineParagraph )
			{
				sendLine();
			}
			else if( para instanceof TableParagraph )
			{
				sendTableParagraph( ((TableParagraph)para), false, okayToPage );
			}
			else if( para instanceof ColumnParagraph )
			{
				sendColumnParagraph( ((ColumnParagraph)para), false, okayToPage );
			}
			else if( para instanceof MultiParagraph )
			{
					//  FUTURE:  Make it so it only calls flush() once, and
					//           so that it pages things properly.  atm if
					//           a multiparagraph started with, for instance,
					//           20 line paragraphs and then some text, it
					//           wouldn't page the lines, even though it might
					//           page the text.  Some sort of clever paragraph
					//           routine might be able to do something like this,
					//           but I hardly think anyone's going to bother.  I'm
					//           sure not going to.
					//
					//           Another option is to change paragraph() so that it
					//           doesn't output any data, just returns the data
					//           to be output - or some other 'middle' routine between
					//           the usual paragraph() call that invokes the pager
					//           for internal private use.
				
				for( Enumeration e = ((MultiParagraph)para).getParagraphs(); e.hasMoreElements(); )
				{
					Object o = e.nextElement();
					
						//  recheck, we now have to call some of these routines
						//  with an 'appending' flag
					if( o instanceof TextParagraph )
					{
						TextParagraph tp = (TextParagraph) o;
						
						sendTextParagraph( tp.getLeftMargin(), tp.getRightMargin(), tp.getLeftFirstMargin(), tp.getRightFirstMargin(), tp.getText(), tp.getAlignment(), true, okayToPage );
					}
					else if( o instanceof HeadingParagraph )
					{
						HeadingParagraph hp = (HeadingParagraph) o;
						sendHeadingParagraph( hp.getText(), hp.getAlignment(), true, okayToPage );
					}
					else if( o instanceof BlankLineParagraph )
					{
						if( okayToPage && pageBuffer != null )
							pageBuffer.addElement( new String( CRLF ) );
						else
							blankLine();
					}
					else if( o instanceof LineParagraph )
					{
						if( okayToPage && pageBuffer != null )
							pageBuffer.addElement( new String( dashes( width ) + CRLF ) );
						else
							sendLine();
					}
					else if( para instanceof ColumnParagraph )
						sendColumnParagraph( ((ColumnParagraph)o), true, okayToPage );
					else if( para instanceof TableParagraph )
						sendTableParagraph( ((TableParagraph)o), true, okayToPage );
					else
						send( (Paragraph)o, okayToPage );
				}
			}
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void blankLines( int c )
	{
		try
		{
			while( c-- > 0 )
				output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	public void blankLine()
	{
		try
		{
			output.write( CRLF );
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	/**
	  *  You never know what they _might_ have available on their terminal...
	  *  *grin*
	 */
	public void sendSubliminal( String message, int duration, int frequency )
	{
		try
		{
			if( teloptSubliminal.enabledHim() )
			{
				output.flush();
				rawOutput.write( IAC );
				rawOutput.write( SB );
				rawOutput.write( TelnetOption.TELOPT_SUBLIMINAL );
				write16Int( duration );
				write16Int( frequency );
				rawOutput.flush();
				output.write( message );
				output.flush();
				rawOutput.write( IAC );
				rawOutput.write( SE );
				rawOutput.flush();
			}
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}
	
	/**
	  *  Looks for the first string in u to appear in str & returns
	  *  the offset, or -1.  Start looking at index start.
	 */
	private final int terminatingOffset( String str, String[] u, int start )
	{
		if( u == null || u.length <= 0 )
			return( -1 );
		
		int skipuntil = Integer.MAX_VALUE;
		
		for( int j = 0; j < u.length; j++ )
		{
			int k = str.indexOf( u[j], start );
			
			if( k != -1 && k < skipuntil )
				skipuntil = k;
		}
		
		if( skipuntil == Integer.MAX_VALUE )
			skipuntil = -1;
		
		return( skipuntil );
	}
	
	/**
	  *  Returns the length of the word.  If it returns a negative, that
	  *  means the length to date, and that you should step into skipping
	  *  mode.  A negative return should also have one added to it
	  *  to discover the real length (because -1*0 == 0).
	 */
	private final int colourString( String str, StringBuffer gen )
	{
		int colourIndex = str.indexOf( '^' );
		int start = 0;
		int wl = 0;  // word length
		int strlen = str.length() -1;
		
			//  here we insert our check for colour codes -
			//  if the line has a ^ in it, there is something
			//  we have to filter out :)
		
		while( colourIndex != -1 && colourIndex < strlen )
		{
			gen.append( str.substring( start, colourIndex ) );
			wl += colourIndex - start;
			
			char c = str.charAt( colourIndex + 1 );
			
			if( c == '^' )
			{		//  its been doubled to display it - thats cool
				wl++;
				gen.append( c );
				start = colourIndex+2;
			}
			else
			{		//  lets just skip one character
				start = colourIndex+2;
				String s = terminal.stringForCode( c );
				
				if( s != null )
				{
					gen.append( s );
				}
				else
				{
					String[] u = terminal.getUntil();
					int skipuntil = terminatingOffset( str, u, start );
					
					if( skipuntil != -1 )
					{
						str = str.substring( skipuntil );
						strlen = str.length() -1;
						//System.out.println( "leaves '" + str + "'" );
						start = 0;
					}
					else
					{
							//  it isn't in this string, keep searching
							//  outside this scope
						return( (-1 * wl) - 1 );
					}
				}
			}
			
			colourIndex = str.indexOf( '^', start );
		}
		
		str = str.substring( start );
		wl += str.length();
		
		gen.append( str );
		
		return( wl );
	}
	
	/**
	  *  A not-so-trivial routine that does word-wrap'ing WITH
	  *  colour codes, and outputs the result through the
	  *  pager (if required) down the users terminal.
	  * <p>
	  *  <i>(Funny... this routine was a lot harder to write
	  *  in C++) (1998: Funny, in C++ it was also a lot faster)</i>
	  *  <p>
	  *  Anything put in preLine is additional to the left
	  *  indent - so to guarantee a left indent of 4, ensure
	  *  you subtract the length of preLine
	  *  <p>
	  *  Setting prelines that are wider than the
	  *  terminal has an undefined effect on the output.
	  *
	  *  @param message The text to format
	  *  @param li <b>L</b>eftedge <b>I</b>ndent
	  *  @param ri <b>R</b>ightedge <b>I</b>ndent
	  *  @param lfi <b>L</b>eftedge <b>F</b>irstline <b>I</b>ndent (in addition to the leftedge)
	  *  @param rfi <b>R</b>ightedge <b>F</b>irstline <b>I</b>ndent (in addition to the rightedge)
	  *  @param preLine A string constant to put at the start of every line
	 */
	
	public void paragraph( String message, int li, int ri, int lfi, int rfi, boolean stripLeadingSpaces, boolean appending, boolean okayToPage, boolean ww ) throws IOException
	{
		StringBuffer gen = new StringBuffer();
		boolean skipping = false;
		
		//System.out.println( "Sending ww=" + ww + ", '" + message + "'" );
		//System.out.println( "term options: width=" + width + ", height=" + height + ", wordwrap=" + wordwrap + ", fwidth=" + forced_width + ", fheight=" + forced_height );
		
		if( !ww )
		{
				//  without word wrap, we can't page effectively either,
				//  so we don't even bother.  It makes our job a lot easier,
				//  all we need to do is substitute CRLF for the CR's the
				//  program uses internally, in order to conform to the
				//  telnet protocol.
			StringTokenizer lines = new StringTokenizer( message, "\n", true );
			
			while( lines.hasMoreTokens() )
			{
				String str = lines.nextToken();
				
				if( skipping )
				{
						//  look for a terminating colour code
					ensureUntil();
					int i = terminatingOffset( str, until, 0 );

					if( i == -1 )
						continue;
					else
						str = str.substring( i );
				}
				
				if( str.equals( "\n" ) )
					gen.append( CRLF );
				else
				{
					gen.append( spaces( li + lfi ) );
					
						//  so it only gets used once
					lfi = 0;
					
					if( colourString( str, gen ) < 0 )
					{
						skipping = true;
						continue;
					}
				}
			}
			
			output.write( gen.toString() );
			return;
		}
		else
		{
			Vector generatedLines = new Vector( 5, 25 );
			
			okayToPage = okayToPage && canPageEver;
			
			int x=0;  //  x position on the virtual screen
			boolean firstLine = true;
			boolean startOfLine = true;
			
			StringTokenizer lines = new StringTokenizer( message, "\n", true );
			
			while( lines.hasMoreTokens() )
			{
				String str = lines.nextToken();
				
					//  internally, the program uses *only*
					//  a single \n to represent a new line.
					//  The telnet protocol demands, however,
					//  that \n\r be used to represent this.
					//  What we do here is catch \n's and turn
					//  them into \n\r's.
				if( str.equals( "\n" ) )
				{
					gen.append( CRLF );
					generatedLines.addElement( gen.toString() );
					gen = new StringBuffer();
					
					x = 0;
					
					firstLine = false;
					startOfLine = true;
				}
				else
				{
					StringTokenizer st = new StringTokenizer( str, " ", true );
					if( x == 0 )
					{
						gen.append( spaces( li + lfi ) );
						x += li + lfi;
						startOfLine = true;
					}
		
					while( st.hasMoreTokens() )
					{
							//  the actual word to insert into the stream
						String thisWord = st.nextToken();
						
						if( skipping )
						{
							ensureUntil();
							int i = terminatingOffset( thisWord, until, 0 );
							
							if( i == -1 )
								continue;
							else
							{
								skipping = false;
								thisWord = thisWord.substring( i );
							}
						}
						
							//  just a shortcut in case we're just dealing with
							//  a space
						if( thisWord.equals( " " ) )
						{
							if( !stripLeadingSpaces || !startOfLine )
							{
								if( !startOfLine )
								{
									x++;
									
										//  start a new line if we go too far
									if( x >= width )
									{
										gen.append( CRLF );
										generatedLines.addElement( gen.toString() );
										gen = new StringBuffer( spaces( li ) );
										x = li;

										firstLine = false;
										startOfLine = true;
									}
									else
										gen.append( " " );
								}
								else
									gen.append( " " );
							}
							
							continue;
						}
						
							//  the word that will appear - use it for
							//  getting lengths and stuff - colour
							//  codes are filtered out
						int wordLength = 0;
						
						//  convert the word into something that can be displayed,
						//  and generate the length of the word for word-wrap purposes
						{
								//  here we insert our check for colour codes -
								//  if the word has a ^ in it, there is something
								//  we have to filter out :)
							StringBuffer fullWord = new StringBuffer();
							
							int wl = colourString( thisWord, fullWord );
							if( wl < 0 )
							{
								skipping = true;
								wordLength += -1 * wl;
									
									//  META: fix
									// this doesn't keep the word
									// we have so far?  Bad, bad...
								continue;
							}
							
							thisWord = fullWord.toString();
							wordLength = wl;
						}
						
							//  incidently, EW had a feature here 
							//  which enabled you to say how big a
							//  word can be before its split at the
							//  end of a line.  This isn't implemented
							//  here yet, I'm just noting down the fact
							//  that its an idea.
						
						//  Now add this word to the buffer, taking word wrap into account.
						//System.out.println( "adding word '" + thisWord + "'" );
						if( firstLine )
						{  //  first line
								//  if this word is going to wrap and the word
								//  isn't longer than a full line by itself
							if( ( (wordLength + x) > (width - (ri+rfi)) ) && (wordLength < (width-(ri+li)) ) )
							{
								gen.append( CRLF );
								generatedLines.addElement( gen.toString() );
								gen = new StringBuffer( spaces( li ) );
								x = li;

								firstLine = false;
								startOfLine = true;
							}
							//else
								//System.out.println( "word '" + thisWord + ", x=" + x + ", wl=" + wordLength + ", ri=" + ri + ", rfi=" + rfi + ", li=" + li );
						}
						else
						{
								//  if this word is going to wrap and the word
								//  isn't longer than a full line by itself
							if( ( (wordLength + x) > (width - ri) ) && (wordLength < (width-ri-li) ) )
							{
								gen.append( CRLF );
								generatedLines.addElement( gen.toString() );
								gen = new StringBuffer( spaces( li ) );
								x = li;
								startOfLine = true;
							}
							//else
								//System.out.println( "word '" + thisWord + ", x=" + x + ", wl=" + wordLength + ", ri=" + ri + ", li=" + li );
						}
						
							//  this if doesn't add the word if its a
							//  space, we're at the start of a line,
							//  and we're stripping leading spaces.
						if( !stripLeadingSpaces || !startOfLine )
						{
							gen.append( thisWord );
							x += wordLength;
							startOfLine = false;
						}
						else
						{
							if( !thisWord.equals( " " ) )
							{
								gen.append( thisWord );
								x += wordLength;
								startOfLine = false;
							}

								//  we don't reset the startOfLine if
								//  we're stripping and its just a space,
								//  in order to strip more than one space
								//  at the start of the line...
						}
					}
				}
			}
			
			if( gen.length() > 0 )
				generatedLines.addElement( gen.toString() );
			
				//  gen.toString() now contains the
				//  buffer to be displayed
				//  generatedLines contain the amount of lines
				//  ready to be displayed
			int numberOfLines = generatedLines.size();
			
			if( !okayToPage || ((!appending) && numberOfLines + PAGER_THRESHOLD <= height) )
			{
				for( int i = 0; i < numberOfLines; i++ )
					output.write( (String) generatedLines.elementAt( i ) );
			}
			else
			{
					//  invoke the pager
					//  if we're already paging through something, simply
					//  overwrite it.  (Don't append to a page buffer,
					//  unless explicitly told to)
				if( appending && pageBuffer != null )
				{
					for( int i = 0; i < numberOfLines; i++ )
						pageBuffer.addElement( generatedLines.elementAt( i ) );
				}
				else
				{
					pageBuffer = generatedLines;
					topOfScreen = 0;
					
					drawNextPage();
				}
			}
		}
	}
	
	public boolean canPage()
	{
		return( canPageEver );
	}
	
	public void setCanPage( boolean b )
	{
		canPageEver = b;
	}
	
	public boolean isPaging()
	{
		return( pageBuffer != null );
	}

	public void quitPager()
	{
		pageBuffer = null;
		topOfScreen = 0;
	}
	
	public void drawNextPage()
	{
		try
		{
			if( pageBuffer == null || topOfScreen >= pageBuffer.size() )
			{
				sendError( PAGER_LACKS_PAGES );
				
				quitPager();
				return;
			}
			
			int totalSize = pageBuffer.size();
			int newTop = topOfScreen + height - PAGER_VERTICAL_SPACE;
			
			if( newTop > totalSize )
				newTop = totalSize;
		
			for( int i = topOfScreen; i < newTop; i++ )
				output.write( (String) pageBuffer.elementAt( i ) );
			
			output.write( CRLF );
			
			if( newTop >= totalSize )
			{
					//  we're done, that was the last page
				quitPager();
			}
			else
			{
				send( "<Pager: lines " + topOfScreen + " to " + (newTop-1) + ", from a total " + totalSize + ": (^hn^-)ext page or (^hq^-)uit>" );
				
				topOfScreen = newTop;
			}
		}
		catch( IOException e )
		{
			close();
			throw new NetworkException( e );
		}
	}

/*
   This is some old code which generated the un-touched word from
   a word with colourcodes in it - it wasn't useful, so its been
   removed
	
		//  this is the colour matching scope
	{
		StringBuffer appearBuffer = new StringBuffer();
	
			//  here we insert our check for colour codes -
			//  if the word has a ^ in it, there is something
			//  we have to filter out :)
		int colourIndex = thisWord.indexOf( '^' );
		int start = 0;
		while( colourIndex != -1 && colourIndex < thisWord.length() )
		{
			sb.append( thisWord.substring( start, colourIndex ) );
			
			char c = thisWord.charAt( colourIndex + 1 );
			
			if( c == '^' )
			{		//  its been doubled to display it - thats cool
				sb.append( c );
				start = colourIndex+2;
			}
			else
			{		//  lets just skip one character
				start = colourIndex+2;
			}
			colourIndex = thisWord.indexOf( '^', start );
		}
		else
			sb.append( thisWord.substring( start ) );
				send( substitute( 

		appearWord = appearBuffer.toString();
	}
*/

		//  70 spaces - requesting more than this will
		//  slow the routine down
	private static final String SPACES="                                                                      "; 
	private static final int SPACES_LENGTH = SPACES.length();
	
	/**
	  * Returns a string of the specified number of spaces
	  * <p>
	  * This routine has been optimised for up to 70 spaces,
	  * requesting more than this will slow it down.
	 */
	public static String spaces( int n )
	{
		if( n > SPACES_LENGTH )
		{
			StringBuffer sb = new StringBuffer();
			while( n > SPACES_LENGTH )
			{
				sb.append( SPACES );
				n -= SPACES_LENGTH;
			}
			sb.append( SPACES.substring( SPACES_LENGTH - n ) );
			return( sb.toString() );
		}
		else
			return( SPACES.substring( SPACES_LENGTH - n ) );
	}
	
		//  70 dashes - requesting more than this will
		//  slow the routine down
	private static final String DASHES="----------------------------------------------------------------------"; 
	private static final int DASHES_LENGTH = DASHES.length();
	
	/**
	  * Returns a string of the specified number of dashes
	  * <p>
	  * This routine has been optimised for up to 70 spaces,
	  * requesting more than this will slow it down.
	 */
	public static String dashes( int n )
	{
		if( n > DASHES_LENGTH )
		{
			StringBuffer sb = new StringBuffer();
			while( n > DASHES_LENGTH )
			{
				sb.append( DASHES );
				n -= DASHES_LENGTH;
			}
			sb.append( DASHES.substring( DASHES_LENGTH - n ) );
			return( sb.toString() );
		}
		else
			return( DASHES.substring( DASHES_LENGTH - n ) );
	}
	
	/**
	  *  Calculates the length of a string, discarding colourcodes.
	  *
	  *  [ META: Should probably move to a different location.]
	 */
	public static final int stringLength( String s )
	{
		int start = 0;
		int subtraction = 0;
		int match;
		int length = s.length();
		
		while( true )
		{
			if( start < length )
			{
				match = s.indexOf( '^', start );

				if( match != -1 )
				{
					subtraction+=2;
					
					try
					{
						if( s.charAt( match+1 ) == '^' )
							subtraction--;
					}
					catch( StringIndexOutOfBoundsException e )
					{
						break;
					}
					
					start = match+2;
					
					continue;
				}
			}
			
			break;
		}
		
		return( length - subtraction );
	}
	
	/**
	  *  Will is Us, Do is them.
	 */
	static class TelnetOption
	{
		/**
		  *  The number of the code that we are
		 */
		public int teloptCode;
		
			//  telnet options
		public static final int TELOPT_BINARY=0;
		public static final int TELOPT_ECHO=1;
		public static final int TELOPT_SGA=3;
		public static final int TELOPT_TTYPE=24;
		public static final int TELOPT_EOR=25;
		public static final int TELOPT_NAWS=31;
		public static final int TELOPT_LINEMODE=34;
		public static final int TELOPT_SUBLIMINAL=257;

			//  variables required by the Q method - RFC 1143
		public int us;
		public int usq;
		public int him;
		public int himq;

		/**
		  *  True if we're allowed to enable this option in that
		  *  way (if we can Do this option or if we can support it)
		 */
		public boolean canDo;
		public boolean canWill;

		public static final int NO = 0;
		public static final int YES = 1;
		public static final int WANTNO = 2;
		public static final int WANTYES = 3;
		public static final int EMPTY = 4;
		public static final int OPPOSITE = 5;
		public static final int NONE = 6;
		
		public TelnetOption( int code, boolean supportHim, boolean supportUs )
		{
			teloptCode = code;
			us = NO;
			him = NO;
			usq = EMPTY;
			himq = EMPTY;
			canDo = supportHim;
			canWill = supportUs;
		}

		public final boolean enabledUs()
		{
			return( us == YES );
		}

		public final boolean enabledHim()
		{
			return( him == YES );
		}

		/**
		  *  Attempts to initiate a don't message for this option
		  *  to the output stream
		 */
		public void sendDont( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "sendDon't called" );
			switch( him )
			{
				case NO:
					Log.log( "telnet", "ERROR: Already disabled" );
					break;
				case YES:
					him = WANTNO;
					out.write( TelnetIC.IAC );
					out.write( TelnetIC.DONT );
					out.write( teloptCode );
					out.flush();
					break;
				case WANTNO:
					switch( himq )
					{
						case EMPTY:
							Log.log( "telnet", "ERROR: Already negotiating for disable" );
							break;
						case OPPOSITE:
							himq = EMPTY;
							break;
					}
					break;
				case WANTYES:
					switch( himq )
					{
						case EMPTY:
							Log.log( "telnet", "ERROR: Cannot initiate new request in the middle of negotiation" );
							break;
						case OPPOSITE:
							Log.log( "telnet", "ERROR: Already queued a disable request" );
							break;
					}
					break;
			}
		}

		/**
		  *  Attempts to initiate a do message for this option
		  *  to the output stream
		 */
		public void sendDo( OutputStream out ) throws IOException
		{
			if( canDo )
			{
				//Log.log( "telnet", "sendDo called" );
				switch( him )
				{
					case NO:
						him = WANTYES;
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.DO );
						out.write( teloptCode );
						out.flush();
						break;
					case YES:
						Log.log( "telnet", "ERROR: Already disabled" );
						break;
					case WANTNO:
						switch( himq )
						{
							case EMPTY:
								Log.log( "telnet", "ERROR: Cannot initiate new request in the middle of negotiation" );
								break;
							case OPPOSITE:
								Log.log( "telnet", "ERROR: Already queued a enable request" );
								break;
						}
						break;
					case WANTYES:
						switch( himq )
						{
							case EMPTY:
								Log.log( "telnet", "ERROR: Already negotiating for enable" );
								break;
							case OPPOSITE:
								himq = EMPTY;
								break;
						}
						break;
				}
			}
			else
				Log.log( "telnet", "ERROR: Attempting to sendDo when can false" );
		}

		/**
		  *  Attempts to initiate a don't message for this option
		  *  to the output stream
		 */
		public void sendWont( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "sendWon't called" );
			switch( us )
			{
				case NO:
					Log.log( "telnet", "ERROR: Already disabled" );
					break;
				case YES:
					us = WANTNO;
					out.write( TelnetIC.IAC );
					out.write( TelnetIC.WONT );
					out.write( teloptCode );
					out.flush();
					break;
				case WANTNO:
					switch( usq )
					{
						case EMPTY:
							Log.log( "telnet", "ERROR: Already negotiating for disable" );
							break;
						case OPPOSITE:
							usq = EMPTY;
							break;
					}
					break;
				case WANTYES:
					switch( usq )
					{
						case EMPTY:
							Log.log( "telnet", "ERROR: Cannot initiate new request in the middle of negotiation" );
							break;
						case OPPOSITE:
							Log.log( "telnet", "ERROR: Already queued a disable request" );
							break;
					}
					break;
			}
		}

		/**
		  *  Attempts to initiate a do message for this option
		  *  to the output stream
		 */
		public void sendWill( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "sendWill called" );
			if( canWill )
			{
				switch( us )
				{
					case NO:
						us = WANTYES;
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.WILL );
						out.write( teloptCode );
						out.flush();
						break;
					case YES:
						Log.log( "telnet", "ERROR: Already enabled" );
						break;
					case WANTNO:
						switch( usq )
						{
							case EMPTY:
								Log.log( "telnet", "ERROR: Cannot initiate new request in the middle of negotiation" );
								break;
							case OPPOSITE:
								Log.log( "telnet", "ERROR: Already queued an enable request" );
								break;
						}
						break;
					case WANTYES:
						switch( usq )
						{
							case EMPTY:
								Log.log( "telnet", "ERROR: Already negotiating for enable" );
								break;
							case OPPOSITE:
								usq = EMPTY;
								break;
						}
						break;
				}
			}
			else
				Log.log( "telnet", "ERROR: Attempting to sendWill when can false" );
		}
		
		public void rcptWont( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "rcptWon't called" );
			switch( him )
			{
				case NO:
					// ignore
					break;
				case YES:
					him = NO;
					out.write( TelnetIC.IAC );
					out.write( TelnetIC.DONT );
					out.write( teloptCode );
					out.flush();
					break;
				case WANTNO:
					switch( himq )
					{
						case EMPTY:
							him = NO;
							break;
						case OPPOSITE:
							him = WANTYES;
							himq = NONE;
							out.write( TelnetIC.IAC );
							out.write( TelnetIC.DO );
							out.write( teloptCode );
							out.flush();
							break;
					}
					break;
				case WANTYES:
					switch( himq )
					{
						case EMPTY:
							him = NO;
							break;
						case OPPOSITE:
							him = NO;
							himq = NONE;
							break;
					}
					break;
			}
		}
		
		public void rcptWill( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "rcptWill called" );
			switch( him )
			{
				case NO:
					if( canDo )
					{
						him = YES;
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.DO );
						out.write( teloptCode );
						out.flush();
					}
					else
					{
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.DONT );
						out.write( teloptCode );
						out.flush();
					}
					break;
				case YES:
						//  ignore
					break;
				case WANTNO:
					Log.log( "telnet", "ERROR:  Don't answered by Will." );
					switch( himq )
					{
						case EMPTY:
							him = NO;
							break;
						case OPPOSITE:
							him = YES;
							himq = EMPTY;
							break;
					}
					break;
				case WANTYES:
					switch( himq )
					{
						case EMPTY:
							him = YES;
							break;
						case OPPOSITE:
							him = WANTNO;
							himq = EMPTY;
							out.write( TelnetIC.IAC );
							out.write( TelnetIC.DONT );
							out.write( teloptCode );
							out.flush();
							break;
					}
					break;
			}
		}
		
		public void rcptDont( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "rcptDon't called" );
			switch( us )
			{
				case NO:
					// ignore
					break;
				case YES:
					us = NO;
					out.write( TelnetIC.IAC );
					out.write( TelnetIC.WONT );
					out.write( teloptCode );
					out.flush();
					break;
				case WANTNO:
					switch( usq )
					{
						case EMPTY:
							us = NO;
							break;
						case OPPOSITE:
							us = WANTYES;
							usq = NONE;
							out.write( TelnetIC.IAC );
							out.write( TelnetIC.WILL );
							out.write( teloptCode );
							out.flush();
							break;
					}
					break;
				case WANTYES:
					switch( usq )
					{
						case EMPTY:
							us = NO;
							break;
						case OPPOSITE:
							us = NO;
							usq = NONE;
							break;
					}
					break;
			}
		}
		
		public void rcptDo( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "rcptDo called" );
			switch( us )
			{
				case NO:
					if( canWill )
					{
						us = YES;
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.WILL );
						out.write( teloptCode );
						out.flush();
					}
					else
					{
						out.write( TelnetIC.IAC );
						out.write( TelnetIC.WONT );
						out.write( teloptCode );
						out.flush();
					}
					break;
				case YES:
						//  ignore
					break;
				case WANTNO:
					Log.log( "telnet", "ERROR:  Won't answered by Do." );
					switch( usq )
					{
						case EMPTY:
							us = NO;
							break;
						case OPPOSITE:
							us = YES;
							usq = EMPTY;
							break;
					}
					break;
				case WANTYES:
					switch( usq )
					{
						case EMPTY:
							us = YES;
							break;
						case OPPOSITE:
							us = WANTNO;
							usq = EMPTY;
							out.write( TelnetIC.IAC );
							out.write( TelnetIC.WONT );
							out.write( teloptCode );
							out.flush();
							break;
					}
					break;
			}
		}

	/*
		public String toString()
		{
			return( "[TelnetOption " + teloptCode + "]: 
		}
	*/
		
		void negotiation( TelnetIC tc ) throws IOException
		{
		}
	}

	final static class NAWSTelnetOption extends TelnetOption
	{
		public NAWSTelnetOption( int code, boolean supportHim, boolean supportUs )
		{
			super( code, supportHim, supportUs );
		}
		
		/**
		  *  This is the routine to handle the subnegotiation
		  *  of the telnet window size option
		 */
		void negotiation( TelnetIC tc ) throws IOException
		{
			try
			{
				tc.width = tc.read16Int()-1;
				tc.height = tc.read16Int();
				
				int a = tc.width;
				int b = tc.height;
				
				
				if( tc.width < TelnetIC.SMALLEST_BELIEVABLE_WIDTH || tc.width > TelnetIC.LARGEST_BELIEVABLE_WIDTH )
					tc.width = TelnetIC.DEFAULT_WIDTH;
				
				if( tc.height < TelnetIC.SMALLEST_BELIEVABLE_HEIGHT || tc.height > TelnetIC.LARGEST_BELIEVABLE_HEIGHT )
					tc.height = TelnetIC.DEFAULT_HEIGHT;
				
				if( a != tc.width || b != tc.height )
					Log.log( "telnet", "Didn't accepted Negotiated window size of : " + a + "x" + b + ", instead using: " + tc.width + "x" + tc.height );
				//else
					//tc.send( "Screen size of " + tc.width + "x" + tc.height + " detected" );
				
				int i;
				i = tc.inputStream.read();
				if( i == TelnetIC.IAC )
				{
					switch( tc.handleIAC() )
					{
						case TelnetIC.SE:
							break;
						default:
							Log.log( "telnet", "ERROR: Didn't receive expected SE" );
							break;
					}
				}
				else
					Log.log( "telnet", "ERROR: Didn't receive expected SE" );
			}
			catch( IACRecievedException e )
			{
				Log.log( "telnet", e.toString() );
			}
		}
	}

	final static class TerminalTypeTelnetOption extends TelnetOption
	{
		static final int IS=0;  //  used in Terminal-Type negotiations
		static final int SEND=1;

		String last="";
		TelnetIC telnet;
		
		public TerminalTypeTelnetOption( TelnetIC tc, int code, boolean supportHim, boolean supportUs )
		{
			super( code, supportHim, supportUs );
			
			telnet = tc;
		}
		
		/**
		  *  This is the routine to handle the subnegotiation
		  *  of the telnet terminal type option
		 */
		void negotiation( TelnetIC tc ) throws IOException
		{
			int i = tc.inputStream.read();
			if( i == IS )
			{
				//  receiving a terminal type
				String s = tc.readIACSETerminatedString();
				if( !telnet.setDetectedTerminal( s ) )
				{
					//Log.log( "telnet", "failed terminal match: " + s );
					if( !last.equals( s ) )
					{
						last = s;
						requestAnotherTerminal( tc.rawOutput );
						//Log.log( "telnet", "getting another terminal..." );
					}
				}
				//else
					//Log.log( "telnet", "made terminal match: " + telnet.getTerminal().getName() );
			}
		}

		public void rcptWill( OutputStream out ) throws IOException
		{
			super.rcptWill( out );

			if( enabledHim() )
			{
				requestAnotherTerminal( out );
			}
		}

		void requestAnotherTerminal( OutputStream out ) throws IOException
		{
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.SB );
			out.write( TELOPT_TTYPE );
			out.write( SEND );
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.SE );
			out.flush();
		}
	}

	/**
	  *  For echos, rather than implement some sort of complicated
	  *  queueing system for options, I choose to simply always
	  *  send do and won't requests, and to ignore the replies.  This
	  *  is what EW2 does with all options.
	  *
	  *  I understand that this isn't a particularly wonderful implementation,
	  *  however, echo gets turned on and off for passwords, and an auto-connect
	  *  client (like tinyfugue) can send the password before it responds to the
	  *  echo requests that it recieves.  We would, conceivably, only need a 
	  *  queue of length 2 in order to implement this properly, but its a
	  *  step that I'm not willing to make at the moment, for fear of screwing
	  *  it up.  I daresay that I only managed to get the options working at
	  *  all with several days effort and the help of the Q-method RFC.  (I
	  *  forget the number for it itself, its one of the ones listed at the
	  *  top of the file.  - subtle
	 */
	final static class EchoTelnetOption extends TelnetOption
	{
		public EchoTelnetOption( int code, boolean supportHim, boolean supportUs )
		{
			super( code, supportHim, supportUs );
		}
		
		/**
		  *  Attempts to initiate a don't message for this option
		  *  to the output stream
		 */
		public final void sendDont( OutputStream out ) throws IOException
		{
			//Log.log( "telnet", "sendDon't called" );
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.DONT );
			out.write( teloptCode );
			out.flush();
		}
		
		/**
		  *  Attempts to initiate a do message for this option
		  *  to the output stream
		 */
		public final void sendDo( OutputStream out ) throws IOException
		{
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.DO );
			out.write( teloptCode );
			out.flush();
		}

		/**
		  *  Attempts to initiate a don't message for this option
		  *  to the output stream
		 */
		public final void sendWont( OutputStream out ) throws IOException
		{
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.WONT );
			out.write( teloptCode );
			out.flush();
		}

		/**
		  *  Attempts to initiate a do message for this option
		  *  to the output stream
		 */
		public final void sendWill( OutputStream out ) throws IOException
		{
			out.write( TelnetIC.IAC );
			out.write( TelnetIC.WILL );
			out.write( teloptCode );
			out.flush();
		}
		
		public void rcptWont( OutputStream out )
		{
			// ignore
		}
		
		public void rcptWill( OutputStream out )
		{
			//  ignore
		}
		
		public void rcptDont( OutputStream out )
		{
			// ignore
		}
		
		public void rcptDo( OutputStream out )
		{
			//  ignore
		}
	}

	final static class IACRecievedException extends Exception
	{
		int following;
		public IACRecievedException( int codeAfter )
		{
			super( "IAC recieved unexpectedly" );
			following = codeAfter;
		}
	}
}
