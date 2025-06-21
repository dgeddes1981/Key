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
**  $Id: FileSystemOutputStream.java,v 1.1 2000/03/30 23:17:12 subtle Exp $
**
**
*/

package key.io;

import java.io.*;

import com.groovyj.filesystem.*;

/**
  *  This class is capable of accurately resolving objects that
  *  need to be referred to statically.  In particular, the
  *  Type and Action classes are dealt with in this manner.
 */
public final class FileSystemOutputStream extends java.io.OutputStream
{
	Filesystem.File file;
	
	public FileSystemOutputStream( Filesystem.File f )
		throws IOException
	{
		file = f;
	}
	
	public void close() throws IOException
	{
		file.truncate();
	}
	
	public void flush()
	{
	}
	
	public void write( byte[] b ) throws IOException
	{
		file.write( b );
	}
	
	public void write( byte[] b, int off, int len ) throws IOException
	{
		byte[] c = new byte[len];
		System.arraycopy( b, off, c, 0, len );
		file.write( c );
	}
	
	public void write( int b ) throws IOException
	{
		byte[] c = new byte[1];

		if( b < 0 )
			c[0] = (byte) (b + 256);
		else
			c[0] = (byte) b;
		file.write( c );
	}
}
