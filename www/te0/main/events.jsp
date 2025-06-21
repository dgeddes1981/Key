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
		
		String event = request.getParameter( "event" );
		if (event == null) event = "";
			
		if ( event.equalsIgnoreCase( "quest" ) )
		{
			Screen quest = null;
		
			try {
				quest = (Screen) new Search( "/online/help/quest", null ).result;
			}
			catch( InvalidSearchException ise ) {}
				
			if( quest==null || quest.isEmpty() )
			{
			%>
			<center>There is no current Quest.</center>
			<%
			}
			else
			{
			%>
				<center>
				The current Quest was set by <a href="/finger?name=<%=quest.getAuthor()%>"><%=quest.getAuthor()%></a> , on <font color=red><%=quest.getModified().toShortString()%></font>.
				<p>
				<table border=1 bordercolor="black" width="95%" cellpadding=2 cellspacing=0><tr><td>
				<%=Converter.convert( quest.getParagraph() )%>
				</td></tr></table>
				</center>
			<%
			}
		}
		else if ( event.equalsIgnoreCase( "survey" ) )
		{
			if (uName == null || uName.length() == 0)  // not logged in
			{
			%>
				<center>You are not logged in.</center>
			<%
			}
			else
			{		
				Survey surv = te0.getCurrentSurvey();
					
				if( surv == null )
				{
				%>
				<center>There is no current Survey.</center>
				<%
				}
				else
				{
					String type = request.getParameter( "type" );
						
					if( type==null )
					{					
					%>
					<center>
					<b>"<%=Converter.convert( surv.getTopic() , false )%>"</b>
					<p>
					<%
					if( !surv.description.isEmpty() )
					{
					%>
						<%=Converter.convert( surv.description )%>
						<p>
					<%
					}
					%>
					<form method=post action="events.jsp">					
					<table>
					<%
						SurveyOption so = null;
						int count = 0;
							
						for( Enumeration e = surv.options(); e.hasMoreElements(); )
						{
							so = (SurveyOption) e.nextElement();
							%>
							<tr>
							  <td><input type=radio name=choice value="<%=count%>"></td>
							  <td><%=so.value%></td>
							</tr>
							<%
							count++;							
						}
					%>
					</table>
					<%
						if( count > 0 )
						{
						%>
						<p>
						<input type=hidden name=event value=survey>
						<input type=hidden name=type value=results>
						<input type=submit value="Vote">
						<%
						}					
					%>
					</form>
					<p>
					<a href="/te0/events.jsp?event=survey&type=results">Current Results</a> | <a href="events.jsp?event=survey&type=results&past=1">Last Survey Results</a>
					</center>
					<%					
					}
					else
					{
					
						// if they voted , set new vote
						
						String vote = request.getParameter( "choice" );
						if( vote != null )
						{
							if( surv.voteOnce && (surv.getPlayerVote( JSPUtils.getPlayer( uName ) ) >=0 ) )
							{
							%>
							<center><font color=red>You are not allowed to change your Vote for this Survey.</font><p></center>
							<%
							}
							else
							{
								try
								{
									int ch = Integer.parseInt( vote );
								
									surv.comment( JSPUtils.getPlayer( uName ) , ch );
								}
								catch( NumberFormatException ne )
								{
									// invalid vote , do nothing
								}
							}
						}
						
						// now show results - past results if specified
						
						String past = request.getParameter( "past" );
						if( past != null )						
						{
							try
							{
								int res = Integer.parseInt( past );
								
								surv = (Survey) ( (Container) te0.getApproved().getElement( "surveys" ) ).getElementAt( res );
								
								if( surv == null )
									surv = te0.getCurrentSurvey();
							}
							catch( NumberFormatException ne )
							{
								// surv remains as the current one
							}						
						}
						
						// display results
						
						%>
						<center>
						<b>"<%=Converter.convert( surv.getTopic() , false )%>"</b>
						<p>
						<table>
						<%
							SurveyOption so = null;
							int perc;
							int up = 0;
							
							int myvote = surv.getPlayerVote( JSPUtils.getPlayer( uName ) );
								
							for( Enumeration e = surv.options(); e.hasMoreElements(); )
							{
								so = (SurveyOption) e.nextElement();
								perc = (int) (so.getVotes() * 100.0 / surv.getTotalComments() );
								%>
								<tr>
								  <td><%=so.value%></td>
								  <td><font color=red><%=so.getVotes()%></font></td>
								  <td><I><small>(<%=perc%>%)</small></I><% if(up == myvote) {%> &#149;<%} %></td>
								</tr>
								<%
								up++;
							}
						%>
						<tr><td colspan=3>&nbsp;</td></tr>
						<tr><td colspan=2>&nbsp;</td><td>&#149; Your Vote</td></tr>
						</table>
						</center>
						<%						
						
					}					
				}

			}
		}
		else // session is the default
		{
			if (uName == null || uName.length() == 0)  // not logged in
			{
			%>
				<center>You are not logged in.</center>
			<%
			}
			else
			{		
		
				String title = request.getParameter( "title" );
				
				if ( title != null )
				{
					if ( title.length() > 0 && title.length() <= 60 )
					{
						if ( realm.sessionCanReset == null || (new DateTime()).after( realm.sessionCanReset ) )
						{
							realm.setSessionTitle( JSPUtils.getPlayer( uName ), null, title );
						}
					}
				}
			
				String sess = realm.sessionTitle;
				
				boolean canset = false;
				
				if( sess != null && sess.length() > 0 )
				{
					Reference setby = realm.sessionSetBy;
					
					%>
					<center>
					<b>"<%=Converter.convert( sess , false )%>"</b>
					<%
					  	Player scan;
					  	int count = 0;
					  	
						for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
						{
							scan = (Player) e.nextElement();
							String com = scan.getSessionComment();
							
							if( com != null && com.length() > 0 )
							{
								if( count == 0 )
								{
								%>
								<p>
								<table width="95%" border="0" cellPadding="2" cellSpacing="2">
								  <tr bgcolor="#EEEEEE">
									<td align=center><b><font size=-1>WHO</font></b></td>
									<td align=center><b><font size=-1>COMMENT</font></b></td>
								  </tr>								
								<%
								}
								count++;
							%>
								  <tr>
									<td><%=scan.getName()%></td>
									<td><%=Converter.convert( com )%></td>
								  </tr>
							<%
							}
						}
						
						if ( count != 0 )
						{
						%>
							</table>
						<%
						}
						
					if ( setby != null && setby.isValid() )
					{
					%>
						<p>&nbsp;<br>
						The current session was set by <a href="/finger?name=<%=setby.get().getName()%>"><%=setby.get().getName()%></a> ,<br> and can be reset
					<%
						if ( (new DateTime()).after( realm.sessionCanReset ) )
						{
							canset = true;
							%>
							any time now.
							<%
						}
						else
						{
						%>
							in <font color=red><%=realm.sessionCanReset.difference( new DateTime() )%></font>.
						<%
						}
					}						
						
					%>
						</center>
					<%
					// SetComment removed (isnt relevant without an IC connection)
				}
				else
				{
				%>
					<center>There is no current Session.</center>
				<%
					canset = true;
				}
				
				if ( canset )
				{
				%>
					<p>
					<form method=post action="<%=request.getRequestURI()%>">
					<input type=hidden name=event value=session>
					<center><input border=0 type=submit name=send value="Set:">&nbsp;
					<input type=text value="<%=sess%>" maxlength="60" size="60" name=title></center>
					</form>
				<%
				}
			}
		}
	}
}
catch( Exception e )
{
	Log.debug( "events.jsp", e.toString() );
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
