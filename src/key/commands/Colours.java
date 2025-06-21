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
**  $Id: Colours.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Colours extends Command
{
	private static final long serialVersionUID = 34645005566708980L;
	public Colours()
	{
		setKey( "colours" );
		
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		QualifierList ql = p.getQualifierList();
		int c = 0;
		
		ColumnParagraph.Generator cp = new ColumnParagraph.Generator( 30, 5 );
		
		for( Enumeration e = ql.elements(); e.hasMoreElements(); )
		{
			QualifierList.Entry qle = (QualifierList.Entry) e.nextElement();

			if( qle.get() != Qualifiers.SUPPRESSION_CODE )
			{
				cp.appendEntry( qle.toString() );
				c++;
			}
			else if( qle.getMark() != Qualifiers.UNKNOWN_CODE )
			{
				cp.appendEntry( qle.toMarkedString() );
				c++;
			}
		}
		
		ic.send( headingParagraph );
		
		if( c == 0 )
		{
			ic.send( noColoursParagraph );
			ic.sendLine();
			return;
		}
		
		ic.send( cp.getParagraph() );
		ic.send( hintParagraph );
		ic.send( new HeadingParagraph( c + " entr" + ((c==1) ? "y" : "ies"), HeadingParagraph.RIGHT ) );
	}
	
	private static HeadingParagraph headingParagraph = new HeadingParagraph( "colour entries" );
	
	private static TextParagraph hintParagraph = new TextParagraph(
		TextParagraph.CENTERALIGNED,
	"\nYou may see the full list of what may be coloured by typing 'colour'" );
	
	private static TextParagraph noColoursParagraph = new TextParagraph(
		TextParagraph.CENTERALIGNED,
	"      You haven't coloured anything, at the moment.\n" +
	"\n" +
	"Typing 'colour' by itself will show you how to set up some colours." );
}
