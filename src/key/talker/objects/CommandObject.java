/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
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
**  $Id: CommandObject.java,v 1.2 1999/10/11 13:58:48 noble Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  04Oct99     Noble       created this object
**
*/
package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;


public class CommandObject extends Message
{
	private static final long serialVersionUID = 6509333317160514849L;

	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( CommandObject.class, String.class, "command",
			AtomicElement.PUBLIC_FIELD,
			"the command(s) to be executed by the object (use %m for user args) (can be pipe seperated)" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Message.STRUCTURE, ELEMENTS );
	
	public static final char messageCode = 'm';
	
	public String command = null;
			
	public CommandObject()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{	// overrides Message.use() making target redundant (any target output will be the result of the command)
		
		
		if( command == null || command.length() == 0 )
			return;

		if( room != null )
		{
			String self = room;
				
			p.putCode( originatorCode, p.getName() );
			p.putCode( prefixNameCode, p.getFullName() );
			p.putCode( portraitCode, getFullPortrait( p ) );
			
			if( feedback != null )
				self = Grammar.substitute( feedback, p.getCodes() );
			else
				self = Grammar.substitute( room, p.getCodes() );
			
			
			String main = Grammar.substitute( room, p.getCodes() );
				
			new key.effect.ObjectEffect( this, p, main, self ).cause();
			
			itemWasUsed( p, ic, flags, item, originating );
		}

		if( args.hasMoreTokens() )
			p.putCode( messageCode, args.nextToken( "" ) );
		else
			p.putCode( messageCode, "" );
		
		StringTokenizer st = new StringTokenizer( command, "|" );
		
		while( st.hasMoreTokens() )
			p.command( Grammar.substitute( st.nextToken(), p.getCodes() ), ic, false );		
		
	}

}
