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

import java.io.*;
import java.util.Stack;

/**
 */
public class FileIC
extends DumbIC
{
	transient File from;
	transient LineNumberReader br;
	
	/**
	  *  The constructor must take a socket as its sole
	  *  argument
	  *
	  * @param s The socket that the connection is on
	 */
	public FileIC( File f )
	{
		super();
		
		from = f;
		
		try
		{
			br = new LineNumberReader( new FileReader( from ) );
		}
		catch( IOException e )
		{
			close();
			return;
		}
	}
	
	public String getName()
	{
		return( "console" );
	}
	
	public void setDisplay( boolean yn )
	{
		if( yn )
			setPrintStream( System.out );
		else
			setPrintStream( null );
	}
	
	public void stopBeingTemporary()
	{
		//  do nothing, we always want to be temporary
	}
	
	public final String getSiteName()
	{
		return( "local console" );
	}
	
	public final void discard()
	{
	}

	public synchronized void close()
	{
		super.close();
		
		if( br != null )
		{
			try
			{
				br.close();
			}
			catch( Exception t )
			{
			}
			finally
			{
				br = null;
				
				Registry.instance.delete( this );
			}
		}
	}
	
	/**
	  * return true if the socket is still open
	 */
	public boolean isConnected()
	{
		if( br != null )
			return true;
		else
			return false;
	}
	
	transient Stack parent_files = null;
	
	public String input( String prompt )
	{
		unIdle();
		
		try
		{
			String s = br.readLine();
			if( s != null )
			{
				s = s.trim();
				
				if( s.startsWith( "#" ) )
				{
					if( s.startsWith( "#include " ) )
					{
						s = s.substring( 9 );
						
						File newFile = null;
						
						String pf = from.getParent();
						
						if( pf != null )
						{
							File parent = new File( from.getParent() );
							newFile = new File( parent, s );
						}
						else
							newFile = new File( s );
						
						if( newFile.exists() )
						{
							LineNumberReader nbr = null;
							try
							{
								nbr = new LineNumberReader( new FileReader( newFile ) );
							}
							catch( IOException e )
							{
								System.err.println( "  error reading from " + newFile.getAbsolutePath() + ", skipping." );
								return( input( prompt ) );
							}
							
							System.err.println( "  including " + newFile.getAbsolutePath() + "..." );
							if( parent_files == null )
								parent_files = new Stack();
							
							parent_files.push( new RecursedFile( from, br ) );
							
							from = newFile;
							br = nbr;
							
							return( input( prompt ) );
						}
						else
							System.err.println( "  include file not found '" + newFile.getName() + "', skipping." );
						
						return( input( prompt ) );
					}
					else if( s.startsWith( "#:" ) )
					{
							//  it's just a comment otherwise, display it but
							//  don't send it.
						System.err.println( s.substring( 2 ) );
						return( input( prompt ) );
					}
					else
					{
							//  everything else is a comment that isn't
							//  printed
						return( input( prompt ) );
					}
				}
				else
					return( s );
			}
			else
			{
				if( parent_files == null || parent_files.empty() )
				{
					br.close();
					throw new IOException( "End Of File" );
				}
				else
				{
					RecursedFile rf = (RecursedFile) parent_files.pop();
					br.close();
					from = rf.file;
					br = rf.br;
					
					return( input( prompt ) );
				}
			}
		}
		catch( IOException e )
		{
			throw new NetworkException( e.getMessage() );
		}
	}
	
	public String hiddenInput( String prompt )
	{
		return( input( prompt ) );
	}
	
	class RecursedFile
	{
		File file;
		LineNumberReader br;

		public RecursedFile( File f, LineNumberReader b )
		{
			file = f;
			br = b;
		}
	}
}
