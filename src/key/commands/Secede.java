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
**  Class: secede
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  20Jul97     snapper      finally added this header
**  20Jul97     snapper      added logging
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.Enumeration;

import java.util.StringTokenizer;

public class Secede extends Command
{
	public Secede()
	{
		setKey( "secede" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Clan currentClan = (Clan) p.getClan();
		
		if( currentClan == null )
		{
			ic.sendFeedback( "You don't need to secede, you're not in a Clan." );
			return;
		}
		
		if( currentClan.getFounder() == p )
		{
			ic.sendFeedback( "Delete your clan (delete /clans/" + currentClan.getName() + ") if you really want to get rid of it." );
			return;
		}
		
		try
		{
				//  remove the player from the base rank
			Rank r = (Rank) currentClan.getProperty( "baseRank" );
			
			if( r != null )
			{
				r.remove( p );
			}
			else
			{
				ic.send( "WARNING: This clan has no baserank property.  Get your founder to fix it, or email the world administrators.  We're going to take you out as much as we can - but you might still get some messages from the clan." );
				Log.log( "clan_errors", "Clan: " + currentClan.getId() + " has a null baseRank while seceding player " + p.getName() );
			}
			
			p.setProperty( "clan", null );
			
				//  if they're blocking a clan, make them stop
			QualifierList ql = p.getQualifierList();
			Type c = Type.CLAN;
			
			if( ql.check( c ) == Qualifiers.SUPPRESSION_CODE )
				ql.set( c, Qualifiers.UNSUPPRESSION_CODE );
		}
		catch( BadKeyException t )
		{
			throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
		}
		catch( NonUniqueKeyException t )
		{
			throw new UnexpectedResult( t.toString() + " on removing a matched atom" );
		}
		
		(new key.effect.Broadcast( currentClan, p, p.getName() + " hath left the clan.", "You leave the Clan." )).cause();
		Log.log( "clans/" + currentClan.getName() + ".records", "'" + p.getName() + "' seceded." );
	}
}
