package key.web.servlets;

import com.mortbay.Servlets.*;
import com.mortbay.Base.*;
import com.mortbay.HTML.*;
import com.mortbay.HTTP.*;

import key.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Profile extends HttpServlet
{
    String pageType;
	
    public void init(ServletConfig config) throws ServletException
    {
		super.init(config);
    }
	
    public void doPost( HttpServletRequest request, HttpServletResponse response ) 
		throws ServletException, IOException
    {
		doGet( request,response );
    }
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException
    {
		Page page = Page.getPage( "key.web.LaF", request, response );
		response.setContentType("text/html");
		OutputStream out = response.getOutputStream();
		PrintWriter pout = new PrintWriter(out);
		
		String playerName = request.getParameter( "player" );
		if( playerName == null || playerName.length() <= 0 )
		{
			page.title( "Profile - Error" );
			page.add( "The 'player' parameter must be specified in order to view a profile.<P>" );
			page.write( pout );
			pout.flush();
			return;
		}
		
			//  This screen shows a profile of a player
		Player p = (Player) Key.instance().getResidents().getExactElement( playerName );
		if( p == null )
		{
			page.title( "Profile - Error" );
			page.add( "Cannot find player '" + playerName + "'<P>" );
			page.write( pout );
			pout.flush();
			return;
		}
		
			//  Okay, we've got a player, output some statistics
		page.title( "Profile of " + p.getName() );
		page.add( p.getPrefix() + p.getTitledName() + "<P>" );
		page.write( pout );
		pout.flush();
    }
}
