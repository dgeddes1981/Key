<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>
<%@ import="key.web.*" %>
<%@ import="java.util.Date" %>
<%@ import="key.primitive.DateTime" %>

<%
	if( session == null )
	{
		%>
		Could not create session.
		<BR>
		<%
	}
	else
	{
	%>
		<table>
		<form method="post" action="<%=request.getRequestURI()%>">
		  <tr>
			<td width=140>		  
	<%	
		String button = null;
		DateTime thedate = new DateTime( (new Date()).getTime() );
		
		String action = request.getParameter( "stype" );
		
		if( action != null && action.equalsIgnoreCase( "logout" ) )
		{
			session.invalidate();
			session = request.getSession( true );  // ensure there is always a valid session (empty or not)
		}

		String uName = (String) session.getValue( "name" );
		
		if( uName == null || uName.length() == 0 )
		{
			String supp = request.getParameter( "name" );
			
			if( supp == null || supp.length()== 0 )
			{
				button = "Login";
			
				%>
					<TABLE cellpadding=0 cellspacing=0 border=0>
					  <TR>
						<TD><font color="#FCFEB8" size=-1>Name: </font></TD><TD><INPUT size=9 maxlength=16 name="name"></TD>
					  </TR>
					  <TR>
						<TD><font color="#FCFEB8" size=-1>Password: </font></TD><TD><INPUT size=9 maxlength=16 type=password name="password"></TD>
					  </tr>
					</TABLE>
				<%
			}
			else
			{
				button = "Logout";
			
				String password = request.getParameter( "password" );
				
					//  verify this name
				int i = JSPUtils.authenticateName( supp, password );
				
				if( i > 0 )
				{
					session.putValue( "name", supp );
					session.putValue( "password", password );
					te0.login( JSPUtils.getPlayer( supp ) );
					
					%>
						<TABLE cellpadding=0 cellspacing=0 border=0>
						  <TR>
							<TD><font color=blue size=-1>Name: </font></td><td><font color="#FCFEB8"><%=JSPUtils.getPlayer( supp ).getName()%></font></TD>
						  </TR>
						  <TR>
							<TD><font color=blue size=-1>From: </font></td><td><font color="#FCFEB8" size=-1><%=request.getRemoteAddr()%></font></TD>
						  </tr>
						  <TR>
							<TD><font color=blue size=-1>Date: </font></td><td><font color="#FCFEB8" size=-1><%=thedate.toShortString( JSPUtils.getPlayer( supp ) )%></font></TD>
						  </tr>						  
						</TABLE>
					<%
				}
				else
				{
					button = "Login";
					if( i == -1 )
					{
						%>
							<center><FONT color=red size=-1>Incorrect password.</FONT><br>&nbsp;<br>
							<A HREF="/te0/emailPassword.jsp?name=<%=supp%>"><font color="#FCFEB8" size=-1>Mail me my Password</font></A></center>
						<%
					}
					else if( i == -2 )
					{
						%>
							<center><FONT color=red size=-1>Character not found.</FONT><br>&nbsp;<br>
							<A HREF="/te0/register.jsp?name=<%=supp%>"><font color="#FCFEB8">Register Me</font></A></center>
						<%
					}
					else
					{
						%>
							<center><FONT color=red size=-1>More information required.</FONT></center>
						<%
					}
				}
			}
		}
		else
		{
			button = "Logout";
		
			%>
				<TABLE cellpadding=0 cellspacing=0 border=0>
				  <TR>
					<TD><font color=blue size=-1>Name: </font></td><td><font color="#FCFEB8"><%=JSPUtils.getPlayer( uName ).getName()%></font></TD>
				  </TR>
				  <TR>
					<TD><font color=blue size=-1>From: </font></td><td><font color="#FCFEB8" size=-1><%=request.getRemoteAddr()%></font></TD>
				  </tr>
				  <TR>
					<TD><font color=blue size=-1>Date: </font></td><td><font color="#FCFEB8" size=-1><%=thedate.toShortString( JSPUtils.getPlayer( uName ) )%></font></TD>
				  </tr>					  
				</TABLE>
			<%
		}
	%>
			</td>
			<td align=center>
				<INPUT type=hidden name=stype value="<%=button%>">
				<INPUT type="image" src="/te0/images/<%=button%>.gif" border="0">
			</td>	
		  </tr>
		</form>
		</table>
		
	<%
	}
%>
