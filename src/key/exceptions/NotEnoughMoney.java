package key;

/**
  *  Kind of like an AccessViolation, but not so serious (it isn't
  *  logged, for instance).  It just means that you've tried to
  *  do something you can't, but something that isn't necessarily
  *  malicious.
 */
public class NotEnoughMoney
extends RuntimeException
implements UserOutputException
{
	int shrt;
	
	public NotEnoughMoney( int cost, int amt )
	{
		shrt = cost - amt;
	}
	
	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( "You can't afford that!  (You're " + shrt + " florins short)" );
	}
}
