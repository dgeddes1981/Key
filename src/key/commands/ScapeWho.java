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
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class ScapeWho extends Command
{
	private static final long serialVersionUID = -7917221259650472207L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( ScapeWho.class, Scape.class, "scapeFor",
			AtomicElement.PUBLIC_FIELD,
			"the scape to list" ),
		AtomicElement.construct( ScapeWho.class, String.class, "empty",
			AtomicElement.PUBLIC_FIELD,
			"the message if there isn't anyone" ),
		AtomicElement.construct( ScapeWho.class, String.class, "singular",
			AtomicElement.PUBLIC_FIELD,
			"the footer if there is only one member" ),
		AtomicElement.construct( ScapeWho.class, String.class, "footer",
			AtomicElement.PUBLIC_FIELD,
			"the footer" ),
		AtomicElement.construct( ScapeWho.class, String.class, "doesntExist",
			AtomicElement.PUBLIC_FIELD,
			"the message if the scapeFor doesn't exist or is null" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Reference scapeFor = Key.instance().getThis();
	public String empty = "Nothing in the scape.";
	public String singular = "1 only";
	public String footer = "%n total";
	public String doesntExist = "That scape doesn't exist.";
	
	public static final char numberCode = 'n';
	
	public ScapeWho()
	{
		setKey( "scapeWho" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		MultiParagraph.Generator para = new MultiParagraph.Generator();
		
		para.append( LineParagraph.LINE );
		
		ColumnParagraph.Generator columns = new ColumnParagraph.Generator( Player.MAX_NAME, 2 );
		int numPlayers = 0;
		
		Scape sf = null;
		
		try
		{
			sf = (Scape) scapeFor.get();
		}
		catch( OutOfDateReferenceException e )
		{
			scapeFor = Reference.EMPTY;
			disable();
			Log.error( getId() + ".scapeFor", e );
			return;
		}
		catch( ClassCastException e )
		{
			scapeFor = Reference.EMPTY;
			disable();
			Log.error( getId() + ".scapeFor", e );
			return;
		}
		
		if( sf == null )
		{
			ic.sendFailure( doesntExist );
			return;
		}
		
		Type t = Type.typeOf( sf );
		
		for( Enumeration e = sf.players(); e.hasMoreElements(); )
		{
			Player o = (Player) e.nextElement();
			
			QualifierList.Immutable ql = o.getImmutableQualifierList();
			
			if( ql.check( t ) == Qualifiers.SUPPRESSION_CODE )
			{
				columns.appendEntry( "(" + o.getName() + ")" );
				
				numPlayers++;
				continue;
			}
			
			if( sf instanceof Friends )
			{
				Player friendsOf = (Player) ((Friends)sf).getParent();
				
				if( !o.remotePermissionCheck( friendsOf, Player.friendAction, false, false ) )
				{
					columns.appendEntry( "(" + o.getName() + ")" );
					numPlayers++;
					continue;
				}
			}
			
			columns.appendEntry( o.getName() );
			numPlayers++;
		}
		
		if( numPlayers == 0 )
		{
			ic.sendFeedback( empty );
		}
		else
		{
			para.append( columns.getParagraph() );
			
			if( numPlayers == 1 )
			{
				para.append( new HeadingParagraph( singular ) );
			}
			else
			{
				if( p != null )
				{
					p.putCode( numberCode, Integer.toString( numPlayers ) );
					para.append( new HeadingParagraph( Grammar.substitute( footer, p.getCodes() ) ) );
				}
				else
				{
					String[] codes = new String[26];
					
					try
					{
						codes[ numberCode - 'a' ] = Integer.toString( numPlayers );
					}
					catch( Exception e )
					{
						e.printStackTrace();
						Log.error( "During ScapeWho", e );
					}
					
					para.append( new HeadingParagraph( Grammar.substitute( footer, codes ) ) );
				}
			}
			
			ic.send( para.getParagraph() );
		}
	}
}
