/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key.commands;

import key.*;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Read extends Verb
{
	public Read()
	{
		setKey( "read" );
		usage = "<object> [<page #>]";
		verb = "read";
		method = null;
		checkInventory = true;
		checkRoom = true;
	}
	
	protected boolean handleKeyword( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags, String keyword ) throws IOException
	{
		if( keyword.equalsIgnoreCase( "mail" ) )
		{
			Thing g = p.getMailbox();
			g.read( p, args, ic, flags, g, null );
			return( true );
		}
		if( keyword.equalsIgnoreCase( "news" ) )
		{
			Thing g = p.getRealm().news;
			g.read( p, args, ic, flags, g, null );
			return( true );
		}
		else
			return( super.handleKeyword( p, args, fullLine, caller, ic, flags, keyword ) );
	}
	
	public String getVerb()
	{
		return( "read" );
	}
	
	public void setVerb( String v )
	{
		throw new InvalidArgumentException( "you may not set this property" );
	}
}
