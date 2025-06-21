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
**  $Id: ReferenceCommandList.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.collections.ShortcutCollection;

/**
  *  Intended to be used to have 'optional' commands added to it
  *  from a static command base (such as for clans).  This class
  *  is essentially a commandlist which holds references, instead
  *  of actual commands.
 */
public class ReferenceCommandList extends Container
{
	public ReferenceCommandList()
	{
		super( true );
		
		contained = new ShortcutCollection();
		setConstraint( Type.COMMAND );
	}
}
