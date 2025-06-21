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
**  $Id: PublicRoom.java,v 1.6 2000/07/11 14:10:56 noble Exp $
**
**  $Log: PublicRoom.java,v $
**  Revision 1.6  2000/07/11 14:10:56  noble
**  PublicRoom's history no longer Grammar substituted
**
**  Revision 1.5  2000/02/22 17:16:19  subtle
**  bugfixes, added blockautohistory so it only triggers once
**
**  Revision 1.4  2000/02/22 15:42:51  subtle
**  bugfix on supplemental; bugfix on room history
**
**  Revision 1.3  2000/02/22 15:04:48  subtle
**  changed ff
**
**  Revision 1.2  2000/02/21 22:26:26  subtle
**  added Room history command, untested
**
**  
**
*/

package key;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.util.CircularBuffer;

public class PublicRoom extends Room
{
	private static final long serialVersionUID = -7359939118837376995L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( PublicRoom.class, String.class, "loginMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message that is shown when someone logs in to this room" ),
		AtomicElement.construct( PublicRoom.class, String.class, "logoutMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message that is shown when someone logs out from this room" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Room.STRUCTURE, ELEMENTS );
	
	public static final int MAX_HISTORY_LINES = 10;
	
	public String loginMessage;
	public String logoutMessage;
	
	/** a list of communication to/from this player */
	private transient CircularBuffer history;
	
	public PublicRoom()
	{
		init();
		loginMessage = "%o appears.";
		logoutMessage = "%o disappears.";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getLoginMessage()
	{
		return( loginMessage );
	}
	
	public String getLogoutMessage()
	{
		return( logoutMessage );
	}
	
	private void init()
	{
		if( history == null )
			history = new CircularBuffer( MAX_HISTORY_LINES );
	}

	public void loaded()
	{
		super.loaded();
		init();
	}
	
	public Enumeration getRoomHistory()
	{
		if( history != null )
		{
			return( history.elements() );
		}
		else
			init();
		
		return( key.util.EmptyEnumeration.EMPTY );
	}
	
	public void addHistoryLine( Effect s )
	{
		if( s instanceof key.effect.Broadcast )
		{
			history.addElement( s.getMessage( null ) );
		}
	}
	
	public void splash( Effect t, SuppressionList s )
	{
		super.splash( t, s );
		addHistoryLine( t );
	}
	
	public void splashExcept( Effect t, Splashable except, SuppressionList sl )
	{
		super.splashExcept( t, except, sl );
		addHistoryLine( t );
	}
	
	public Paragraph aspect( Player pl )
	{
		Paragraph parent = Grammar.substitute( super.aspect( pl ) );

		if( !pl.blockAutoHistory )
		{
			try
			{
				StringBuffer sb = new StringBuffer( "\n^hRoom history:\n" );
				int c=0;
				
				for( Enumeration e = getRoomHistory(); e.hasMoreElements(); )
				{
					sb.append( "\n? " );
					sb.append( (String) e.nextElement() );
					c++;
				}
				
				if( c == 0 )
					return( parent );
				
				pl.blockAutoHistory = true;
				MultiParagraph.Generator p = new MultiParagraph.Generator();
				p.append( parent );
				
				p.append( new TextParagraph( TextParagraph.LEFT, sb.toString() ) );
				sb = null;
				
				return( p.getParagraph() );
			}
			catch( Exception e )
			{
				Log.error( "during PublicRoom::linkPlayer", e );
				return( parent );
			}
		}
		else
			return( parent );
	}
}
