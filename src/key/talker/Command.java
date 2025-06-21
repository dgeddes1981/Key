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

package key;

import key.util.Trie;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

/**
  *  The base class for a command also includes some nice utility
  *  routines that command writers tend to need
 */
public abstract class Command extends Atom implements Commandable
{
	private static final long serialVersionUID = 7107251377754958246L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Command.class, String.class, "usage",
			AtomicElement.PUBLIC_FIELD,
			"the usage specifier for this command" ),
		AtomicElement.construct( Command.class, Boolean.TYPE, "disabled",
			AtomicElement.PUBLIC_FIELD,
			"true if this command can't be used (usually set when something goes wrong)" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	protected String usage = "~unspecified~";
	protected boolean disabled = false;
	
	public Command()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public boolean isDisabled()
	{
		return( disabled );
	}
	
	public void disable()
	{
		disabled = true;
	}
	
	public void usage( InteractiveConnection ic )
	{
		ic.send( "Format: " + getName() + " " + usage );
	}

	public void usage( InteractiveConnection ic, CategoryCommand caller )
	{
		ic.send( "Format: " + getQualifiedName( caller ) + " " + usage );
	}
	
	public final String getQualifiedName( CategoryCommand caller )
	{
		if( caller instanceof Atom )
			return( ((Atom)caller).getName() + " " + getName() );
		else
			return( getName() );
	}

	public String getWhichId()
	{
		return( getId() );
	}
	
	/**
	  *  If this method is not overridden, it calls
	  *  the others.  Only override *one*, depending on
	  *  what arguments you want to recieve
	  *  <p>
	  *  Presumably you'll want to do one or the other,
	  *  overload the method you'd prefer
	  * @param p the player who is executing the command
	  * @param args a tokenizer (whitespace) for the command arguments
	  * @param fullLine the complete line that was typed
	  * @param caller the parent CategoryCommand that contains this command
	  * @param ic the interactiveConnection to send the results to
	 */
	public abstract void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException;

	/**
	  *  An atom is returned in case they are like 'player,player,player',
	  *  so it will return a scape.
	 */
	public static final Splashable getPlayerInside( Player p, InteractiveConnection ic, String name, Scape inside )
	{
		Key instance = Key.instance();
		Object t = instance.getPlayer( p, name );
		if( t == null )
		{
			if( ic != null )
				ic.sendError( "Cannot find player '" + name + "' online" );
			
			return null;
		}
		else if( t instanceof Splashable )
		{
			if( inside != instance && t instanceof Player )
			{
				if( !inside.containsPlayer( (Player) t ) )
				{
					if( ic != null )	ic.sendError( ((Player)t).HeShe() + " is not in the " + inside.getName() );
					return( null );
				}
			}
			
			return( (Splashable) t );
		}
		else if( t instanceof Trie )
		{
				//  its more efficient to only work with online
				//  players - ie, prefer only holds when they're
				//  around - because its a smaller list to scan
			for( Enumeration e = ((Trie)t).elements(); e.hasMoreElements(); )
			{
				Player o = (Player) e.nextElement();
				
				if( p.getPrefer().containsPlayer( o ) )
				{
					if( inside.containsPlayer( o ) )
						return( o );
					else
					{
						ic.sendError( o.getName() + " is not in the " + inside.getName() );
						return( null );
					}
				}
			}
			
			int i = ((Trie)t).length();

			if( i > 10 )
			{
				if( ic != null )	ic.sendError( i + " multiple matches (not listed) [Tip: a look at the 'prefer' command]" );
			}
			else
			{
				if( ic != null )	ic.sendError( "Multiple matches: " + ((Trie)t).contents() );
			}
			
			return null;
		}
		else
		{
			if( ic != null )	ic.sendError( "'" + name + "' is not a player" );
			return null;
		}
	}
	
	/**
	  *  The basic concept is this:  The routine returns a player, or null.
	  *  If it returns null, it has already output an error message to the
	  *  player involved, so you don't have to.  (just return)
	  *
	  *  An atom is returned in case they are like 'player,player,player',
	  *  so it will return a scape
	 */
	public static final Splashable getOnlinePlayer( Player p, InteractiveConnection ic, String name )
	{
		if( name.equalsIgnoreCase( "me" ) )
			return( p );
		else
			return( getPlayerInside( p, ic, name, Key.instance() ) );
	}
	
	public static final Symbol getReferenceSymbolInside( InteractiveConnection ic, String s, Atom inside )
	{
		try
		{
			Object o = new Search( s, inside, false, true ).result;
			if( o == null )
				ic.sendError( "Could not find '" + s + "'" );
			else
			{
				if( o instanceof Atom || o instanceof Reference )
					return( (Symbol) o );
				else if( o instanceof Trie )
				{
					int i = ((Trie)o).length();
					if( i > 10 )
						ic.sendError( i + " multiple matches (not listed)" );
					else
						ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
				}
				else
					ic.sendError( "'" + s + "' is non-atomic" );
			}
		}
		catch( NoSuchPropertyException e )
		{
			ic.sendError( e.getMessage() );
		}

		return( null );
	}
	
	public static final Atom getSymbolInside( InteractiveConnection ic, String s, Atom inside )
	{
		try
		{
			Object o = new Search( s, inside ).result;
			if( o == null )
				ic.sendError( "Could not find '" + s + "'" );
			else
			{
				if( o instanceof Atom )
					return( (Atom) o );
				else if( o instanceof Trie )
				{
					int i = ((Trie)o).length();
					if( i > 10 )
						ic.sendError( i + " multiple matches (not listed)" );
					else
						ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
				}
				else
					ic.sendError( "'" + s + "' is non-atomic" );
			}
		}
		catch( NoSuchPropertyException e )
		{
			ic.sendError( e.getMessage() );
		}

		return( null );
	}
		
	/**
	  *  A slightly higher level routine - will match the
	  *  symbol and give error messages as appropriate.  It
	  *  is not necessary to check for anything other than
	  *  the return being null, in which case an error
	  *  has already been sent to the provided connection.
	 */
	public static final Atom getSymbol( InteractiveConnection ic, String s )
	{
		return( getSymbolInside( ic, s, Key.instance() ) );
	}

	public static final Reference getReferenceElementInside( InteractiveConnection ic, String s, Container inside )
	{
		Object o = inside.getReferenceElement( s );
		if( o != null )
		{
			if( o instanceof Atom )
				return( ((Atom)o).getThis() );
			else if( o instanceof Reference )
				return( (Reference) o );
			else if( o instanceof Trie )
			{
				int i = ((Trie)o).length();
				if( i > 10 )
					ic.sendError( i + " multiple matches (not listed)" );
				else
					ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
			}
			else
				ic.sendError( "'" + s + "' is non-atomic or doesn't match required type" );
		}

		return( null );
	}
	
	public static final Atom getElementInside( InteractiveConnection ic, String s, Type type, Container inside )
	{
		Object o = inside.getElement( s );
		if( o != null )
		{
			if( o instanceof TransitAtom )
				o = ((TransitAtom)o).getRealAtom();
			
			if( o instanceof Atom && Type.typeOf( o ).isA( type ) )
				return( (Atom) o );
			else if( o instanceof Trie )
			{
				int i = ((Trie)o).length();
				if( i > 10 )
					ic.sendError( i + " multiple matches (not listed)" );
				else
					ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
			}
			else
				ic.sendError( "'" + s + "' is non-atomic or doesn't match required type" );
		}
		else
			ic.sendError( "Couldn't find '" + s + "'" );
		
		return( null );
	}
	
	public static final Thing getObjectFromInventory( Player p, InteractiveConnection ic, String name )
	{
		return( getObjectIn( p, p.getInventory(), ic, name, "You don't seem to be holding that object." ) );
	}
	
	public static final Thing getObjectFromLocation( Player p, InteractiveConnection ic, String name )
	{
		return( getObjectIn( p, p.getLocation(), ic, name, "That object isn't in this room." ) );
	}
	
	public static final Thing getObjectIn( Player p, Container c, InteractiveConnection ic, String name, String notFound )
	{
		Object temp = c.getElement( name );
		
		if( temp == null )
		{
			if( notFound != null )
			{
				if( ic != null )
					ic.sendFeedback( notFound );
			}
		}
		else
		{
			if( temp instanceof TransitAtom )
				temp = ((TransitAtom)temp).getRealAtom();
			
			if( !(temp instanceof Thing) )
			{
				if( temp instanceof Trie )
				{
					if( ic != null )	ic.sendError( "Multiple matches: " + ((Trie)temp).contents() );
				}
				else if( notFound != null )
				{
					if( ic != null ) ic.sendError( "This object does not seem to be one of the primitive types of matter." );
				}
			}
			else
			{
				Thing t = (Thing) temp;
				
				if( t.isAvailableTo( p, p.getLocation() ) )
					return( t );
				else
				{
					if( ic != null ) ic.sendError( "(this object has not been approved, therefore...) " );
				}
			}
		}
		
		return( null );
	}
	
	public static final Atom getElementInside( InteractiveConnection ic, String s, Container inside )
	{
		Object o = inside.getElement( s );
		if( o != null )
		{
			if( o instanceof TransitAtom )
				o = ((TransitAtom)o).getRealAtom();
			
			if( o instanceof Atom )
				return( (Atom) o );
			else if( o instanceof Trie )
			{
				int i = ((Trie)o).length();
				if( i > 10 )
					ic.sendError( i + " multiple matches (not listed)" );
				else
					ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
			}
			else
				ic.sendError( "'" + s + "' is non-atomic" );
		}
		else
			ic.sendError( "Cannot find '" + s + "'" );
		
		return( null );
	}
	
	public static final Atom getSymbolInside( InteractiveConnection ic, String s, Type type, Atom i )
	{
		Atom a = getSymbolInside( ic, s, i );
		if( a != null )
		{
			if( Type.typeOf( a ).isA( type ) )
				return( a );
			else
				ic.sendError( a.getName() + " is not a " + type.getName() );
		}

		return( null );
	}

	public static final Atom getSymbol( InteractiveConnection ic, String s, Type type )
	{
		return( getSymbolInside( ic, s, type, Key.instance() ) );
	}
	
	/**
	  *  This routine will return a Player or null.
	 */
	public static final Player getPlayer( InteractiveConnection ic, String name )
	{
		Object t = Key.instance().getResidents().getElement( name );
		if( t == null )
		{
			ic.sendError( "No player of the name '" + name + "'" );
			return null;
		}
		else if( t instanceof Player )
		{
			return( (Player) t );
		}
		else if( t instanceof Trie )
		{
			int i = ((Trie)t).length();
			if( i > 10 )
				ic.sendError( i + " multiple matches (not listed)" );
			else
				ic.sendError( "Multiple matches: " + ((Trie)t).contents() );
			return null;
		}
		
		ic.sendError( "'" + name + "' is not a player" );
		return null;
	}
	
	public static final Reference getActionReference( String firstArg, InteractiveConnection ic, Player p )
	{
		if( firstArg.startsWith( "@" ) )
		{
			Clan c = p.getClan();
			
			if( c == null )
			{
				ic.sendError( "But you're not in a clan!" );
				return( Reference.EMPTY );
			}
			
			return( getReferenceElementInside( ic, firstArg.substring( 1 ), c.ranks ) );
		}
		else if( firstArg.startsWith( "#" ) )
		{
			String num = firstArg.substring( 1 );
			
			try
			{
				int idx = Integer.parseInt( num );
				return( Reference.to( idx, Registry.instance.getTimestamp( idx ) ) );
			}
			catch( NumberFormatException e )
			{
				throw new InvalidArgumentException( "'" + num + "' is not an index number" );
			}
		}
		else
			return( getReferenceElementInside( ic, firstArg, Key.shortcuts() ) );
	}
	
	public final String nextArgument( StringTokenizer st, InteractiveConnection ic )
		throws NotEnoughParametersException
	{
		if( st.hasMoreTokens() )
			return( st.nextToken() );
		
		usage( ic );
		throw new NotEnoughParametersException();
	}
	
	public String getUsage()
	{
		return( usage );
	}
	
	/**
	  *  This routine is notified just before a command is executed.
	  *  If it returns this (the default), then run() will be called.
	  *  If it returns null, another matching command will be looked
	  *  for (this might provide a neat way of disabling commands).
	  *  If it returns another command, that command has getMatch
	  *  called on it, and the process repeats.  This allows for
	  *  CategoryCommands to be implemented.
	 */
	public Commandable getMatch( final Player p, key.util.StringTokenizer st )
	{
		return( this );
	}

	public Match getFinalMatch( final Player p, key.util.StringTokenizer st )
	{
		return( new Command.Match()
		{
			{
				match = Command.this;
			}
			
			public String getErrorString()
			{
				return( "" );
			}
		} );
	}
	
	public boolean recloneArgs()
	{
		return( false );
	}
	
	public static abstract class Match
	{
		public Commandable match = null;
		public Commandable lastchance = null;
		public abstract String getErrorString();
	}
}
