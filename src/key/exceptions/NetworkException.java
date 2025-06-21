package key;

import java.io.IOException;

public class NetworkException extends RuntimeException
{
	public NetworkException( String msg )
	{
		super( msg );
	}
	
	public NetworkException( IOException t )
	{
		this( t.getMessage() );
	}
}
