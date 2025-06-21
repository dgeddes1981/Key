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
**  $Id: Commands.java,v 1.2 2000/02/11 14:29:55 noble Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import java.util.Enumeration;
import java.io.IOException;
import java.util.StringTokenizer;
import key.*;

public class Commands extends Command
{
	private static final long serialVersionUID = 5990785328128283650L;
	
	public Commands()
	{
		setKey( "commands" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		int cc = 0;
		
		for( Enumeration e = p.getCategoryCommandsMatching( args ); e.hasMoreElements(); )
		{
			Object[] o = (Object[]) e.nextElement();
			
			CommandList cl;
			String t;
			
			if( o[0] != null )
				cl = ((CommandContainer) o[0]).getCommandList();
			else
				cl = (CommandList) o[1];
			
			t = ((CommandList)o[1]).getTitle();
			
			if( cl != o[1] )
				t += "/" + cl.getTitle();
			
			cc += oneCommandList( cl, ic, t );
		}
		
		if( cc == 0 )
		{
			ic.send( "No matching commands." );
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			sb.append( cc );
			sb.append( " command" );
			
			if( cc != 1 )
				sb.append( 's' );
			
			sb.append( " listed" );
			ic.send( new HeadingParagraph( sb.toString(), HeadingParagraph.RIGHT ) );
		}
	}
	
	public int oneCommandList( CommandList cList, InteractiveConnection ic, String prefix )
	{
		if( cList == null )
			return 0;
		
		int c = cList.count();
		if( c > 0 )
		{
			StringBuffer sb = new StringBuffer();
			
			sb.append( "^h[" );
			sb.append( prefix );
			sb.append( " - " );
			sb.append( c );
			sb.append( " command" );
			
			if( c != 1 )
				sb.append( 's' );
			
			sb.append( "]^-" );
			
			ic.send( new HeadingParagraph( sb.toString(), HeadingParagraph.LEFT ) );
			
			String[] commands = new String[ c ];
			int upto = 0;
			
			for( Enumeration e = cList.referenceElements(); e.hasMoreElements(); )
				commands[upto++] = ((Reference)e.nextElement()).getName();
			
			TextParagraph tp = new TextParagraph( TextParagraph.LEFT, Grammar.commaSeperate( commands ), 4, 0, 0, 0 );
			
			ic.send( tp );
		}
		
		return( c );
	}
}
