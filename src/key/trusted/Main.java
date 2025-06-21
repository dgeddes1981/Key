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
*/

package key;

import java.net.*;
import java.io.*;
import java.util.*;

public class Main
{
	/**
	  *  Set to true if you want to halve your disk space usage
	 */
	public static final boolean COMPRESS_DISK_FILES = false;
	
	public static final boolean COMPRESS_FS_FILES = false;
	
	/**
	 ** Use this to use the SQL classes.  (Not implemented yet!) 
	 */
	public static final boolean CONNECT_TO_DATABASE = false;
	
	/**
	 ** Use Robert Baruch's filesystem class.  (I had one of these
	 ** in C++ but skipped it for Java, yay, someone else has written
	 ** one for me!
	 */
	public static final boolean USE_FILESYSTEM = false;
	public static final boolean USE_DISTINCT = true;
	public static final boolean CLEANUP_DISTINCT_ON_FS_WRITE = false;
	
	/**
	  *  Set to true if you want key to do automatic ident
	  *  lookups
	 */
	public static final boolean SOCKET_IDENT_LOOKUP = true;
	
	private File db;
	private Key key;
	private Registry registry;
	private boolean adminMode;
	
	public Main( boolean create, boolean admin )
	{
		Key.creation = create;
		adminMode = admin;
	}
	
	public int doMenu( Menu m ) throws IOException
	{
		int choice;
		
		do
		{
			System.err.println( m.title );
			int i;
			
			for( i = 0; i < m.options.length; i++ )
			{
				MenuOption mo = m.options[i];
				System.err.println( Integer.toString( mo.number ) + "] " + mo.title );
			}
			
			System.err.println( "\n\n(enter displays this menu again)\nPlease select a menu option: " );
			
			do
			{
				try
				{
					String s = getLine( "[1-" + i + "]: " );
					if( s.length() > 0 )
					{
						choice = Integer.parseInt( s );

						if( choice > i || choice <= 0 )
						{
							System.err.println( "Your choice must be between 1 and " + i + "." );
							choice = -1;
						}
					}
					else
						choice = 0;
				}
				catch( NumberFormatException e )
				{
					System.err.println( "Please make your choice using digits only." );
					choice = -1;
				}
			} while( choice < 0 );
		} while( choice <= 0 );
		
		return( choice );
	}
	
	public boolean findDB()
	{
		db = new File( "database" );
		
		return( db.exists() );
	}
	
	public boolean load()
	{
		if( db.exists() )
		{
			Registry.baseDirectory = Key.confirmedDirectory( "distinct" );
			Log.bootLog( "Loading '" + db.getPath() + "'..." );
			registry = (Registry) Factory.loadObject( new Factory.PersistLocation( db, null ) );
			key = Key.instance();
			key.initFieldCache();
			return true;
		}
		
		return( false );
	}
	
	private Player getValidPlayer() throws IOException
	{
		Player p;
		
		do
		{
			String name = getValidName();
			p = (Player) key.getResidents().getElement( name );
			
			if( p != null )
				return( p );
			
			System.err.println( "\nCould not find player '" + name + "'.  Please enter a different name." );
		}
		while( true );
	}

	private File getValidFile() throws IOException
	{
		File f;
		
		do
		{
			String name = getLine( "Filename: " );
			f = new File( name );
			
			if( f.exists() )
				return( f );
			
			System.err.println( "\nCould not find file '" + f.getAbsolutePath() + "'.\nPlease enter a different filename." );
		}
		while( true );
	}
	
	public void addRevoke() throws IOException
	{
		Player p = getValidPlayer();
		
		if( p.isBeyond() )
		{
			p.maybeClearBeyond();
			System.err.println( p.getName() + " no longer has the beyond priviledge." );
		}
		else
		{
			p.maybeSetBeyond();
			System.err.println( p.getName() + " now has the beyond priviledge." );
		}
		
		p.sync();
	}
	
	public void doScript() throws IOException
	{
		System.err.println(
			"\n\n" +
			"Two things are required to execute a script.  First, a player\n" +
			"name to execute the script as, and second, the filename of the\n" +
			"script itself." );
		
		Player p = getValidPlayer();
		
		File file = getValidFile();
		FileIC fic = new FileIC( file );
		
		runScript( p, file, fic );
	}
	
