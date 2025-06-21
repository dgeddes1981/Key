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
**  $Id: ViewLog.java,v 1.1.1.1 1999/10/07 19:58:30 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Jul97     subtle      created this command
**
*/

package key.commands;

import key.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class ViewLog extends Command
{
	private static final long serialVersionUID = 4263891712481759116L;
	
	public static final AtomicElement[] ELEMENTS =
	{
			//  if this field is specified, it is used as the log file.
			//  if this field ends with '/', what the player types as
			//  an argument is appended to this name to get the path
		AtomicElement.construct( ViewLog.class, String.class, "logName",
			AtomicElement.PUBLIC_FIELD,
			"the name of the log file" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String logName = "";
	
	public ViewLog()
	{
		setKey( "vlog" );
		usage = "<logname>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String thisLogname;
		
		boolean isnull = (logName == null);
		boolean islengthzero = false;
		boolean directory = false;
		
		if( !isnull )
		{
			islengthzero = (logName.length() == 0);
			
			if( !islengthzero )
				directory = (logName.endsWith( "/" ));
		}
		
		if( isnull || islengthzero || directory )
		{
			if( args.hasMoreTokens() )
			{
				String enteredName = args.nextToken();
				
				if( directory )
					thisLogname = logName + enteredName;
				else
					thisLogname = enteredName;
			}
			else
			{
				usage( ic );
				return;
			}
		}
		else
			thisLogname = logName;
		
		try
		{
			File logsBasePath = Key.instance().getLogsBasePath();
			File logFile = new File( logsBasePath, thisLogname );
			
			if( !logFile.exists() || !logFile.canRead() )
			{
				ic.sendError( "The log file '" + thisLogname + "' does not exist or is not readable." );
				return;
			}
			else
			{
					//  if this is a directory, check that the player
					//  hasn't done something clever (ie, ".." or "/") to get
					//  out of it.
				if( directory )
				{
					File basePath = new File( logsBasePath, logName );
					String lfc = logFile.getCanonicalPath();
					
						//  check to see if this is a valid path
					if( !lfc.startsWith( basePath.getCanonicalPath() ) )
					{
							//  this is not okay
						ic.sendError( "This is not a valid log file name." );
						return;
					}
				}
			}
			
			FileReader fr = Log.viewLog( thisLogname );
			BufferedReader br = new BufferedReader( fr );

				//  META:  this is temporary.  I want to eventually
				//  be able to reverse the lines and display some of them
				//  in reverse order, but this is way into the future.  It'd
				//  be nice if the pager was retro-active, too - allowing me
				//  to pump in a page of data, and have no more requested until
				//  they actually asked for more to be displayed.
			
			String l = br.readLine();
			//StringBuffer sb = new StringBuffer();
			
			while( l != null )
			{
				ic.sendFeedback( l );
				l = br.readLine();
				//sb.append( l );
				//sb.append( '\n' );
			}
			
			//ic.send( new TextParagraph( TextParagraph.LEFT, sb.toString() ) );
			
				//  make it easier to garbage collect all this quickly
			//sb.setLength( 0 );
			//sb = null;
		}
		catch( IOException e )
		{
			ic.sendFailure( "An error occurred trying to read the '" + thisLogname + "' log file" );
		}
	}
	
	public void usage( InteractiveConnection ic )
	{
		super.usage( ic );
		ic.send( "\nThe following logs are available to be viewed:" );
		File logsBasePath = Key.instance().getLogsBasePath();
		File path;
		
		if( logName == null || logName.length() == 0 )
			path = logsBasePath;
		else
			path = new File( logsBasePath, logName );
		
		String[] li = path.list();
		ic.send( Grammar.commaSeperate( li ) );
	}
}
