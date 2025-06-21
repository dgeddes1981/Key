/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
**
**  $Id: InvalidSearchException.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  07Aug98     subtle       created
**
*/

package key;

public class InvalidSearchException
	extends RuntimeException
	implements UserOutputException
{
	public InvalidSearchException( String msg )
	{
		super( msg );
	}

	public void send( InteractiveConnection ic )
	{
		ic.sendFailure( getMessage() );
	}
}
