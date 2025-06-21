package key;

/**
  *  Kind of like an AccessViolation, but not so serious (it isn't
  *  logged, for instance).  It just means that you've tried to
  *  do something you can't, but something that isn't necessarily
  *  malicious.
 */
public class PermissionDeniedException
extends RuntimeException
implements UserOutputException
{
	Object on;
	
	public PermissionDeniedException( Object o, String msg )
	{
		super( msg );
		on = o;
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( "Permission denied while " + getMessage() );
	}
}
