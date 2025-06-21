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
import java.util.StringTokenizer;
import java.io.IOException;

public class Block extends CommandCategoryContainer
{
	private static final long serialVersionUID = -2639627322424562207L;
	public static final String SPOON_CLAUSE = "Worried about having to talk to yourself?";
	
	public Block()
	{
		setKey( "block" );
		usage = "( subcommand | <player> ) [on | off]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			super.run( p, args, fullLine, caller, ic, flags );
			
			ic.send( "For convenience, you may also use " + getName() + " to block individual players from talking to you.  Just use the players name instead of the subcommand, such as 'block subtle'.  (This is a shortcut for 'deny <name> tell')" );
			
			return;
		}
			
			//  if this gets executed, we have not matched any
			//  of the custom block routines, so we're going to
			//  try and block a player.
		String name = args.nextToken();
		String toggle = "";
		
		if( args.hasMoreTokens() )
			toggle = args.nextToken();
		
		Player t = (Player) getPlayer( ic, name );
		
		if( t == null )
			return;
		
		PermissionList pl = p.getPermissionList();
		PermissionList.Entry ple = pl.getEntryFor( t.getThis() );
		
		if( ple == null && toggle.equalsIgnoreCase( "off" ) )
		{
			pl.allow( t.getThis(), p.tellAction );
			ic.sendFeedback( "You were not blocking " + t.getName() + " and you still aren't... so whatever." );
		}
		else if( ple == null )
		{
			if( p == t )
				ic.sendFailure( SPOON_CLAUSE );
			else
			{
					//  this person wasn't in the list previously
				pl.deny( t.getThis(), p.tellAction );
				ic.sendFeedback( "You block " + t.getName() );
			}
		}
		else
		{
			if( toggle.equalsIgnoreCase( "on" ) )
			{
				if( p == t )
					ic.sendFailure( SPOON_CLAUSE );
				else
				{
					ple.deny( p.tellAction );
					ic.sendFeedback( "You block " + t.getName() );
				}
			}
			else if( toggle.equalsIgnoreCase( "off" ) )
			{
				ple.allow( p.tellAction );
				ic.sendFeedback( "You stop blocking " + t.getName() );
			}
			else if( ple.isDenying( p.tellAction ) )
			{
				ple.allow( p.tellAction );
				ic.sendFeedback( "You stop blocking " + t.getName() );
			}
			else
			{
				if( p == t )
					ic.sendFailure( SPOON_CLAUSE );
				else
				{
					ple.deny( p.tellAction );
					ic.sendFeedback( "You block " + t.getName() );
				}
			}
		}
	}
	
	public boolean recloneArgs()
	{
		return( true );
	}
	
	public Commandable getMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			Commandable c = (Commandable) commands.getExactElement( st.nextToken() );
			
			if( c != null )
				return( c );
			else
				return( null );
		}
		else
			return( this );
	}
	
	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			final String command = st.nextToken();
			
			return( new Command.Match()
			{
				{
					Commandable c = (Commandable) commands.getExactElement( command );
					if( c != null )
						match = c;
					else
						match = null;
					
					lastchance = Block.this;
				}
				
				public String getErrorString()
				{
					return( "No such command '" + command + "' in " + Block.this.getName() );
				}
			} );
		}
		else
			return( super.getFinalMatch( p, st ) );
	}
	
	public abstract static class block extends Command
	{
		public block()
		{
			usage = "[on | off]";
		}
		
		/**
		  * @return true iff blocked
		 */
		public boolean doBlock( Player p, StringTokenizer args, InteractiveConnection ic, Type c )
		{
			QualifierList ql = p.getQualifierList();
			
			String toggle = "";
			
			if( args.hasMoreTokens() )
				toggle = args.nextToken();
			
				//  for groups (clans, friends etc)
			if( toggle.equalsIgnoreCase( "on" ) )
				ql.set( c, Qualifiers.SUPPRESSION_CODE );
			else if( toggle.equalsIgnoreCase( "off" ) )
				ql.set( c, Qualifiers.UNSUPPRESSION_CODE );
			else if( ql.check( c ) == Qualifiers.SUPPRESSION_CODE )
				ql.set( c, Qualifiers.UNSUPPRESSION_CODE );
			else
				ql.set( c, Qualifiers.SUPPRESSION_CODE );
			
			return( ql.check( c ) == Qualifiers.SUPPRESSION_CODE );
		}
	}
	
		//  hack until JDK 1.2 fixes this bug
	public static final AtomicElement[] TYPE_ELEMENTS =
	{
		AtomicElement.construct( Block.type.class, String.class, "type",
			AtomicElement.PUBLIC_FIELD,
			"the type to block, such as friends, movement, etc" ),
		AtomicElement.construct( Block.type.class, String.class, "blockMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the type is blocked." ),
		AtomicElement.construct( Block.type.class, String.class, "unblockMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the type is unblocked." ),
	};
	
	public static final AtomicStructure TYPE_STRUCTURE = new AtomicStructure( Command.STRUCTURE, TYPE_ELEMENTS );
	
	public static class type extends block
	{
		public String type = null;
		public String blockMsg = "You block";
		public String unblockMsg = "You unblock";
		
		public type()
		{
			setKey( "type" );
			usage = "[on | off]";
		}
		
		public AtomicStructure getDeclaredStructure()
		{
			return( TYPE_STRUCTURE );
		}
		
		protected boolean doBlock( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			if( type == null )
			{
				ic.sendFailure( "This command is not properly set up." );
				throw new InvalidArgumentException( "This command is not properly set up." );
			}
			
			Type t = null;
			
			try
			{
				t = Type.forName( type );
			}
			catch( ClassNotFoundException e )
			{
			}
			
			if( t == null )
			{
				ic.sendFailure( "This command is not properly set up.  Type '" + type + "' unknown." );
				throw new InvalidArgumentException( "This command is not properly set up.  '" + type + "' unknown." );
			}
			
			return( this.doBlock( p, args, ic, t ) );
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			try
			{
				if( doBlock( p, args, fullLine, caller, ic, flags ) )
					ic.sendFeedback( blockMsg );
				else
					ic.sendFeedback( unblockMsg );
			}
			catch( InvalidArgumentException e )
			{
			}
		}
	}
	
	public static class clan extends block
	{
		public clan()
		{
			setKey( "clan" );
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Clan clan = p.getClan();
			
			if( this.doBlock( p, args, ic, Type.CLAN ) )
			{
				if( clan == null )
				{
					ic.sendFeedback( "Schmuck, you're not *in* a clan.  Next time you are, though, it'll be blocked, just like you asked." );
				}
				else
				{
					key.effect.Block block = new key.effect.Block( p, clan, "(" + clan.getPrefix() + "): " + p.getName() + " blocks the clan." );
					block.cause();
					ic.sendFeedback( "You block your clan." );
				}
			}
			else
			{
				if( clan == null )
				{
					ic.sendFeedback( "You're no longer blocking the clan you're not in." );
				}
				else
				{
					key.effect.Block unblock = new key.effect.Block( p, clan, "(" + clan.getPrefix() + "): " + p.getName() + " stops blocking the clan." );
					unblock.cause();
					ic.sendFeedback( "You stop blocking your clan." );
				}
			}
		}
	}
	
		//  hack until JDK 1.2 fixes this bug
	public static final AtomicElement[] SCAPE_ELEMENTS =
	{
		AtomicElement.construct( Block.scape.class, String.class, "notinBlockMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when blocked and you aren't in the scape." ),
		AtomicElement.construct( Block.scape.class, String.class, "notinUnblockMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when unblocked and you aren't in the scape." ),
		AtomicElement.construct( Block.scape.class, String.class, "blockScapeMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the scape when blocked." ),
		AtomicElement.construct( Block.scape.class, String.class, "unblockScapeMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the scape when unblocked." ),
		AtomicElement.construct( Block.scape.class, Scape.class, "scapeFor",
			AtomicElement.PUBLIC_FIELD,
			"the scape to block (the type must still be unique, this indicates the scape to notify)" )
	};
	
	public static final AtomicStructure SCAPE_STRUCTURE = new AtomicStructure( TYPE_STRUCTURE, SCAPE_ELEMENTS );
	
	public static class scape extends type
	{
		public String notinBlockMsg = blockMsg;
		public String notinUnblockMsg = unblockMsg;
		public String blockScapeMsg = "<> %o blocks.";
		public String unblockScapeMsg = "<> %o stops blocking.";
		public Reference scapeFor = Reference.EMPTY;
		
		public scape()
		{
			setKey( "scape" );
		}
		
		public AtomicStructure getDeclaredStructure()
		{
			return( SCAPE_STRUCTURE );
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Scape scape = null;
			
			try
			{
				scape = (Scape) scapeFor.get();
			}
			catch( OutOfDateReferenceException e )
			{
				scapeFor = Reference.EMPTY;
			}
			catch( ClassCastException e )
			{
				scapeFor = Reference.EMPTY;
				Log.error( "somebody set " + getId() + ".scapeFor wrong (reset)", e );
			}
			
			try
			{
				if( this.doBlock( p, args, fullLine, caller, ic, flags ) )
				{
					if( scape == null || !scape.containsPlayer( p ) )
					{
						ic.sendFeedback( notinBlockMsg );
					}
					else
					{
						p.putCode( 'o', p.getName() );
						key.effect.Block block = new key.effect.Block( p, scape, blockScapeMsg );
						block.cause();
						ic.sendFeedback( blockMsg );
					}
				}
				else
				{
					if( scape == null || !scape.containsPlayer( p ) )
					{
						ic.sendFeedback( notinUnblockMsg );
					}
					else
					{
						p.putCode( 'o', p.getName() );
						key.effect.Block unblock = new key.effect.Block( p, scape, unblockScapeMsg );
						unblock.cause();
						ic.sendFeedback( unblockMsg );
					}
				}
			}
			catch( InvalidArgumentException e )
			{
			}
		}
	}
}
