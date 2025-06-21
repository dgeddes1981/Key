<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>
<%
	String player = request.getParameter( "name" );
	if (player == null || player.length()==0)
	{
		String clan = request.getParameter( "clan" );
	
		if (clan == null || clan.length()==0)
		{
			response.sendRedirect( "/te0/people.jsp?action=finger" );
		}
		else
			response.sendRedirect( "/te0/clans.jsp?action=finger&clan=" + clan );
	}
	else
		response.sendRedirect( "/te0/people.jsp?action=finger&player=" + player );
%>