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
**  $Id: ClanBroadcast.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class ClanBroadcast extends Broadcast
{
	public static final char clanPrefixCode = 'c';
	
	protected void placeCodes( Player p, String message )
	{
		super.placeCodes( p, message );
		
		try
		{
			p.putCode( clanPrefixCode, p.getClan().getPrefix() );
		}
		catch( NullPointerException e )
		{
				//  this command isn't really intended to be used
				//  when the player isn't in a clan, but it isn't
				//  disasterous when it occurs - simply ignore this.
				//
				//  an exception is used rather than an if because
				//  we don't expect this to happen often, and
				//  exceptions are completely free unless they're
				//  thrown.  (I read that somewhere, but I forget
				//  where... I suppose I should read up on the VM
				//  in order to verify this)
		}
	}
}
