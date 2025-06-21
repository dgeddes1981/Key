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
**  $Id: FileSystemInputStream.java,v 1.1 2000/03/30 23:17:12 subtle Exp $
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
public final class FileSystemInputStream extends java.io.InputStream
{
	Filesystem.File file;
	int markp;
	
	public FileSystemInputStream( Filesystem.File f )
		throws IOException
	{
		file = f;
	}
	
	public void close()
	{
	}
	
	public void flush()
	{
	}
	
	public int read( byte[] b ) throws IOException
	{
		byte[] c = file.read( b.length );
		System.arraycopy( c, 0, b, 0, b.length );
		return( c.length );
	}
	
	public int read( byte[] b, int off, int len ) throws IOException
	{
		byte[] c = file.read( len );
		System.arraycopy( c, 0, b, off, len );
		return( c.length );
	}
	
	public int read() throws IOException
	{
		byte[] c = file.read( 1 );
		
		if( c.length == 0 )
			return( -1 );
		
		return( c[0] >= 0 ? c[0] : 256 + c[0] );
	}
	
	public int available()
	{
		System.out.println( "available called()" );
		return( file.length() - file.getFilePointer() );
	}
	
	public boolean markSupported()
	{
		return( true );
	}
	
	public void mark( int readLimit )
	{
		markp = file.getFilePointer();
	}

	public void reset() throws IOException
	{
		file.seek( markp );
	}
}
