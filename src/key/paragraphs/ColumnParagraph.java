package key;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 */
public final class ColumnParagraph extends Paragraph
{
	Vector entries;
	int maxEntryWidth;
	int spaceBetween;
	
	ColumnParagraph( Generator g )
	{
		maxEntryWidth = g.maxEntryWidth;
		spaceBetween = g.spaceBetween;
		entries = g.entries;
	}
	
	public int count()
	{
		return( entries.size() );
	}

	public int getMaxEntryWidth()
	{
		return( maxEntryWidth );
	}
	
	public Enumeration elements()
	{
		return( entries.elements() );
	}

	public String toString()
	{
		return( "ColumnParagraph" );
	}
	
	public static class Generator
	{
		Vector entries;
		int maxEntryWidth;
		int spaceBetween;
		
		public Generator()
		{
			maxEntryWidth = 38;
			spaceBetween = 2;
			entries = new Vector( 4, 4 );
		}
		
		public Generator( int maxWidth, int space )
		{
			maxEntryWidth = maxWidth;
			spaceBetween = space;
			
			entries = new Vector( 4, 4 );
		}
		
		public void appendEntry( String value )
		{
			entries.addElement( value );
		}
		
		public int count()
		{
			return( entries.size() );
		}

		public ColumnParagraph getParagraph()
		{
			return( new ColumnParagraph( this ) );
		}
	}
}
