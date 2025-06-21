/**
  *  Command: Gc
  *
  *
 */

package key.commands;
import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Gc extends Command
{
	public Gc()
	{
		setKey( "gc" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Key key = Key.instance();
		Runtime rt = Runtime.getRuntime();
		LatentCache lc = key.getLatencyCache();
		
		if( !lc.isAlive() && key.isRunning() )
		{
			ic.send( "The latency cache has died.  No further processing." );
			return;
		}
		
		if( p.isBeyond() )
		{
			ic.send( "Compressing registry." );
			Registry.instance.compress();
			
			//ic.send( "Interupting latency cache." );
			//lc.interrupt();
			Thread.yield();
			
			ic.send( "Running garbage collection." );
			rt.gc();
			ic.send( "Running finalisation." );
			rt.runFinalization();
			
			try
			{
				Thread.sleep( 250 );
			}
			catch( Exception e )
			{
			}
		}
		
		ic.send( Long.toString( rt.totalMemory() ) + " bytes allocated, "
			+ Long.toString( rt.freeMemory() ) + " bytes of that remaining" );
		System.err.println( Long.toString( rt.totalMemory() ) + " bytes allocated, "
			+ Long.toString( rt.freeMemory() ) + " bytes of that remaining" );
	}
}
