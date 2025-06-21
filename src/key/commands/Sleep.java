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
**  $Id: Sleep.java,v 1.1 2000/02/23 15:07:29 noble Exp $
**
**  $Log: Sleep.java,v $
**  Revision 1.1  2000/02/23 15:07:29  noble
**  checkin
**
**
*/

package key.commands;

import key.*;
import key.primitive.Duration;
import java.io.IOException;
import java.util.StringTokenizer;

public class Sleep extends Command
{
	public Sleep()
	{
		setKey( "sleep" );
		usage = "<time>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{

			String t = args.nextToken();
			long time = 0;

			try {
				time = Long.parseLong( t );
				
				time = Math.abs( time ) * 1000;  // time in seconds
			}
			catch( NumberFormatException nfe )
			{
				
				try
				{
					time = Math.abs( Duration.parse( t ) );
					
				}
				catch( NumberFormatException n )
				{
					ic.sendError( "'" + t + "' is not a valid time." );
					return;
				}
				
			}

			try
			{
				Thread.sleep( time );
			}
			catch( InterruptedException e )
			{
			}

		}
		else
			usage( ic );
	}
}
