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
**  $Id: Memo.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  12Aug98    subtle       start of history
**
*/

package key;

import java.util.StringTokenizer;
import java.io.*;

/**
  *  Some sort of note
 */
public class Memo extends Atom
{
	private static final long serialVersionUID = 4259738772195249580L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Memo.class, String.class, "value",
			AtomicElement.PUBLIC_FIELD,
			"the contents of this memo" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public String value = "";
	
	public Memo()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
}
