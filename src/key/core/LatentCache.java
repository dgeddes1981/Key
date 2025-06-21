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

import key.primitive.*;
import key.util.LinkedList;

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
  *  The LatentCache is the final memory cache that is outside the
  *  CachedAtom mechanism of locks.  It is used to 'transparently'
  *  cache things that can be easily rebuilt from the available
  *  information on demand.
  *
  *  This class is used, for instance, by a ShortcutCollection.  As a
  *  ShortcutCollection stores redundant information - all those Trie's
  *  and Hashtables are irrelevant and often unrequired, when it creates
  *  the Trie and Hashtable, it also add's itself to this latentcache.
  *  Every 5 minutes the latent cache checks back with the linkedtrie to
  *  see if the trie or hashtable has been used within those five minutes.
  *  If it has not been used at any time within those 5 minutes, the
  *  LatentlyCached atom is called to deallocate itself as it sees fit.
  *
  *  This class provides an efficient mechanism for this behaviour
 */
public final class LatentCache extends Daemon
{
	private static final long serialVersionUID = 887548235534240810L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( LatentCache.class, Duration.class, "sleepLength",
			AtomicElement.PUBLIC_ACCESSORS,
			"the delay between latency scans" ),
		AtomicElement.construct( LatentCache.class, Paragraph.class, "entries",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"the entries in the latency cache" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Daemon.STRUCTURE, ELEMENTS );
	
	transient LinkedList list;
	public Duration sleepLength;

	public Duration getSleepLength()
	{
		return( sleepLength );
	}
	
	public int countEntries()
	{
		return( list.count() );
	}
	
	public void setSleepLength( Duration d )
	{
		sleepLength = d;
			
			//  This is not compatible with the JDK1.2 for linux
		//interrupt();  //  this makes sure it comes into play quickly
	}
	
	public LatentCache()
	{
		super( false );
		
		list = new LinkedList();
		
			//  default to 5 minutes
		sleepLength = new Duration( 300000 );
		setKey( "latencyCache" );
	}

	public Paragraph getEntries()
	{
		TableParagraph.Generator tp = new TableParagraph.Generator( columns );
		
		for( Enumeration e = list.elements(); e.hasMoreElements(); )
		{
			String[] rowContents = new String[ 2 ];
			
			LatentlyCached o = (LatentlyCached) e.nextElement();

			if( o instanceof Atom )
				rowContents[0] = ((Atom)o).getId();
			else
				rowContents[0] = o.toString();
			
			rowContents[1] = o.modified() ? "true" : "false";
			
			tp.appendRow( rowContents );
		}
		
		tp.setFooter( Integer.toString( list.count() ) + " entries" );
		
		return( tp.getParagraph() );
	}
	
	public static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "id", 50 ),
		new TableParagraph.Column( "modified", 8 )
	};
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void loaded()
	{
		super.loaded();
		
		list = new LinkedList();
	}
	
		//  synchronizing these methods can cause deadlock
	public void removeFromCache( LatentlyCached lc )
	{
		list.remove( lc );
	}
	
		//  synchronizing these methods can cause deadlock
	public void addToCache( LatentlyCached lc )
	{
		list.append( lc );
	}
	
	public synchronized void stop()
	{
		super.stop();
		
		Log.debug( this, "stopping latentcache" );
		
		for( Enumeration e = list.elements(); e.hasMoreElements(); )
		{
			LatentlyCached lc = (LatentlyCached) e.nextElement();
			
			try
			{
				lc.deallocate();
				removeFromCache( lc );
			}
			catch( Exception except )
			{
					//  generally, it won't let you swap out a player
					//  that is still online - at this point in the
					//  shutdown, we don't particularly care - just
					//  ignore it.
				Log.debug( this, except.toString() );
				except.printStackTrace( System.out );
			}
		}
	}
	
	public synchronized void deallocateNow( LatentlyCached lc )
	{
		lc.resetModify();
		
		if( !lc.modified() )
		{
			removeFromCache( lc );
			
			try
			{
				lc.deallocate();
			}
			catch( Exception except )
			{
				Log.error( "during latency cache deallocation, ignoring", except );
			}
		}
	}
	
	public void run()
	{
		setPriority( Thread.MAX_PRIORITY - 1 );
		
		while( true )
		{
			//System.out.println( "LC: begin loop " );
			synchronized( this )
			{
				try
				{
					for( Enumeration e = list.elements(); e.hasMoreElements(); )
					{
						LatentlyCached lc = (LatentlyCached) e.nextElement();
						
						//System.out.println( "LC: scanning " + lc.toString() );
						
						if( lc.modified() )
							lc.resetModify();
						else
						{
							//System.out.println( "LC: deallocating " + lc.toString() );
							
							try
							{
								lc.deallocate();
							}
							catch( Exception except )
							{
								Log.debug( this, except.toString() + " during latency cache deallocation, ignoring..." );
								except.printStackTrace( System.out );
							}
							
							removeFromCache( lc );
						}
					}
				}
				catch( ThreadDeath e )
				{
					Log.debug( this, "Latency cache shutting down..." );
					throw e;
				}
				catch( Throwable e )
				{
					Log.debug( this, e.toString() + " in latency cache, ignoring..." );
					e.printStackTrace( System.out );
				}
			}
			
			//System.out.println( "LC: begin gc " );
			
			//Runtime rt = Runtime.getRuntime();
			
			//setPriority( Thread.NORM_PRIORITY );
			//Thread.yield();
			
			//rt.runFinalization();
			//rt.gc();
			
			//System.out.println( "LC: begin sleep " );
			
			try
			{
				//setPriority( Thread.MAX_PRIORITY - 1 );
				Thread.sleep( sleepLength.getTime() );  //  five minutes
			}
			catch( InterruptedException e )
			{
			}
		}
	}
}
