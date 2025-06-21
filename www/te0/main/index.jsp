<%@ import="key.*" %>
<%@ import="key.web.*" %>
<%@ import="key.primitive.DateTime" %>
<%@ import="java.util.Enumeration" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<br>
<center>

<%
try {
	if( session != null )
	{
		String uName = (String) session.getValue( "name" );
		
		if (uName != null && uName.length() != 0)  // logged in
		{
		%>
			<center><b>Welcome to te0, <%=JSPUtils.getPlayer( uName ).getName()%>.</b></center><p>
		<%
			int unread = JSPUtils.getPlayer( uName ).getMailbox().unread;
			
			if( unread > 0 )
			{
			%>
			<center>You have <font color=red><%=unread%></font> unread message<% if (unread != 1) {%>s<%}%> in your mailbox.</center><p>
			<%
			}
			
		%>
			<hr width="100%" color="black">
		<%
		}

		// frontpage spool
		
		Screen blurb = (Screen) te0.getProperty( "headline" );
		
		if( blurb != null && !(blurb.isEmpty()) )
		{
		%>
			<%=((TextParagraph) blurb.getParagraph()).getText()%>
		
			<hr width="100%" color="black">
		<%
		}

		// time
		
		%><p><%=Converter.convert( key.getName() + " has been running for " + key.bootStats.getTimeSinceConnection().toTruncString() )%><%

		int po = key.numberPlayers();
		%><i><%=Converter.convert( "There " + Grammar.isAre(po) + " " + po + " " + Grammar.personPeople(po) + " currently online." )%></i><%

		// page hits
		
		%>
		<p>te0 has had <b><%=te0.getHits()%></b> logins in total.
		<%
		
		// bottom of frontpage spool
		
		blurb = (Screen) te0.getProperty( "footline" );
		
		if( blurb != null && !(blurb.isEmpty()) )
		{
		%>
			<%=((TextParagraph) blurb.getParagraph()).getText()%>
		<%
		}		
		%>
		
		</center>
		<p>
		<%
	}
}
catch( Exception e )
{
	Log.debug( "index.jsp", e.toString() );
	%>
	<center>An error occured while generating this page.
	<p>
	Error: <font color=red><%=e.toString()%></font>
	<p>
	If the problem persists , please mail <a href="mailto:te0@realm.progsoc.uts.edu.au">te0@realm.progsoc.uts.edu.au</a> with a brief description of what went wrong.
	<p>
	Thanks , te0 management.
	<p>
	</center>
	<%
}
%>
