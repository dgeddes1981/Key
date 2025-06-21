/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"

The Twig Protocol

For lack of a better standard, the follow system was implemented.

For convenience, ASCII convention will be used for the delimiters.

Data is in blocks.  At the end of a block, the EOT (end of transmission)
code is sent:

ASCII 0/4 is decimal 004, hex 04, octal 004, bits 00000100: called ^D, EOT
Official name: End Of Transmission

Data sent to twig will be broken into "forms".  A 'form' is simply a nice
way of seperating standard command arguments.  For instance, all data will
begin with an identifier, such as "SYSMSG", then a "form seperator".  A form
seperator is an ASCII convention:

ASCII 1/12 is decimal 028, hex 1c, octal 034, bits 00011100: called ^\, FS
Official name: Form Separator

The number of forms for each command is determined by the name of the form.

The SYSMSG commandform has the following structure:

	SYSMSG<FS>random text data to be displayed to user<EOT>

More complicated structures exist.

For instance, to send columns, we use the COLUMNS format:

	COLUMNS<FS>column entry 1<GS>column entry 2<GS>column entry 3<GS><EOT>

"GS" is:

ASCII 1/13 is decimal 029, hex 1d, octal 035, bits 00011101: called ^], GS
Official name: Group Separator

For tables, we send: (for the session command)

	TABLE<FS><RS><GS>name<GS>comment<RS><GS>subtle<GS>this is a cool
	feature<RS>snapper<GS>this sucks!<FS>footer text<EOT>

Notice this has three "form" level entries, the "commandname" TABLE, the
"entries" (all the names and comments), and the "footer text".

The most important entry is the "MESSAGE" format, through which most tells
are received.  It's kind of strange, in order to allow for future
expansion, but it looks like:

	MESSAGE<FS>player who sent it<FS>type of the message<FS>type of where it was sent from last<FS>id of where it was last sent from<FS>the message itself<EOT>

	Fields of this CAN be blank, so don't assume their length is > 0
	
Here's an example of a tell:
	
	MESSAGE<FS>puck<FS>key.effect.Directed<FS>key.Player<FS>/players/puck<FS>^M^@puck^$ exclaims to you '^@hi there!^$<EOT>

Here's a room entry to the town square:

	MESSAGE<FS>puck<FS>key.effect.Enter<FS>key.PublicRoom<FS>/realm/city/square<FS>^rpuck appears from no-where.<EOT>

Here's a say from the town square:

	MESSAGE<FS>puck<FS>key.effect.Broadcast<FS>key.PublicRoom<FS>/realm/city/square<FS>^c^@puck^$ says '^@I'm baack^$'<EOT>

You get the idea, I'm sure.

Oh, immediately after any prompt, a ENQ ascii character is sent, saying it's a
prompt.  A prompt looks like:

	PROMPT<FS>prompt text<EOT><ENQ>

For instance, when you first connect, you'll get:

	PROMPT<FS>SENDNAME:Please send your name<EOT><ENQ>

then

	PROMPT<FS>SENDPASS:Please send your password<EOT><ENQ>

