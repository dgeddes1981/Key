package key;

/**
  *  Secret is a persistant cookie authentication class that
  *  is given out to describe permissions.  For instance,
  *  when a dual/delayed permission check needs to be performed,
  *  a new secret is constructed and passed to the opposite
  *  party.  The originating party remembers that this secret
  *  indicates that permission was granted for a particular
  *  operation and permits it.
 */
public final class Secret implements java.io.Serializable
{
	int secCode = hashCode();

	public Secret()
	{
	}

	public boolean equals( Object o )
	{
		if( o instanceof Secret )
			return( ((Secret)o).secCode == secCode );
		else
			return( false );
	}
}
