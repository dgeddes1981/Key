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
import key.primitive.*;
import key.util.Trie;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

public class Who extends Command
{
	private static final long serialVersionUID = 967373524804005474L;
	public Who()
	{
		setKey( "who" );
		usage = "[<field>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Vector fields = null;
		
		if( args.hasMoreTokens() )
		{
			fields = new Vector( 5, 5 );
			
			AtomicStructure as = p.getDeclaredStructure();
			
			while( args.hasMoreTokens() )
			{
				String field = args.nextToken();
				AtomicElement f = as.getElement( field );
				
				if( f == null )
				{
					ic.sendFailure( "No such property '" + field + "'" );
					return;
				}
				
				if( fields.size() < 3 )
				{
					fields.addElement( f );
				}
				else
				{
					ic.sendFailure( "A maximum of three fields are permitted" );
					return;
				}
				
				/*
					--  This code used to use who
					--  like 'who s', or 'who z'
					--
					--  We don't do it that way anymore
				String pName = args.nextToken();
				Object t = Key.instance().getPlayer( pName );
				
				if( t == null )
				{
					ic.sendFeedback( "No players beginning with '" + pName + "' online." );
				}
				else if( t instanceof Atom )
				{
					Player o = (Player)t;
					
					o.sendExamineScreen( ic );
				}
				else if( t instanceof Trie )
					displayTable( ic, ((Trie)t).elements() );
				*/
			}
		}
		
		displayTable( ic, Key.instance().players(), fields );
	}
	
	public static final void displayTable( InteractiveConnection ic, Enumeration e, Vector fields )
	{
		Player scan;

		TableParagraph.Generator table = null;
		
		if( fields == null )
			table = new TableParagraph.Generator( defaultColumns );
		else
		{
			TableParagraph.Column[] columns = new TableParagraph.Column[ fields.size() + 1 ];
			columns[0] = new TableParagraph.Column( "name", Player.MAX_NAME );
			
			for( int i = 1; i <= fields.size(); i++ )
			{ 
				AtomicElement ae = (AtomicElement) fields.elementAt( i-1 );
				if( ae.getSpecial() instanceof StringLengthWrapper )
				{
					AtomicSpecial as = ae.getSpecial();
					columns[i] = new TableParagraph.Column( ae.getName(), ((StringLengthWrapper)as).getByteLimit() );
				}
				else
				{
					Class c = ae.getClassOf();
					if( c == Integer.class || c == Integer.TYPE )
						columns[i] = new TableParagraph.Column( ae.getName(), Math.max( 10, ae.getName().length() ) );
					else if( c == Gender.class )
						columns[i] = new TableParagraph.Column( ae.getName(), Math.max( 5, ae.getName().length() ) );
					else if( c == Boolean.class || c == Boolean.TYPE )
						columns[i] = new TableParagraph.Column( ae.getName(), Math.max( 5, ae.getName().length() ) );
					else
						columns[i] = new TableParagraph.Column( ae.getName(), 40 );
				}
			}
			
			table = new TableParagraph.Generator( columns );
		}
		
		DateTime now = new DateTime();
		
		while( e.hasMoreElements() )
		{
			String rowContents[] = new String[ table.countColumns() ];
			scan = (Player)e.nextElement();
			
				//  the players name
			rowContents[0] = (String)scan.getName();
			
			if( fields == null )
			{
				try
				{
						//  set up the players location...
					if( scan.isHiding() )
						rowContents[1] = "[hiding]";
					else
					{
						Room current = scan.getLocation();
						
						if( current == null )
							rowContents[1] = "no-where";
						else if( current == scan.getHome() )
							rowContents[1] = "@home";
						else
						{
							Object o = current.getParent();
							
							if( o != null && !(o instanceof Player) )
								rowContents[1] = current.getName();
						}
					}
					
						//  the clan
					Clan tmpClan = scan.getClan();
					if( tmpClan != null )
						rowContents[2] = tmpClan.getName();
					else if( scan.isLiberated() )
						rowContents[2] = "no clan please";
					else
						rowContents[2] = "";
					
						//  the idle time
					rowContents[3] = scan.getIdle( now ).toShortString();
					
					rowContents[4] = scan.getRank();
				}
				catch( Exception ex )
				{
					rowContents[1] = "$-$";
					rowContents[2] = "$-$";
					rowContents[3] = "$-$";
					rowContents[4] = "$-$";
					Log.error( "during who scan", ex );
				}
			}
			else
			{
				for( int i = 0; i < fields.size(); i++ )
				{
					try
					{
						Object o = ((AtomicElement)fields.elementAt(i)).getValue( scan );
						if( o == null )
							rowContents[i+1] = "";
						else
							rowContents[i+1] = o.toString();
					}
					catch( Exception ex )
					{
						rowContents[i+1] = "-";
					}
				}
			}
			
				//  add the row
			table.appendRow( rowContents );
		}
		
		int numPlayers = Key.instance().numberPlayers();
		StringBuffer footer = new StringBuffer();
		footer.append( "There ");
		footer.append( Grammar.isAreCount( numPlayers ) );
		footer.append( " " );
		footer.append( Grammar.personPeople( numPlayers ) );
		footer.append( " online." );
		table.setFooter( footer.toString() );
		
		ic.send( table.getParagraph() );
	}
	
	public static final TableParagraph.Column[] defaultColumns = 
	{
		new TableParagraph.Column( "name", Player.MAX_NAME ),
		new TableParagraph.Column( "location", 10 ),
		new TableParagraph.Column( "clan", Clan.MAX_NAME ),
		new TableParagraph.Column( "idle", 5 ),
		new TableParagraph.Column( "rank", 8 )
	};
}
