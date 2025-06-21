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
**  $Id: Article.java,v 1.3 2000/02/18 10:15:17 noble Exp $
**
**  $Log: Article.java,v $
**  Revision 1.3  2000/02/18 10:15:17  noble
**  added summary to aspect
**
**  Revision 1.2  2000/01/31 16:37:50  noble
**  added aspect
**
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
**
*/

package key.te0;

import key.primitive.DateTime;
import key.*;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.*;

/**
  *  An article for te0
 */
public class Article extends Atom
{
	private static final long serialVersionUID = 5131186121428215416L;

	public static final int MAX_TITLE_LENGTH = 60;

	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Article.class, Paragraph.class,
			"contents",
			AtomicElement.PUBLIC_FIELD | AtomicElement.GENERATED,
			"the main text of the article" ),
		AtomicElement.construct( Article.class, Paragraph.class,
			"summary",
			AtomicElement.PUBLIC_FIELD | AtomicElement.GENERATED,
			"the short heading of the article" ),
		AtomicElement.construct( Article.class, String.class,
			"title", "title",
			AtomicElement.PUBLIC_FIELD,
			"the heading (in yellow on blue) of the article",
			AtomicSpecial.StringLengthLimit( MAX_TITLE_LENGTH, false, true ) ),
		AtomicElement.construct( Article.class, String.class,
			"author",
			AtomicElement.PUBLIC_FIELD,
			"the author of the article" ),			
		AtomicElement.construct( Article.class, DateTime.class,
			"when",
			AtomicElement.PUBLIC_FIELD,
			"the date the article was posted" ),
		AtomicElement.construct( Article.class, String.class,
			"iconUrl",
			AtomicElement.PUBLIC_FIELD,
			"the URL of the icon to use" ),
		AtomicElement.construct( Article.class, Integer.TYPE,
			"hits",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the number of hits this article got" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public Paragraph contents = new TextParagraph( "" );
	public Paragraph summary = new TextParagraph( "" );
	public String title;
	public String author;
	public String iconUrl;
	public DateTime when = new DateTime();
	
	protected int hits;
	
	public Article()
	{
		title = "";
		author = "";
		hits = 0;
		
		setKey( new Integer( 0 ) );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public boolean isEmpty()
	{
		return( contents.isEmpty() );
	}
	
	public int getHits()
	{
		return( hits );
	}	
	
	public void Hit()
	{
		hits++;
	}
	
	public Paragraph aspect()
	{
		MultiParagraph.Generator p = new MultiParagraph.Generator();
		
		p.append( new HeadingParagraph( title ) );
		
		if( summary != null && !summary.isEmpty() )
		{
			p.append( BlankLineParagraph.BLANKLINE );
			p.append( summary );			
		}
		
		p.append( BlankLineParagraph.BLANKLINE );
		p.append( contents );
		p.append( BlankLineParagraph.BLANKLINE );
		p.append( new HeadingParagraph( "author: " + author, HeadingParagraph.RIGHT ) );
		
		return( p.getParagraph() );
	}	
}
