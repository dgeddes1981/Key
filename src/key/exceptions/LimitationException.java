package key;


/**
  *  This exception is used to specify that there is a
  *  defined limitation in the code that has been reached
  *  with this statement.  It could be because I haven't
  *  got around to coding that bit yet, or just because
  *  its too damn hard.
  *  <p>
  *  Don't expect to run into these often, I'm constructing
  *  this exception for its use referenceProperty - where
  *  a relative reference to an atom can't pass through a
  *  transit atom.
 */
public class LimitationException
extends RuntimeException
implements UserOutputException
{
	public LimitationException( String msg )
	{
		super( msg );
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( getMessage() );
	}
}
