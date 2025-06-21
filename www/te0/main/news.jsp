<%@ import="key.*" %>
<%@ import="key.web.*" %>
<%@ import="key.primitive.DateTime" %>
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
			boolean viewDefault = true;
			MessageBox newsbox = realm.news;
			
			String action = request.getParameter( "action" );
			if (action == null) action = "";
			
			if ( action.equalsIgnoreCase( "delete" ) )
			{
			
				String letternum = request.getParameter( "letter" );
				
				try
				{
					int number = Integer.parseInt( letternum );
				
					newsbox.removeByNumber( number );
				}
				catch( NumberFormatException nfe ) { }
				
			}
			else if ( action.equalsIgnoreCase( "compose" ) )
			{
				viewDefault = false;
				
				String subject = request.getParameter( "subject" );
				String body = request.getParameter( "body" );
				
				String errorstr = null;
				
				String post = request.getParameter( "post" );
					if (post == null) post = "";
					
				String mid = request.getParameter( "letter" );
					if (mid == null) mid = "";					
				
				if ( post.equalsIgnoreCase( "true" ) )
				{
					if (subject == null || subject.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Subject.";
					else if (subject != null && subject.length() > Letter.MAX_SUBJECT )
						errorstr = "That Subject is too long.";
					else if (body == null || body.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Message.";
					
					if (errorstr == null)
					{
						Letter l = (Letter) Factory.makeAtom( Letter.class );
						
						try
						{
							l.setProperty( "description", subject );
							l.setProperty( "contents", new TextParagraph( body ) );
							l.setProperty( "from", JSPUtils.getPlayer( uName ).getName() );
							l.setProperty( "when", new DateTime() );
							
							l.setOwner( JSPUtils.getPlayer( uName ) );
							
							newsbox.add( l );

							viewDefault = true;
						}
						catch( Exception ep )
						{
							l.dispose();
						
							errorstr = "Can not write to " + newsbox.getName();
						}
					}
				}
				else if ( ! mid.equalsIgnoreCase( "" ) )
				{
					try
					{
						int number = Integer.parseInt( mid );
					
						Letter r = (Letter) newsbox.getElementAt( number );
	
						if( !( r.description.startsWith( "Re:" ) ) )
						{
							if( r.description.length()+4 <= Letter.MAX_SUBJECT )
								subject = "Re: " + r.description;
							else
								subject = r.description;
						}
						else
							subject = r.description;
									
						body = ((TextParagraph)r.contents).getText(); // this could be cleaned to include >'s
					}
					catch( NumberFormatException nn )
					{
						viewDefault = true;
					}
				}
				else
				{
					subject = "";
					body = "";
				}
				
				if (post.equalsIgnoreCase( "" ) || ( errorstr != null && (! errorstr.equalsIgnoreCase( "" )) ) )
				{
					%>
					<p>
					<% if ( errorstr != null && (! errorstr.equalsIgnoreCase( "" )) ) { %>
						<center><font color=red><%=errorstr%></font></center>
					<% } else { %>
						&nbsp;
					<% } %>
					<p>
					<form method=post action="<%=request.getRequestURI()%>">
					<ul>
					<input type=hidden name=action value=compose>
					<input type=hidden name=post value=true>
					<B>Subject: </B><br>
					<input type=text value="<%=subject%>" maxlength="<%=Letter.MAX_SUBJECT%>" size="<%=Letter.MAX_SUBJECT%>" name=subject>
					<p>
					<B>Message:</B><br>
					<textarea name=body cols=45 rows=10 wrap=soft><%=body%></textarea>
					</ul>
					<p><center><INPUT border=0 type=submit name=send value="Post"></center>
					</form>
					<%	
				}
				
			}
			else if ( action.equalsIgnoreCase( "read" ) )
			{
				String mid = request.getParameter( "letter" );
				
				if( mid != null && mid.length() != 0 )
				{
					try {
						Letter a = (Letter) newsbox.getElementAt( mid );
						
						if( a != null )
						{
							a.readCount++;
							
							int midnum = 0;
								
							try {
								midnum = Integer.parseInt( mid );
							}
							catch( NumberFormatException nfe )
							{
								// this will never occur because it has already passed in getElement above
							}							
							%>
							<p>
							<center><a href="/te0/news.jsp?action=read&letter=<%=(midnum-1)%>"><img src="/te0/images/up.gif" border=0></a></center>
							<p>
							<center><table border=1 width="95%" cellpadding=2 cellspacing=0><tr><td>
							<B>From: </B><a href="/finger?name=<%=a.from%>"><%=a.from%></a><BR>
							<B>Date: </B><%=a.when.toString()%><BR>
							<B>Subject: </B><%=Converter.convert( a.description )%><BR>
							This message has been read <%=a.readCount%> times.
							<HR>
							<%=Converter.convert( a.contents )%>
							</td></tr></table>
							<p>
							<table border=0 width="95%" cellpadding=0 cellspacing=0>
							  <tr>
								<td align=center><form method=post action="<%=request.getRequestURI()%>">
								<INPUT border=0 type=submit name=reply value="Reply">
								<INPUT border=0 type=hidden name=action value="compose">
								<INPUT border=0 type=hidden name=letter value="<%=mid%>">
								</form></td>
								<td align=center>
								<%
								if ( a.isOwner( JSPUtils.getPlayer( uName ) ) )
								{
								%>
									<form method=post action="<%=request.getRequestURI()%>">
									<INPUT border=0 type=submit name=reply value="Delete">
									<INPUT border=0 type=hidden name=action value="delete">
									<INPUT border=0 type=hidden name=letter value="<%=mid%>">
									</form>
								<%
								} else {
								%>
									&nbsp;
								<% }
								%>
								</td>
							  </tr>
							</table>
							<p>
							<a href="/te0/news.jsp?action=read&letter=<%=(midnum+1)%>"><img src="/te0/images/down.gif" border=0></a>
							<p>						
							</center>
							<%						
							
							viewDefault = false;
						}
					}
					catch( InvalidSearchException ise )
					{
					}
				}
				
			}
			
			if( viewDefault )  // the newsBox is the default
			{
				int count = newsbox.count();
				
			%>
				<center>There are <font color=red><%=count%></font> message<% if (count != 1) {%>s<%}%> on the Newsboard.</center>
				<p>
				
			<%
				if (count > 0)
				{
					String min = request.getParameter( "min" );
					int low = 0;
					if (min == null) low = 0;
					else
					{
						try
						{
							low = Integer.parseInt( min );
							if( low < 0 ) low = 0;
							else if( low >= count ) low = count-1;
						}
						catch( NumberFormatException nfc )
						{
							low = 0;
						}
					}
					%>
					<center><table width="90%" border="0" cellPadding="2" cellSpacing="1">
					<tr bgcolor="#EEEEEE">
						<td align=center><b><font size=-1>FROM</font></b></td>
						<td align=center><b><font size=-1>SUBJECT</font></b></td>
						<td align=center><b><font size=-1>DATE</font></b></td>
					</tr>
					<%
						for( int upto=low; upto < (low+10); upto++ )
						{
							Letter a = (Letter) newsbox.getElementAt( upto );
							if( a == null ) break;
							else
							{
							%>
							<tr>
								<td><small><a href="/te0/news.jsp?action=read&letter=<%=upto%>"><%=a.from%></a></small></td>
								<td><small><%=Converter.convert( a.description )%></small></td>
								<td align=center><%=a.when.toShortString( JSPUtils.getPlayer( uName ) )%></td>
							</tr>
							<%
							}
						}
					%>
					</table></center>
					<%
					
					if( count > 10 )
					{
					%>
						<br><b>Page: </b>
					<%
						int pages = (count-1) / 10 + 1;
						for(int c=0;c<pages;c++)
						{
							if( low == (c*10) )  // this makes the current page not hyperlinked
							{
							%>
								<%=(c+1)%>&nbsp;
							<%							
							}
							else
							{
							%>
								<a href="<%=request.getRequestURI()%>?min=<%=(c*10)%>"><%=(c+1)%></a>&nbsp;
							<%
							}
							
							if( (c+1)%15 == 0 )  // this wraps the page numbers every 15 pages
							{
							%>
								<br><font color="white"><b>Page: </b></font>
							<%
							}
						}						
					}
				}
			}




		}

	}
}
catch( Exception e )
{
	Log.debug( "news.jsp", e.toString() );
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
