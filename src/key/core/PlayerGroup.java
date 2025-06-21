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
**  $Id: PlayerGroup.java,v 1.1 1999/10/11 13:25:06 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  30Sep98     subtle       created
**
*/

package key;

import key.collections.Collection;

import java.util.Enumeration;

public interface PlayerGroup extends Splashable
{
	public void linkPlayer( Player p ) throws NonUniqueKeyException,BadKeyException;
	public void unlinkPlayer( Player p ) throws NonUniqueKeyException,java.util.NoSuchElementException,BadKeyException;
	public Object getPlayer( Player p, String match );
	public Object getPlayer( String match );
	public Enumeration players();
	public boolean containsPlayer( Player p );
	public String allNames();
	public String allNames( Player p );
	public int getNumberPlayers();
	public String getAllNames();
	public int numberPlayers();
	public void splashExcept( Effect t, Splashable except, SuppressionList sl );
	public void splashExcept( Effect t, Splashable[] except, SuppressionList sl );
	public void setCollection( Collection c );
}
