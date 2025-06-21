package key;

import java.io.*;
import java.util.StringTokenizer;

/**
  *  A rough guide would be not to put anything much more than about 30
  *  characters in here, or it may start to look really dumb
 */
public class HeadingParagraph extends Paragraph
{
	private String text;
	private int alignment;

	public static final int CENTRE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	
	public HeadingParagraph()
	{
		text = "";
		alignment = CENTRE;
	}
	
	public HeadingParagraph( HeadingParagraph p )
	{
		text = p.text;
		alignment = p.alignment;
	}
	
	public HeadingParagraph( String value )
	{
		text = value;
		alignment = CENTRE;
	}
	
	public HeadingParagraph( String value, int align )
	{
		text = value;
		alignment = align;
	}
	
	public final String getText()
	{
		return( text );
	}

	public final int getAlignment()
	{
		return( alignment );
	}

	public String toString()
	{
		return( "HeadingParagraph: '" + text + "'" );
	}
	
	public Paragraph substitute( String[] codes )
	{
		String s = Grammar.substitute( text, codes );
		
		if( s != text )
		{
			HeadingParagraph tp = new HeadingParagraph( this );
			tp.text = s;
			return( tp );
		}
		else
			return( this );
	}
}
