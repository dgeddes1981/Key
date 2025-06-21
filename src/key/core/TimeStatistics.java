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
**  19Jul97     subtle       added first connect ever
**
*/

package key;

import key.primitive.*;
import java.io.*;

/**
  * Ideas:
  *   - Number of connections atm
  *   - all time highest number of simultaneous connections
  *  (with registering connection code (who, etc))
  *   - average length of connection
  *   - maximum connection time ever
 */
public final class TimeStatistics extends Atom
{
	private static final long serialVersionUID = 2403704723594674612L;
	static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( TimeStatistics.class, Duration.class,
			"totalConnectionTime",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the total time spent connected" ),
		AtomicElement.construct( TimeStatistics.class, Duration.class,
			"timeSinceConnection",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the time since the most recent connection" ),
		AtomicElement.construct( TimeStatistics.class, Integer.TYPE,
			"currentConnections",
			AtomicElement.PUBLIC_FIELD | AtomicElement.READ_ONLY,
			"the number of connections open at the moment" ),
		AtomicElement.construct( TimeStatistics.class, DateTime.class,
			"lastConnection",
			AtomicElement.PUBLIC_FIELD,
			"the time of the most recent connection" ),
		AtomicElement.construct( TimeStatistics.class, DateTime.class,
			"lastDisconnection",
			AtomicElement.PUBLIC_FIELD,
			"the time of the most recent disconnection" ),
		AtomicElement.construct( TimeStatistics.class, DateTime.class,
			"firstConnection",
			AtomicElement.PUBLIC_FIELD,
			"the time of the first connection ever" ),
		AtomicElement.construct( TimeStatistics.class, Integer.TYPE,
			"highestNumberConnections",
			AtomicElement.PUBLIC_FIELD,
			"the maximum number of connections ever at one time" ),
		AtomicElement.construct( TimeStatistics.class, Integer.TYPE,
			"numberConnections",
			AtomicElement.PUBLIC_FIELD,
			"the total number of connections" )
	};
	
	static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	/**
	  *  Used for the players total connection
	  *  time.  Calculated by adding the different
	  *  between lastSaved and the current time
	  *  to the savedConnectTime if the player
	  *  is online, otherwise calculated by
	  *  using the savedConnectTime only.
	 */
	public Duration getTotalConnectionTime()
	{
		if( lastSaved != null )
			updateSavedConnectTime( new DateTime() );
		return( savedConnectTime );
	}

	/**
	  *  The time *since* the most recent connection
	 */
	public Duration getTimeSinceConnection()
	{
		if( lastConnection == null )
			return( null );
		else
			return( lastConnection.difference( new DateTime() ) );
	}
	
	public DateTime getLastConnection()
	{
		return( lastConnection );
	}

	public DateTime getLastDisconnection()
	{
		if( lastDisconnection != null )
			return( lastDisconnection );
		else
			return( lastConnection );
	}
	
	public double getParticipation()
	{
		double totalsec = getTotalConnectionTime().getTime();
		double possisec = firstConnection.difference(new DateTime()).getTime();
		return( totalsec / possisec * 100 );
	}
	
	/**
	  *  The time of the most recent connection
	 */
	DateTime lastConnection;
	
	DateTime lastDisconnection;
	
	/**
	  *  The time of the first connection
	 */
	DateTime firstConnection;

	/**
	  *  The total connection time when the
	  *  player was last saved
	 */
	Duration savedConnectTime = new Duration();

	/**
	  *  The time when the 'savedConnectTime' was
	  *  last updated from the currentTime
	 */
	transient DateTime lastSaved;
	
	/**
	  *  The number of connections at the moment.
	  *
	  *  Every second, the number here is the amount
	  *  that the totalConnectTime goes up by.  Every
	  *  time startConnection() is called, it is increased
	  *  by one, and it is decreased by one when
	  *  endConnection() is called.
	 */
	transient int currentConnections = 0;
	
	/**
	  *  The total number of connections ever.
	 */
	int numberConnections = 0;
	
	/**
	  *  The maximum number of simultaneous connections ever
	 */
	int highestNumberConnections = 0;
	
	public TimeStatistics()
	{
		lastSaved = null;
	}
	
	protected AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	void reset()
	{
		lastConnection = null;
		lastDisconnection = null;
		firstConnection = null;
		savedConnectTime = new Duration();
		lastSaved = null;
		currentConnections = 0;
		numberConnections = 0;
		highestNumberConnections = 0;
	}
	
	private void updateSavedConnectTime( DateTime current )
	{
		if( lastSaved != null )
		{
			savedConnectTime = savedConnectTime.addMultiple( lastSaved.difference( current ), currentConnections );
			lastSaved = current;
		}
	}

	void startConnection()
	{
		DateTime current = new DateTime();
		
		if( lastSaved != null )
			updateSavedConnectTime( current );
		else
			lastSaved = current;
		
		if( firstConnection == null )
			firstConnection = current;
		
			//  new connection
		numberConnections++;
		currentConnections++;
		
		if( currentConnections > highestNumberConnections )
			highestNumberConnections = currentConnections;
		
		lastConnection = current;
	}
	
	void endConnection()
	{
		DateTime current = new DateTime();
		updateSavedConnectTime( current );
		
		if( currentConnections <= 1 )
			lastSaved = null;
		
		currentConnections--;
		lastDisconnection = current;
	}
}
