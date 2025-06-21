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

import key.util.Trie;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

public interface Commandable
{
	public boolean isDisabled();
	public void disable();
	public void usage( InteractiveConnection ic );
	public void usage( InteractiveConnection ic, CategoryCommand caller );
	public String getWhichId();
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException;
	public String getUsage();
	public Commandable getMatch( final Player p, key.util.StringTokenizer st );
	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st );
	public boolean recloneArgs();
}
