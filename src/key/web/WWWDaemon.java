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

package key.web;

import com.mortbay.Base.*;
import com.mortbay.Util.*;
import com.mortbay.Servlets.*;
import com.mortbay.HTML.*;
import com.mortbay.Jetty.*;
import com.mortbay.HTTP.*;
import com.mortbay.HTTP.Handler.*;
import com.mortbay.HTTP.Filter.*;
import com.mortbay.HTTP.Configure.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import java.util.*;

/**
  *  This class is a thread which opens and binds a port,
  *  creating new instances of 'InteractiveConnectionion' to
  *  deal with each connection request that comes in.
 */

public class WWWDaemon extends key.Daemon
{
	private static final long serialVersionUID=-1359963406925714554L;
	transient boolean running = false;
	
	public WWWDaemon()
	{
		setKey( "WWWDaemonStarter" );
	}
	
	public void run()
	{
		if( !running )
		{
			running = true;
			String[] args = new String[1];
			args[0] = "etc/keyweb.prp";
			//Code.setDebug( true );
			Server.main( args );
		}
	}
	
	public boolean isAlive()
	{
		return( running );
	}
	
	public void stop()
	{
		System.out.println( "stop() in WWWDaemon()" );
		Server.shutdown();
		System.out.println( " shutdown done in WWWDaemon()" );
		running = false;
		super.stop();
		System.out.println( " stopped all" );
	}
}
