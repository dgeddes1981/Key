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

package key;

import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.reflect.*;

/**
 */
public class StartDaemon extends Daemon
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( StartDaemon.class, String.class, "commandLine",
			AtomicElement.PUBLIC_FIELD,
			"the java class to run" )
	};
	
	String commandLine = "";
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Daemon.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public StartDaemon()
	{
	}
	
	public void argument( String args )
	{
		if( args != null && args.length() > 0 )
		{
			commandLine = args;
		}
		else
			throw new IllegalArgumentException( "You must specify the java class to start" );
	}

	static Class[] mainArgTypes = new Class[]
	{
		((new String[0]).getClass())
	};
	
	public void run()
	{
		StringTokenizer args = new StringTokenizer( commandLine );
		
		if( !args.hasMoreTokens() )
		{
			System.err.println( "No arguments to StartDaemon" );
			return;
		}
		
		String cname = args.nextToken();
		Vector params = new Vector( 20, 20 );
		Class fn = null;
		
		try
		{
			fn = Class.forName( cname );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return;
		}
		
		while( args.hasMoreTokens() )
			params.addElement( args.nextToken() );
		
		String[] sub_args = new String[ params.size() ];
		params.copyInto( sub_args );
		
		try
		{
			Method main = fn.getMethod( "main", mainArgTypes );
			Object[] a = new Object[1];
			a[0] = sub_args;
			
			try
			{
				main.invoke( null, a );
			}
			catch( Exception t )
			{
				Log.error( t );
				
				if( t instanceof RuntimeException )
				{
					throw ((RuntimeException)t);
				}
			}
			
			main = null;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return;
		}
		
		fn = null;
	}
}
