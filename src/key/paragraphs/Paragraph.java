package key;

import java.util.StringTokenizer;
import java.io.File;

/**
  *  The base class for most things which are output.  Examples are the simple
  *  'TextParagraph', which is used for many room descriptions, and which
  *  can be set to be formatted in various ways, such as justification (left,
  *  right and center), as well as indenting (left, right, firstlineLeft, firstlineRight)
  * <p>
  *  Another example is 'LineParagraph', which is just a simple line across the
  *  screen.
  * <p>
  *  'MultiParagraph' is basically a paragraph which consists of others, ordered
  *  sequentially.  So you might have a line, then some text, then a trailing line,
  *  and wish to treat it all as a single paragraph.
  * <p>
  *  'TableParagraph' is, well, a table.  It is set up with rows and columns, and you
  *  can add new rows and columns, and the columns will be set to the correct width and
  *  laid out as best we can.  Spacing between columns has a pre-set minimum, but no
  *  maximum, if the columns are small enough.  You may specify what the table is to
  *  do if there isn't enough space in any particular column - whether you wish to
  *  truncate the entry, use more than one row for a single entry, or start removing
  *  the columns displayed based on their priority.  A table may also have a heading,
  *  and it will generally use a footer if its paged.
  * <p>
  *  A 'ListParagraph' is similar to HTML &ltul&gt tags.  It lays the text elements
  *  out in a nice pretty bulletin, (as best it can), for lists of points or elements.
  * <p>
  *  The priciple use of paragraphs will be in descriptions.  There will be extensions
  *  to the editor to support editting a paragraph, as opposed to just a string
  *  (as it does now).  These extensions will support users being able to center
  *  text in their rooms, put lines across, embedded colours (obviously), and embedded
  *  images.
  * <p>
  *  Another feature that paragraphs will require is the ability to output in html
  *  format, so that anything that is stored on the program in paragraph format can
  *  be easily converted to HTML.
 */
public abstract class Paragraph
	implements java.io.Serializable
{
	private static final long serialVersionUID = 1502728405608403164L;
	public Paragraph()
	{
	}

	public boolean isEmpty()
	{
		return( false );
	}

	public Paragraph substitute( String[] codes )
	{
		return( this );
	}
}
