<%@ import="key.*" %>
<%@ import="key.web.*" %>
<%@ import="key.primitive.DateTime" %>
<%@ import="java.util.Enumeration" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<p>

<%
try {
	if( session != null )
	{
		String uName = (String) session.getValue( "name" );
		
		if (uName == null || uName.length() == 0)  // not logged in
		{
		%>
			<center>You are not logged in.</center>
		<%
		}
		else
		{		
			String command = request.getParameter( "command" );
			if (command == null || command.equalsIgnoreCase(""))
			{
			%>	<center>
				<b>Note:</b> Im still experimenting with this, especially with regards to security and which commands work etc. So play around and let me know what does and does not work for you.
				<ul>
				<li>Broadcast's will only work if you are also logged into Forest
				<li>Scape commands are not available (even if you are logged in) - so you are pretty much left with the Base commandSet
				</ul>
				<p>
				What is your command:
				<p>
				<form action="" method=post>
					<input type=text name=command>&nbsp;<input type=submit value="Command">	
				</form>
				</center>
			<%
			}
			else
			{
			
				Player t = JSPUtils.getPlayer( uName );
				if( t != null )
				{
					t.command( command, new WebInteractiveConnection( out ), false );
				}		
	
			}
			
		}


	}
}
catch( Exception e )
{
	Log.debug( "command.jsp", e.toString() );
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

<p>
