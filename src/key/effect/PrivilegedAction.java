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
**  29Jul97     subtle       checked if splash scape is null
**
*/

package key.effect;

import key.*;

import java.util.Enumeration;

/**
  *  A communication effect that goes to an individual
  *  player with no message (preset message). 
 */
public class PrivilegedAction extends Communication
{
	protected PlayerGroup scap;

	Player directedAt;
	Player issued;
	String feedbackMessage;
	String otherMessage;
	String splashbackMessage;
	Splashable dont[] = new Splashable[2];
	
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param to the player to warn
	  * @param self the string tp tell the sender
	 */
	public PrivilegedAction( Player from, Player to, String feedback, String message, String splashback, PlayerGroup splashTo )
	{
		super( from );
		
		issued = from;
		directedAt = to;
		otherMessage = message;
		splashbackMessage = splashback;
		feedbackMessage = feedback;
		scap = splashTo;
	}

	public String getMessage( Player receiver )
	{
		if( receiver == directedAt )
			return( otherMessage );
		else
			return( splashbackMessage );
	}
	
	public void cause()
	{
		directedAt.splash( this, null );
		System.out.println( "privil. splashed to " + directedAt.getName() );
		
		dont[0] = originator;
		dont[1] = directedAt;
		
		if( scap != null )
			scap.splashExcept( this, dont, null );
		
		originator.sendFeedback( feedbackMessage );
	}
}
