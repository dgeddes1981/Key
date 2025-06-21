package key;


/**
 */
public class InvalidArgumentException
extends RuntimeException
implements UserOutputException
{
	public InvalidArgumentException( String msg )
	{
		super( msg );
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( getMessage() );
	}
}
