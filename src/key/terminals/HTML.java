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

package key.terminals;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.Writer;
import java.io.IOException;
import key.*;

/**
  *  This class defines the basic abstract implementation of
  *  a terminal type.  It's specific to a TelnetConnection,
  *  since the other (custom) terminals will need changes at
  *  a much higher level.
 */
public class HTML extends Terminal
{
	Vector undoStack = new Vector( 15, 15 );
	boolean inHyper = false;
	
	public HTML()
	{
		super();
		names = new String[1];
		names[0] = "html";
	}

	public void reset( Writer outputStream ) throws IOException
	{
		outputStream.write( getReset() );
	}

	public final String getReset()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = undoStack.size() - 1; i >= 0; i-- )
		{
			sb.append( (String) undoStack.elementAt( i ) );
		}
		undoStack.setSize( 0 );
		normal = true;
		return( sb.toString() );
	}

	public String stringForCode( char code )
	{
		String result="";
		
		switch( code )
		{
			case '@':  // set mark if its not already
				if( marked == -1 )
					marked = undoStack.size();
				
				return( "" );
			
			case '$':
				if( marked != -1 )
				{
					StringBuffer sb = new StringBuffer();
					
						//  roll back to the marked place
					for( int i = undoStack.size() - 1; i >= marked; i-- )
					{
						sb.append( (String) undoStack.elementAt( i ) );
					}
					
					undoStack.setSize( marked );
					
						//  restore the previous colour
					return( sb.toString() );
				}
			
			case '-':
				if( undoStack.size() > 0 )
				{
					String s = (String) undoStack.elementAt( undoStack.size() - 1 );
					undoStack.setSize( undoStack.size() - 1 );
					return( s );
				}
				else
					return "";
		}
		
		normal = false;
		
		result = codeLookup( code );
		
			//  push this value onto the stack
		if( stackValue < STACK_SIZE )
			stack[ stackValue++ ] = code;
		else
			stack[ STACK_SIZE-1 ] = code;
		
		return( result );
	}
	
	public String codeLookup( char code )
	{
		switch( code )
		{
			case 'h':
			case 'H':
				undoStack.addElement( "</B>" );
				return( "<B>" );
			
			case 'n':
			case 'N':
				return( getReset() );

			case 'd':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='gray'>" );
			case 'D':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='black'>" );

			case 'r':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='red'>" );
			case 'R':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='red'><B>" );

			case 'g':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='green'>" );
			case 'G':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='green'><B>" );

			case 'y':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='yellow'>" );
			case 'Y':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='yellow'><B>" );

			case 'b':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='blue'>" );
			case 'B':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='blue'><B>" );
			
			case 'm':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='magenta'>" );
			case 'M':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='magenta'><B>" );

			case 'c':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='cyan'>" );
			case 'C':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='cyan'><B>" );

			case 'w':
				undoStack.addElement( "</FONT>" );
				return( "<FONT color='white'>" );
			case 'W':
				undoStack.addElement( "</B></FONT>" );
				return( "<FONT color='white'><B>" );
				
			case '_':
				undoStack.addElement( "</U>" );
				return( "<U>" );
				
			case '(':
				return null;
			
			case '{':
				inHyper = true;
				return( "<IMG SRC=\"" );
			
			case '}':
				return( "\">" );
			
				//  intended usage: ^[http://mylink.com|mylink^]
				//  in html, allows you to name the link
			case '|':
				if( inHyper )
				{
					inHyper = false;
					return( "\">" );
				}
				else
					return( "" );
			
			case '[':
				inHyper = true;
			case '<':
				return( "<A HREF=\"" );
			
			case ']':
				if( inHyper )
					return( "\">link</A>" );
				else
					return( "</A>" );
			
			case '>':
				return( "\">link</A>" );
		}
		
		return( "" );
	}
	
	private final static String until[] = { "^)" };
	public String[] getUntil()
	{
		return( until );
	}
	
	public String clearScreen()
	{
		return( "<HR>" );
	}
	
	public String beep()
	{
		return( "" );
	}
}
