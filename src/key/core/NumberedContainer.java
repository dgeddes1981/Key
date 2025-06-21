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
**  $Id: NumberedContainer.java,v 1.1 1999/10/11 13:25:06 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.collections.NumberedCollection;

import java.util.Enumeration;
import java.io.*;

/**
 */
public class NumberedContainer extends Container
{
	public NumberedContainer()
	{
		super( false, "", new NumberedCollection() );
	}
}
