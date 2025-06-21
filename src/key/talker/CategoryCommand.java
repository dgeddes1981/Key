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

public interface CategoryCommand extends CommandContainer
{
	public void runCommand( Commandable c, Player p, StringTokenizer args, String fullLine, InteractiveConnection ic, Flags flags ) throws IOException;
	public String getPrompt( Player p );
	public String getEnd();
	public Commandable getMatch( final Player p, key.util.StringTokenizer st );
	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st );
	public String getWhichId();
}
