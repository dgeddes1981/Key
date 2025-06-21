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
** $Id: Version.java,v 1.2 2000/02/10 20:04:48 subtle Exp $
**
** $Log: Version.java,v $
** Revision 1.2  2000/02/10 20:04:48  subtle
** small cosmetic change
**
**
*/

package key.commands;

import key.*;
import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
 */
public class Version extends Command
{
	public Version()
	{
		setKey( "version" );
		usage = "[<identifier>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Key k = Key.instance();
		
		ic.send( k.getName() + ".  Version " + Key.RELEASE + "." + Key.VERSION + ".  (C)opyright 1998-1999 Paul Mclachlan" );
		ic.send( "  Running on: " + System.getProperty( "os.arch" ) + "/" + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) );
		
		String s = System.getProperty( "java.compiler" );
		
		if( s != null && s.length() != 0 )
			s = "\nJIT: " + s + "";
		
		if( s == null )
			s = "";
		
		ic.sendFeedback( "  Java VM: " + System.getProperty( "java.version" ) + "/" + System.getProperty( "java.class.version" ) + "\n[" + System.getProperty( "java.vendor" ) + "]" + s );
	}
}
