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
**  Class: contents
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  22Jul97     snapper      created this command
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Contents extends Command
{
	private static final long serialVersionUID = -5468011751675710336L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Contents.class, Container.class, "containerFor",
			AtomicElement.PUBLIC_FIELD,
			"the container to list the contents of" ),
		AtomicElement.construct( Contents.class, String.class, "empty",
			AtomicElement.PUBLIC_FIELD,
			"the string to send if there is nothing in the container" ),
		AtomicElement.construct( Contents.class, String.class, "footer",
			AtomicElement.PUBLIC_FIELD,
			"the footer of the listing" ),
		AtomicElement.construct( Contents.class, String.class, "singular",
			AtomicElement.PUBLIC_FIELD,
			"the footer if there is only one element in the container" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Reference containerFor = Key.instance().getThis();
	public String empty = "Nothing in container.";
	public String footer = "%n total";
	public String singular = "only one";
	
	public static final char numberCode = 'n';
	
	public Contents()
	{
		setKey( "contents" );
		usage = "<name or name segment>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		ColumnParagraph.Generator columns = new ColumnParagraph.Generator( Player.MAX_NAME, 2 );
		int numElements = 0;
		
		Container cf = (Container) containerFor.get();
		
		for( Enumeration e = cf.referenceElements(); e.hasMoreElements(); )
		{
			Reference o = (Reference) e.nextElement();
			
			//  META: commented out this code, I honestly don't know
			//  what it was meant to do... can't think of a reason
			//  to do this... subtle Oct 10, 1998
			//QualifierList ql = p.getQualifierList();
			//Type t = Type.typeOf( cf );
			//if( ql.check( t ) != Qualifiers.SUPPRESSION_CODE )
			//{
				columns.appendEntry( o.getName() );
				
				numElements++;
			//}
		}
		
		if( numElements == 0 )
		{
			ic.sendFeedback( empty );
		}
		else
		{
			ic.send( LineParagraph.LINE );
			ic.send( columns.getParagraph() );
			
			if( numElements == 1 )
			{
				ic.send( new HeadingParagraph( Grammar.substitute( singular ) ) );
			}
			else
			{
				p.putCode( numberCode, Integer.toString( numElements ) );
				
				String foot = Grammar.substitute( footer, p.getCodes() );
				ic.send( new HeadingParagraph( foot ) );
			}
		}
	}
}
