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

package key.web;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import key.terminals.HTML;

public final class Converter
{
	public static final String CRLF = "\r\n";
	
	public static String convert( Paragraph p )
	{
		StringBuffer gen = new StringBuffer();
		convertAppendParagraph( p, gen, new HTML() );
		return( gen.toString() );
	}
	
	private static void convertAppendParagraph( Paragraph p, StringBuffer gen, HTML terminal )
	{
		if( p instanceof TextParagraph )
		{
			TextParagraph tp = (TextParagraph) p;
			
			switch( tp.getAlignment() )
			{
				case TextParagraph.LEFT:
					gen.append( "<DIV ALIGN=LEFT>" );
					gen.append( CRLF );
					convertAppendString( tp.getText(), gen, true, terminal );
					gen.append( "</DIV>" );
					break;
				case TextParagraph.CENTERALIGNED:
					gen.append( "<DIV ALIGN=CENTER><PRE>" );
					gen.append( CRLF );
					convertAppendString( tp.getText(), gen, false, terminal );
					gen.append( "</PRE></DIV>" );
					break;
				default:
					gen.append( "<DIV>" );
					gen.append( CRLF );
					convertAppendString( tp.getText(), gen, true, terminal );
					gen.append( "</DIV>" );
					break;
			}
			
			gen.append( CRLF );
			gen.append( terminal.getReset() );
		}
		else if( p instanceof HeadingParagraph )
		{
			HeadingParagraph hp = (HeadingParagraph) p;
			
			switch( hp.getAlignment() )
			{
				case HeadingParagraph.LEFT:
					gen.append( "<TABLE WIDTH=100% CELLPADDING=0 CELLSPACING=0><TR><TD ALIGN=CENTER><B>" );
					gen.append( CRLF );
					
					convertAppendString( hp.getText(), gen, true, terminal );
					
					gen.append( "</B></TD><TD WIDTH=80%><HR WIDTH=100%></TD></TR></TABLE>" );
					break;
				case HeadingParagraph.CENTRE:
					gen.append( "<TABLE WIDTH=100% CELLPADDING=0 CELLSPACING=0><TR><TD WIDTH=40%><HR WIDTH=100%></TD><TD ALIGN=CENTER><B>" );
					gen.append( CRLF );
					
					convertAppendString( hp.getText(), gen, true, terminal );
					
					gen.append( "</B></TD><TD WIDTH=40%><HR WIDTH=100%></TD></TR></TABLE>" );
					break;
				case HeadingParagraph.RIGHT:
					gen.append( "<TABLE WIDTH=100% CELLPADDING=0 CELLSPACING=0><TR><TD WIDTH=80%><HR WIDTH=100%></TD><TD ALIGN=CENTER><B>" );
					gen.append( CRLF );
					
					convertAppendString( hp.getText(), gen, true, terminal );
					
					gen.append( "</B></TD></TR></TABLE>" );
					break;
				default:
					gen.append( "<H1>" );
					gen.append( CRLF );
					convertAppendString( hp.getText(), gen, true, terminal );
					gen.append( "</H1>" );
					break;
			}
			
			gen.append( CRLF );
			gen.append( terminal.getReset() );
		}
		else if( p instanceof ColumnParagraph )
		{
			ColumnParagraph cp = (ColumnParagraph) p;
			
			int numCols = 120 / cp.getMaxEntryWidth();
			int count = 0;
			int pos = 0;
			
			gen.append( "<CENTER><TABLE><TR>" );
			
			for( Enumeration e = cp.elements(); e.hasMoreElements(); )
			{
				gen.append( "<TD>" );
				convertAppendString( (String) e.nextElement(), gen, false, terminal );
				gen.append( terminal.getReset() );
				gen.append( "</TD>" );
				count++;
				pos = (++pos) % numCols;

				if( pos == 0 )
					gen.append( "</TR><TR>" );
			}
			
			gen.append( "</TR></TABLE></CENTER>" );
			gen.append( CRLF );
		}
		else if( p instanceof TableParagraph )
		{
			TableParagraph tap = (TableParagraph) p;
			
			gen.append( "<TABLE border=1 width=100%>" );
			gen.append( CRLF );
			gen.append( "<TR>" );
			
			int columns = tap.countColumns();
			
			for( int i = 0; i < columns; i++ )
			{
				gen.append( "<TH>" );
				convertAppendString( tap.getColumnAt( i ).getHeading(), gen, false, terminal );
				gen.append( "</TH>" );
			}
			
			gen.append( "</TR>" );
			gen.append( CRLF );
			
			for( Enumeration e = tap.elements(); e.hasMoreElements(); )
			{
				String[] rd = (String[]) e.nextElement();
				gen.append( "<TR>" );

				for( int i = 0; i < columns; i++ )
				{
					if( rd == null )
						gen.append( "<TD>&nbsp;</TD>" );
					else
					{
						gen.append( "<TD>" );
						convertAppendString( rd[i], gen, false, terminal );
						gen.append( terminal.getReset() );
						gen.append( "</TD>" );
					}
				}
				
				gen.append( terminal.getReset() );
				gen.append( "</TR>" );
				gen.append( CRLF );
			}
			
			gen.append( "</TABLE><BR>" );
			convertAppendString( tap.getFooter(), gen, false, terminal );
			gen.append( terminal.getReset() );
			gen.append( "<BR>" );
			gen.append( CRLF );
		}
		else if( p instanceof LineParagraph )
		{
			gen.append( "<HR>" );
			gen.append( CRLF );
		}
		else if( p instanceof BlankLineParagraph )
		{
			gen.append( "<BR>" );
			gen.append( CRLF );
		}
		else if( p instanceof MultiParagraph )
		{
			for( Enumeration e = ((MultiParagraph)p).getParagraphs(); e.hasMoreElements(); )
			{
				convertAppendParagraph( (Paragraph) e.nextElement(), gen, terminal );
			}
		}
	}
	
