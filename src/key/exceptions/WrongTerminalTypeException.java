package key;

public class WrongTerminalTypeException
extends RuntimeException
implements UserOutputException
{
	public WrongTerminalTypeException()
	{
		super();
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( "You are connected through the wrong type of terminal for this operation." );
	}
}