	public void doConsoleLogin() throws IOException
	{
		System.err.println(
			"\n\n" +
			"Enter a player name to execute the script as." );
		
		Player p = getValidPlayer();
		
		System.err.println(
			"\nConsole Login for " + p.getName() + "...\n" +
			"Press EOF <CONTROL-D on unix or CONTROL-Z on dos> to close the connection.\n" );
		
		ConsoleIC cic = new ConsoleIC();
		
		try
		{
			p.connectTo( cic );
			cic.interactWith( p );
			p.run( cic );
			System.err.println( "Connection terminated." );
		}
		catch( BadKeyException e )
		{
			e.printStackTrace();
		}
		catch( NonUniqueKeyException e )
		{
			e.printStackTrace();
		}
	}
	
	public void runScript( Player p, File file, FileIC fic ) throws IOException
	{
		System.err.println( "\nWould you like to watch the output of the script scroll past?" );
		fic.setDisplay( yesNo() );
		
		System.err.println( "\nExecuting script '" + file.getAbsolutePath() + "'..." );
		
		try
		{
			p.connectTo( fic );
			fic.interactWith( p );
			p.run( fic );
			System.err.println( "Script complete." );
		}
		catch( BadKeyException e )
		{
			e.printStackTrace();
		}
		catch( NonUniqueKeyException e )
		{
			e.printStackTrace();
		}
		catch( NetworkException e )
		{
			System.out.println( e.getMessage() );
		}
		catch( Error e )
		{
			e.printStackTrace();
		}
	}
	
	public String getValidName() throws IOException
	{
		int len;
		boolean con;
		String name;
		
		System.err.println();
		
		do
		{
			con = false;
			name = getLine( "Administrator name: " );
			len = name.length();
			
			if( len < Player.MIN_NAME )
			{
				System.err.println( "Key requires that names are at least " + Player.MIN_NAME + " characters long." );
				con = true;
			}
			else if( len > Player.MAX_NAME )
			{
				System.err.println( "Key requires that names are less than " + Player.MAX_NAME + " characters long." );
				con = true;
			}
			else if( !Grammar.isStringCompletelyAlphabetical( name ) )
			{
				System.err.println( "Names must contain only alphabetical characters." );
				con = true;
			}
			else if( name.equalsIgnoreCase( "quit" ) )
			{
				System.err.println( "You wouldn't be able to log in with this name." );
				con = true;
			}
		} while( con );
		
		return( name );
	}
	
	public void create( Class cl ) throws IOException
	{
		Key.creation = true;
		adminMode = true;
		
		Registry.baseDirectory = Key.confirmedDirectory( "distinct" );
		
		registry = new Registry();
		key = (Key) Factory.makeAtom( cl );
		key.initFieldCache();
		
			//  set up the initial admin player
		System.err.println();
		System.err.println(
			"It is necessary to create a single administration account in order to\n" +
			"allow you to set up the server.  You will now be prompted for the name\n" +
			"and password of this account." );
		
		String name;
		String password;
		Player admin = null;
		
		do
		{
			name = getValidName();
			password = getLine( "Password (will echo): " );
			
			System.err.println();
			System.err.println( "Administrator name: " + name + ", password: " + password + ".  Okay?" );
		} while( !yesNo() );
		
		System.err.println();
		
		try
		{
			admin = (Player) Factory.makeAtom( Player.class, name );
			admin.setPassword( password );
			admin.maybeSetBeyond();
			admin.canSave = true;
			key.getResidents().add( admin );
			admin.sync();
			
			if( registry.getStorageTypeIndex( admin.index, admin.timestamp ) != Registry.STORAGE_LOADED )
			{
				System.err.println( "An error has occurred creating this character: it has not been sync'd." );
				System.exit( 1 );
			}
		}
		catch( BadKeyException e )
		{
			System.err.println( "The name you have selected is not appropriate: " + e.toString() );
			System.exit( 1 );
		}
		catch( NonUniqueKeyException e )
		{
			System.err.println( "The name you have selected is in use for something else: " + e.toString() );
			System.exit( 1 );
		}
		
		key.sync();
		System.err.println( "Created new Administrator '" + name + "'." );
		System.err.println();

		File setupDirectory = new File( "setup" );
		File basicSetup = new File( setupDirectory, "basic.ks" );
		File standardSetup = new File( setupDirectory, "standard.ks" );
		
		if( basicSetup.exists() && standardSetup.exists() )
		{
			int mo = doMenu( seedMenu );
			switch( mo )
			{
				case 1:
				case 2:
				case 3:
					File use;
					
					if( mo == 1 )
						use = basicSetup;
					else if( mo == 2 )
						use = standardSetup;
					else
					{
						System.err.println();
						use = getValidFile();
					}
					
					FileIC fic = new FileIC( use );
					runScript( admin, use, fic );
					break;
				case 4:
					return;
			}
		}
		else
		{
			if( basicSetup.exists() || standardSetup.exists() )
			{
				System.err.println(
				"Unfortunately, this distribution of key seems to be missing one of these\n" +
				"ready-made setup scripts.  They normally reside in the 'setup' directory\n" +
				"of the distribution, and are called 'standard.ks' and 'basic.ks'." );
			}
			else
			{
				System.err.println(
				"Unfortunately, Key is either not running in its distribution directory, or\n" +
				"these scripts have been inadvertently erased." );
			}
			
			System.err.println(
				"\n" +
				"This means that it is impossible at this time to automatically seed the\n" +
				"database: you will need to set it up by hand." ); 
		}
		
			//  this code doesn't get executed if they choose to go
			//  straight to the administration menu from option 4, above
		System.err.println( 
			"\n" +
			"We will now proceed to the administration menu.\n" +
			"Press any key to continue." );
		
		getLine( "" );
	}

