package key;

import key.util.LinkedList;

import java.io.*;
import java.util.Enumeration;

/**
  *  'MultiParagraph' is basically a paragraph which consists of others, ordered
  *  sequentially.  So you might have a line, then some text, then a trailing line,
  *  and wish to treat it all as a single paragraph.
 */
public final class MultiParagraph extends Paragraph
{
	private LinkedList subParagraphs;
	
	protected MultiParagraph( LinkedList ll )
	{
		subParagraphs = ll;
	}
	
	private MultiParagraph()
	{
		subParagraphs = new LinkedList();
	}
	
	public Enumeration getParagraphs()
	{
		return( subParagraphs.elements() );
	}
	
	public Paragraph substitute( String[] codes )
	{
		MultiParagraph mp = new MultiParagraph();
		
		for( Enumeration e = subParagraphs.elements(); e.hasMoreElements(); )
			mp.subParagraphs.append( ((Paragraph)e.nextElement()).substitute( codes ) );
		
		return( mp );
	}
	
	public String toString()
	{
		return( "Multi-paragraph, " + subParagraphs.count() + " sub-paragraphs" );
	}
	
	/**
	  *  You use this class to build a MultiParagraph step by step,
	  *  as a MultiParagraph is immutable and may not be modified
	 */
	public static class Generator
	{
		private LinkedList subParagraphs;
		
		public Generator()
		{
			subParagraphs = new LinkedList();
		}
		
		public void append( Paragraph p )
		{
			if( p != null )
				subParagraphs.append( p );
		}
		
		public MultiParagraph getParagraph()
		{
			return( new MultiParagraph( subParagraphs ) );
		}
	}
}
