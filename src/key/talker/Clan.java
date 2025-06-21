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
**  $Id: Clan.java,v 1.6 2000/05/01 16:28:51 subtle Exp $
**
**  $Log: Clan.java,v $
**  Revision 1.6  2000/05/01 16:28:51  subtle
**  Null Ptr Exception on clan create before founder has connected
**
**  Revision 1.5  1999/10/15 14:30:25  noble
**  More finicky adjustments
**
**  Revision 1.4  1999/10/14 19:35:10  pdm
**  fixed grammar for Brett.  *grumble*
**
**  Revision 1.3  1999/10/14 19:04:59  pdm
**  slight grammar/style tidyup
**
**  Revision 1.2  1999/10/14 18:54:48  noble
**  Added Finger Screen
**
**
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  18Jul97     snapper      added countMembers() routine
**  18Jul97     subtle       added timestatistics keeping
**
*/

package key;

import key.primitive.*;
import key.commands.ClanBroadcast;
import java.util.Enumeration;
import java.io.*;

/**
  *  A clan is land - but it also encompasses the
  *  rank and structures of that land
 */
public class Clan extends Channel
{
	private static final long serialVersionUID = -46981576610977923L;
	
	public static final int MAX_DESCRIPTION_LINES = Player.MAX_DESCRIPTION_LINES;
	public static final int MAX_DESCRIPTION_BYTES = Player.MAX_DESCRIPTION_BYTES;
	public static final int MAX_TITLE_LENGTH = 60;
	public static final int MAX_PREFIX_LENGTH = 10;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Clan.class, TimeStatistics.class, "loginStats",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"information about the cumulative time spent in the clan" ),
		AtomicElement.construct( Clan.class, Player.class, "founder",
			AtomicElement.PUBLIC_FIELD,
			"the leader and owner of the clan" ),
		AtomicElement.construct( Clan.class, Room.class, "hall",
			AtomicElement.PUBLIC_FIELD,
			"the clan's main room" ),
		//AtomicElement.construct( Clan.class, Landscape.class, "land",
			//AtomicElement.PUBLIC_FIELD,
			//"the clan's rooms" ),
		AtomicElement.construct( Clan.class, Landscape.class, "ranks",
			AtomicElement.PUBLIC_FIELD,
			"the clan's ranks" ),
		AtomicElement.construct( Clan.class, Landscape.class, "commandSets",
			AtomicElement.PUBLIC_FIELD,
			"the clan's command sets" ),
		AtomicElement.construct( Clan.class, Rank.class, "baseRank",
			AtomicElement.PUBLIC_FIELD,
			"the clan's ranks" ),
		AtomicElement.construct( Clan.class, String.class, "title", "title",
			AtomicElement.PUBLIC_FIELD,
			"a string that sometimes comes after the clan's name",
			AtomicSpecial.StringLengthLimit( MAX_TITLE_LENGTH, false, true ) ),
		AtomicElement.construct( Clan.class, String.class, "prefix", "prefix",
			AtomicElement.PUBLIC_FIELD,
			"a string that sometimes comes before the clan's name",
			AtomicSpecial.StringLengthLimit( MAX_PREFIX_LENGTH, false, true ) ),
		AtomicElement.construct( Clan.class, TextParagraph.class,
			"description", "description",
			AtomicElement.PUBLIC_FIELD,
			"a short description of the clan",
				//  these constants should be put elsewhere
			AtomicSpecial.TextParagraphLengthLimit( MAX_DESCRIPTION_BYTES,
			                                        MAX_DESCRIPTION_LINES ) ),
		AtomicElement.construct( Clan.class, Webpage.class, "homepage",
			AtomicElement.PUBLIC_FIELD,
			"the clan's homepage" ),
		AtomicElement.construct( Clan.class, Screen.class, "motd",
			AtomicElement.PUBLIC_FIELD,
			"a message of the day for the clan" ),
		AtomicElement.construct( Clan.class, String.class, "titledName",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"the name and title of the clan" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Channel.STRUCTURE, ELEMENTS );
	
	public final TimeStatistics loginStats = new TimeStatistics();
	public Reference founder = Reference.EMPTY;
	public Reference hall = Reference.EMPTY;
	//public final Landscape land = (Landscape) Factory.makeAtom( Landscape.class, "land" );
	public final Container ranks = (Container) Factory.makeAtom( Container.class, "ranks" );
	public final Container commandSets = (Container) Factory.makeAtom( Container.class, "commandSets" );
	public final Screen motd = (Screen) Factory.makeAtom( Screen.class, "motd" );
	public Reference baseRank = Reference.EMPTY;
	public String title = " the shiny new clan";
	public String prefix = "CLAN";
	public TextParagraph description = new TextParagraph();
	public Webpage homepage = new Webpage();
	
	public static final int MAX_RANKS = 6;
	public static final int MAX_NAME = 20;
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Clan()
	{
		ranks.setConstraint( Type.RANK );
		ranks.setLimit( MAX_RANKS );
		
			// make founder, leader, and member ranks...
			// founder
		
		Rank founder = (Rank) Factory.makeAtom( Rank.class, "founder" );
		Rank leader = (Rank) Factory.makeAtom( Rank.class, "leader" );
		Rank member = (Rank) Factory.makeAtom( Rank.class, "member" );
		
		try
		{
			ranks.add( founder );
			ranks.add( leader );
			ranks.add( member );
			
				// set the base rank to member.
			baseRank = member.getThis();
			
				// add the joined-to property so they can use commands in
				// clan as opposed to rank.
			member.setJoinedTo( this );
			
			PermissionList pl;
			
			pl = leader.getPermissionList();
			pl.allow( founder.getThis(), Container.addToAction );
			pl.allow( founder.getThis(), Container.removeFromAction );
			
			pl = member.getPermissionList();
			pl.allow( leader.getThis(), Container.addToAction );
			pl.allow( leader.getThis(), Container.removeFromAction );
			
				// set up the implies, founder implies leader,
				//                     leader implies member.
			
			founder.implies.add( leader );
			leader.implies.add( member );
			
			CommandList clanCommands = (CommandList) Factory.makeAtom( CommandList.class, "clanCommands" );
			clanCommands.setLimit( 20 );
			clanCommands.setTitle( "clan" );
			commandSets.add( clanCommands );
			
			commands = clanCommands.getThis();
			
			{
				CommandCategory refClan = (CommandCategory) Factory.makeAtom( key.CommandCategoryContainer.class );
				refClan.setKey( "clan" );
				clanCommands.add( refClan );
				
				CommandList clanClanCommands = refClan.getCommandList();
				clanClanCommands.setTitle( "clan" );
				
				Command cmds = (Command) Factory.makeAtom( key.commands.Alias.class, "commands" );
				cmds.setProperty( "command", "commands clan" );
				clanClanCommands.add( cmds );
				
				Command cwho = (Command) Factory.makeAtom( key.commands.ScapeWho.class, "who" );
				cwho.setProperty( "scapeFor", this );
				cwho.setProperty( "singular", "There is one clan member online" );
				cwho.setProperty( "footer", "You have %n clan members online" );
				cwho.setProperty( "empty", "No clan members on at the moment" );
				clanClanCommands.add( cwho );
				
					//  Communication, ick...
				
					//  clan say 
				
				ClanBroadcast csay = (ClanBroadcast) Factory.makeAtom( ClanBroadcast.class, "cl" );
				csay.setProperty( "broadcast", "(%c): %o says '^@%m^$'" );
				csay.setProperty( "broadcastQuestion", "(%c): %o asks '^@%m^$'" );
				csay.setProperty( "broadcastExclaim", "(%c): %o exclaims '^@%m^$'" );
				csay.setProperty( "feedback", "(%c): %o says '^@%m^$'" );
				csay.setProperty( "feedbackQuestion", "(%c): %o asks '^@%m^$'" );
				csay.setProperty( "feedbackExclaim", "(%c): %o exclaims '^@%m^$'" );
				csay.setScapeFor( this );
				clanClanCommands.add( csay );
				
					// clan emote 
				
				ClanBroadcast cemote = (ClanBroadcast) Factory.makeAtom( ClanBroadcast.class, "ce" );
				cemote.setProperty( "broadcast", "(%c): %o%s^@%m^$" );
				cemote.setProperty( "feedback", "(%c): %o%s^@%m^$" );
				cemote.setScapeFor( this );
				clanClanCommands.add( cemote );
				
					// clan think 

				ClanBroadcast cthink = (ClanBroadcast) Factory.makeAtom( ClanBroadcast.class, "ct" );
				cthink.setProperty( "broadcast", "(%c): %o thinks . o O ( ^@%m^$ )" );
				cthink.setProperty( "feedback", "(%c): %o thinks . o O ( ^@%m^$ )" );
				cthink.setScapeFor( this );
				clanClanCommands.add( cthink );
			
					// Now punctuation for the communication
				
				Command cPunctuation = (Command) Factory.makeAtom( key.commands.Punctuation.class, "c" );
				clanCommands.add( cPunctuation );
				
					// add the say, emote and think to c
				
				cPunctuation.setProperty( "say", csay );
				cPunctuation.setProperty( "emote", cemote );
				cPunctuation.setProperty( "think", cthink );
				
					// Add commands to the ranks, founders get generic 
					// grant style command named 'raise'
					// leader get accept, exile
					// member secede
				
					// code:	construct the new commandlist;
					//			set the rank's command list to created 
					//			create a new command
					//			put in in the list 
					//			make the founder the owner
				
				Command secede = (Command) Factory.makeAtom( key.commands.Secede.class );
				clanClanCommands.add( secede );
				
				clanClanCommands.sort();
			}
			
				// member
			
			CommandList memberCommands = (CommandList) Factory.makeAtom( CommandList.class, "member" );
			memberCommands.setLimit( 20 );
			commandSets.add( memberCommands );
			member.setProperty( "commands", memberCommands );
			
			{
				CommandCategory refClan = (CommandCategory) Factory.makeAtom( key.CommandCategoryContainer.class );
				refClan.setKey( "clan" );
				memberCommands.add( refClan );
				
				CommandList clanClanCommands = refClan.getCommandList();
				clanClanCommands.setTitle( "clan" );
				
				Command clanMotd = (Command) Factory.makeAtom( key.commands.clan.ClanMotd.class );
				clanMotd.setKey( "clanmotd" );
				clanClanCommands.add( clanMotd );
				
				clanClanCommands.sort();
			}
			
				// leader
			
			CommandList leaderCommands = (CommandList) Factory.makeAtom( CommandList.class, "leader" );
			leaderCommands.setLimit( 20 );
			commandSets.add( leaderCommands );
			leader.setProperty( "commands", leaderCommands );

			{
				CommandCategory refClan = (CommandCategory) Factory.makeAtom( key.CommandCategoryContainer.class );
				refClan.setKey( "clan" );
				leaderCommands.add( refClan );
				
				CommandList clanClanCommands = refClan.getCommandList();
				clanClanCommands.setTitle( "clan" );
				Command accept = (Command) Factory.makeAtom( key.commands.clan.Accept.class );
				clanClanCommands.add( accept );
				
				Command revoke = (Command) Factory.makeAtom( key.commands.clan.Revoke.class );
				clanClanCommands.add( revoke );
				
				clanClanCommands.sort();
			}
			
				// founders
			
			CommandList founderCommands = (CommandList) Factory.makeAtom( CommandList.class, "founder" );
			founderCommands.setLimit( 20 );
			commandSets.add( founderCommands );
			founder.setProperty( "commands", founderCommands );
			
			CommandCategoryContainer create = (CommandCategoryContainer) Factory.makeAtom( CommandCategoryContainer.class, "create" );
			founderCommands.add( create );
			
			Command rank = (Command) Factory.makeAtom( key.commands.clan.Create_rank.class, "rank" );
			create.getCommandList().add( rank );
			
			{
				CommandCategory refClan = (CommandCategory) Factory.makeAtom( key.CommandCategoryContainer.class );
				refClan.setKey( "clan" );
				founderCommands.add( refClan );
				
				CommandList clanClanCommands = refClan.getCommandList();
				clanClanCommands.setTitle( "clan" );
				
				Command refRank = (Command) Factory.makeAtom( key.commands.clan.RefRank.class );
				refRank.setKey( "rank" );
				clanClanCommands.add( refRank );
				
				Command add = (Command) Factory.makeAtom( key.commands.clan.Add.class );
				add.setKey( "add" );
				clanClanCommands.add( add );
				
				Command take = (Command) Factory.makeAtom( key.commands.clan.Take.class );
				clanClanCommands.add( take );
				 
				Command baserank = (Command) Factory.makeAtom( key.commands.clan.BaseRank.class );
				clanClanCommands.add( baserank );
				
				Command editmotd = (Command) Factory.makeAtom( key.commands.clan.EditMotd.class );
				clanClanCommands.add( editmotd );
				
				Command setHall = (Command) Factory.makeAtom( key.commands.clan.SetHall.class );
				clanClanCommands.add( setHall );
				
				Command dissolve = (Command) Factory.makeAtom( key.commands.clan.Dissolve.class );
				clanClanCommands.add( dissolve );
				
				Command implies = (Command) Factory.makeAtom( key.commands.clan.Implies.class );
				clanClanCommands.add( implies );
				
				Command notImplies = (Command) Factory.makeAtom( key.commands.clan.NotImplies.class );
				notImplies.setKey( "notimplies" );
				clanClanCommands.add( notImplies );
				
				//Command clanland = (Command) Factory.makeAtom( key.commands.clan.RefClanLand.class );
				//clanland.setKey( "clanland" );
				//clanClanCommands.add( clanland );
				
				Command structure = (Command) Factory.makeAtom( key.commands.clan.Structure.class );
				structure.setKey( "structure" );
				clanClanCommands.add( structure );
				
				clanClanCommands.sort();
			}
			
				// sort those command sets!
			
			memberCommands.sort();
			leaderCommands.sort();
			founderCommands.sort();
		}
		catch( BadKeyException e )
		{
			throw new UnexpectedResult( "static string should contain only alphabetic characters" );
		}
		catch( NonUniqueKeyException e )
		{
			throw new UnexpectedResult( "There is already something of that name here" );
		}
	}
	
	public Player getFounder()
	{
		try
		{
			return( (Player) founder.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			founder = Reference.EMPTY;
			Log.log( "clan", "Clan " + getName() + " no longer has a valid founder" );
			return( null );
		}
		catch( ClassCastException e )
		{
			founder = Reference.EMPTY;
			Log.log( "clan", "Clan " + getName() + " no longer has a valid founder" );
			return( null );
		}
	}
	
	public void setFounder( Player p )
	{
		if( founder == Reference.EMPTY )
		{
			founder = p.getThis();
			Container c = (Container) ranks.getElement( "founder" );
			
			if( c != null )
			{
				try
				{
					c.add( p );
				}
				catch( Exception e )
				{
					Log.error( "during clan setFounder()", e );
				}
			}
		}
		else
			throw new AccessViolationException( this, "founder already set in clan " + getName() + ".  Cannot set the founder to be " + p.getName() );
	}
	
	public String getTitledName()
	{
		return( getName() + title );
	}
	
	public String getPrefix()
	{
		return( prefix );
	}
	
	public int countMembers()
	{
		int temp = 0;
		
		for( Enumeration e = ranks.elements(); e.hasMoreElements(); )
			temp += ((Rank)e.nextElement()).count();
		
		return temp;
	}
	
	public String getCreationDate()
	{
		if( loginStats != null && loginStats.firstConnection != null )
			return( loginStats.firstConnection.toString() );
		else
			return( "<brand new clan>" );
	}
	
	public void linkPlayer( Player p ) throws NonUniqueKeyException,BadKeyException
	{
		super.linkPlayer( p );
		
		loginStats.startConnection();
	}
	
	public void unlinkPlayer( Player p ) throws NonUniqueKeyException,java.util.NoSuchElementException,BadKeyException
	{
		super.unlinkPlayer( p );
		
		loginStats.endConnection();
	}
	
	public void sendFingerScreen( InteractiveConnection ic )
	{
		ic.send( new HeadingParagraph( "^@" + getPrefix() + " " + getName() + "^$" ) );
		
		ic.send( getName() + " was created on " + getCreationDate() );
		
		if( getFounder() != null )
			ic.send( "Founder: " + getFounder().getName() );
		else
			ic.send( "Founder: None <^RWARNING^- - this clan may be deleted at any time>" );
		
		int c = countMembers();
		ic.send( "There " + Grammar.isAre(c) + " a total of " + c + " member" + ( (c == 1) ? "" : "s") + " in Clan " + getName() + "." );
		int i = numberPlayers();
		ic.send( "(There " + Grammar.isAreCount(i) + " member" + ( (i == 1) ? "" : "s") + " currently online)" );
		
			// perhaps some permission checking?
		{
			String wp = homepage.get();
			
			if( wp != null )
				ic.send( "The Clan web page is: ^<" + wp + "^>" );
			
			Room ch = (Room) hall.get();
			
			if( ch != null )
				ic.send( "Clan Hall is [" + ch.getContainedId() + "]");
		}
		
		ic.sendLine();
	}
}
