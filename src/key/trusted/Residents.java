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
**  $Id: Residents.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.io.*;

public class Residents extends Container
{
	private static final long serialVersionUID = -4112134996692786486L;
	public Residents()
	{
		super( false );
		
		contained = new ShortcutCollection();
	}
	
	void setRecursiveOwner_imp( Atom newOwner )
	{
		setOwner( newOwner );
		
			//  setting this recursively would be bad.  this is
			//  a security precaution.
	}
}
