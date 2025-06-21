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
**  $Id: SeperatedIdentifier.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.util;

/**
  * To reference the null property or the null atom, you need to just
  * specify a trailing '.' or '/', respectively.
 */
public final class SeperatedIdentifier
	implements java.io.Serializable
{
	public String id="";
	public String location="";
	public boolean property;
	
	public SeperatedIdentifier( String fullId ) throws IllegalArgumentException
	{
		int lastSlash = fullId.lastIndexOf( '/' );
		int lastAt = fullId.lastIndexOf( '@' );
		int lastPeriod = fullId.lastIndexOf( '.' );
		
		if( lastSlash >= 0 || lastPeriod >= 0 || lastAt >= 0 )
		{
			int a = Math.max( lastSlash, lastAt );
			a = Math.max( a, lastPeriod );
			
			int skip;
			
			if( a == lastAt )
				skip = 0;
			else
				skip = 1;
			
			if( a == lastPeriod )
				property = true;
			else
				property = false;
			
			if( a == -1 )
			{
				location = "";
				id = fullId;
			}
			else if( a < (fullId.length()-1) )
			{
				id = fullId.substring( a + skip );
				
				location = fullId.substring( 0, a );
			}
			else
			{
				location = fullId;
				id = "";
			}
		}
		else
		{
			location = "";
			id = fullId;
		}

		//Log.debug( this, "From: '" + fullId + "': Location '" + location + "' Id '" + id + "'" );
	}
}
