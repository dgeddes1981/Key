package key;

import key.primitive.*;

import java.io.*;

/**
  *  A statistics class that keeps idle time
 */
public final class ConnectionStatistics extends Atom
{
	private static final long serialVersionUID = 4197321215561299181L;
	
	public static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( ConnectionStatistics.class, DateTime.class, "lastUnIdle",
			AtomicElement.PUBLIC_FIELD,
			"the time the last command was executed" ),
		AtomicElement.construct( ConnectionStatistics.class, Duration.class, "idleTime",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the amount of time this connection has been idle" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	/**
	  *  The time the last command was executed
	 */
	public DateTime lastUnIdle;
	
	public ConnectionStatistics()
	{
		unIdle();
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void loaded()
	{
		super.loaded();
		unIdle();
	}
	
	/**
	  *  Unidles the connection to the current time
	 */
	public void unIdle()
	{
		lastUnIdle = new DateTime();
	}
	
	public final Duration getIdleTime()
	{
		return( lastUnIdle.difference( new DateTime() ) );
	}
	
	public final Duration getIdleTime( DateTime currentTime )
	{
		return( lastUnIdle.difference( currentTime ) );
	}
}
