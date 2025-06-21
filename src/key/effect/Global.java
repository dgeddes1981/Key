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
**  25Jul97     snapper      created this effect
**  26Jul97     snapper      added a splash to a scape for privileged action
**                           called it privileged action
**
*/

package key.effect;

import key.*;

import java.util.Enumeration;

/**
  *  Global simply goes to everyone.
 */
public class Global extends Effect
{
	String message;
	
	/**
	 */
	public Global( String message )
	{
		super( null );
		
		this.message = message;
	}

	public String getMessage( Player receiver )
	{
		return( message );
	}

	public void cause()
	{
		Key.instance().splash( this, null );
	}
}
