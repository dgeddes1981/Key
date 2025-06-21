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
**  $Id: FriendsBroadcast.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle       created this class
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class FriendsBroadcast extends Broadcast
{
	public static final char genderCode = 'h';
	
	protected void placeCodes( Player p, String message )
	{
		super.placeCodes( p, message );
		
		p.putCode( genderCode, p.hisHer() );
	}
}
