package key;

import key.io.Replaceable;

/**
  *  Atm, leave a blank line
  *  maybe change to be as many blank lines as required?
 */
public final class BlankLineParagraph
extends Paragraph
implements Replaceable
{
	public static final BlankLineParagraph BLANKLINE = new BlankLineParagraph();
	
	private BlankLineParagraph()
	{
	}

	public String toString()
	{
		return( "Blank line paragraph" );
	}
	
	public Object getReplacement()
	{
		return( BLANKLINE );
	}
}
