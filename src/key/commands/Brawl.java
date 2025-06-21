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
**  $Id: Brawl.java,v 1.2 2000/05/05 17:06:15 noble Exp $
**
**  $Log: Brawl.java,v $
**  Revision 1.2  2000/05/05 17:06:15  noble
**  uses getOnlinePlayer
**
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  9Aug99      Noble        created this command
**
*/

package key.commands;

import key.*;

import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  This command requires a "brawl" Container in /online ,
  *  filled with Memos that represent each brawl action.
 */
public class Brawl extends Command
{
	public static final char originatorCode = 'o';
	public static final char targetCode = 't';
	public static final char orighisherCode = 'u';
	public static final char targhisherCode = 'h';	
	
	public Brawl()
	{
		setKey( "brawl" );
		usage = "<player>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{	

		if( args.hasMoreTokens() )
		{

			Player target = (Player) getOnlinePlayer( p, ic, args.nextToken() );
			
			if(target == null) return; //player not found (error already reported)

			if( !target.isLocation( p.getLocation() ) )
			{
				ic.send( "Perhaps you should get a voodoo doll?  (" + target.getName() + " isn't here)");
				return;
			}
			
			if( target == p )
			{
				ic.send( "You give yourself an uppercut." );
				return;
			}
			
			Scape to = (Scape) p.getLocation();
		
			String main = null;
		
		 	Container brawl = (Container) Key.instance().getOnline().getElement( "brawl" );
		 	
		 	if( brawl != null )
		 	{
		 		
			 	int max = (int) brawl.getCountContents();
				
				int choice = (int) (Math.random() * max);
				
				Memo attack = (Memo) brawl.getElementAt( choice );
				
				if( attack != null )
				{	
					main =  attack.value;
				}
				else
					main =  p.getName() + " wants to cause some trouble."; // no brawls set
			}
			else
				main =  p.getName() + " broods quietly."; // no brawl container
				
				
				
				
			p.putCode( originatorCode , p.getName() );
			p.putCode( targetCode , target.getName() );
			p.putCode( orighisherCode , p.hisHer() );
			p.putCode( targhisherCode , target.hisHer() );
			
			
			main = Grammar.substitute( main, p.getCodes() );
			
			new key.effect.Broadcast( to, p, main, main).cause();
			p.aboutToBroadcast( 0 ); //throttletype
				
				
										
		}
		else
			usage( ic);
	}
	
}
			
		