	public void boot()
	{
		//System.setSecurityManager( Security.instance );
		key.start();
	}
	
	public void acceptControl( Class bootClass )
	{
		try
		{
			if( findDB() )
			{
				if( load() )
				{
					if( Key.instance().getClass() != bootClass && bootClass != Key.class )
					{
						System.err.println( "\n\nWARNING: Found database using " + Key.instance().getClass().getName() + ", which is not the specified " + bootClass.getName() + " (ignoring argument)\n\n" );
					}
					
					if( !adminMode )
					{
						boot();
						return;
					}
				}
			}
			else
			{
				System.err.println( "Database file '" + db.getPath() + "' does not exist." );
				System.err.println();
				System.err.println( "Would you like to store Key's files in '" + System.getProperty( "user.dir", "" ) + "'?" );
				
				if( !yesNo() )
				{
					System.err.println();
					System.err.println( "Please run this program from the directory you wish to store the files in." );
					
					String home = System.getProperty( "user.home", null );
					String sep = System.getProperty( "file.separator", null );
					if( home != null && sep != null )
						System.err.println( "For instance, you may wish to run this program from '" + home + sep + "key'." );
					return;
				}
				else
					System.err.println();
				
					//  at the moment, this is hard coded.  we might
					//  change it in the future.
				create( bootClass );
			}
			
			while( adminMode )
			{
				switch( doMenu( mainMenu ) )
				{
					case 1:
						adminMode = false;
						System.err.println( "\n" );
						break;
					case 2:
						addRevoke();
						break;
					case 3:
						doScript();
						break;
					case 4:
						doConsoleLogin();
						break;
					case 5:
						doLicense();
						break;
					case 6:
						System.err.println( "\n" );
						key.shutdown();
						System.exit( 0 );
						break;
				}
			}
			
			boot();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public boolean yesNo() throws IOException
	{
		String yn = getLine( "(y/N): " );

		if( yn.length() > 0 )
		{
			char c = yn.charAt( 0 );
			
			if( c == 'y' || c == 'Y' )
				return( true );
		}

		return false;
	}
	
	BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
	
	public String getLine( String prompt ) throws IOException
	{
		System.err.print( prompt );
		System.err.flush();
		return( br.readLine() );
	}
	
	public static void main( String args[] )
	{
		boolean admin = false;
		boolean creation = false;
		Class bootClass = Key.class;
		
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equalsIgnoreCase( "admin" ) )
				admin = true;
			else if( args[i].equalsIgnoreCase( "creation" ) )
				creation = true;
			else if( args[i].startsWith( "key." ) )
			{
					//  here we specify what type of class we wish
					//  to init for this Key.
				try
				{
					bootClass = Class.forName( args[i] );
				}
				catch( Exception e )
				{
					System.out.println( "Invalid initial class '" + args[i] + "': " + e.toString() );
					return;
				}
			}
			else
			{
				System.err.println( "Unknown argument '" + args[i] + "'" );
				System.err.println(
					"Available arguments:\n" +
					"\n" +
					"\tadmin   \tStarts the program in an interactive mode\n" +
					"\tswing   \tAllows program monitoring with a Swing GUI\n" +
					"\tcreation\tAllows the program to create directories as needed\n" );
				
				return;
			}
		}
		
		System.err.println( Key.copyright );

		if( admin )
		{
			System.err.println( "\nKey comes with ABSOLUTELY NO WARRANTY.\nThis is free software, and you are welcome to redistribute it\nunder certain conditions; type '5' for details.\n" );
		}
		
		System.err.println();
		
		System.getProperties().put( "THREADED_SERVER_THREAD_CLASS", "key.web.PoolThread" );
		
		try
		{
			Main m = new Main( creation, admin );
			m.acceptControl( bootClass );
		}
		catch( ExceptionInInitializerError e )
		{
			System.out.println( e.toString() + ":" );
			Throwable t = e.getException();
			t.printStackTrace();
		}
		catch( Exception e )
		{
			System.out.println( e.toString() );
			e.printStackTrace( System.out );
		}
	}

