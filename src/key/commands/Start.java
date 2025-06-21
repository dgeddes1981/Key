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

package key.commands;

import key.*;
import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.reflect.*;

/**
 */
public class Start extends Command
{
	public Start()
	{
		setKey( "start" );
		usage = "<class> <arguments>";
	}

	static Class[] mainArgTypes = new Class[]
	{
		((new String[0]).getClass())
	};
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !p.isBeyond() )
		{
			throw new AccessViolationException( this, "Start not permitted without beyond" );
		}
		
		String cname = nextArgument( args, ic );
		Vector params = new Vector( 20, 20 );
		Class fn = null;
		
		try
		{
			fn = Class.forName( cname );
		}
		catch( Exception e )
		{
			ic.printStackTrace( e );
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
			
			new SubThread( main, a, p ).start();
			
			main = null;
		}
		catch( Exception e )
		{
			ic.printStackTrace( e );
			return;
		}

		fn = null;
	}

	class SubThread extends Thread
	{
		Method meth;
		Object[] o;
		Reference player;
		
		public SubThread( Method m, Object[] args, Player p )
		{
			meth = m;
			o = args;
			player = p.getThis();
		}

		public void run()
		{
			try
			{
				meth.invoke( null, o );
			}
			catch( Throwable t )
			{
				try
				{
					Player p = (Player) player.get();
					
					if( p != null && p.connected() )
						p.getConnection().printStackTrace( t );
					else
						Log.debug( this, "offline player when exception: " + t.toString() );
				}
				catch( OutOfDateReferenceException e )
				{
						//  this player no longer exists
					Log.error( "from player: " + player.toString(), t );
				}
				
				if( t instanceof RuntimeException )
				{
					throw ((RuntimeException)t);
				}
			}
		}
	}
}
