package key;

import key.io.Replaceable;

/**
  *  Another example is 'LineParagraph', which is just a simple line across the
  *  screen.
 */
public final class LineParagraph
extends Paragraph
implements Replaceable
{
	public static final LineParagraph LINE = new LineParagraph();
	
	private LineParagraph()
	{
	}

	public String toString()
	{
		return( "Line paragraph" );
	}

	public Object getReplacement()
	{
		return( LINE );
	}
}
