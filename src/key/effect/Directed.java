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
**  22Jun97     subtle       added primitive SuppressionList support, now
**                           also modifies directedAt scape if people are
**                           blocking.
**
*/

package key.effect;

import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

/**
  *  A communication effect that goes to an entire
  *  medium, such as a room or channel.  (Technically,
  *  it goes to any scape)
 */
public class Directed extends Communication
{
	Splashable directedAt;
	String selfMessage;
	String otherMessage;
	char targetCode;
	
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param to the player who's meant to receive the tell, or, alternatively
	  *           a _temporary_ scape (one generated for the explicit purpose
	  *           of sending a group of players).  The reason for the temporary
	  *           'ness' of it is that the players which are blocking will be
	  *           removed from this scape.  Consider this fair warning.
	  * @param main the message to show to everyone in the location scape
	  * @param self the UNPARSED message to show to the player - thats
	  *             what the tCode is for, what to replace the recipients name
	  *             with.
	 */
	public Directed( Player from, Splashable to, String self, String other, char tCode )
	{
		super( from );
		
		directedAt = to;
		otherMessage = other;
		selfMessage = self;
		targetCode = tCode;
	}

	public String getMessage( Player receiver )
	{
		if( directedAt instanceof PlayerGroup )
			originator.putCode( targetCode, ((PlayerGroup)directedAt).allNames( receiver ) );
		else
		{
			if( directedAt == receiver )
				originator.putCode( targetCode, "you" );
			else
				originator.putCode( targetCode, ((Player)directedAt).getName() );
		}
		
		if( originator == receiver )
			return( Grammar.substitute( selfMessage, originator.getCodes() ) );
		else
			return( Grammar.substitute( otherMessage, originator.getCodes() ) );
	}
	
		//  problem with this routine as it stands - there is no 'double'
		//  scan - if you send a tell and someone's blocking, the originator
		//  gets the error, but the people who still receive it, it looks like
		//  the tell went through to that other person...
		//
		//  modifying the scape is a good thing - it means that multi-tells
		//  will skip blocking individuals in the future.
	public void cause()
	{
		SuppressionList sl = new SuppressionList();
		PlayerGroup plg = null;
		
		directedAt.splash( this, sl );
		
		if( sl.count() > 0 )
		{
			boolean remove = false;
			boolean noblocked = false;
			Vector blockedNames = null;
			
			if( directedAt instanceof PlayerGroup )
			{
				remove = true;
				plg = (PlayerGroup) directedAt;
			}
			
			for( Enumeration e = sl.elements(); e.hasMoreElements(); )
			{
				SuppressionList.Entry sle = (SuppressionList.Entry) e.nextElement();
				boolean isActuallyBlocked;
				
				originator.sendFailure( sle.getText() );
				
				isActuallyBlocked = sle.isActuallyBlocked();
				
				if( !isActuallyBlocked )
					noblocked = true;
				
				if( remove && isActuallyBlocked )
				{
					Player bp = (Player) sle.getFrom();
					
					if( blockedNames == null )
						blockedNames = new Vector( 10, 10 );
					
					blockedNames.addElement( bp.getName() );
					
					try
					{
						plg.unlinkPlayer( bp );
					}
					catch( NonUniqueKeyException ex )
					{
						Log.debug( this, ex.toString() + " during a blocked directed suppresion" );
					}
					catch( BadKeyException ex )
					{
						Log.debug( this, ex.toString() + " during a blocked directed suppresion" );
					}
				}
			}
			
			if( remove || noblocked )
			{
				if( remove && plg.numberPlayers() == 0 )
					;
				else
					originator.splash( this, null );
				
				if( blockedNames != null )
				{
						//  now we should notify other people who received this
						//  event of the people who were blocking
						//  build the list of names:
					String nameList = Grammar.enumerate( blockedNames.elements() );
					blockedNames = null;
					
						//  notify them all individually:
					Effect eff;
					
					if( plg.getNumberPlayers() == 1 )
						eff = new key.effect.BlockNotification( originator, plg, "(" + nameList + " was blocking " + originator.getName() + " and has been removed from the reply list)" );
					else
						eff = new key.effect.BlockNotification( originator, plg, "(" + nameList + " were blocking " + originator.getName() + " and have been removed from the reply list)" );
					
					eff.cause();
				}
				
					//  generate reply lists here?
			}
			else
			{
				//  its just an individual, we've already output
				//  a message, do nothing
			}
		}
		else
			originator.splash( this, null );
		
			//  this will log all tells
		//Log.log( "tells", getMessage( null ) );
	}
	
	public void sending( String message, Player p )
	{
		p.addHistoryLine( message );
	}
}
