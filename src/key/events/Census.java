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
**  $Id: Census.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  27Aug98     subtle       start of recorded history
**
*/

package key.events;

import key.*;
import java.util.Enumeration;
import java.io.*;
import java.util.StringTokenizer;

public class Census extends Event
{
	public static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( Census.class, Reference.class, "container",
			AtomicElement.PUBLIC_FIELD,
			"the container to scan in" ),
		AtomicElement.construct( Census.class, String.class, "scan",
			AtomicElement.PUBLIC_FIELD,
			"the scan parameters" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Event.STRUCTURE, ELEMENTS );
	
	Reference container = Reference.EMPTY;
	String scan = null;
	
	public Census()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Daemon scheduler )
	{
		if( container != null )
		{
			Container c = (Container) container.get();
			if( c != null )
			{
				String scanLine = scan;
				
				if( scanLine != null )
				{
					Log.debug( this, "scanning container " + c.getId() );
					
					Type type = null;	
					IntegerCensus integerCensusData = null;
					
					for( Enumeration e = c.elements(); e.hasMoreElements(); )
					{
						Atom a = (Atom) e.nextElement();
						
						Log.debug( this, "  scanning " + a.getName() );

						Object o = new Search( scanLine, Key.instance() ).result;
						if( type != null )
						{
							Type scannedType = Type.typeOf( o );
							if( scannedType != type )
							{
								Log.debug( this, " invalid " + scannedType.getName() + " type doesn't match previously scanned " + type.getName() + " type." );
								continue;
							}
						}
						else
							type = Type.typeOf( o );
						
						if( type == Type.INTEGER )
						{
							int v = ((Integer)o).intValue();
							if( integerCensusData == null )
							{
								integerCensusData = new IntegerCensus( v );
							}
							else
							{
								integerCensusData.sum += v;
								integerCensusData.count++;
								if( integerCensusData.largest < v )
									integerCensusData.largest = v;
								if( integerCensusData.smallest > v )
									integerCensusData.smallest = v;
							}
						}
						else
						{
							Log.debug( this, "Unsupported census data type '" + type.getName() + "'" );
							return;
						}

						Thread.yield();
					}
					
					Log.debug( this, "Census complete - type was '" + type.getName() +"'" );
					if( integerCensusData != null )
						Log.debug( this, integerCensusData.toString() );
				}
				else
					Log.debug( this, "scanLine null" );
			}
			else
				Log.debug( this, "container null" );
		}
		else
			Log.debug( this, "container null" );
	}
}

class IntegerCensus
{
	public int sum;
	public int count;
	public int largest;
	public int smallest;

	public IntegerCensus( int v )
	{
		sum = v;
		count = 1;
		largest = v;
		smallest = v;
	}

	public String toString()
	{
		return( count + " integers, adding to " + sum + ".  The values ranged between " + smallest + " and " + largest );
	}
}
