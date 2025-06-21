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
**  08Jul97     subtle       created this command
**
*/

package key.commands;

import key.*;
import java.io.IOException;

import java.util.StringTokenizer;

public class Threads extends Command
{
	public Threads()
	{
		setKey( "threads" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup ptg;
		
		do
		{
			ptg = tg.getParent();
			
			if( ptg != null )
				tg = ptg;
		} while( ptg != null );
		
		Thread allThreads[] = new Thread[ 100 ];
		
		tg.enumerate( allThreads, true );

		for( int i = 0; i < 100; i++ )
		{
			Thread t = allThreads[i];
			if( t == null )
				break;
			
			ic.sendFeedback( "Thread " + t.toString() + ": alive = " + t.isAlive() + "   isDaemon = " + t.isDaemon() + "   name = " + t.getName() );
		}
	}
}
