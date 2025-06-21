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

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class ListInventory extends Command
{
	public ListInventory()
	{
		setKey( "i" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Inventory cf = (Inventory) p.getInventory();
		int numElements = cf.count();
		
		if( numElements == 0 )
			ic.sendFeedback( "You have nothing in your inventory." );
		else
			ic.send( cf.contents( p ) );
	}
}
