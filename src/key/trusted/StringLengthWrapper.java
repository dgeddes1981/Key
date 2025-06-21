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
**  $Id: StringLengthWrapper.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  06Nov98    subtle       created
**
*/

package key;

/**
  *  Generally speaking, a string may not include newlines, and
  *  may not be more than 'byteLimit' characters long.
 */
public final class StringLengthWrapper extends AtomicSpecial
{
	int byteLimit;
	boolean canHaveLineBreaks;
	boolean useVisibleLength;
	
	/**
	  *  Constructs a new StringLengthWrapper.
	  *
	  * @param bl the byte limit on the string
	  * @param mayContainLineBreaks true iff this string may contain CR's
	  * @param visible true if this length is the visible length of the string,
	  *                as opposed to the actual.  A string's "visible" length
	  *                is it's length on the screen after colour codes are
	  *                taken into account.
	 */
	StringLengthWrapper( int bl, boolean mayContainLineBreaks, boolean visible )
	{
		byteLimit = bl;
		canHaveLineBreaks = mayContainLineBreaks;
		useVisibleLength = visible;
	}
	
	/**
	  * @return the byte limit for this string
	 */
	public int getByteLimit()
	{
		return( byteLimit );
	}
	
	public boolean canUseWith( AtomicElement ae )
	{
		return( ae.getClassOf() == String.class );
	}
	
	/**
	  *  Checks to ensure that this object is in compliance with
	  *  the rules.  In particular, the object must be a String,
	  *  it must have a length <= the byte limit, and it must not
	  *  contain Carriage Returns (unless mayContainLineBreaks) is
	  *  set.
	 */
	public Object validateNewValue( Object newValue )
	{
		if( newValue == null )
			return null;
		
		String tp = (String) newValue;
		int tpl;

		if( useVisibleLength )
		{
				//  because someone could just write thousands of ^H's,
				//  we additionally limit the length to the string length,
				//  times 3, + 3.  The rational is, for a string of length
				//  3 (such as abc), we might want to color each letter
				//  differently (such as ^ra^gb^bc), and we also wish to
				//  terminate the string gracefully (add a ^n).  An extra
				//  byte is permitted as a grace area for ^^, or similar.
			tpl = Math.min( TelnetIC.stringLength( tp ), (tp.length() * 3) + 3 );
		}
		else
			tpl = tp.length();
		
		if( tpl > byteLimit )
			throw new LimitExceededException( "too many characters: " + tpl + " > " + byteLimit + "." );

		if( !canHaveLineBreaks )
		{
			int ct = tp.indexOf( "\n" );
			
			if( ct != -1 )
				throw new LimitExceededException( "no line breaks in strings permitted." );
		}
		
		return( newValue );
	}
}
