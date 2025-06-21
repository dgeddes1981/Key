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

public class Yield extends Command
{
	public Yield()
	{
		setKey( "yield" );
		setProperty( "usage", "" );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Thread.yield();
	}
}
