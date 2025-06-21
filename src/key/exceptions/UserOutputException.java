package key;

/**
  *  Implemented by those exceptions that support pretty user output
 */
public interface UserOutputException
{
	public void send( InteractiveConnection ic );
}
