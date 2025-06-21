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
**  $Id: Shortcuts.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.collections.*;

/**
  *  Only really subclasses so it can be differentiated
  *  with the qualification code
 */
public final class Shortcuts extends Container
{
	private static final long serialVersionUID = -7551109498802937777L;
	public Shortcuts()
	{
		super( true );
		
		contained = new ShortcutCollection();
		setKey( "shortcuts" );
	}
	
	void setRecursiveOwner_imp( Atom newOwner )
	{
		setOwner( newOwner );
		
			//  setting this recursively would be bad.  this is
			//  a security precaution.
	}
}
