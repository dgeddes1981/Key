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
**  $Id: AtomicSpecial.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  06Nov98    subtle       created
**
*/

package key;

import java.lang.reflect.*;

/**
  *  All these classes need to remain immutable after their construction for
  *  security reasons (they can be retrieved by public methods).
 */
public abstract class AtomicSpecial
{
	/**
	  *  This method is called to determine if this special may
	  *  be used with the supplied atomic element.  It is generally
	  *  used to ensure correct typing.
	  *
	  * @return true iff this special is valid with the specific atomic element
	 */
	public abstract boolean canUseWith( AtomicElement ae );
	
	/**
	  *  This method is used to determine if the new value may be
	  *  assigned to this property.  The method should throw
	  *  a suitable exception if the new value is not acceptable,
	  *  or return the value (or a replacement) if it is.
	  *
	  * @param newValue the new value (may be null)
	 */
	public abstract Object validateNewValue( Object newValue );
	
	/**
	  *  Returns a 'special' object suitable for passing to construct that
	  *  limits the length of text paragraphs that are put into this element.
	  * <BR>
	  *  This is a unique and custom form of access control to this particular
	  *  property that is only enforced when a set() is issued.
	  *
	  * @param byteLimit the maximum number of bytes that may be in the buffer
	  * @param lineLimit the maximum number of lines that may be in the buffer
	 */
	public static AtomicSpecial TextParagraphLengthLimit( int byteLimit, int lineLimit )
	{
		return( new TextParagraphLengthWrapper( byteLimit, lineLimit ) );
	}
	
	/**
	  *  Returns a 'special' object suitable for passing to construct that
	  *  limits the length of strings that are put into this element.
	  * <BR>
	  *  This is a unique and custom form of access control to this particular
	  *  property that is only enforced when a set() is issued.
	  *
	  * @param byteLimit the maximum length of the string
	  * @param mayHaveLineBreaks true iff this string may contain CR's
	  * @param visible use the visible length of the string?
	 */
	public static AtomicSpecial StringLengthLimit( int byteLimit, boolean mayHaveLineBreaks, boolean visible )
	{
		return( new StringLengthWrapper( byteLimit, mayHaveLineBreaks, visible ) );
	}
}
