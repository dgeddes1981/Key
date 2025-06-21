package key;

import key.primitive.EmailAddress;
import key.primitive.DateTime;

import java.io.*;

/**
   * We'd use Serializable, but you can't have static variables in inner classes
   * with Java atm (whose oversight was that, grrr).  So we couldn't change
   * the class and still load it.  Therefore, this would be a useless thing.  So, it
   * looks like we're stuck doing this the hard way
  */
public class PlayerSupplemental implements java.io.Serializable
{
	private static final long serialVersionUID = 817269616157612438L;
	
		//  used for checking for duplicates
	EmailAddress email;
	String emailAddress;
	Reference loginStats;
	long lastTouch;
	int timeoutFlags;
	
	public PlayerSupplemental()
	{
	}
		
	public EmailAddress getEmail()
	{
		return( email );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		if( email != null )
			emailAddress = email.get();
	}
	
	public Reference getLoginStats()
	{
		return( loginStats );
	}
		
	public PlayerSupplemental( Player base, EmailAddress email )
	{
		update(base, email);
	}
		
	/** 
	   * This method is called to update these values from the cached ones.
	  */
	public void update( Player base, EmailAddress ea )
	{
		email = ea;
		if( ea != null )
			emailAddress = ea.get();
		
		DateTime dt =  null;
		
		if( base.loginStats != null )
		{
			loginStats = Reference.to( base.loginStats, false );
			dt = base.loginStats.getLastDisconnection();
		}
		
		if( dt == null )
			lastTouch = System.currentTimeMillis();
		else
			lastTouch = dt.getTime();
	}

	public static final int TIMEOUT_CITIZEN = (1<<1);
	public static final int TIMEOUT_TWOHOUR = (1<<2);
	public static final int TIMEOUT_NOTIMEOUT = (1<<3);
	public static final int TIMEOUT_CLAN = (1<<4);
}
