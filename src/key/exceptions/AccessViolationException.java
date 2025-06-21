package key;

public class AccessViolationException
extends RuntimeException
implements UserOutputException
{
	Object on;
	
	public AccessViolationException( Object o, String msg )
	{
		super( msg );
		on = o;
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( "Access violation: " + getMessage() );
		
				//  the logging is done here, since if
				//  this is called we know that
				//  the exception wasn't caught.  This is
				//  slightly faulty logic (what if the player
				//  isn't online, so this method is never
				//  called, but it's not incredibly important)
			//  determine who's fault it is
		Player p = Player.getCurrent();
		if( p != null )
			Log.log( "security", p.getName() + ":" + getMessage() + "  (from: " + on.toString() + ":" + on.getClass().getName() + ")" );
		else
			Log.log( "security", "(system process):" + getMessage() + "  (from: " + on.toString() + ":" + on.getClass().getName() + ")" );
		
		printStackTrace();
	}
}
