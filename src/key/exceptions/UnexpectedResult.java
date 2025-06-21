package key;

public class UnexpectedResult extends RuntimeException
{
	public UnexpectedResult( String msg )
	{
		super( msg );
	}

	public UnexpectedResult( Throwable t )
	{
		this( t.toString() );
	}
}
