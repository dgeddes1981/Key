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
**  $Id: Repeated.java,v 1.1 1999/10/11 13:25:07 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

/**
  *  Just a simple store class to hold the information needed
  *  by the 'repeat' command.
 */
public class Repeated
{
	protected String message;
	protected Repeatable command;

	public Repeated()
	{
		message = null;
		command = null;
	}

	public void set( String m, Repeatable c )
	{
		message = m;
		command = c;
	}

	public String getMessage()
	{
		return( message );
	}

	public Repeatable getCommand()
	{
		return( command );
	}
}
