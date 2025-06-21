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
**  $Id: Blocking.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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

public class Blocking extends Command
{
	private static final long serialVersionUID = 6644375402988691405L;
	public Blocking()
	{
		setKey( "blocking" );
		
		usage = "";
	}
	
	public static final ColumnParagraph buildColumnParagraph( Player p )
	{
		QualifierList.Immutable ql = p.getImmutableQualifierList();
		
		ColumnParagraph.Generator cp = new ColumnParagraph.Generator( 30, 5 );
		
		for( Enumeration e = ql.elements(); e.hasMoreElements(); )
		{
			QualifierList.Entry.Immutable qle = (QualifierList.Entry.Immutable) e.nextElement();
			
			if( qle.get() == Qualifiers.SUPPRESSION_CODE )
			{
				Type t = qle.getTypeFor();
				
				if( t == Type.PLAYER )
					cp.appendEntry( "tells" );
				else
					cp.appendEntry( t.getName() );
			}
		}
		
		return( cp.getParagraph() );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		ColumnParagraph cp = buildColumnParagraph( p );
		int c = cp.count();
		
		ic.send( headingParagraph );
		
		if( c == 0 )
		{
			ic.send( noBlockingParagraph );
			ic.sendLine();
			return;
		}
		
		ic.send( cp );
		ic.send( new HeadingParagraph( c + " entr" + ((c==1) ? "y" : "ies"), HeadingParagraph.RIGHT ) );
	}
	
	private static HeadingParagraph headingParagraph = new HeadingParagraph( "blocking" );
	
	private static TextParagraph noBlockingParagraph = new TextParagraph(
		TextParagraph.CENTERALIGNED,
	"      You haven't blocked anything, at the moment.\n" +
	"\n" +
	"Look at the command 'block' to add things to this list." );
}