	private static final String license =
"		    GNU GENERAL PUBLIC LICENSE\n" +
"		       Version 2, June 1991\n" +
"\n" +
" Copyright (C) 1989, 1991 Free Software Foundation, Inc.\n" +
" 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n" +
" Everyone is permitted to copy and distribute verbatim copies\n" +
" of this license document, but changing it is not allowed.\n" +
"\n" +
"			    Preamble\n" +
"\n" +
"  The licenses for most software are designed to take away your\n" +
"freedom to share and change it.  By contrast, the GNU General Public\n" +
"License is intended to guarantee your freedom to share and change free\n" +
"software--to make sure the software is free for all its users.  This\n" +
"General Public License applies to most of the Free Software\n" +
"Foundation's software and to any other program whose authors commit to\n" +
"using it.  (Some other Free Software Foundation software is covered by\n" +
"the GNU Library General Public License instead.)  You can apply it to\n" +
"your programs, too.\n" +
"\n" +
"  When we speak of free software, we are referring to freedom, not\n" +
"price.  Our General Public Licenses are designed to make sure that you\n" +
"have the freedom to distribute copies of free software (and charge for\n" +
"this service if you wish), that you receive source code or can get it\n" +
"if you want it, that you can change the software or use pieces of it\n" +
"in new free programs; and that you know you can do these things.\n" +
"\n" +
"  To protect your rights, we need to make restrictions that forbid\n" +
"anyone to deny you these rights or to ask you to surrender the rights.\n" +
"These restrictions translate to certain responsibilities for you if you\n" +
"distribute copies of the software, or if you modify it.\n" +
"\n" +
"  For example, if you distribute copies of such a program, whether\n" +
"gratis or for a fee, you must give the recipients all the rights that\n" +
"you have.  You must make sure that they, too, receive or can get the\n" +
"source code.  And you must show them these terms so they know their\n" +
"rights.\n" +
"\n" +
"  We protect your rights with two steps: (1) copyright the software, and\n" +
"(2) offer you this license which gives you legal permission to copy,\n" +
"distribute and/or modify the software.\n" +
"\n" +
"  Also, for each author's protection and ours, we want to make certain\n" +
"that everyone understands that there is no warranty for this free\n" +
"software.  If the software is modified by someone else and passed on, we\n" +
"want its recipients to know that what they have is not the original, so\n" +
"that any problems introduced by others will not reflect on the original\n" +
"authors' reputations.\n" +
"\n" +
"  Finally, any free program is threatened constantly by software\n" +
"patents.  We wish to avoid the danger that redistributors of a free\n" +
"program will individually obtain patent licenses, in effect making the\n" +
"program proprietary.  To prevent this, we have made it clear that any\n" +
"patent must be licensed for everyone's free use or not licensed at all.\n" +
"\n" +
"  The precise terms and conditions for copying, distribution and\n" +
"modification follow.\n" +
"\n" +
"		    GNU GENERAL PUBLIC LICENSE\n" +
"   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n" +
"\n" +
"  0. This License applies to any program or other work which contains\n" +
"a notice placed by the copyright holder saying it may be distributed\n" +
"under the terms of this General Public License.  The \"Program\", below,\n" +
"refers to any such program or work, and a \"work based on the Program\"\n" +
"means either the Program or any derivative work under copyright law:\n" +
"that is to say, a work containing the Program or a portion of it,\n" +
"either verbatim or with modifications and/or translated into another\n" +
"language.  (Hereinafter, translation is included without limitation in\n" +
"the term \"modification\".)  Each licensee is addressed as \"you\".\n" +
"\n" +
"Activities other than copying, distribution and modification are not\n" +
"covered by this License; they are outside its scope.  The act of\n" +
"running the Program is not restricted, and the output from the Program\n" +
"is covered only if its contents constitute a work based on the\n" +
"Program (independent of having been made by running the Program).\n" +
"Whether that is true depends on what the Program does.\n" +
"\n" +
"  1. You may copy and distribute verbatim copies of the Program's\n" +
"source code as you receive it, in any medium, provided that you\n" +
"conspicuously and appropriately publish on each copy an appropriate\n" +
"copyright notice and disclaimer of warranty; keep intact all the\n" +
"notices that refer to this License and to the absence of any warranty;\n" +
"and give any other recipients of the Program a copy of this License\n" +
"along with the Program.\n" +
"\n" +
"You may charge a fee for the physical act of transferring a copy, and\n" +
"you may at your option offer warranty protection in exchange for a fee.\n" +
"\n" +
"  2. You may modify your copy or copies of the Program or any portion\n" +
"of it, thus forming a work based on the Program, and copy and\n" +
"distribute such modifications or work under the terms of Section 1\n" +
"above, provided that you also meet all of these conditions:\n" +
"\n" +
"    a) You must cause the modified files to carry prominent notices\n" +
"    stating that you changed the files and the date of any change.\n" +
"\n" +
"    b) You must cause any work that you distribute or publish, that in\n" +
"    whole or in part contains or is derived from the Program or any\n" +
"    part thereof, to be licensed as a whole at no charge to all third\n" +
"    parties under the terms of this License.\n" +
"\n" +
"    c) If the modified program normally reads commands interactively\n" +
"    when run, you must cause it, when started running for such\n" +
"    interactive use in the most ordinary way, to print or display an\n" +
"    announcement including an appropriate copyright notice and a\n" +
"    notice that there is no warranty (or else, saying that you provide\n" +
"    a warranty) and that users may redistribute the program under\n" +
"    these conditions, and telling the user how to view a copy of this\n" +
"    License.  (Exception: if the Program itself is interactive but\n" +
"    does not normally print such an announcement, your work based on\n" +
"    the Program is not required to print an announcement.)\n" +
"\n" +
"These requirements apply to the modified work as a whole.  If\n" +
"identifiable sections of that work are not derived from the Program,\n" +
"and can be reasonably considered independent and separate works in\n" +
"themselves, then this License, and its terms, do not apply to those\n" +
"sections when you distribute them as separate works.  But when you\n" +
"distribute the same sections as part of a whole which is a work based\n" +
"on the Program, the distribution of the whole must be on the terms of\n" +
"this License, whose permissions for other licensees extend to the\n" +
"entire whole, and thus to each and every part regardless of who wrote it.\n" +
"\n" +
"Thus, it is not the intent of this section to claim rights or contest\n" +
"your rights to work written entirely by you; rather, the intent is to\n" +
"exercise the right to control the distribution of derivative or\n" +
"collective works based on the Program.\n" +
"\n" +
"In addition, mere aggregation of another work not based on the Program\n" +
"with the Program (or with a work based on the Program) on a volume of\n" +
"a storage or distribution medium does not bring the other work under\n" +
"the scope of this License.\n" +
"\n" +
"  3. You may copy and distribute the Program (or a work based on it,\n" +
"under Section 2) in object code or executable form under the terms of\n" +
"Sections 1 and 2 above provided that you also do one of the following:\n" +
"\n" +
"    a) Accompany it with the complete corresponding machine-readable\n" +
"    source code, which must be distributed under the terms of Sections\n" +
"    1 and 2 above on a medium customarily used for software interchange; or,\n" +
"\n" +
"    b) Accompany it with a written offer, valid for at least three\n" +
"    years, to give any third party, for a charge no more than your\n" +
"    cost of physically performing source distribution, a complete\n" +
"    machine-readable copy of the corresponding source code, to be\n" +
"    distributed under the terms of Sections 1 and 2 above on a medium\n" +
"    customarily used for software interchange; or,\n" +
"\n" +
"    c) Accompany it with the information you received as to the offer\n" +
"    to distribute corresponding source code.  (This alternative is\n" +
"    allowed only for noncommercial distribution and only if you\n" +
"    received the program in object code or executable form with such\n" +
"    an offer, in accord with Subsection b above.)\n" +
"\n" +
"The source code for a work means the preferred form of the work for\n" +
"making modifications to it.  For an executable work, complete source\n" +
"code means all the source code for all modules it contains, plus any\n" +
"associated interface definition files, plus the scripts used to\n" +
"control compilation and installation of the executable.  However, as a\n" +
"special exception, the source code distributed need not include\n" +
"anything that is normally distributed (in either source or binary\n" +
"form) with the major components (compiler, kernel, and so on) of the\n" +
"operating system on which the executable runs, unless that component\n" +
"itself accompanies the executable.\n" +
"\n" +
"If distribution of executable or object code is made by offering\n" +
"access to copy from a designated place, then offering equivalent\n" +
"access to copy the source code from the same place counts as\n" +
"distribution of the source code, even though third parties are not\n" +
"compelled to copy the source along with the object code.\n" +
"\n" +
"  4. You may not copy, modify, sublicense, or distribute the Program\n" +
"except as expressly provided under this License.  Any attempt\n" +
"otherwise to copy, modify, sublicense or distribute the Program is\n" +
"void, and will automatically terminate your rights under this License.\n" +
"However, parties who have received copies, or rights, from you under\n" +
"this License will not have their licenses terminated so long as such\n" +
"parties remain in full compliance.\n" +
"\n" +
"  5. You are not required to accept this License, since you have not\n" +
"signed it.  However, nothing else grants you permission to modify or\n" +
"distribute the Program or its derivative works.  These actions are\n" +
"prohibited by law if you do not accept this License.  Therefore, by\n" +
"modifying or distributing the Program (or any work based on the\n" +
"Program), you indicate your acceptance of this License to do so, and\n" +
"all its terms and conditions for copying, distributing or modifying\n" +
"the Program or works based on it.\n" +
"\n" +
"  6. Each time you redistribute the Program (or any work based on the\n" +
"Program), the recipient automatically receives a license from the\n" +
"original licensor to copy, distribute or modify the Program subject to\n" +
"these terms and conditions.  You may not impose any further\n" +
"restrictions on the recipients' exercise of the rights granted herein.\n" +
"You are not responsible for enforcing compliance by third parties to\n" +
"this License.\n" +
"\n" +
"  7. If, as a consequence of a court judgment or allegation of patent\n" +
"infringement or for any other reason (not limited to patent issues),\n" +
"conditions are imposed on you (whether by court order, agreement or\n" +
"otherwise) that contradict the conditions of this License, they do not\n" +
"excuse you from the conditions of this License.  If you cannot\n" +
"distribute so as to satisfy simultaneously your obligations under this\n" +
"License and any other pertinent obligations, then as a consequence you\n" +
"may not distribute the Program at all.  For example, if a patent\n" +
"license would not permit royalty-free redistribution of the Program by\n" +
"all those who receive copies directly or indirectly through you, then\n" +
"the only way you could satisfy both it and this License would be to\n" +
"refrain entirely from distribution of the Program.\n" +
"\n" +
"If any portion of this section is held invalid or unenforceable under\n" +
"any particular circumstance, the balance of the section is intended to\n" +
"apply and the section as a whole is intended to apply in other\n" +
"circumstances.\n" +
"\n" +
"It is not the purpose of this section to induce you to infringe any\n" +
"patents or other property right claims or to contest validity of any\n" +
"such claims; this section has the sole purpose of protecting the\n" +
"integrity of the free software distribution system, which is\n" +
"implemented by public license practices.  Many people have made\n" +
"generous contributions to the wide range of software distributed\n" +
"through that system in reliance on consistent application of that\n" +
"system; it is up to the author/donor to decide if he or she is willing\n" +
"to distribute software through any other system and a licensee cannot\n" +
"impose that choice.\n" +
"\n" +
"This section is intended to make thoroughly clear what is believed to\n" +
"be a consequence of the rest of this License.\n" +
"\n" +
"  8. If the distribution and/or use of the Program is restricted in\n" +
"certain countries either by patents or by copyrighted interfaces, the\n" +
"original copyright holder who places the Program under this License\n" +
"may add an explicit geographical distribution limitation excluding\n" +
"those countries, so that distribution is permitted only in or among\n" +
"countries not thus excluded.  In such case, this License incorporates\n" +
"the limitation as if written in the body of this License.\n" +
"\n" +
"  9. The Free Software Foundation may publish revised and/or new versions\n" +
"of the General Public License from time to time.  Such new versions will\n" +
"be similar in spirit to the present version, but may differ in detail to\n" +
"address new problems or concerns.\n" +
"\n" +
"Each version is given a distinguishing version number.  If the Program\n" +
"specifies a version number of this License which applies to it and \"any\n" +
"later version\", you have the option of following the terms and conditions\n" +
"either of that version or of any later version published by the Free\n" +
"Software Foundation.  If the Program does not specify a version number of\n" +
"this License, you may choose any version ever published by the Free Software\n" +
"Foundation.\n" +
"\n" +
"  10. If you wish to incorporate parts of the Program into other free\n" +
"programs whose distribution conditions are different, write to the author\n" +
"to ask for permission.  For software which is copyrighted by the Free\n" +
"Software Foundation, write to the Free Software Foundation; we sometimes\n" +
"make exceptions for this.  Our decision will be guided by the two goals\n" +
"of preserving the free status of all derivatives of our free software and\n" +
"of promoting the sharing and reuse of software generally.\n" +
"\n" +
"			    NO WARRANTY\n" +
"\n" +
"  11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY\n" +
"FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW.  EXCEPT WHEN\n" +
"OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES\n" +
"PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED\n" +
"OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\n" +
"MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE ENTIRE RISK AS\n" +
"TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU.  SHOULD THE\n" +
"PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING,\n" +
"REPAIR OR CORRECTION.\n" +
"\n" +
"  12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING\n" +
"WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR\n" +
"REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES,\n" +
"INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING\n" +
"OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED\n" +
"TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY\n" +
"YOU OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER\n" +
"PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE\n" +
"POSSIBILITY OF SUCH DAMAGES.\n" +
"\n" +
"		     END OF TERMS AND CONDITIONS\n";

