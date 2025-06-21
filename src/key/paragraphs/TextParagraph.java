package key;

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
  *  'TextParagraph' is used for many room descriptions, and which
  *  can be set to be formatted in various ways, such as justification (left,
  *  right and center), as well as indenting (left, right, firstlineLeft, firstlineRight)
 */
public class TextParagraph extends Paragraph
{
	private static final long serialVersionUID = -8586198511161550286L;
	private String text;
	
	private int left=0;
	private int right=0;
	private int leftFirst=0;
	private int rightFirst=0;
	
	private int alignment;
	
	public static final int LEFT = 0;

	/**
	  *  Displays the paragraph centered on the screen, but
	  *  left aligned within itself
	  * <p>
	  *  Doesn't make use of first line indents -
	  *  they are irrelevant.
	  * <p>
	  *  It doesn't make an incredible amount of sense to use
	  *  left and right indent either, but it could be useful
	  *  to have them non-zero if you wanted, say, to center
	  *  something 'just left of center' on the screen or
	  *  something like that.
	 */
	public static final int CENTERALIGNED = 1;
	
	public static final int CENTERED = 2;
	//(unsupported)  public static final int RIGHT = 3;
	//(unsupported)  public static final int FULL = 4;

	public TextParagraph()
	{
		text = "";
	}

	public TextParagraph( String value )
	{
		text = value;
		if( text == null )
			text = "";
		stripTrailingCR();
	}
	
	public TextParagraph( TextParagraph p )
	{
		text = p.text;
		left = p.left;
		right = p.right;
		leftFirst = p.leftFirst;
		rightFirst = p.rightFirst;
		alignment = p.alignment;
		stripTrailingCR();
	}
	
	public TextParagraph( int newAlignment, String value )
	{
		text = value;
		if( text == null )
			text = "";
		alignment = newAlignment;
		stripTrailingCR();
	}
	
	public TextParagraph( int newAlignment, String value, int left, int right, int leftFirst, int rightFirst )
	{
		this( newAlignment, value );
		setMargins( left, right, leftFirst, rightFirst );
	}
	
	private void stripTrailingCR()
	{
		if( text.endsWith( "\n" ) )
			text = text.substring( 0, text.length() - 1 );
	}
	
	public final String getText()
	{
		return( text );
	}

	public final boolean isEmpty()
	{
		return( text.length() == 0 );
	}

	private final void setMargins( int li, int ri, int lfi, int rfi )
	{
		left = li;
		right = ri;
		leftFirst = lfi;
		rightFirst = rfi;
	}

	public final int getAlignment()
	{
		return( alignment );
	}

	public final int getLeftMargin()
	{
		return( left );
	}

	public final int getRightMargin()
	{
		return( right );
	}

	public final int getLeftFirstMargin()
	{
		return( leftFirst );
	}

	public final int getRightFirstMargin()
	{
		return( rightFirst );
	}

	public final long length()
	{
		return( text.length() );
	}
	
	public final int numberOfNewLines()
	{
		int i = 0;
		int c = -1;
		
		if( text == null )
			return( 0 );
		
		int tl = text.length();
		
		do
		{
			c++;
			i = text.indexOf( '\n', i ) + 1;
		} while( i != 0 && i < tl );
		
		return( c + 1 );
	}
	
	public String toString()
	{
		return( "TextParagraph, " + text.length() + " characters." );
	}
	
	public Paragraph substitute( String[] codes )
	{
		String s = Grammar.substitute( text, codes );
		
		if( s != text )
		{
			TextParagraph tp = new TextParagraph( this );
			tp.text = s;
			return( tp );
		}
		else
			return( this );
	}
}
