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
**  $Id: CommandContainer.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
*/

package key;

public interface CommandContainer
{
	/**
	  *  A shortcut, since matching it from the
	  *  string could be prohibitively slow
	 */
	public CommandList getCommandList();
}
