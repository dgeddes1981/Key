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

import key.events.*;
import key.util.LinkedList;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;
import java.util.Enumeration;
import java.util.StringTokenizer;

public final class Scheduler extends Daemon
{
	private static final long serialVersionUID = 2876463129193477587L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Scheduler.class, Paragraph.class, "entries",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"the entries in the scheduler" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Daemon.STRUCTURE, ELEMENTS );
	
	LinkedList list;
	
	public Scheduler()
	{
		super( false );
		
		setKey( "scheduler" );
		list = new LinkedList();
	}
	
	public void remove( Event evt )
	{
		list.remove( evt.getThis() );
	}
	
	public void add( Event evt )
	{
		LinkedList.Iterator lli = list.iterator();
		
		lli.first();
		
		while( lli.isValid() )
		{
			Event storedEvt = (Event) ((Reference)lli.element()).get();
			
			if( evt.isBefore( storedEvt ) )
			{
				lli.insertBefore( evt.getThis() );
				return;
			}
			
			lli.next();
		}
		
		list.append( evt.getThis() );
	}
	
	public static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "event", 43 ),
		new TableParagraph.Column( "at", 27 )
	};
	
	public Paragraph getEntries()
	{
		TableParagraph.Generator tp = new TableParagraph.Generator( columns );
		
		for( Enumeration e = list.elements(); e.hasMoreElements(); )
		{
			String[] rowContents = new String[ 2 ];
			
			Event o = (Event) ((Reference)e.nextElement()).get();
			
			rowContents[0] = o.getId();
			rowContents[1] = o.scheduledFor.toString();
			
			tp.appendRow( rowContents );
		}
		
		tp.setFooter( Integer.toString( list.count() ) + " entries" );
		
		return( tp.getParagraph() );
	}
	
	public void run()
	{
		while( true )
		{
			try
			{
				try
				{
					Thread.sleep( 10000 );  //  ten seconds
				}
				catch( InterruptedException e )
				{
				}
				
				Reference r = (Reference) list.getFirstElement();
				
				if( r != null )
				{
					try
					{
						Event evt = (Event) r.get();
						
						if( evt != null )
						{
							if( evt.runNow() )
							{
								list.remove( r );
								
								try
								{
									evt.run( this );
								}
								catch( Exception e )
								{
									Log.error( "in scheduler/run", e );
								}
							}
						}
					}
					catch( OutOfDateReferenceException e )
					{
						System.out.println( "SCHEDULER: removed out of date reference: " + r.toString() );
						list.remove( r );
					}
				}
			}
			catch( Exception e )
			{
				Log.error( "in scheduler", e );
			}
		}
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
}
