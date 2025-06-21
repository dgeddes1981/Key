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

import key.primitive.*;
import key.collections.*;

import java.net.*;
import java.io.*;
import java.util.*;

import key.commands.Load;
import key.commands.Link;
import key.commands.Sync;
import key.commands.Quit;
import key.commands.Dump;

import key.config.BaseConfiguration;
import key.config.ConnectionConfiguration;

/**
  *  Key is the root object in the hierarchy.
  *
  *  This class is a little badly behaved, as far as containers
  *  go.  It starts out life as a normal container, so it can
  *  get its defaults loaded in.  These are other containers, such
  *  as 'players' and 'groups'.
  *
  *  Once these are in place, it switches to being a reference
  *  container, meaning that everything *else* that is placed in here
  *  is just a reference to something else.
  *
  *  This would be a problem, except that this class is never
  *  stored anywhere, it is generated everytime at bootup by
  *  its constructor.
 */
public class Key extends Scape
{
	private static final long serialVersionUID = 894671100741606045L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Key.class, TimeStatistics.class, "loginStats",
			AtomicElement.PUBLIC_FIELD,
			"the amount of time spent by people on the program" ),
		AtomicElement.construct( Key.class, TimeStatistics.class, "sinceBootStats",
			AtomicElement.PUBLIC_FIELD,
			"statistics since the program was last reset" ),
		AtomicElement.construct( Key.class, TimeStatistics.class, "bootStats",
			AtomicElement.PUBLIC_FIELD,
			"statistics about when the program is restarted" ),
		AtomicElement.construct( Key.class, Player.class, "anonymous",
			AtomicElement.PUBLIC_FIELD,
			"an anonymous user" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Scape.STRUCTURE, ELEMENTS );
	
	
	protected static final int DEFAULT_PORT = 2809;
	
	protected static Key reference = null;
	public static boolean DEBUG_MODE = true;
	
	protected transient LatentCache latencyCache = (LatentCache) Factory.makeAtom( LatentCache.class, "latencyCache" );
	protected transient Scheduler scheduler = (Scheduler) Factory.makeAtom( Scheduler.class, "scheduler" );
	protected transient Room voidRoom;
	protected transient Realm realm;
	protected transient Container ranks;
	protected transient Rank player;
	
	
		//  directories
	//protected transient File logsBasePath;
	//protected transient File clanLogsBasePath;
	
	protected transient Landscape locations;
	protected transient Container commandSets;
	protected transient Container groups;
	protected transient Container clans;
	protected transient Container var;
	protected transient BaseConfiguration config;
	protected transient Daemons daemons;
	protected transient Residents residents;
	protected transient Container online;
	protected transient Shortcuts shortcuts = (Shortcuts) Factory.makeAtom( Shortcuts.class, "shortcuts" );
	
		//  every time someone connects
		//  this is saved and keeps connection information
	//public final TimeStatistics loginStats = new TimeStatistics();
	public final TimeStatistics loginStats = (TimeStatistics) Factory.makeAtom( TimeStatistics.class, "loginStats" );
	
		//  this is reset at every startup
	//public final TimeStatistics sinceBootLoginStatistics = new TimeStatistics();
	public final TimeStatistics sinceBootStats = (TimeStatistics) Factory.makeAtom( TimeStatistics.class, "sinceBootStats" );
	
		//  this is for key boots - it can tell you the amount of time
		//  key has been online for, etc, etc
	//public final TimeStatistics bootStats = new TimeStatistics();
	public final TimeStatistics bootStats = (TimeStatistics) Factory.makeAtom( TimeStatistics.class, "bootStats" );
	
		/**  If blank, no-one is permitted to be anonymous */
	public Reference anonymous = Reference.EMPTY;
	
		/**  Indicates whether the program is still in the process of booting */
	private static boolean running = false;
	
		//  META: change to private
	protected static boolean creation = false;
	
	public static final String nullString = "$";
	public static final String unlinkedString = "$unlinked$ ";
	public static final char nullEntry = '$';
	
	/**
	  * The easiest way to get the top level
	  * object in the running system.
	 */
	public static Key instance()
	{
		return( reference );
	}
	
	public static boolean isRunning()
	{
		return( running );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	private void init()
	{
		reference = this;
		
		Log.logsBasePath = confirmedDirectory( "logs" );
		Log.clanLogsBasePath = confirmedDirectory( "logs/clans" );
		confirmedDirectory( "logs/exceptions" );
	}
	
	/**
	  *  Before calling start, have called "initFieldCache()", 
	  *  or problems will result.
	 */
	protected void start()
	{
		if( latencyCache == null )
			throw new UnexpectedResult( "initFieldCache() not called or not working" );
		
		startDaemons();
		Registry.instance.start();
		running = true;
		
		loaded();
		
			//  this should always be *last* in the system startup procedure
		bootStats.startConnection();
		Log.bootLog( "boot completed" );
	}
	
	public void setupPlayersCollection()
	{
		playergroup.setCollection( new ShortcutCollection() );
	}
	
	protected Key()
	{
		super( false );
		
		reference = this;
		setKey( "Key" );
		
		stopBeingTemporary();
		
		init();
		
		/*
		latencyCache = new LatentCache();
		scheduler = new Scheduler();
		shortcuts = new Shortcuts();
		*/
		
		Log.bootLog( "initialising default configuration..." );
		
		commandSets = new Container( "commandSets", Type.COMMANDLIST );
		
		try
		{
				//  the 'base' commandset is the hardcoded default
				//  that you recieve when you log in.  Almost certainly,
				//  the first thing you'll want to do is make a group
				//  for the core administrators and move all these
				//  cool commands (like shutdown) to there.
			CommandList base = new CommandList( "base" );
			commandSets.add( base );
			
			base.add( Factory.makeAtom( Load.class ) );
			base.add( Factory.makeAtom( Link.class ) );
			base.add( Factory.makeAtom( Sync.class ) );
			base.add( Factory.makeAtom( Quit.class ) );
			base.add( Factory.makeAtom( Dump.class ) );
			
			groups = (Container) Factory.makeAtom( Container.class, "groups" );
			groups.setConstraint( Type.GROUP );
			
			clans = (Container) Factory.makeAtom( Container.class, "clans" );
			clans.setConstraint( Type.CLAN );
			
			var = (Container) Factory.makeAtom( Container.class, "var" );
			var.setConstraint( Type.CONTAINER );
			
			config = (BaseConfiguration) Factory.makeAtom( BaseConfiguration.class );
			config.add( Factory.makeAtom( ConnectionConfiguration.class ) );
			
			online = (Container) Factory.makeAtom( Container.class, "online" );
			
			realm = (Realm) Factory.makeAtom( Realm.class, "realm" );
			add( realm );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "couldn't initialise defaults", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "couldn't initialise defaults", e );
		}
		
		try
		{
			ranks = (Container) Factory.makeAtom( Container.class, "ranks" );
			ranks.setConstraint( Type.RANK );
			add( ranks );
			
			player = (ReverseRank) Factory.makeAtom( ReverseRank.class, "everyone" );
			ranks.add( player );
			
			Log.bootLog( "set up default forest ranks" );
		}
		catch( NonUniqueKeyException e )
		{
			Log.debug( this, "couldn't fill forest ranks: " + e.toString() );
		}
		catch( BadKeyException e )
		{
			Log.debug( this, "bad forest ranks: " + e.toString() );
		}
		
		try
		{
			TextParagraph logo = new TextParagraph( TextParagraph.CENTERALIGNED,
				"\n\n" + 
    			"      j###t ########## ####   ####\n" + 
    			"     j###t  ########## ####   ####\n" + 
    			"    j###T              \"###L J###\"\n" + 
    			"######P'    ##########  #########\n" + 
    			"######k,    ##########   T######T\n" + 
    			"####~###L   ####\n" + 
    			"#### q###L  ##########   .#####\n" +
    			"####  \\###L ##########   #####\"\n\n" );
			
			Screen hs = (Screen) Factory.makeAtom( Screen.class, "logo" );
			hs.text = logo;
			online.add( hs );
			
			online.add( Factory.makeAtom( HelpContainer.class ) );
            online.add( Factory.makeAtom( StringSet.class, "reservedNames" ) );
            online.add( Factory.makeAtom( PatternStringSet.class, "bannedNames" ) );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "Couldn't initialise default online", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "Couldn't initialise default online", e );
		}

		daemons = (Daemons) Factory.makeAtom( Daemons.class );
			
		TelnetConnectPort connectSocket = null;
		
		try
		{
			connectSocket = (TelnetConnectPort) Factory.makeAtom( TelnetConnectPort.class, "telnetPort" );
			daemons.add( connectSocket );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "Couldn't initialise default daemons", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "Couldn't initialise default daemons", e );
		}

		residents = (Residents) Factory.makeAtom( Residents.class, "players" );
		residents.setConstraint( Type.PLAYER );
		
		locations = (Landscape) Factory.makeAtom( Landscape.class, "landscape" );
		
		try
		{
			voidRoom = (Room) Factory.makeAtom( Room.class, "void" );
			locations.add( voidRoom );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "Couldn't initialise default landscape", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "Couldn't initialise default landscape", e );
		}
		
			//  add,
		try
		{
			add( shortcuts );
			add( daemons );
			add( residents );
			add( locations );
			add( commandSets );
			add( groups );
			add( clans );
			add( config );
			add( online );
			add( var );
			
			add( latencyCache );
			add( scheduler );
			
			addTransient( new Me() );
			addTransient( new Here() );
			addTransient( new Context() );
			addTransient( new FriendsRef() );
			addTransient( new InformRef() );
			addTransient( new ClanRef() );
			
			locations.addTopLevel( shortcuts );
			groups.addTopLevel( shortcuts );
			clans.addTopLevel( shortcuts );
			residents.addTopLevel( shortcuts );
			realm.addTopLevel( shortcuts );
			ranks.addTopLevel( shortcuts );
		}
		catch( NonUniqueKeyException e )
		{
			Log.fatal( this, "couldn't add required global symbol" + e.toString() );
		}
		catch( BadKeyException e )
		{
			Log.fatal( this, "couldn't add required global symbol: " + e.toString() );
		}
	}
	
	private void addTransient( TransitAtom ta )
		throws NonUniqueKeyException, BadKeyException
	{
		add( ta );
		shortcuts.add( ta );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		init();
		
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		sinceBootStats.reset();
	}
	
	/**
	  *  For speed, Key caches the lookups for many of it's contained
	  *  atoms.  This method initialises these caches after Key has been
	  *  loaded.
	  * <P>
	  *  All the addReference()s are required to prevent these atoms from
	  *  being swapped out while we have pointers to them (which would invalidate
	  *  the pointers and screw up the whole system).
	 */
	protected void initFieldCache()
	{
		latencyCache = (LatentCache) getElement( "latencyCache" );
		latencyCache.addReference( this );
		scheduler = (Scheduler) getElement( "scheduler" );
		scheduler.addReference( this );
		shortcuts = (Shortcuts) getElement( "shortcuts" );
		shortcuts.addReference( this );
		
		realm = (Realm) getElement( "realm" );
		realm.addReference( this );
		ranks = (Container) getElement( "ranks" );
		ranks.addReference( this );
		player = (Rank) ranks.getElement( "everyone" );
		player.addReference( this );
		
		commandSets = (Container) getElement( "commandSets" );
		commandSets.addReference( this );
		groups = (Container) getElement( "groups" );
		groups.addReference( this );
		clans = (Container) getElement( "clans" );
		clans.addReference( this );
		config = (BaseConfiguration) getElement( "config" );
		((Container)config).addReference( this );
		online = (Container) getElement( "online" );
		online.addReference( this );
		daemons = (Daemons) getElement( "daemons" );
		daemons.addReference( this );
		residents = (Residents) getElement( "players" );
		residents.addReference( this );
		locations = (Landscape) getElement( "landscape" );
		locations.addReference( this );
		voidRoom = (Room) locations.getElement( "void" );
		voidRoom.addReference( this );
		var = (Container) getElement( "var" );
		var.addReference( this );
		
			//  the latency cache is needed before we're really
			//  running.
		if( !latencyCache.isAlive() )
		{
			Log.bootLog( "starting " + latencyCache.getName() );
			latencyCache.start();
		}
	}
	
	protected void startDaemons()
	{
		for( Enumeration e = daemons.elements(); e.hasMoreElements(); )
		{
			Animate daemon = (Animate) e.nextElement();
			
			if( !daemon.isAlive() )
			{
				Log.bootLog( "starting " + ((Atom)daemon).getName() );
				daemon.start();
			}
		}
		
			//  okay, lets start the system up
		if( !latencyCache.isAlive() )
		{
			Log.bootLog( "starting " + latencyCache.getName() );
			latencyCache.start();
		}
		
		if( !scheduler.isAlive() )
		{
			Log.bootLog( "starting " + scheduler.getName() );
			scheduler.start();
		}
	}
	
	/**
	  * Returns the landscape referenced by the supplied
	  * id.  eg 'realm.hill.top'
	 */
	public Landscape getLandscape( String match )
	{
		//Object a = locations.getSymbol( match );
		Object a = locations.getElement( match );
		if( a instanceof Landscape )
			return( (Landscape) a );
		else
			return( null );
	}
	
		//  the rank a new player gets put in
		//  almost should always be null, unless you're doing
		//  something really strange
	public Rank getInitialRank()
	{
		return( null );
	}
	
	public Room getConnectRoom( Player p )
	{
			//  if they had a last public
			//  location, use it.
		{
			Room r = p.getLastPublicRoom();
			
			if( r != null )
				return( r );
		}
		
			//  pick a random room for them from the
			//  entry rooms for realm.
		Landscape er = realm.getEntryRooms();
		int c = er.count();
		
		if( c == 0 )
			return( voidRoom );
		else
		{
			Random r = new Random();
			int ran = Math.abs( r.nextInt() ) % c;
			return( (Room) er.getElementAt( ran ) );
		}
	}
	
	public Container getOnline()
	{
		return( online );
	}

	public Container getClans()
	{
		return( clans );
	}
	
	/**
	  *  Override for each talker
	 */
	public Realm getDefaultRealm()
	{
		return( realm );
	}
	
		//  the rank a new player gets put in
	public Container getRanks()
	{
		return( ranks );
	}
	
	public BaseConfiguration getConfig()
	{
		return( config );
	}

	/**
	  *  overrides default so that timeStatistics can be updated
	 */
	public void linkPlayer( Player p ) throws NonUniqueKeyException,BadKeyException
	{
		super.linkPlayer( p );
		loginStats.startConnection();
		sinceBootStats.startConnection();
	}
	
	/**
	  *  overrides default so that timeStatistics can be updated
	 */
	public void unlinkPlayer( Player p ) throws NonUniqueKeyException,BadKeyException
	{
		super.unlinkPlayer( p );
		loginStats.endConnection();
		sinceBootStats.endConnection();
	}

	public void addTopLevel( Container addTo )
	{
		throw new UnexpectedResult( "trying to addTopLevel Key to something" );
	}
	
	String getId_imp()
	{
		return( "" );
	}
	
	String getId_kickoff()
	{
		return( "/" );
	}
	
	/**
	  *  This routine is only _ever_ meant to be called from
	  *  the scheduler.
	 */
	public synchronized void shutdown()
	{
		Player p = Player.getCurrent();
		if( p != null && p.connected() )
		{
			p.send( "You struggle in vain against the pattern.  To destroy something, you must be outside of it." );
			return;
		}
		
		running = false;
		bootStats.endConnection();
		
		Log.bootLog( "shutdown initiated, disconnecting players" );
		
			//  disconnect all the players
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			p = (Player) e.nextElement();
			
			try
			{
				p.disconnect();
			}
			catch( Exception t )
			{
				Log.error( "during shutdown disconnect", t );
			}
		}
		
		Log.bootLog( "stopping daemons" );
		
			//  stop all running daemons
		for( Enumeration e = daemons.elements(); e.hasMoreElements(); )
		{
			Animate daemon = (Animate) e.nextElement();
			try
			{
				if( daemon.isAlive() )
					daemon.stop();
			}
			catch( Exception ex )
			{
				Log.error( ex );
			}
		}
		
		if( latencyCache.isAlive() )
			latencyCache.stop();
		
		Log.bootLog( "writing database..." );
		Log.sync();
		
		try
		{
			sync();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		Log.bootLog( "Normal system shutdown at " + new Date().toString() );
		Log.sync();
		System.exit( 0 );
	}
	
	public static final LatentCache getLatencyCache()
	{
		return( reference.latencyCache );
	}
	
	public static final Scheduler getScheduler()
	{
		return( reference.scheduler );
	}
	
	public static final File getLogsBasePath()
	{
		return( Log.logsBasePath );
	}

	public static final Container shortcuts()
	{
		return( reference.shortcuts );
	}
	
	/**
	  *  Use this to build a var container for a particular name
	 */
	protected Container ensureAndGetVarEntry( String name )
	{
		Container c = (Container) var.getElement( name );
		
		if( c != null )
			return c;
		
		c = new Container( false, name, new NoKeyCollection() );
		
		try
		{
			var.addInternal( c.getThis() );
		}
		catch( Exception e )
		{
			throw new UnexpectedResult( e.toString() + " while adding unmatched container" );
		}
		
		return( c );
	}
	
	/**
	  *  Updates the system disk files so that
	  *  the changes to non-saved objects will
	  *  be saved.  (eg, which commands are
	  *  where, etc)
	 */
	public synchronized void sync()
	{
		File temp = new File( "database" );
		Factory.storeObject( Registry.instance, new Factory.PersistLocation( temp, null ) );
	}
	
	public Residents getResidents()
	{
		return( residents );
	}
	
	public Player getAnonymous()
	{
		try
		{
			return( (Player) anonymous.get() );
		}
		catch( OutOfDateReferenceException ex )
		{
			Log.error( "No anonymous!", ex );
			anonymous = Reference.EMPTY;
			return( null );
		}
	}
	
	public File confirmedFile( File basePath, String name )
	{
		File opened;

		opened = new File( basePath, name );
		
		if( opened.exists() )
		{
			if( !opened.canRead() )
				Log.fatal( this, "'" + opened.getPath() + "' is not readable" );
			if( !opened.canWrite() )
				Log.fatal( this, "'" + opened.getPath() + "' is not writeable" );
		}
		else if( !creation )
			Log.fatal( this, "'" + opened.getAbsolutePath() + "' does not exist.  Use the 'creation' command line argument to allow Key to automatically generate paths." );
	
		return( opened );
	}

	public static File confirmedDirectory( String path )
	{
		File opened;

		opened = new File( path );
		
		if( !opened.exists() )
		{
			if( creation )
			{
				if( opened.mkdir() )
					Log.bootLog( "'" + path + "' created" );
				else
					Log.fatal( "Key", "'" + path + "' does not exist and cannot be created" );
			}
			else
				Log.fatal( "Key", "'" + opened.getAbsolutePath() + "' does not exist.  Use the 'creation' command line argument to allow Key to automatically generate paths." );
		}
		if( !opened.canRead() )
			Log.fatal( "Key", "'" + path + "' is not readable" );
		if( !opened.canWrite() )
			Log.fatal( "Key", "'" + path + "' is not writeable" );
		
		return( opened );
	}
	
		//  You may ONLY override this routine if you're ME!
		//  *grin*.
	public String getCopyright()
	{
		return( copyright );
	}
	
		//  you may not change this message.
	public static final String copyright =
		"\n\nKey:  \"building virtual worlds\", Copyright (C) 1997-2000 Paul Mclachlan.\nSee '^hhelp credits^-' for more information.";
	/**  Major release number */
	public static final int RELEASE = 1;
	
	/**  Minor version.  Eg:  Version release.version */
	public static final int VERSION = 0;
}
