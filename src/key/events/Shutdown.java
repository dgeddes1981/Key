
package key.events;

import key.*;

public class Shutdown extends Event
{
	private static final long serialVersionUID = -5877248483942442278L;
	int remaining;
	
	public Shutdown()
	{
	}

	public void run( Daemon scheduler )
	{
			//  Total warning period of 30 seconds.
		remaining = 30;
		sleepAndWarn( 10 );		//  Warning, 30 seconds til shutdown
		sleepAndWarn( 10 );		//  Warning, 20 seconds til shutdown
		sleepAndWarn( 5 );		//  Warning, 10 seconds til shutdown
		sleepAndWarn( 5 );		//  Warning, 5 seconds til shutdown
		
		(new key.effect.Global( "system reset" )).cause();
		Log.debug( this, "Shutdown event called" );
		Key.instance().shutdown();
	}
	
	public void sleepAndWarn( int seconds )
	{
		(new key.effect.Global( "SHUTDOWN WARNING: " + remaining + " seconds" )).cause();
		
		try
		{
			Thread.sleep( seconds * 1000 );
		}
		catch( InterruptedException e )
		{
		}
		
		remaining -= seconds;
	}
}
