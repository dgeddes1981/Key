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
**  $Id: Status.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import key.util.LinkedList;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Status extends Command
{
	public Status()
	{
		setKey( "status" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Key key = Key.instance();
		Runtime rt = Runtime.getRuntime();
		
		ic.send( new java.util.Date().toString() );
		ic.blankLine();
		ic.send( key.getName() + " has been running since "
			+ key.bootStats.getLastConnection() );
		
		ic.send( Long.toString( rt.totalMemory() ) + " bytes allocated, "
			+ Long.toString( rt.freeMemory() ) + " bytes of that remaining" );
		
		int po = key.numberPlayers();
		
		ic.send( "There " + Grammar.isAre( po ) + " " + po + " "
			+ Grammar.personPeople( po ) + " online" );
		
		int atoms, containers, scapes, players, lists;
		
		atoms = Atom.getTotalAtoms();
		containers = Container.getTotalContainers();
		atoms -= containers;

		scapes = Scape.getTotalScapes();
		containers -= scapes;

		players = Player.getTotalPlayers();
		containers -= players;

		lists = LinkedList.getTotalLists();
		
		ic.send( "Totals: " + atoms + " atoms, " + containers
			+ " containers, " + scapes + " scapes, " + players + " players, "
			+ lists + " lists" );

		ic.blankLine();
		ic.send( Registry.instance.statistics() );
	}
}
