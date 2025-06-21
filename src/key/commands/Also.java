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
**  $Id: Also.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
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
import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

/**
  *  Used to indicate a command has many names.  This class will
  *  simply forward it's "run" method to the specified command.
  *
  *  The context changing capabilities of this class make it easy
  *  to emulate some EW behaviour, such as "mail block", but using
  *  a context-switch to change the players context to their mailbox,
  *  and executing the 'block' command.  (or something)
 */
public class Also extends Command
{
	private static final long serialVersionUID = 2282992396833160677L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Also.class, Command.class, "command",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.REFERENCE,
			"the command to be executed by the proxy" ),
		AtomicElement.construct( Also.class, Atom.class, "context",
			AtomicElement.PUBLIC_FIELD,
			"the context to execute the new command under, or null to leave it be" ),
		AtomicElement.construct( Also.class, Boolean.TYPE, "passArgs",
			AtomicElement.PUBLIC_FIELD,
			"true if user entered arguments are passed to the command" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	private Reference command = Reference.EMPTY;
	public boolean passArgs = true;
	public Reference context = Reference.EMPTY;
	
	public Also()
	{
		setKey( "also" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void setCommand( Command r )
	{
		command = Reference.to( r, false );
		usage = r.getUsage();
	}
	
	public void setCommand( String s, Atom r )
	{
		if( s.equals( Key.nullString ) )
			command = Reference.EMPTY;
		
		Search se = new Search( s, r );
		
		if( se.result instanceof Command )
			setCommand( ((Command)se.result) );
		else if( se != null )
			throw new InvalidSearchException( "invalid result trying to set field of type Command to " + se.result.toString() );
		else
			throw new InvalidSearchException( "could not find '" + s + "'" );
	}
	
	public Command getCommand()
	{
		try
		{
			return( (Command) command.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			command = Reference.EMPTY;
			usage = "";
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".command wrong (reset)", e );
			command = Reference.EMPTY;
			usage = "";
			return( null );
		}
	}
	
	public String getWhichId()
	{
		Command c = getCommand();
		if( c != null )
			return( getId() + " (also link to '" + c.getWhichId() + "')" );
		else
			return( getId() + " (blank also link)" );
	}
	
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Command c = getCommand();
		
		if( c == null )
		{
			ic.sendError( "This alias is incorrectly set up." );
			return;
		}
		
		if( !passArgs )
		{
			fullLine = c.getName();
			args = new StringTokenizer( "" );
		}
		
		Atom cxt = null;
		
		try
		{
			cxt = (Atom) context.get();
		}
		catch( OutOfDateReferenceException e )
		{
			context = Reference.EMPTY;
		}
		
		if( cxt != null )
		{
			Atom old = p.getContext();
			p.setContext( cxt );
			c.run( p, args, fullLine, caller, ic, flags );
			p.setContext( old );
		}
		else
			c.run( p, args, fullLine, caller, ic, flags );
	}
}
