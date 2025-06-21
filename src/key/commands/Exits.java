/**
  *  Command: Exits
  *
  *
 */

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Exits extends Command
{
	public Exits()
	{
		setKey( "exits" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.connected() )
		{
			if( p.located() )
			{
				Paragraph e = p.getLocation().exits();
				
				if( e != null )
					ic.send( e );
				else
					ic.sendFeedback( "There doesn't appear to be a way out." );
			}
			else
				ic.sendError( "You don't appear to be anywhere?" );
		}
	}
}
