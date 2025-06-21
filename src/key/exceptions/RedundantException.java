package key;


/**
  *  This exception is used when something is called that
  *  is simply not necessary - an add on a group that is
  *  already added, and maybe (in the future), remove on
  *  a atom that isn't there.  In fact, I'm not going
  *  to use this at all, atm
 */
public class RedundantException extends RuntimeException
{
	public RedundantException( String msg )
	{
		super( msg );
	}
}
