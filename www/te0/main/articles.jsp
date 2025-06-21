<%@ import="key.*" %>
<%@ import="key.te0.*" %>
<%@ import="key.web.*" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<p>

<%
try {
	if( session != null )
	{
		boolean viewDefault = true;
		
		String action = request.getParameter( "action" );
		if (action == null) action = "";
		
		if ( action.equalsIgnoreCase( "submit" ) )
		{
				viewDefault = false;
				
				String title = request.getParameter( "title" );
				String contents = request.getParameter( "contents" );
				String icon = request.getParameter( "icon" );
				String summary = request.getParameter( "summary" );
				
				String errorstr = null;
				
				String post = request.getParameter( "post" );
					if (post == null) post = "";
					
				
				if ( post.equalsIgnoreCase( "true" ) )
				{	
					if (title == null || title.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Title for the Article.";
					else if (contents == null || contents.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply an Article.";
					else if (icon == null || icon.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must choose an Icon.";
					else if (summary == null || summary.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Summary.";
						
					if (errorstr == null)
					{
						String uName = (String) session.getValue( "name" );
						String author = null;
						
						if (uName == null || uName.length() == 0)  // not logged in
						{
							author = "Anonymous";
						}
						else
						{
							author = JSPUtils.getPlayer( uName ).getName();
						}		
						
						Article art = (Article) Factory.makeAtom( Article.class );
						
						try
						{
							art.setProperty( "title", title );
							art.setProperty( "contents", new TextParagraph( contents ) );
							art.setProperty( "summary", new TextParagraph( summary ) );
							art.setProperty( "author", author );
							art.setProperty( "iconUrl", icon );
							
							if (uName != null && uName.length() > 0) 							
								art.setOwner( JSPUtils.getPlayer( uName ) );
								
							PermissionList pl = art.getPermissionList();
							pl.allow( Atom.modifyAction ); // for summarizing
							pl.allow( Atom.moveAction );   // for approving
							
							Container tbin = (Container) te0.getSubmitted().getElement( "articles" );
							
							tbin.add( art );
							
							%>
							<p>
							Thank you for your submission.
							<p>
							It will now be reviewed by an Editor and published once approved.							
							<%

							Log.log( "te0", author + " submitted an article" );

							viewDefault = false;
						}
						catch( Exception ep )
						{
							art.dispose();
						
							throw ep;   // let the page catch it
						}
					}
				}
				else
				{
					title = "";
					contents = "";
					icon = "";
					summary = "";
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
					<input type=hidden name=action value=submit>
					<input type=hidden name=post value=true>
					<B>Title: </B><br>
					<input type=text value="<%=title%>" maxlength="<%=Article.MAX_TITLE_LENGTH%>" size="<%=Article.MAX_TITLE_LENGTH%>" name=title>
					<p>
					<B>Icon: </B><br>
					<input type=radio value="/te0/images/icons/articles.gif" name=icon><img src="/te0/images/icons/articles.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/clans.gif" name=icon><img src="/te0/images/icons/clans.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/events.gif" name=icon><img src="/te0/images/icons/events.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/mail.gif" name=icon><img src="/te0/images/icons/mail.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/news.gif" name=icon><img src="/te0/images/icons/news.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/objects.gif" name=icon><img src="/te0/images/icons/objects.gif" height=30 width=30 align=absmiddle><br><br>
					<input type=radio value="/te0/images/icons/oclans.gif" name=icon><img src="/te0/images/icons/oclans.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/ofeature.gif" name=icon><img src="/te0/images/icons/ofeature.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/oobjects.gif" name=icon><img src="/te0/images/icons/oobjects.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/opeople.gif" name=icon><img src="/te0/images/icons/opeople.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/ostaff.gif" name=icon><img src="/te0/images/icons/ostaff.gif" height=30 width=30 align=absmiddle>
					<input type=radio value="/te0/images/icons/people.gif" name=icon><img src="/te0/images/icons/people.gif" height=30 width=30 align=absmiddle>
					<p>
					<B>Summary:</B><br>
					<textarea name=summary cols=50 rows=3 wrap=hard><%=summary%></textarea>
					<p>
					<B>Article:</B><br>
					<textarea name=contents cols=50 rows=10 wrap=hard><%=contents%></textarea>
					</ul>
					<p><center><INPUT border=0 type=submit name=send value="Submit"></center>
					</form>
					<%	
				}
			
			
			
						
		}
		else if ( action.equalsIgnoreCase( "read" ) )
		{
			String article = request.getParameter( "article" );

			if( article != null )
			{
				try
				{
					Container tbin = (Container) te0.getApproved().getElement( "articles" );
					Article art = (Article) tbin.getElementAt( article );
					
					if( art!=null && !(art.isEmpty()) )
					{
						art.Hit();
					%>
						<center>
						<table border="0" width="100%">
						  <tr>
							<td bgcolor="#003355" colspan=3>
								<font size="3" color="yellow"><%=Converter.convert( art.title )%></font>
							</td>
						  </tr>
						  <tr>
							<td>&nbsp;</td>
							<td valign=top>
								<font size="2">
								<%=art.when.toString()%>
								<BR>
								Author: <%=art.author%>
								<BR><BR>
								This Article has been read <%=art.getHits()%> times.
								</font>
							</td>
							<td>
								<img src="<%=art.iconUrl%>" align="right">
							</td>
						  </tr>
						</table>
						<hr color="black">
						<p>
						<table border=0 width="100%" cellpadding=0 cellspacing=0>
						<tr><td>
						<%=((TextParagraph) art.contents).getText()%>
						</td></tr>
						</table>						
						</center>
					<%
					}					
					
					
					viewDefault = false;
				}
				catch( InvalidSearchException inv )
				{
				}
			}
						
		}
		
		
		if( viewDefault ) // headlines is the default
		{
			Container tbin = (Container) te0.getApproved().getElement( "articles" );
		
			Article art = (Article) tbin.getElementAt( 0 );
			
			if( art==null || art.isEmpty() )
			{
			%>
			<center>There are no current Articles.</center>
			<%
			}
			else
			{
				for(int i=0;i<6;i++)
				{
					art = (Article) tbin.getElementAt( i );
					
					if( art!=null && !(art.isEmpty()) )
					{
					%>
						<center>
						<table border="0" width="100%">
						  <tr>
							<td bgcolor="#003355" colspan=3>
								<font size="3" color="yellow"><%=Converter.convert( art.title )%></font>
							</td>
						  </tr>
						  <tr>
							<td>&nbsp;</td>
							<td valign=top>
								<font size="2">
								<%=art.when.toString()%>
								<BR><BR>
								<%=Converter.convert( art.summary )%> 
								<A HREF="articles.jsp?action=read&article=<%=i%>">(more)...</A>
								</font>
							</td>
							<td>
								<img src="<%=art.iconUrl%>" align="right">
							</td>
						  </tr>
						</table>
						</center>
					<%
					}
				}
			}

		}
	}
}
catch( Exception e )
{
	Log.debug( "articles.jsp", e.toString() );
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
