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
**  $Id: KeyOutputStream.java,v 1.1.1.1 1999/10/07 19:58:37 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  27Aug98     subtle       created
**
*/

package key.io;

import key.Atom;

import java.io.*;
import java.lang.reflect.*;

/**
  *  This class is capable of accurately resolving objects that
  *  need to be referred to statically.  In particular, the
  *  Type and Action classes are dealt with in this manner.
 */
public final class KeyOutputStream extends java.io.ObjectOutputStream
{
	protected boolean willSwapImplicit = false;
	protected boolean swappingNow = false;
	
	public KeyOutputStream( OutputStream out )
		throws IOException
	{
		super( out );
		
		enableReplaceObject( true );
	}
	
	public void doSwapping()
	{
		willSwapImplicit = true;
	}
	
	protected Object replaceObject( Object o )
	{
		if( o instanceof Atom )
		{
			if( willSwapImplicit )
			{
				if( !swappingNow )
				{
					swappingNow = true;
					//System.out.println( "writing basic #" + ((Atom)o).index + ": " + o.toString() );
				}
				else
				{
						//  we have to not write atoms to this stream, other
						//  than the first one (hence the 'swappingNow' boolean).
						//
						//  this is because those atoms are direct references
						//  from this one, and each atom must be stored in a
						//  different file.
					StoredImplicitReference sir = new StoredImplicitReference( (Atom) o );
					
					//System.out.println( "writing implicitref for " + o );
					
					return( sir );
				}
			}
			//else
				//System.out.println( "writing #" + ((Atom)o).index + ": " + o.toString() );
		}
		
		return( o );
	}
}
