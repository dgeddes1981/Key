package key.util;

public class StringTokenizer extends java.util.StringTokenizer
implements Cloneable
{
	public StringTokenizer( String s )
	{
		super( s );
	}

	public StringTokenizer( String s, String d )
	{
		super( s, d );
	}

	public StringTokenizer( String s, String d, boolean b )
	{
		super( s, d, b );
	}
	
	public Object clone()
	{
		try
		{
			return( super.clone() );
		}
		catch( CloneNotSupportedException e )
		{
			throw new key.UnexpectedResult( e );
		}
	}
}
