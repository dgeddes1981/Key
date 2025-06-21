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
**  17Jul97     druss       created this command
**  30Oct98     subtle      fixed a typo
**
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class KBLock extends Command
{
	public KBLock()
	{
		setKey( "kblock" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
			usage( ic );
		else
		{
			int attempts = 0;
			boolean correct = false;
			ic.sendFeedback( "Locking your keyboard..." );
			p.afk( true );
			while( (attempts < 3) && (correct != true) )
			{
				try
				{
					if( p.authenticate( ic ) == true )
						correct = true;
					attempts++;
				}
				catch( PasswordEntryCancelled e )
				{
				}
				
				if( (attempts < 3) && (correct != true) )
				{
					String output = "You have " + (3 - attempts) + " more chance";
					if( ( 3 - attempts ) == 1 )
						output = output + ". \n";
					else
						output = output + "s. \n";
					ic.sendFeedback( output );
				}
			}
			if( ( attempts == 3 ) && ( correct != true ) )
			{
				ic.sendFeedback( "Feel free to come back when you remember your password." );
				p.disconnect();
			}
			
			if( correct )
			{
				ic.sendFeedback( "Did you have a nice break?" );
				p.afk( false );
			}
		}
	}
}
