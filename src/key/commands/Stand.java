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

public class Stand extends Verb
{
	public Stand()
	{
		setKey( "stand" );
		usage = "";
		verb = "stand";
		method = null;
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		ic.send( "Stand not implemented" );
	}
	
	public String getVerb()
	{
		return( "stand" );
	}
	
	public void setVerb( String v )
	{
		throw new InvalidArgumentException( "you may not set this property" );
	}
}
