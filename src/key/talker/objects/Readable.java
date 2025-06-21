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
** $Log: Readable.java,v $
** Revision 1.3  2000/05/01 16:12:38  subtle
** added log
**
*/

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

/**
 */
public class Readable extends Prop
{
	private static final long serialVersionUID = -8541314343678330420L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Readable.class, Paragraph.class, "read",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to a person who reads the object" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Prop.STRUCTURE, ELEMENTS );
	
	public Paragraph read = new TextParagraph( "You see nothing to read" );
	
	public Readable()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( Grammar.substitute( read ) );
	}
}
