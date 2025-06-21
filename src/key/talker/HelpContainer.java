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
**  $Id: HelpContainer.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.collections.AliasCollection;

public class HelpContainer extends Container
{
	public HelpContainer()
	{
		super( false );
		contained = new AliasCollection();
		setConstraint( Type.SCREEN );
		setKey( "help" );
	}
}
