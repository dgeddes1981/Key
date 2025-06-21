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
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  18Jul97    merlin       created class
**  19Jul97    subtle       now implements Thing, implemented read() and
**                          look
**
*/

package key;

import key.collections.*;
import key.primitive.StringSymbol;

import java.io.*;
import java.util.Enumeration;

/**
  *  A StringSet a a collections of String
  *  META: make this into a Thing
 */
public class StringSet extends Atom
{
	private static final long serialVersionUID = -1507681343901685357L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( StringSet.class, Paragraph.class, "description",
			AtomicElement.PUBLIC_FIELD,
			"the description of this stringset" ),
		AtomicElement.construct( StringSet.class, String.class, "called",
			AtomicElement.PUBLIC_FIELD,
			"the title of this string set" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	protected Collection strings;
	public Paragraph description;
	public String called;
	
	public StringSet()
	{
		description = new TextParagraph( "A leather-bound book\n" );
		called = "the leather-bound book";
		strings = new StringKeyCollection();
	}

	public StringSet( Object key )
	{
		this();
		setKey( key );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void addString( String string ) throws NonUniqueKeyException, BadKeyException
	{
		strings.link( new StringSymbol( string ) );
	}

	public void removeString( String string ) throws NonUniqueKeyException, BadKeyException
	{
		strings.unlink( new StringSymbol( string ) );
	}
	
	public Object get( String s )
	{
		return( strings.get( s ) );
	}

	public Object getElementAt( int i )
	{
		return( strings.getElementAt( i ) );
    }
	
	public int count()
	{
		return( strings.count() );
	}
	
	/*
	public Paragraph read( String args )
	{
		MultiParagraph mp = new MultiParagraph();
		
		StringBuffer sb = new StringBuffer();

		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			sb.append( (String) e.nextElement() );
			sb.append( '\n' );
		}

		mp.append( new HeadingParagraph( called ) );
		mp.append( new TextParagraph( sb.toString() ) );
		mp.append( LineParagraph.LINE );

		return( mp );
	}
	*/
	
	public Enumeration elements()
	{
		return( strings.elements() );
	}
	
	/*
	public Paragraph look()
	{
		return( description );
	}
	*/
}
