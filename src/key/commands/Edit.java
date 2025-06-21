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
**  $Id: Edit.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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

public class Edit extends Command
{
	public static final int MAX_LINES = 200;
	public static final int MAX_BYTES = 50 * MAX_LINES;
	
	public Edit()
	{
		setKey( "edit" );	//  a default
		usage = "<property>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String id = args.nextToken();
			
			Search search = new Search( id, p.getContext() );
			
			Object o = search.matchedResult;
			
			try
			{
				if( o instanceof AtomicElement )
				{
					AtomicElement ae = (AtomicElement) o;
					
					if( ae.isReadOnly() )
					{
						ic.sendFailure( "That property is read-only." );
						return;
					}
					else if( ae.isReference() || ae.isAtomic() )
					{
						o = ae.getValue( search.lastAtom );
							//  fall through
					}
					else
					{
						Object a = ae.getValue( search.lastAtom );
						
						if( a instanceof Paragraph )
						{
								//  see if we can find the limits for
								//  this paragraph...
							AtomicSpecial special = ae.getSpecial();
							int max_lines = MAX_LINES;
							int max_bytes = MAX_BYTES;
							
							if( special instanceof TextParagraphLengthWrapper )
							{
								TextParagraphLengthWrapper wrapper = (TextParagraphLengthWrapper) special;
								max_lines = wrapper.getLineLimit();
								max_bytes = wrapper.getByteLimit();
							}
							
								//  edit this
							Paragraph para = Editor.edit( p, (Paragraph)a, ic, max_lines, max_bytes );
							if( para != a )
								ae.setValue( search.lastAtom, para, null );
						}
						else
						{
							ic.sendFailure( "That is not a paragraph, it is a " + Type.typeOf( a ).getName() );
						}
						
						return;
					}
				}
			}
			catch( IllegalAccessException e )
			{
				ic.sendFailure( "Error accessing: " + e.getMessage() );
				return;
			}
			
				//  some things fall through to here
			
			if( o instanceof Reference )
			{
				o = ((Reference)o).get();
			}
			
			if( o instanceof Screen )
			{
				Screen s = (Screen) o;
				Paragraph from = s.getParagraph();
				Paragraph para = Editor.edit( p, from, ic, MAX_LINES, MAX_BYTES );
				
				if( para != from )
					s.setParagraph( para );
			}
			else
				ic.sendError( "You can't edit a " + Type.typeOf( o ).getName() );
		}
		else
			usage( ic );
	}
}
