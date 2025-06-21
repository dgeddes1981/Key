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
**  $Id: TextParagraphLengthWrapper.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  06Nov98    subtle       created
**
*/

package key;

public final class TextParagraphLengthWrapper extends AtomicSpecial
{
	int byteLimit;
	int lineLimit;
	
	TextParagraphLengthWrapper( int bl, int ll )
	{
		byteLimit = bl;
		lineLimit = ll;
	}

	public int getByteLimit()
	{
		return( byteLimit );
	}

	public int getLineLimit()
	{
		return( lineLimit );
	}
	
	public boolean canUseWith( AtomicElement ae )
	{
		return( ae.getClassOf() == TextParagraph.class );
	}
	
	public Object validateNewValue( Object newValue )
	{
		if( newValue == null )
			return null;
		
		TextParagraph tp = (TextParagraph) newValue;
		
			//  check the byte count
		String s = tp.getText();
		if( s.length() > byteLimit )
			throw new LimitExceededException( "too many characters: " + s.length() + " > " + byteLimit + "." );
		
		int ct = tp.numberOfNewLines();
		
		if( ct > lineLimit )
			throw new LimitExceededException( "too many lines: " + ct + " > " + lineLimit + "." );
		
		return( newValue );
	}
}

