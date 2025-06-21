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

import key.collections.ShortcutCollection;
import key.primitive.DateTime;
import key.primitive.Duration;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

/**
  *  Realm
 */
public class Realm extends Landscape
{
	private static final long serialVersionUID = -670816069729829015L;
	public static final AtomicElement[] ELEMENTS =
	{
			//  String getName();
		AtomicElement.construct( Realm.class, Landscape.class, "entryRooms",
			AtomicElement.PUBLIC_FIELD,
			"the rooms in this realm that may be logged into" ),
		AtomicElement.construct( Realm.class, NoKeyContainer.class, "objects",
			AtomicElement.PUBLIC_FIELD,
			"the objects for this realm, since rooms are reference containers only" ),
		AtomicElement.construct( Realm.class, MessageBox.class, "news",
			AtomicElement.PUBLIC_FIELD,
			"the news postings for this realm" ),
		AtomicElement.construct( Realm.class, String.class, "sessionTitle",
			AtomicElement.PUBLIC_FIELD,
			"the name of the session" ),
		AtomicElement.construct( Realm.class, DateTime.class, "sessionCanReset",
			AtomicElement.PUBLIC_FIELD,
			"the time the session was last set" ),
		AtomicElement.construct( Realm.class, Integer.TYPE, "sessionTimestamp",
			AtomicElement.PUBLIC_FIELD,
			"the session index" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Landscape.STRUCTURE, ELEMENTS );
	
	public final Landscape entryRooms = (Landscape) Factory.makeAtom( Landscape.class, "entryRooms" );
	public final NoKeyContainer objects = (NoKeyContainer) Factory.makeAtom( NoKeyContainer.class, "objects" );
	public final MessageBox news = (MessageBox) Factory.makeAtom( MessageBox.class, "news" );
	
	public transient String sessionTitle = "";
	public transient Reference sessionSetBy = Reference.EMPTY;
	public transient DateTime sessionCanReset;
	public transient int sessionTimestamp = 0;
	
	public Realm()
	{
		super( false );
		entryRooms.reference = true;
		
		news.getPermissionList().allow( Container.addToAction );
	}

	public String getSessionSetByName()
	{
		try
		{
			return( sessionSetBy.get().getName() );
		}
		catch( OutOfDateReferenceException e )
		{
			sessionSetBy = Reference.EMPTY;
		}
		catch( NullPointerException e )
		{
			sessionSetBy = Reference.EMPTY;
		}
		
		return( "<unknown>" );
	}
	
	public void setupPlayersCollection()
	{
		playergroup.setCollection( new ShortcutCollection() );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Landscape getEntryRooms()
	{
		return( entryRooms );
	}
	
	public void setSessionTitle( Player by, InteractiveConnection ic, String title )
	{
		DateTime now = new DateTime();
		
		if( sessionCanReset != null && !now.after( sessionCanReset ) && sessionSetBy.isValid() )
		{
			ic.sendFailure( "The session cannot be reset for another " + sessionCanReset.difference( now ).toString() );
			return;
		}
		
		sessionCanReset = DateTime.nowPlus( new Duration( 5 * 60 * 1000 ) );
		sessionSetBy = by.getThis();
		sessionTitle = title;
		sessionTimestamp++;
		
		(new key.effect.Shout( Key.instance(), by, by.getName() + " sets the session to '" + sessionTitle + "'", "You set the session to '" + sessionTitle + "'" )).cause();
	}
}