	public void doLicense()
	{
		StringTokenizer st = new StringTokenizer( license, "\n" );
		int c = 0;
		
		while( st.hasMoreTokens() )
		{
			if( (c % 22 == 0) && (c != 0) )
			{
				try
				{
					String l = getLine( "(q)uit, return for more: " );
					
					if( l.startsWith( "q" ) || l.startsWith( "Q" ) )
					{
						return;
					}
				}
				catch( IOException e )
				{
					return;
				}
			}
			
			c++;
			
			System.err.println( st.nextToken() );
		}
	}
	
	static MenuOption[] mainMenuOptions = 
	{
		new MenuOption( 1, "Start Key" ),
		new MenuOption( 2, "Add or Revoke Administrator priviledges" ),
		new MenuOption( 3, "Execute a KeyScript" ), 
		new MenuOption( 4, "Log in on the console" ),
		new MenuOption( 5, "View the license" ),
		new MenuOption( 6, "Exit" )
	};
	
	static Menu mainMenu = new Menu( "\n\nKey Administration Menu\n", mainMenuOptions );
	
	static MenuOption[] seedMenuOptions = 
	{
		new MenuOption( 1, "Basic (commands and ranks only)" ),
		new MenuOption( 2, "Standard (basic + sample room descriptions & help files)" ),
		new MenuOption( 3, "A custom script" ),
		new MenuOption( 4, "Exit to the administration menu" )
	};
	
	static Menu seedMenu = new Menu(
		"A brand new Key database is generally fairly sparse and unusable.  It is\n" +
		"strongly recommended that you seed the initial database using one of the\n" +
		"provided setup scripts.\n", seedMenuOptions );
}

class MenuOption
{
	String title;
	int number;

	MenuOption( int n, String t )
	{
		number = n;
		title = t;
	}
}

class Menu
{
	MenuOption[] options;
	String title;
	
	Menu( String t, MenuOption[] o )
	{
		title = t;
		options = o;
	}
}
