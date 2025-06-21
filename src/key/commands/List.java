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
**  $Id: List.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  23Aug98     subtle       revised for new element system
**
*/

package key.commands;

import key.*;
import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class List extends Command
{
private static final long serialVersionUID = -7097875217663773125L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( List.class, Integer.TYPE, "maxEntriesListed",
			AtomicElement.PUBLIC_FIELD,
			"the maximum number of entries in a container output" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	public int maxEntriesListed = 400;
	
	public List()
	{
		setKey( "ls" );
		usage = "[<identifier>]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String id;
		if( args.hasMoreTokens() )
			id = args.nextToken( " " );
		else
			id = "";
		Object o;
		
		try
		{
			o = new Search( id, p.getContext() ).result;
		}
		catch( InvalidSearchException e )
		{
			ic.sendError( e.getMessage() );
			return;
		}
		
		if( o == null )
			ic.sendError( id + ": No such container" );
		else if( o instanceof Container )
		{
			Container c = (Container) o;
			int count = c.count();
			
			if( count > maxEntriesListed )
				ic.sendFeedback( "\nContainer contains " + count + " objects.  (not listed)" );
			else if( count == 0 )
				;  //  output nothing
			else
			{
				StringBuffer contents = new StringBuffer();
				for( Enumeration e = c.referenceElements(); e.hasMoreElements(); )
				{
					contents.append( ((Reference)e.nextElement()).getName() );
					if( e.hasMoreElements() )
						contents.append( ", " );
				}
				
				ic.sendFeedback( contents.toString() );
			}
		}
		else if( o instanceof Atom )
			ic.sendFeedback( ((Atom)o).getId() + " (" + Type.typeOf( o ).getName() + ")" );
		else if( o instanceof Trie )
			ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
		else
			ic.sendError( id + ": No such container" );
	}
}
