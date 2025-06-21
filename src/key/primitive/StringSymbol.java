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
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  17Jul97     merlin       created this datatype
**
*/

package key.primitive;

import key.Symbol;
import key.UnexpectedResult;

import java.io.*;
import java.util.Enumeration;

public final class StringSymbol implements Symbol,java.io.Serializable
{
	String key;
	
	public StringSymbol()
	{
		this( "" );
	}

	public StringSymbol( String theName )
	{
		setKey( theName );
	}
	
		//Symbol implementation	
	public final Object getKey()
	{
		return( getName() );
	}

	public final String getName()
	{
		return( key );
	}

		//symbol implementation
	public void setKey( Object newkey )
	{
		key = (String) newkey;
	}

	public final Object clone()
	{
		try
		{
			return( super.clone() );
		}
		catch( CloneNotSupportedException e )
		{
			throw new UnexpectedResult( e.toString() + " in reference::clone()" );
		}
	}
	
	public boolean equals( Object o )
	{
		try
		{
			return( key == ((StringSymbol)o).key );
		}
		catch( Exception e )
		{
			return( false );
		}
	}
}
