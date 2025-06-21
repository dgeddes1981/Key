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
import java.util.Vector;
import java.util.Enumeration;

public class RefExit extends CommandCategoryContainer
{
	public RefExit()
	{
		setKey( "exit" );
		usage = "<exit> [<command>]";
	}
	
	/**
	  * @see key.Command.getMatch
	 */
	public Commandable getMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			String ename = st.nextToken();
			
			Commandable co = super.getMatch( p, st );
			
			if( co != null && co != this )
			{
				Atom exit = getSymbolInside( p.getConnection(), ename, Type.EXIT, p.getLocation() );
				if( exit != null )
					return( new ContextChanger( co, exit ) );
				else
					return( co );
			}
			else
				return( co );
		}
		else
			return( this );
	}
	
	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			final String ename = st.nextToken();
			
			Command.Match cm = super.getFinalMatch( p, st );

			if( cm.match != null )
			{
				Atom exit = getSymbolInside( p.getConnection(), ename, p.getLocation() );
				if( exit != null )
					cm.match = new ContextChanger( cm.match, exit );
				else
					cm.match = null;
			}
			
			return( cm );
		}
		else
			return( super.getFinalMatch( p, st ) );
	}
	
	/**
	  * should put the player into this mode
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
			//  this will only be called if none of the sub-commands
			//  were validated.
		
		fullLine = fullLine.trim().toLowerCase();
		
		StringTokenizer st = new StringTokenizer( fullLine );
		String name = fullLine;
		
		if( st.hasMoreTokens() )
		{
			name = st.nextToken();
			
			if( st.hasMoreTokens() )
			{
				st.nextToken();
				
				if( st.hasMoreTokens() )
					st = new StringTokenizer( name + " " + st.nextToken( "" ) );
				else
					st = new StringTokenizer( name );
			}
			else
				st = new StringTokenizer( name );
		}
		
		Vector v = new Vector( 25, 50 );
		
		for( Enumeration e = p.getCategoryCommandsMatching( st ); e.hasMoreElements(); )
		{
			Object[] o = (Object[]) e.nextElement();
			
			CommandList cl;
			
			if( o[0] != null )
				cl = ((CommandContainer) o[0]).getCommandList();
			else
				cl = (CommandList) o[1];
				
			for( Enumeration f = cl.elements(); f.hasMoreElements(); )
			{
				String s = ((Command)f.nextElement()).getName();
				
				if( !v.contains( s ) )
					v.addElement( s );
			}
		}
		
		if( v.size() > 0 )
		{
			ic.send( "'" + name +
				"' is not a command, it is a category that contains other commands.  For instance, you might type '" +
				name + " gate " + ((String) v.elementAt( 0 )) +
				"' (If 'gate' is an exit in this room).  The full list of available commands is: ^h" +
				Grammar.commaSeperate( v.elements() ) + "^-" );
		}
		else
			ic.send( "There are no available sub-commands in this category." );
	}
}
