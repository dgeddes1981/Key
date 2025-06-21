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
			boolean viewDefault = true;
			MessageBox mailbox = JSPUtils.getPlayer( uName ).getMailbox();
			
			String action = request.getParameter( "action" );
			if (action == null) action = "";
			
			if ( action.equalsIgnoreCase( "delete" ) )
			{
			
				String letternum = request.getParameter( "letter" );
				
				try
				{
					int number = Integer.parseInt( letternum );
				
					mailbox.removeByNumber( number );
				}
				catch( NumberFormatException nfe ) { }
				
			}
			else if ( action.equalsIgnoreCase( "compose" ) )
			{
				viewDefault = false;
				
				String to = request.getParameter( "to" );
				String subject = request.getParameter( "subject" );
				String body = request.getParameter( "body" );
				
				String errorstr = null;
				
				String post = request.getParameter( "post" );
					if (post == null) post = "";
					
				String mid = request.getParameter( "letter" );
					if (mid == null) mid = "";					
				
				if ( post.equalsIgnoreCase( "true" ) )
				{
					Player target = JSPUtils.getPlayer( to );
					
					if (to == null || to.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Player to mail to.";
					else if (target == null)
						errorstr = "Cant find a Player with that name.";
					else if (target == JSPUtils.getPlayer( uName ) )
						errorstr = "Cant mail to yourself.";
					else if (subject == null || subject.trim().equalsIgnoreCase( "" ) )
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
							
							target.addMail( l );

							viewDefault = true;
						}
						catch( Exception ep )
						{
							l.dispose();
						
							errorstr = "Can not write to " + target.getName() + "'s mailbox.";
						}
					}
				}
				else if ( ! mid.equalsIgnoreCase( "" ) )
				{
					try
					{
						int number = Integer.parseInt( mid );
					
						Letter r = (Letter) mailbox.getElementAt( number );
	
						to = r.from;
						
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
					to = "";
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
					<B>To: </B><br>
					<input type=text value="<%=to%>" maxlength="<%=Player.MAX_NAME%>" size="<%=Player.MAX_NAME%>" name=to>
					<p>
					<B>Subject: </B><br>
					<input type=text value="<%=subject%>" maxlength="<%=Letter.MAX_SUBJECT%>" size="<%=Letter.MAX_SUBJECT%>" name=subject>
					<p>
					<B>Message:</B><br>
					<textarea name=body cols=45 rows=10 wrap=soft><%=body%></textarea>
					</ul>
					<p><center><INPUT border=0 type=submit name=send value="Send"></center>
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
						Letter a = (Letter) mailbox.getElementAt( mid );
						
						if( a != null )
						{
							a.readCount++;
							if( a.readCount == 1 )  // that was the first read
								mailbox.unread--;
								
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
							<center><a href="/te0/mail.jsp?action=read&letter=<%=(midnum-1)%>"><img src="/te0/images/up.gif" border=0></a></center>
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
								<td align=center><form method=post action="<%=request.getRequestURI()%>">
								<INPUT border=0 type=submit name=reply value="Delete">
								<INPUT border=0 type=hidden name=action value="delete">
								<INPUT border=0 type=hidden name=letter value="<%=mid%>">
								</form></td>
							  </tr>
							</table>
							<p>
							<a href="/te0/mail.jsp?action=read&letter=<%=(midnum+1)%>"><img src="/te0/images/down.gif" border=0></a>
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
			
			if( viewDefault )
			{
				int count = mailbox.count();
				int unread = mailbox.unread;
				
			%>
				<center>You have <font color=red><%=count%></font> message<% if (count != 1) {%>s<%}%>, <font color=red><%=unread%></font> of which <% if (unread != 1) {%>are<%} else {%>is<%}%> unread.</center>
				<p>
				
			<%
				if (count != 0)
				{
					count = 1;
					%>
					<center><table width="90%" border="0" cellPadding="2" cellSpacing="1">
					<tr bgcolor="#EEEEEE">
						<td align=center><b><font size=-1>NEW</font></b></td>
						<td align=center><b><font size=-1>FROM</font></b></td>
						<td align=center><b><font size=-1>SUBJECT</font></b></td>
						<td align=center><b><font size=-1>DATE</font></b></td>
					</tr>
					<%
						for( Enumeration e = mailbox.elements(); e.hasMoreElements(); )
						{
							Letter a = (Letter) e.nextElement();
							%>
							<tr>
								<td align=center><% if (a.readCount == 0) { %><img src="images/unread.gif" border=0><% } %></td>
								<td><small><a href="/te0/mail.jsp?action=read&letter=<%=(count-1)%>"><%=a.from%></a></small></td>
								<td><small><%=Converter.convert( a.description )%></small></td>
								<td align=center><%=a.when.toShortString( JSPUtils.getPlayer( uName ) )%></td>
							</tr>
							<%
							count++;
						}
					%>
					</table></center>
					<%
				}
			}




		}

	}
}
catch( Exception e )
{
	Log.debug( "mail.jsp", e.toString() );
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
