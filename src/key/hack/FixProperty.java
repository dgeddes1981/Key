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
*/

package key;

import key.util.Trie;
import java.io.IOException;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class FixProperty extends Command
{
	public FixProperty()
	{
		setKey( "fixprop" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		System.getProperties().put( "THREADED_SERVER_THREAD_CLASS", "key.web.PoolThread" );
	}
}
