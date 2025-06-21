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

package key.io;

import java.io.*;
import java.lang.reflect.*;

/**
  *  This class is capable of accurately resolving objects that
  *  need to be referred to statically.  In particular, the
  *  Type and Action classes are dealt with in this manner.
 */
public final class KeyInputStream extends java.io.ObjectInputStream
{
	public KeyInputStream( InputStream in )
		throws IOException, StreamCorruptedException
	{
		super( in );
		
		enableResolveObject( true );
	}
	
	protected Object resolveObject( Object o )
	{
		/*
		if( o instanceof key.Atom )
			System.out.println( "---FINISHED atom: " + o.toString() );
		else if( o instanceof key.Reference )
			System.out.println( "  reference: " + o.toString() );
		else if( o instanceof key.collections.Collection )
			System.out.println( "  collection: " + o.getClass().getName() );
		*/
		
		if( o instanceof key.Atom )
			((key.Atom)o).loaded();
		
			//  we do the atom load first, since if an atom is
			//  returned from the getReplacement it has already been
			//  loaded through this routine.
		if( o instanceof Replaceable && !( o instanceof key.Reference ) )
			return( ((Replaceable)o).getReplacement() );
		
		return( o );
	}
}
