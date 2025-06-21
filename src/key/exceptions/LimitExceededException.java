package key;

/**
  *  Thrown when some arbitrary system resource limit has been exceeded.
  *  For instance, when a container has too many objects placed in it,
  *  when a string is too long, or when a (possibly) infinite loop exceeds
  *  it's quota of hops.
 */
public class LimitExceededException
extends RuntimeException
implements UserOutputException
{
	public LimitExceededException( String msg )
	{
		super( msg );
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( "Limit Exceeded: " + getMessage() );
	}
}