	public static String convert( String message )
	{
		StringBuffer gen = new StringBuffer();
		convertAppendString( message, gen, true, new HTML() );
		return( gen.toString() );
	}
	
	public static String convert( String message, boolean replaceCR )
	{
		StringBuffer gen = new StringBuffer();
		convertAppendString( message, gen, replaceCR, new HTML() );
		return( gen.toString() );
	}
	
	private static void convertAppendString( String message, StringBuffer gen, boolean replaceCR, HTML terminal )
	{
		StringTokenizer lines = new StringTokenizer( message, "\n", true );
		
		while( lines.hasMoreTokens() )
		{
			String str = lines.nextToken();
			
			if( str.equals( "\n" ) )
			{
				if( replaceCR )
				{
					gen.append( terminal.getReset() );
					gen.append( "<BR>" );
					gen.append(  CRLF );
				}
			}
			else
			{
					//  this is the colour matching scope
					
					//  here we insert our check for colour codes -
					//  if the word has a ^ in it, there is something
					//  we have to filter out :)
				int colourIndex = str.indexOf( '^' );
				int start = 0;
				
				while( colourIndex != -1 && (colourIndex+1) < str.length() )
				{
					gen.append( str.substring( start, colourIndex ) );
					
					char c = str.charAt( colourIndex + 1 );
					
					if( c == '^' )
					{		//  its been doubled to display it - thats cool
						gen.append( '^' );
						start = colourIndex+2;
					}
					else
					{		//  lets just skip one character
						start = colourIndex+2;
						gen.append( terminal.stringForCode( c ) );
					}
					
					colourIndex = str.indexOf( '^', start );
				}
				
				gen.append( str.substring( start ) );
			}
		}
		
		gen.append( terminal.getReset() );
		
		if( replaceCR )
		{
			gen.append( "<BR>" );
		}
	}
}
