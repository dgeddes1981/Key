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
*/

package key;

/**
  *  Thrown by key.talker.Command::nextArgument if
  *  a required parameter doesn't exist.
 */
public class NotEnoughParametersException
extends RuntimeException
implements UserOutputException
{
	public NotEnoughParametersException()
	{
	}
	
	public void send( InteractiveConnection ic )
	{
	}
}
