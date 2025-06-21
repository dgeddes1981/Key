/**
  * $Id: Timeouts.java,v 1.3 2000/06/26 16:14:35 subtle Exp $
  *
  * $Log: Timeouts.java,v $
  * Revision 1.3  2000/06/26 16:14:35  subtle
  * misc bug fixes for timeout process.
  *
 */

package key.events;

import key.*;
import key.primitive.*;

public class Timeouts extends Event
{
	public static final AtomicElement[] ELEMENTS =
	{
			//  boolean getReferencesOnly();
		AtomicElement.construct( Timeouts.class, Duration.class,
		      "timeBetweenScans",
			  AtomicElement.PUBLIC_FIELD,
			  "amount of time between scans" )
	};
	
		//  defaults to 1 day
	public Duration timeBetweenScans = new Duration( 1000*60*60*24 );
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Event.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Timeouts()
	{
	}
	
	public void run( Daemon scheduler )
	{
			//  pre-effect
		(new key.effect.Broadcast( Key.instance(), null, "The wind whispers through the trees, pushing around leaves and memories of old friends...", null )).cause();
		Log.debug( this, "timeouts called by scheduler" );
		
			//  do it
		Registry.instance.scanTimeouts();
		
			//  schedule another one
		schedule( DateTime.nowPlus( timeBetweenScans ) );
		Key.instance().getScheduler().add( this );
	}
}
