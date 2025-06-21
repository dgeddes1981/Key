<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>


<tr colspan=2>
<td bgcolor="#999999" colspan="2" height=20>
<center>

<font face="helvetica" size="2">

<%
	if( session == null )
	{
		%>
		te0 will not work correctly using this browser.
		<%
	}
	else
	{
		%>
			
		<a href="/te0/index.jsp">Main</a>
			&nbsp; &nbsp;
		
		<a href="/te0/articles.jsp">Articles</a>
			&nbsp; &nbsp;
		
		<a href="/te0/news.jsp">News</a>
			&nbsp; &nbsp;
		
		<a href="/te0/mail.jsp">Mail</a>
			&nbsp; &nbsp;
			
		<a href="/te0/events.jsp">Events</a>
			&nbsp; &nbsp;
		
		<a href="/te0/people.jsp">People</a>
			&nbsp; &nbsp;
		
		<a href="/te0/clans.jsp">Clans</a>
			&nbsp; &nbsp;
		
		<a href="/te0/objects.jsp">Objects</a>
			&nbsp; &nbsp;
		
		<a href="/te0/help.jsp">Help</a>
			&nbsp; &nbsp;
					
		<a href="http://realm.progsoc.uts.edu.au/key">Key</a>
			&nbsp; &nbsp;
		
		<a href="mailto:te0@realm.progsoc.uts.edu.au">Contact</a>
		
		<%
	}
%>

</font>

</center>
</td>
</tr>