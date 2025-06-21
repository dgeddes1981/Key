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
**  $Id: Type.java,v 1.4 2000/01/27 07:38:19 noble Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.primitive.*;
import java.util.Hashtable;
import java.io.*;

/**
  *  A wrapper for a class, since we can't extend it.
  *
  *  If class is made un-final, change KeyInputStream to use instanceof
  *  instead of o.getClass() == Type.class
 */
public final class Type
implements Symbol, Serializable, key.io.Replaceable
{
	private static final long serialVersionUID = -3520340202727157134L;
	private static Hashtable registeredTypeNames = new Hashtable();
	private static Hashtable registeredTypeClasses = new Hashtable();
	
	Class type;
	String name;
	String className;
	
	public Type( Class c, String n )
	{
		type = c;
		name = n;
		className = null;
	}
	
	protected Type( String cn, String n )
	{
		className = cn;
		name = n;
		type = null;
	}
	
	private void writeObject( ObjectOutputStream oos ) throws IOException
	{
		if( className != null )
			oos.writeObject( className );
		else if( type != null )
			oos.writeObject( type.getName() );
		else
			oos.writeObject( null );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			className = (String) ois.readObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e );
		}
	}
	
	private final void resolve()
	{
		if( type == null && className != null )
		{
			try
			{
				type = Class.forName( className );

				if( type == null )
					Log.error( "key.Type: Class.forName( " + className + " ) returned null" );
				else
				{
					className = null;
					registeredTypeClasses.put( type, this );
				}
			}
			catch( java.lang.ClassNotFoundException e )
			{
				Log.error( "key.Type", e );
				throw new UnexpectedResult( e.toString() );
			}
		}
	}
	
	public String getName()
	{
		return( name );
	}
	
	public Object getKey()
	{
		return( name );
	}
	
	public final void setKey( Object key )
	{
		name = (String) key;
	}

	public final Object newInstance() throws InstantiationException,
	                                         IllegalAccessException
	{
		resolve();
		return( type.newInstance() );
	}

	public final boolean isInterface()
	{
		resolve();
		return( type.isInterface() );
	}

	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	/**
	  * returns true if Class 'is' is the same as class 't', or is
	  * a specialised version of it (or implements interface t)
	 */
	public boolean isA( Type t )
	{
		resolve();
		t.resolve();
		
		if( t == null )
			return( false );
		
		return( isA( type, t.type ) );
	}

	/**
	  * returns true if Class 'is' is the same as class 't', or is
	  * a specialised version of it (or implements interface t)
	 */
	public static boolean isA( Class is, Class t )
	{
		return( t.isAssignableFrom( is ) );
		/* --  JDK 1.0.2 recursive implementation
		if( t == null || is==null )
			return( false );
		
		if( is == t )
			return( true );
		else
		{
			if( t.isInterface() )
			{
				Class[] interfaces = is.getInterfaces();

				for( int i = 0; i < interfaces.length; i++ )
				{
					if( interfaces[i] == t )
						return( true );
				}
			}
			return( isA( is.getSuperclass(), t ) );
		}
		*/
	}
	
	/**
	  *  One day, this will be able to accept things like:
	  *  'subtle:myRoom', because I've uploaded the appropriate
	  *  class name.  As it is, this only works for *registered*
	  *  types.  One day we'll support loading them from a
	  *  players spot.
	 */
	public static Type forName( String s ) throws ClassNotFoundException
	{
		Type t = (Type) registeredTypeNames.get( s );
		if( t != null )
			return( t );
		else
			throw new ClassNotFoundException( s + " not found" );
	}
	
	public static Type typeOf( Object o )
	{
		return( typeFor( o.getClass() ) );
	}

	public static Type typeFor( Class c )
	{
		Type t = (Type) registeredTypeClasses.get( c );
		
		if( t == null )
		{
			String cn = c.getName();
			
			if( c.isArray() )
			{
					//  it's an array:  we could
					//  be more clever about how we
					//  determined the local name, but
					//  blah.
				return( new Type( cn, cn ) );
			}
			
			t = (Type) registeredTypeNames.get( cn );
			
			if( t == null )
			{
				t = newType( c, cn );
				//Log.debug( "Type", "found new type '" + cn + "'" );
			}
			else
			{
					//  manually resolve this type
				t.type = c;
				t.className = null;
			}
		}
		
		return( t );
	}
	
	/**
	  *  Never use newType when you mean forName().  It's possible
	  *  we'll prevent newType being executed by non-priviledged
	  *  people
	 */
	public static Type newType( String className, String localName )
	{
		Type t;
		
		t = (Type) registeredTypeNames.get( className );
		
		if( t != null )
		{
			t.name = localName;
			registeredTypeNames.put( localName, t );
		}
		else
		{
			t = new Type( className, localName );
			
			registeredTypeNames.put( localName, t );
			registeredTypeNames.put( className, t );
		}
		
		return( t );
	}
	
	/**
	  *  Never use newType when you mean forName().  It's possible
	  *  we'll prevent newType being executed by non-priviledged
	  *  people
	 */
	public static Type newType( Class c, String localName )
	{
		Type t = new Type( c, localName );
		
		registeredTypeNames.put( localName, t );
		registeredTypeNames.put( c.getName(), t );
		registeredTypeClasses.put( c, t );
		
		return( t );
	}
	
	public final Object getReplacement()
	{
		try
		{
			if( className != null )
				return( Type.forName( className ) );
		}
		catch( Exception e )
		{
			Log.error( "while resolving a null Type", e );
		}
		
		return( null );
	}
	
		//  a list of all the types that are needed by the program code
		//  keep in mind that if you use this in a static class, they
		//  may not have been initialised yet.  I wrote some really nice
		//  code in the C++ version that forced initialisation in a certain
		//  order, but it appears I'm not good enough at Java yet. - subtle
	
	public static final Type STRING = newType( "java.lang.String", "string" );
	public static final Type INTEGER = newType( "java.lang.Integer", "integer" );
	public static final Type INT = newType( Integer.TYPE, "int" );
	public static final Type REFERENCE = newType( "key.Reference", "reference" );
	public static final Type BOOLEAN = newType( "java.lang.Boolean", "boolean" );
	public static final Type ATOM = newType( "key.Atom", "atom" );
	public static final Type GENDER = newType( "key.primitive.Gender", "gender" );
	public static final Type STORABLE = newType( "key.Storable", "storable" );
	public static final Type COMMAND = newType( "key.Command", "command" );
	public static final Type COMMANDLIST = newType( "key.CommandList", "commandList" );
	public static final Type DATETIME = newType( "key.primitive.DateTime", "dateTime" );
	public static final Type DURATION = newType( "key.primitive.Duration", "duration" );
	public static final Type PASSWORD = newType( "key.primitive.Password", "password" );
	public static final Type TIMESTATISTICS = newType( "key.TimeStatistics", "timeStatistics" );
	public static final Type CONNECTIONSTATISTICS = newType( "key.ConnectionStatistics", "connectionStatistics" );
	public static final Type CLAN = newType( "key.Clan", "clan" );
	public static final Type FRIENDS = newType( "key.Friends", "friends" );
	public static final Type ROOM = newType( "key.Room", "room" );
	public static final Type PUBLICROOM = newType( "key.PublicRoom", "publicRoom" );
	public static final Type EXIT = newType( "key.Exit", "exit" );
	public static final Type MOVEMENT = newType( "key.effect.Movement", "movement" );
	public static final Type BLOCKING = newType( "key.effect.Blocking", "blocking" );
	public static final Type CONNECTION = newType( "key.effect.Connection", "connection" );
	public static final Type SHOUT = newType( "key.effect.Shout", "shouts" );
	public static final Type PAGE = newType( "key.effect.Page", "page" );
	//public static final Type OBJECT_DIRECT = newType( "key.effect.ObjectDirect", "objdirect" );
	public static final Type OBJECTS = newType( "key.effect.ObjectEffect", "objects" );
	//public static final Type OBJECT_ROOM = newType( "key.effect.ObjectRoom", "objects" );
	public static final Type CONTAINER = newType( "key.Container", "container" );
	public static final Type INTERACTIVECONNECTION = newType( "key.InteractiveConnection", "interactiveConnection" );
	public static final Type PLAYER = newType( "key.Player", "player" );
	public static final Type PARAGRAPH = newType( "key.Paragraph", "paragraph" );
	public static final Type LANDSCAPE = newType( "key.Landscape", "landscape" );
	public static final Type LETTER = newType( "key.Letter", "letter" );
	public static final Type MESSAGEBOX = newType( "key.MessageBox", "messageBox" );
	public static final Type IMPLICATIONS = newType( "key.Implications", "implications" );
	public static final Type GROUP = newType( "key.Group", "group" );
	public static final Type DAEMON = newType( "key.Daemon", "daemon" );
	public static final Type SCAPE = newType( "key.Scape", "scape" );
	public static final Type RANK = newType( "key.Rank", "rank" );
	public static final Type EMAILADDRESS = newType( "key.EmailAddress", "emailAddress" );
	public static final Type WEBPAGE = newType( "key.Webpage", "webpage" );
	public static final Type INFORMLIST = newType( "key.InformList", "informList" );
	public static final Type SUBNET = newType( "key.Subnet", "subnet" );
	
	public static final Type NUMBEREDCONTAINER = newType( "key.NumberedContainer", "NumberedContainer" );

	public static final Type SHORTCUTCOLLECTION = newType( "key.collections.ShortcutCollection", "shortcutCollection" );
	public static final Type NUMBEREDCOLLECTION = newType( "key.collections.NumberedCollection", "numberedCollection" );
	public static final Type SITECOLLECTION = newType( "key.collections.SiteCollection", "siteCollection" );
	public static final Type NETWORKCOLLECTION = newType( "key.collections.NetworkCollection", "networkCollection" );
	public static final Type STRINGKEYCOLLECTION = newType( "key.collections.StringKeyCollection", "stringKeyCollection" );
	public static final Type ALIASCOLLECTION = newType( "key.collections.AliasCollection", "aliasCollection" );
	public static final Type SITE = newType( "key.Site", "site" );
	public static final Type TARGETS = newType( "key.Targets", "targets" );
	public static final Type TARGETABLE = newType( "key.Targetable", "targetable" );
	public static final Type SCREEN = newType( "key.Screen", "screen" );
	public static final Type STRINGSET = newType( "key.StringSet", "StringSet" );
	public static final Type CONFIGURATION = newType( "key.config.Configuration", "configuration" );
	public static final Type MEMO = newType( "key.Memo", "memo" );
	public static final Type CLANACCEPT = newType( "key.commands.clan.Accept", "accept" );
	public static final Type CLANADD = newType( "key.commands.clan.Add", "add" );
	public static final Type CLANBASERANK = newType( "key.commands.clan.BaseRank", "baseRank" );
	public static final Type CLANMOTD = newType( "key.commands.clan.ClanMotd", "ClanMOTD" );
	public static final Type CLANDISSOLVE = newType( "key.commands.clan.Dissolve", "dissolve" );
	public static final Type CLANEDITMOTD = newType( "key.commands.clan.EditMotd", "editMOTD" );
	public static final Type CLANIMPLIES = newType( "key.commands.clan.Implies", "implies" );
	public static final Type CLANMAKE = newType( "key.commands.clan.Make", "make" );
	public static final Type CLANNOTIMPLIES = newType( "key.commands.clan.NotImplies", "notImplies" );
	public static final Type CLANREFCLAN = newType( "key.commands.clan.RefClan", "refClan" );
	public static final Type CLANREFCLANLAND = newType( "key.commands.clan.RefClanLand", "refClanLand" );
	public static final Type CLANREFRANK = newType( "key.commands.clan.RefRank", "refRank" );
	public static final Type CLANREVOKE = newType( "key.commands.clan.Revoke", "revoke" );
	public static final Type CLANSETHALL = newType( "key.commands.clan.SetHall", "setHall" );
	public static final Type CLANTAKE = newType( "key.commands.clan.Take", "take" );
	public static final Type THING = newType( "key.Thing", "thing" );
	public static final Type CHANNEL = newType( "key.Channel", "channel" );
	//public static final Type STAFF_CHANNEL = newType( "key.StaffChannel", "staffChannel" );
	//public static final Type ADMIN_CHANNEL = newType( "key.AdminChannel", "adminChannel" );
	
	public static final Type COUNCIL = newType( "key.talker.forest.Council", "council" );
	public static final Type ADVISOR = newType( "key.talker.forest.Advisors", "advisor" );
	public static final Type FEEDBACK = newType( "key.talker.Feedback", "feedback" );
	public static final Type NAMES = newType( "key.talker.Names", "names" );
	public static final Type MUSIC = newType( "key.effect.forest.Music", "music" );
	public static final Type ACTION = newType( "key.Action", "action" );
	
	public static final Type TE0ARTICLE = newType( "key.te0.Article", "te0Article" );
	//public static final Type TE0NEWS = newType( "key.talker.forest.te0.News", "te0News" );
	public static final Type TE0EDITOR = newType( "key.te0.te0Editor", "te0editor" );
	
	public static final Type SURVEY = newType( "key.te0.Survey", "Survey" );
	public static final Type SURVEYOPTION = newType( "key.te0.SurveyOption", "SurveyOption" );
	
	public static final Type TE0 = newType( "key.te0.te0", "te0" );
	public static final Type TE0CONTAINER = newType( "key.te0.te0Container", "te0Container" );

}
