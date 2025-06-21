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
**  $Id: Rescheck.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  30Jul97    subtle       created this class
**
*/

package key.commands;
import key.*;
import key.util.Trie;
import java.io.IOException;
import java.util.StringTokenizer;

public class Rescheck extends Command
{
	public Rescheck()
	{
		setKey( "rescheck" );
		usage = "[<partialname>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		Object o = null;
		String name = args.nextToken();
		
		if( name.length() < Player.MIN_NAME )
		{
			ic.sendError( "Please use at least " + Player.MIN_NAME + " characters" );
			return;
		}
		
		name = name.substring( 0, Player.MIN_NAME );
		o = Key.instance().getResidents().getTrieFor( name );
		
		if( o != null )
		{
			ic.send( "Results of rescheck for '" + name + "':" );
			
			if( o instanceof Trie )
				ic.send( ((Trie)o).contents() );
			else if( o instanceof Reference )
				ic.send( ((Reference)o).getName() );
		}
		else
			ic.send( "No names close to '" + name + "' found." );
	}
}

