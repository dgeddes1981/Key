// ===========================================================================
// Copyright (c) 1996 Mort Bay Consulting Pty. Ltd. All rights reserved.
// $Id: Who.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
// ---------------------------------------------------------------------------

package key.web.servlets;

import com.mortbay.Servlets.*;
import com.mortbay.Base.*;
import com.mortbay.HTML.*;
import com.mortbay.HTTP.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import key.primitive.DateTime;
import key.primitive.Duration;
import key.primitive.Webpage;
import key.Clan;
import key.Player;
import key.Key;

public class Who extends HttpServlet
{
    String pageType;

    public void init(ServletConfig config) throws ServletException
    {
		super.init(config);
		
		pageType = getInitParameter(Page.PageType);
		if (pageType ==null)
			pageType=Page.getDefaultPageType();
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) 
		throws ServletException, IOException
    {
		doGet( request,response );
    }
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException
    {
		response.setContentType( "text/html" );
		OutputStream out = response.getOutputStream();
		PrintWriter pout = new PrintWriter(out);

		pout.print(
"<html><head><ittle>Forest Online</title>\n" +
"<body bgcolor=\"#FFFFFF\" background=\"http://realm.progsoc.uts.edu.au/forest/images/online.gif\" link=\"purple\" vlink=\"olive\" text=\"#191970\">\n" +
"<center>\n" +
"<A HREF=\"http://realm.progsoc.uts.edu.au/forest/\"><img src=\"http://realm.progsoc.uts.edu.au/forest/images/nhif.gif\" BORDER=0></A>\n" +
"</center>\n" +
"<font size=3 face=\"arial,helvetica\" color=\"#191970\">\n" );
		
		pout.print(
"<CENTER><B><FONT COLOR=\"#197019\">Those who are here...</FONT></B><P>\n" +
"<TABLE width=100% border=1><TR><TD><B>Name</B></TD><TD><B>Clan</B></TD><TD><B>Idle</B></TD><TD><B>Homepage</B></TD></TR>\n" );	
		
		int count = 0;
		for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
		{
			Player scan = (Player) e.nextElement();
			count++;
			
			{
				String name = scan.getName();
				pout.print( "<TR><TD><A href=\"/finger/" + name + "\">" + name + "</a></TD>\n" );
			}
			
			{
				Clan c = scan.getClan();
				if( c != null )
				{
					String n = c.getName();
					pout.print( "<TD><A href=\"/clanexamine/" + n + "\">" + n + "</A></TD>\n" );
				}
				else
					pout.print( "<TD>&nbsp;</TD>\n" );
			}
			
			{
				Duration d = scan.getIdle( new DateTime() );
				pout.print( "<TD>" + d.toShortString() + "</TD>\n" );
			}
			
			{
				String s = scan.getWebpage().get();
				if( s != null )
					pout.print( "<TD><A HREF=\"" + s + "\">" + s + "</A></TD>\n" );
				else
					pout.print( "<TD>&nbsp;</TD>\n" );
			}
			
			pout.print( "</TR>\n" );
		}
		
		if( count == 1 )
			pout.print( "</TABLE><P><I>Only one person online at the moment</I><P><BR><BR>\n" );
		else if( count == 0 )
			pout.print( "<CENTER><P><I>There aren't any people online at the moment</I></P></CENTER><BR><BR>\n" );
		else
			pout.print( "</TABLE><P><I>" + count + " people online at the moment</I><P><BR><BR>\n" );
		
		pout.flush();
    }
}
