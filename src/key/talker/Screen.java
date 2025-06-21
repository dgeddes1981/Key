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
**  $Id: Screen.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.primitive.*;
import java.util.Enumeration;
import java.io.*;
import java.util.StringTokenizer;

public class Screen extends Atom implements WebAccessible
{
	private static final long serialVersionUID = -5710933275636934878L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Screen.class, Paragraph.class, "text",
			AtomicElement.PUBLIC_FIELD,
			"the body of the screen" ),
		AtomicElement.construct( Screen.class, String.class, "title",
			AtomicElement.PUBLIC_FIELD,
			"the title of the screen" ),
		AtomicElement.construct( Screen.class, String.class, "author",
			AtomicElement.PUBLIC_FIELD,
			"the author of this screen" ),
		AtomicElement.construct( Screen.class, DateTime.class, "modified",
			AtomicElement.PUBLIC_FIELD,
			"the last modified date of this screen (not automatic)" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	Paragraph text = new TextParagraph( "" );
	String title = "";
	String author = "";
	DateTime modified = new DateTime();
	
	public Screen()
	{
	}
	
	public Screen( Paragraph p )
	{
		text = p;
	}
	
	public Paragraph getParagraph()
	{
		return( text );
	}

	public String getAuthor()
	{
		return( author );
	}

	public DateTime getModified()
	{
		return( modified );
	}
	
	public void setParagraph( Paragraph p )
	{
		permissionList.check( modifyAction );
		text = p;
		modify();
	}
	
	public void modify()
	{
		modified = new DateTime();
	}

	public boolean isModifiedAfter( DateTime dt )
	{
		return( modified.after( dt ) );
	}
	
	public boolean isEmpty()
	{
		return( text.isEmpty() );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
		/**  An aspect is its description when looked at */
	public Paragraph aspect()
	{
		MultiParagraph.Generator p = new MultiParagraph.Generator();
		
		p.append( new HeadingParagraph( getKey().toString() + " - " + title ) );
		p.append( text );
		p.append( new HeadingParagraph( "author: " + author, HeadingParagraph.RIGHT ) );
		
		return( p.getParagraph() );
	}

	public String getContentType()
	{
		return( "text/html" );
	}
	
	public Paragraph getContent( String line )
	{
		return( aspect() );
	}
}
