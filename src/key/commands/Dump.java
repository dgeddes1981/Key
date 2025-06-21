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
**  $Id: Dump.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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

public class Dump extends Command
{
	private static final long serialVersionUID = -5942145322376456358L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Dump.class, Integer.TYPE, "maxEntriesListed",
			AtomicElement.PUBLIC_FIELD,
			"the maximum number of entries in a container output" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	public int maxEntriesListed = 400;
	
	public Dump()
	{
		setKey( "dump" );
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
			ic.sendFeedback( id + " = " + Key.nullString );
		else if( o instanceof Atom )
		{
			Atom a = (Atom) o;
			
			ic.sendFeedback( Type.typeOf( a ).getName() + ":" );
			
			for( Enumeration e = a.getStructure().elements(); e.hasMoreElements(); )
			{
				AtomicElement ae = (AtomicElement) e.nextElement();
				Object val = null;
				
				if( !ae.isGenerated() )
				{
					try
					{
						val = ae.getValue( a );
					}
					catch( Exception ex )
					{
						ic.sendError( "  " + ex.toString() );
						continue;
					}
					
					if( val == null )
						val = Key.nullString;
					else if( val instanceof String )
						val = "\"" + val + "\"";
				}
				
				StringBuffer sb = new StringBuffer( "  " );
				
				sb.append( ae.getTypeOf().getName() );
				sb.append( ' ' );
				sb.append( ae.getName() );
				
				if( !ae.isGenerated() )
				{
					sb.append( " = " );
					sb.append( val );
				}
				
				ic.send( sb.toString() );
			}
			
			if( o instanceof Container )
			{
				Container c = (Container) o;
				int count = c.count();
				
				if( c.count() > maxEntriesListed )
				{
					ic.sendFeedback( "\nContainer contains " + count + " objects.  (not listed)" );
				}
				else
				{
					StringBuffer contents = new StringBuffer();
					contents.append( "\nContents: " );
					for( Enumeration e = c.referenceElements(); e.hasMoreElements(); )
					{
						contents.append( ((Reference)e.nextElement()).getName() );
						if( e.hasMoreElements() )
							contents.append( ", " );
					}
					
					ic.sendFeedback( contents.toString() );
				}
			}
			
			ic.blankLine();
		}
		else
		{
			if( o instanceof String )
			{
				ic.sendFeedback( id + " = \"" + (String)o + "\"" );
			}
			else if( o instanceof Integer )
			{
				ic.sendFeedback( id + " = " + o.toString() );
			}
			else if( o instanceof Paragraph )
			{
				ic.sendFeedback( "Contents:" );
				ic.send( (Paragraph)o );
			}
			else if( o instanceof Trie )
			{
				ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
			}
			else
				ic.sendError( id + " = (" + Type.typeOf( o ).getName() + ") " + o.toString() );
		}
	}
}
