/**
  * Page 
  *
  *
 */

package key.effect;

import key.*;

import java.util.Enumeration;

/**
  *  A communication effect that goes to an individual
  *  player with no message (preset message). 
 */
public class Page extends Communication
{
	Player directedAt;
	String feedbackMessage;
	String otherMessage;
	
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param to the player to page
	  * @param self the string tp tell the sender
	 */
	public Page( Player from, Player to, String feedback, String other )
	{
		super( from );
		
		directedAt = to;
		otherMessage = other;
		feedbackMessage = feedback;
	}

	public String getMessage( Player receiver )
	{
		if( receiver == directedAt )
			return( otherMessage );
		else
			throw new UnexpectedResult( "someone received a Page effect who is not the receiver" );
	}

	public void cause()
	{
		SuppressionList sl = new SuppressionList();
		
		directedAt.splash( this, sl );
		
		if( sl.count() > 0 )
		{
			for( Enumeration e = sl.elements(); e.hasMoreElements(); )
			{
				SuppressionList.Entry sle = (SuppressionList.Entry) e.nextElement();
				originator.sendFailure( sle.getText() );
			}
		}
		else
			originator.sendFeedback( feedbackMessage );
	}
	
	public void sending( String message, Player p )
	{
		p.beep();
	}
}
