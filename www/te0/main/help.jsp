<%@ import="key.*" %>
<%@ import="java.util.Enumeration" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<p>

<%
try {
	if( session != null )
	{
	
	Container helpC = (Container) key.getOnline().getElement( "help" );
	
	if( helpC == null )
	{
		%>
		<p>
		No Help available at this time, sorry.
		<%
	}
	else
	{	
		String file = request.getParameter( "file" );
		
		if (file != null && file.length() > 0)
		{
			Screen help = (Screen) helpC.getElement( file );
	
			if( help == null )
			{
				%><center><font color="red">No help on '<%=file%>' could be found.</font></center><%
			}
			else
			{
				%><%=Converter.convert( help.aspect() )%><%
			}
					
		}
		else // list is the default
		{
		%>
			<center>
			<%
				int count = 0;
				for( Enumeration e = helpC.elements(); e.hasMoreElements(); )
				{
					Screen help = (Screen) e.nextElement();
					
					Object o = help.getKey();
					
					String aliases;
					if( o instanceof AliasListKey )
					{
						AliasListKey alk = (AliasListKey) o;
						if( alk.countSecondaries() > 0 )
							aliases = Grammar.commaSeperate( alk.secondaryKeys() );
						else
							aliases = "&nbsp;";
					}
					else
						aliases = "&nbsp;";					
					
					String auth = help.getAuthor();
					if( auth == null || auth.length() == 0 )
						auth = "&nbsp;";
					
					if( count == 0 )
					{
					%>
						<table width="98%" border="0" cellPadding="1" cellSpacing="1">
						  <tr bgcolor="#EEEEEE">
							<td align=center><b><font size=-1>NAME</font></b></td>
							<td align=center><b><font size=-1>ALIASES</font></b></td>
							<td align=center><b><font size=-1>AUTHOR</font></b></td>
						  </tr>								
					<%
					}
					count++;						
					%>
						  <tr>
							<td><a href="/te0/help.jsp?file=<%=help.getName()%>"><%=help.getName()%></a></td>
							<td><small><%=aliases%></small></td>
							<td><%=auth%></td>
						  </tr>
					<%						
				}
				
				if ( count != 0 )
				{
				%>
					</table>
				<%
				}
			%>
			<p>
			<% if( count == 1 ) { %>
				<i>Only one help file.</i>
			<% } else if( count == 0 ) { %>
				<i>No help files listed.</i>
			<% } else { %>
				<i><%=count%> help files.</i>
			<% } %>
			<p>				
			</center>
		<%
		}
	}
	}
}
catch( Exception e )
{
	Log.debug( "help.jsp", e.toString() );
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
