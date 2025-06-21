package key;


/**
  *  This exception is used, at the moment, to say that
  *  the attempted action would cause a circular reference.
  *
  *  (For instance, if rank A implies rank B, and you try to make
  *  B imply A)
 */
public class CircularException extends RuntimeException
{
	public CircularException( String msg )
	{
		super( msg );
	}
}
