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
**  $Id: Verb.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Oct98     subtle       created
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class Verb extends Command
{
	private static final long serialVersionUID = 7601571514137070656L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Verb.class, Boolean.TYPE, "checkInventory",
			AtomicElement.PUBLIC_FIELD,
			"true iff the players inventory should be checked for the object" ),
		AtomicElement.construct( Verb.class, Boolean.TYPE, "checkRoom",
			AtomicElement.PUBLIC_FIELD,
			"true iff the players current room should be checked for the object" ),
		AtomicElement.construct( Verb.class, String.class, "verb",
			AtomicElement.PUBLIC_ACCESSORS,
			"the name of the method to call on the matched object" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public boolean checkInventory;
	public boolean checkRoom;
	protected String verb = Material.getDefaultVerb();
	protected transient Method method;
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Verb()
	{
		setKey( "use" );
		usage = "<object> [<option args>]";
		method = Material.getMethod( verb );
	}
	
	/**
	  * @return true iff this was a keyword and we've done everything we need to
	 */
	protected boolean handleKeyword( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags, String keyword ) throws IOException
	{
		if( keyword.equalsIgnoreCase( "all" ) )
		{
			Room r = p.getLocation();
			Inventory i = p.getInventory();
			
			int count = 0;
			
			if( checkInventory )
			{
				for( Enumeration e = i.elements(); e.hasMoreElements(); )
				{
					Thing t = (Thing) e.nextElement();

					doIt( p, (StringTokenizer) ((key.util.StringTokenizer)args).clone(), fullLine, ic, flags, i, r, t, i );
					count++;
				}
			}
			
			if( checkRoom )
			{
				for( Enumeration e = r.elements(); e.hasMoreElements(); )
				{
					Thing t = (Thing) e.nextElement();
					
					if( t instanceof Exit )
						continue;
					
					doIt( p, (StringTokenizer) ((key.util.StringTokenizer)args).clone(), fullLine, ic, flags, i, r, t, r );
					count++;
				}
			}
			
			if( count == 0 )
			{
				if( checkRoom && checkInventory )
					ic.sendFailure( "There aren't any objects in your inventory or this room." );
				else if( checkRoom )
					ic.sendFailure( "There aren't any objects in this room." );
				else if( checkInventory )
					ic.sendFailure( "There aren't any objects in your inventory." );
				else
					ic.sendFailure( "This command will never match any object, it is improperly set up." );
			}
			
			return true;
		}
		else
			return( false );
	}
	
	protected void handleNoArgs( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		usage( ic );
	}
	
		//  We can't run with scissors, but we can run with items.
	protected final void runWithItem( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags, String nt ) throws IOException
	{
		if( !handleKeyword( p, args, fullLine, caller, ic, flags, nt ) )
		{
			Room r = p.getLocation();
			Inventory i = p.getInventory();
			
			Thing t = null;
			Container orig = null;
			
			if( checkInventory )
			{
				t = getObjectIn( p, i, ic, nt, null );
				orig = i;
			}
			
				//  if it wasn't in the inventory, get it from the room
				//  (ideally, a joint object collection would be best,
				//   but it probably isn't worth it.  another way would
				//   be to just get the strength of the match from
				//   both, but it's added complexity that may not be necessary)
			if( t == null && checkRoom )
			{
				t = getObjectIn( p, r, ic, nt, null );
				orig = r;
			}
			
			if( t != null )
				doIt( p, args, fullLine, ic, flags, i, r, t, orig );
			else if( checkRoom && checkInventory )
				ic.sendFailure( "This object is not in your inventory or this room." );
			else if( checkRoom )
				ic.sendFailure( "This object is not in this room." );
			else if( checkInventory )
				ic.sendFailure( "This object is not in your inventory." );
			else
				ic.sendFailure( "This command will never match an object, it is improperly set up." );
		}
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			handleNoArgs( p, args, fullLine, caller, ic, flags );
			return;
		}
		
		String nt = args.nextToken();
		runWithItem( p, args, fullLine, caller, ic, flags, nt );
	}
	
	protected void doIt( Player p, StringTokenizer args, String fullLine, InteractiveConnection ic, Flags flags, Inventory i, Room r, Thing t, Container orig )
	{
		if( verb != null && verb.length() != 0 )
		{
			if( method == null )
				method = Material.getMethod( verb );
			
			if( method != null )
			{
				Object[] params = new Object[ Material.verbParameters.length ];
				
				params[0] = p;
				params[1] = args;
				params[2] = ic;
				params[3] = flags;
				params[4] = t;
				params[5] = orig;
				
				try
				{
					method.invoke( t, params );
				}
				catch( InvocationTargetException e )
				{
					Throwable x = e.getTargetException();
					
					if( x instanceof PermissionDeniedException )
					{
						ic.sendFailure( "You are not able to do that." );
						
						if( p.isExpert() )
						{
							ic.sendFailure( "Expert mode output follows:" );
							((UserOutputException)x).send( ic );
						}
					}
					else if( x instanceof UserOutputException )
					{
						((UserOutputException)x).send( ic );
					}
					else
					{
						Log.error( "while invoking verb " + method.toString(), x );
						if( p.isExpert() )
						{
							ic.sendFailure( x.toString() + "\nduring ^h" + method.toString() + "^-" );
							ic.printStackTrace( x );
						}
						else
							ic.sendFailure( "You may not do this." );
					}
				}
				catch( Exception e )
				{
					Log.error( "while invoking verb " + method.toString(), e );
					if( p.isExpert() )
					{
						ic.sendFailure( e.toString() + "\nduring ^h" + method.toString() + "^-" );
						ic.printStackTrace( e );
					}
					else
						ic.sendFailure( "You may not do this." );
				}
			}
			else
				ic.sendFailure( "Could not find method '" + verb + "'." );
		}
		else
			ic.sendFailure( "This command is incorrectly set up" );
	}
	
	public String getVerb()
	{
		return( verb );
	}
	
	public void setVerb( String v )
	{
		Method m = Material.getMethod( v );
		
		if( m != null )
		{
			verb = v;
			method = m;
		}
		else
			throw new InvalidSearchException( "Unknown verb '" + v + "'.  Try one of: " + Material.getAvailableVerbs() );
	}
}