Your normal logged in prompt might be

	PROMPT<FS>-> <EOT><ENQ>

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
public final class TwigIC
extends SocketIC
{
	private char[] inBuffer;
	
	private transient String[] until = null;
	
	public static final int INPUT_BUFFER = 640;
	public static final int MAX_OVERRUN = 640;
	
	//  for lack of a better protocol, we will use
	//  standard ASCII codes:  (The below codes are in octal)
	
//    0 NUL  
//    1 SOH  
//    2 STX  
//    3 ETX  
//    4 EOT
	
		/** Used to indicate the end of a transmission block */
	public static final String EOT = "\004";
	
//    5 ENQ  
	
		/** Used to indicate that we're asking a question */
	public static final String ENQ = "\005";
	
//   34 FS
	
		/** Used to seperate command words */
	public static final String FS = "\034";
	
//   35 GS  - Group Separator
	
		/** Used to seperate columns in a table or column set */
	public static final String GS = "\035";
	
//   36 RS  - Record Separator
		/** Used to seperate rows in a table */
	public static final String RS = "\036";
	
//   37 US
	
	/**
	  * Constructor
	  *
	  * @param s The socket that the connection is on
	 */
	public TwigIC( Socket s ) throws IOException
	{
		super( s );
		inBuffer = new char[INPUT_BUFFER+2];
		
			//  since we don't have a null constructor, we can't
			//  be created by the factory: this is our concession.
		Factory.postCreateProcess( this );
	}
	
	/**
	  *  beeps the terminal, if supported.
	 */
	public void beep()
	{
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
			throw new NetworkException( e );
		}
	}
	
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
			
			output.write( "PROMPT" );
			output.write( FS );
			output.write( prompt );
			output.write( EOT );
			output.write( ENQ );
			output.flush();
			
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
				}
				else if( b == 8 || b == 127 )
				{
					if( where > 0 )
						where--;
				}
			} while( !((b == '\r') && !(lastCharWas == '\n')) && !((b == '\n') && !(lastCharWas == '\r' )) );
			
			lastCharWas = b;
			
			unIdle();
			
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
		return( input( prompt ) );
	}
	
	public void sendEffect( char preChar, String message, Effect e )
	{
		try
		{
			output.write( "MESSAGE" );
			output.write( FS );
			
			Player o = e.getOriginator();
			
			if( o != null )
				output.write( o.getName() );
			output.write( FS );
			output.write( e.getClass().getName() );
			output.write( FS );
			
			Atom rs = e.getSplasher();
			if( rs != null )
			{
				output.write( rs.getClass().getName() );
				output.write( FS );
				output.write( rs.getId() );
			}
			else
				output.write( FS );
			
			output.write( FS );
			output.write( message );
			output.write( EOT );
		}
		catch( IOException ex )
		{
		}
	}
	
	public void sendLine()
	{
		try
		{
			output.write( "LINE" );
			output.write( EOT );
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
			output.write( "RAW" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "SYSMSG" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "FAILURE" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "ERROR" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "FEEDBACK" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "SYSMSG" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "QUALIFIED" );
			output.write( FS );
			output.write( String.valueOf( qualifier ) );
			output.write( FS );
			output.write( message );
			output.write( EOT );
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
			output.write( "SYSMSG" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
		}
	}
	
	protected void sendColumnParagraph( ColumnParagraph tp, boolean appending, boolean okayToPage ) throws IOException
	{
		output.write( "COLUMNS" );
		output.write( FS );
		
		for( Enumeration e = tp.elements(); e.hasMoreElements(); )
		{
			output.write( (String) e.nextElement() );
			output.write( GS );
		}
		
		output.write( EOT );
	}
	
	protected void sendTableParagraph( TableParagraph tp, boolean appending, boolean okayToPage ) throws IOException
	{
		output.write( "TABLE" );
		output.write( FS );
		
			//  add the top heading rows
		for( int i = 0; i < tp.columns.length; i++ )
		{
			output.write( RS );
			output.write( GS );
			output.write( tp.columns[ i ].getHeading() );
		}
		
		
			//  now add the rows for the data
		for( Enumeration e = tp.elements(); e.hasMoreElements(); )
		{
			String[] rd = (String[]) e.nextElement();
			
			output.write( RS );
			for( int i = 0; i < rd.length; i++ )
			{
				output.write( GS );
				output.write( rd[i] );
			}
		}
		
			//  now add the footer
		String tm = tp.getFooter();

		if( tm != null )
		{
			output.write( FS );
			output.write( tm );
		}
		
		output.write( EOT );
	}
	
	protected void sendHeadingParagraph( String message, int alignment, boolean appending, boolean okayToPage ) throws IOException
	{
		output.write( "HEADING" );
		output.write( FS );
		output.write( message );
		output.write( EOT );
	}
	
	public final void send( Paragraph para )
	{
		send( para, true );
	}
	
	public void send( Paragraph para, boolean okayToPage )
	{
		try
		{
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
						blankLine();
					}
					else if( o instanceof LineParagraph )
					{
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
	}
	
	public void blankLine()
	{
	}
	
	/**
	  *  You never know what they _might_ have available on their terminal...
	  *  *grin*
	 */
	public void sendSubliminal( String message, int duration, int frequency )
	{
		try
		{
			output.write( "SUBLIMINAL" );
			output.write( FS );
			output.write( message );
			output.write( EOT );
		}
		catch( IOException e )
		{
			throw new NetworkException( e );
		}
	}

	public boolean check()
	{
		return( false );
	}

	public boolean isPaging()
	{
		return( false );
	}
}
