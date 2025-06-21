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

package key.util;

import java.io.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CircularBuffer
	implements java.io.Serializable
{
	private Object[] underlying;
	private int maximum;
	private int start;
	private int end;
	
	public CircularBuffer( int maxElements )
	{
		if( maxElements < 2 )
			throw new IllegalArgumentException( "Less than 2 elements in a circular buffer?" );
		
		maximum = maxElements + 1;
		start = maximum-1;
		end = 0;
		underlying = new Object[ maximum ];
	}
	
	public void addElement( Object o )
	{
		underlying[ end ] = o;
		
		end++;
		
		if( end > start )
			start = (start+1) % maximum;
		
		end %= maximum;
	}
	
	public Enumeration elements()
	{
		return( new Enumeration()
		{
			int upto = (start+1) % maximum;
			
			public boolean hasMoreElements()
			{
				return( upto != end );
			}
			
			public Object nextElement()
			{
				if( upto == end )
					throw new NoSuchElementException();
				
				Object o = underlying[ upto ];
				
				upto = (upto + 1) % maximum;
				
				return( o );
			}
		} );
	}
	
	/*
	public static void main( String args[] )
	{
		CircularBuffer cb = new CircularBuffer( 5 );
		
		DataInputStream dis = new DataInputStream( System.in );
		
		try
		{
			while( true )
			{
				System.out.print( "New value: " );
				String s = dis.readLine();
				System.out.println( "Adding '" + s + "'" );
				
				cb.addElement( s );
				
				System.out.println( "------------" );
				int i = 0;
				for( Enumeration e = cb.elements(); e.hasMoreElements(); )
					System.out.println( "  " + i++ + " " + ((String) e.nextElement()) );
				System.out.println( "------------" );
				System.out.println( "" );
				
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	*/
}
