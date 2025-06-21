package key.web;

import com.mortbay.HTML.*;
import key.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This is an example of a simple Servlet
 */
public class CommandServlet extends HttpServlet
{
    String pageType;
    private key.Reference command = null;
	
    public void init( ServletConfig config ) throws ServletException
	{
		super.init( config );
		
		String path = config.getInitParameter( "command" );
		
		if( path != null )
		{
			Command a = (Command) new key.Search( path, key.Key.instance() ).result;
			
			if( a != null )
				command = a.getThis();
			else
				command = Reference.EMPTY;
		}
		else
			command = Reference.EMPTY;
	}
	
	public void doPost( HttpServletRequest sreq, HttpServletResponse sres ) throws ServletException, IOException
	{
		doGet(sreq,sres);
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		Command c = null;
		
		try
		{
			c = (Command) command.get();
		}
		catch( OutOfDateReferenceException e )
		{
			command = Reference.EMPTY;
		}
		
		/*  - this is a security risk for this applet - pick another
		if( c == null )
		{
			String path = request.getParameter( "cmd" );
			c = (Command) new key.Search( path, key.Key.instance() ).result;
		}
		*/
		
		if( c != null )
		{
			response.setContentType( "text/html" );
			OutputStream out = response.getOutputStream();
			final PrintWriter output = new PrintWriter( out );
			InteractiveConnection ic = null;
			
			try
			{
					//  if this is authenticated, this should
					//  should return the player we're auth'd
					//  with.  If it throws AccessViolation then
					//  we're not authed.  This command should
					//  refuse to run arbitrary commands without
					//  a authorised login.
				Player p = Player.getCurrent();
				
				if( p != null )
				{
					ic = new WebInteractiveConnection( output );
					
					StringTokenizer st = new StringTokenizer( "" );
					
					c.run( p, st, "", null, ic, null );
				}
				else
					output.write( "<B>This servlet won't run without a player-level authorisation</B>\n" );
			}
			catch( Exception e )
			{
				Log.error( e );
			}
			finally
			{
				ic.dispose();
			}
			
			output.write( "\n<BR><HR><CENTER>keyweb</CENTER>\n" );
			
			output.flush();
		}
	}
	
	public String getServletInfo()
	{
		return( "CommandServlet" );
	}
}
