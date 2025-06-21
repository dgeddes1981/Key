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
**  $Id: ConnectionConfiguration.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul97     subtle       created this class
**
*/

package key.config;

import key.*;
import java.io.*;

public class ConnectionConfiguration extends Atom implements Configuration
{
	private static final long serialVersionUID = -2560257157422347122L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"newbieConfirm",
			AtomicElement.PUBLIC_FIELD,
			"the message displayed to a newbie to confirm their name" ),
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"disclaimer",
			AtomicElement.PUBLIC_FIELD,
			"the standard disclaimer, agreed to by all on the program" ),
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"newbieA",
			AtomicElement.PUBLIC_FIELD,
			"an introductory screen for newbies" ),
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"newbieB",
			AtomicElement.PUBLIC_FIELD,
			"the second introductory screen for newbies" ),
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"newbieAskEmail",
			AtomicElement.PUBLIC_FIELD,
			"uctory screen for newbies" ),
		AtomicElement.construct( ConnectionConfiguration.class, Paragraph.class,
			"genderRequest",
			AtomicElement.PUBLIC_FIELD,
			"the screen used to ask a newbie for their gender" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public Paragraph newbieConfirm = new TextParagraph( TextParagraph.LEFT, "At this point in time I'm going to ask you if you want to use this name, or select another.  First, however, it is important that you understand that names on Forest are often abbreviated and that its always much easier if you pick a name that doesn't conflict closely with anyone elses.  To help you decide whether you really want to use this name or not, here is a list of names that we believe may be close matches to yours:", 2, 2, 0, 0 );

	public Paragraph disclaimer = new TextParagraph();
	public Paragraph newbieA = new TextParagraph();
	public Paragraph newbieB = new TextParagraph();
	public Paragraph newbieAskEmail = new TextParagraph();
	public Paragraph genderRequest = new TextParagraph( TextParagraph.LEFT, "This program requires that you specify your gender.  This is used for the purposes of correct grammar.  Below, enter either m or f to indicate whether you are male or female.", 2, 2, 0, 0 );
	
	public ConnectionConfiguration()
	{
		setKey( "connection" );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
			//  revision code (example)
		if( newbieAskEmail == null )
			newbieAskEmail = new TextParagraph();
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
}
