/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key;

import key.collections.*;
import key.primitive.StringSymbol;

import java.io.*;
import java.util.Enumeration;

/**
 */
public class PatternStringSet extends StringSet
{
	public PatternStringSet()
	{
		strings = new PatternStringKeyCollection();
	}
	
	public PatternStringSet( Object key )
	{
		super( key );
		strings = new PatternStringKeyCollection();
	}
}
