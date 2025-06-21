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

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;

public abstract class CommandCategory extends Command implements CategoryCommand
{
	public void runCommand( Commandable c, Player p, StringTokenizer args, String fullLine, InteractiveConnection ic, Flags flags ) throws IOException
	{
		c.run( p, args, fullLine, this, ic, flags );
	}

	/**
	  * should put the player into this mode
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
			//  this will only be called if none of the sub-commands
			//  were validated.
		
		fullLine = fullLine.trim().toLowerCase();
		
		StringTokenizer st = new StringTokenizer( fullLine );
		Vector v = new Vector( 25, 50 );
		
		for( Enumeration e = p.getCategoryCommandsMatching( st ); e.hasMoreElements(); )
		{
			Object[] o = (Object[]) e.nextElement();
			
			CommandList cl;
			
			if( o[0] != null )
				cl = ((CommandContainer) o[0]).getCommandList();
			else
				cl = (CommandList) o[1];
				
			for( Enumeration f = cl.elements(); f.hasMoreElements(); )
			{
				String s = ((Command)f.nextElement()).getName();
				
				if( !v.contains( s ) )
					v.addElement( s );
			}
		}
		
		if( v.size() > 0 )
		{
			ic.send( "'" + fullLine +
				"' is not a command, it is a category that contains other commands.  For instance, you might type '" +
				fullLine + " " + ((String) v.elementAt( 0 )) +
				"'.  The full list of available commands is: ^h" +
				Grammar.commaSeperate( v.elements() ) + "^-" );
		}
		else
			ic.send( "There are no available sub-commands in this category." );
	}
	
	public String getPrompt( Player p )
	{
		return( getName() + ": " );
	}
	
	public String getEnd()
	{
		return( "end" );
	}
}
