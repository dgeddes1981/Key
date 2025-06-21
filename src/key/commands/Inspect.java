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
**  $Id: Inspect.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  22Jun97    subtle       renamed from examine
**
*/

package key.commands;

import key.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
  *  This command outputs the game parameters for an object.  If no
  *  argument is specified, it outputs the game parameters for the
  *  players current room, otherwise it outputs the parameters for
  *  the object inside the current room that the argument refers to.
 */
public class Inspect extends Verb
{
	public Inspect()
	{
		setKey( "inspect" );
		usage = "[<object>]";
		verb = "inspect";
		method = null;
	}
	
	/**
	  *  This gets called if 'inspect' is executed by default - 
	  *  it should examine the current room.
	 */
	protected void handleNoArgs( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Room r = p.getLocation();
		Atom c = p.getContext();
		boolean same = true;
		
		if( c instanceof Room )
		{
			r = (Room) c;
			same = false;
		}
		
		if( r == null )
		{
			ic.sendError( "You don't seem to *be* anywhere" );
			return;
		}
		
		ic.sendLine();
		Material.anyAtomInspect( p, args, ic, flags, r );
		
		ic.send( r.countPlayers( r.numberPlayers(), same ) + "." );
		
		int count = r.count();
		ic.send( "There " + Grammar.isAreCount( count ) + " object" + ( (count==1) ? "" : "s" ) + " in this room." );
		
		ic.blankLine();
		
		ic.send( "The portrait reads:  Someone is [" + r.relation() + "] [" + r.portrait() + "]" );
		
		if( r.getProperty( "commands" ) != null )
			ic.send( "There are special abilities conferred while players are in this room.  Check your commands listing for details (type 'commands')." );
		
		ic.sendLine();
	}
	
	public String getVerb()
	{
		return( "inspect" );
	}
	
	public void setVerb( String v )
	{
		throw new InvalidArgumentException( "you may not set this property" );
	}
}
