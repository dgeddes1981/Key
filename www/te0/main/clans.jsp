<%@ import="key.*" %>
<%@ import="key.te0.*" %>
<%@ import="key.web.*" %>
<%@ import="java.util.Enumeration" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<p>

<%
try {
	if( session != null )
	{
		String action = request.getParameter( "action" );
		if (action == null) action = "";
		
		if ( action.equalsIgnoreCase( "finger" ) )
		{
			String supp = request.getParameter( "clan" );
			
			Clan c = null;
			String errorstr = null;
			String fname = null;
			
			if( supp != null )
			{
				c = JSPUtils.getClan( supp ) ;
				if ( c == null )
				{
					errorstr = "Could not find Clan matching that name.";
					fname = supp;
				}
				else
				{
					fname = c.getName();
				}
			}
			else
			{
				fname = "";
			}
			
			%>
				<center>
				<p>
				Which Clan do you wish to Finger:
				<p>
				<form method=post action="<%=request.getRequestURI()%>">
				<select name=clan>
					<option value=""></option>
				<%
					for( Enumeration e = key.getClans().referenceElements(); e.hasMoreElements(); )
					{
						String clanname = ( (Reference) e.nextElement() ).getName();
						
						if ( clanname.equalsIgnoreCase( fname ) )
						{
						%><option value="<%=clanname%>" selected><%=clanname%></option><%
						}
						else
						{
						%><option value="<%=clanname%>"><%=clanname%></option><%
						}
					}
				%>	
				</select>&nbsp;<input type=submit value="Finger">
				<input type=hidden name=action value=finger>
				</form>
				</center>
			<%			
			
			if ( c != null )
			{
				fname = c.getName();

				%>
				<%=Converter.convert( LineParagraph.LINE )%>
				<%=Converter.convert( c.getTitledName() )%><br>

				<%
					%><%=Converter.convert( c.getPrefix() + " " + c.getName() + " was created on " + c.getCreationDate() )%><%
					
					if( c.getFounder() != null )
					{
						%>Founder: <a href="/finger?name=<%=c.getFounder().getName()%>"><%=c.getFounder().getName()%></a><br><%
					}
					
					int cm = c.countMembers();
					%><%=("There " + Grammar.isAre(cm) + " a total of " + cm + " member" + ( (cm == 1) ? "" : "s") + " in Clan " + c.getName() + ".")%><br><%
					
					int ip = c.numberPlayers();
					%><%=("(There " + Grammar.isAreCount(ip) + " member" + ( (ip == 1) ? "" : "s") + " currently online)" )%><br><%					

					String wp = c.homepage.get();
					if( wp != null )
					{
						%><%=Converter.convert( "The Clan web page is: <a href=\"" + wp + "\">" + wp.substring(7) + "</a>" )%><%
					}

					Room ch = (Room) c.hall.get();
					if( ch != null )
					{
						%><%=( "Clan Hall is [" + ch.getContainedId() + "]" )%><br><%
					}

					%><%=Converter.convert( LineParagraph.LINE )%><%
																				
			}
			else
			{
				if (errorstr != null) { %><center><font color=red><%=errorstr%></font></center><% } 
			}
			
		}
		else if ( action.equalsIgnoreCase( "submit" ) )
		{
				String title = request.getParameter( "title" );
				String contents = request.getParameter( "contents" );
				String icon = request.getParameter( "icon" );
				
				String errorstr = null;
				
				String post = request.getParameter( "post" );
					if (post == null) post = "";
					
				
				if ( post.equalsIgnoreCase( "true" ) )
				{	
					if (title == null || title.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Name for the Clan in Review.";
					else if (contents == null || contents.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Review.";
					else if (icon == null || icon.trim().equalsIgnoreCase( "" ) )
						icon = "/te0/images/icons/clans.gif";  //default clan image
					
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
							art.setProperty( "author", author );
							art.setProperty( "iconUrl", icon );
							
							if (uName != null && uName.length() > 0) 							
								art.setOwner( JSPUtils.getPlayer( uName ) );
								
							PermissionList pl = art.getPermissionList();
							pl.allow( Atom.modifyAction ); // for summarizing
							pl.allow( Atom.moveAction );   // for approving
							
							Container tbin = (Container) te0.getSubmitted().getElement( "clans" );
							
							tbin.add( art );
							
							%>
							<p>
							Thank you for your submission.
							<p>
							It will now be reviewed by an Editor and published once approved.							
							<%

							Log.log( "te0", author + " submitted a clan review" );

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
					<B>Clan Name: </B><br>
					<input type=text value="<%=title%>" maxlength="<%=Article.MAX_TITLE_LENGTH%>" size="<%=Article.MAX_TITLE_LENGTH%>" name=title>
					<p>
					<B>Image: </B><br>
					<input type=text value="<%=icon%>" size="<%=Article.MAX_TITLE_LENGTH%>" name=icon>
					<p>
					<B>Review:</B><br>
					<textarea name=contents cols=50 rows=10 wrap=hard><%=contents%></textarea>
					</ul>
					<p><center><INPUT border=0 type=submit name=send value="Submit"></center>
					</form>
					<%	
				}
			
			
			
						
		}		
		else // review is the default
		{
			Container tbin = (Container) te0.getApproved().getElement( "clans" );
			
			Article review = (Article) tbin.getElementAt( 0 );
			
			if( review==null || review.isEmpty() )
			{
			%>
			<center>There is no current Clan Review.</center>
			<%
			}
			else
			{
				review.Hit();
			%>
				<center>
				<table border="0" width="100%">
				  <tr>
					<td bgcolor="#003355" colspan=3>
						<font size="3" color="yellow">Clan Review: <%=review.title%></font>
					</td>
				  </tr>
				  <tr>
					<td>&nbsp;</td>
					<td valign=top>
						<font size="2">
						<%=review.when.toString()%>
						<BR>
						Author: <% if( review.author.equalsIgnoreCase("") ) {%>Anonymous<%} else {%><%=review.author%><%}%>
						<BR><BR>
						This Article has been read <%=review.getHits()%> times.
						</font>
					</td>
					<td>
						<img src="<%=review.iconUrl%>" align="right">
					</td>
				  </tr>
				</table>
				<hr color="black">
				<p>
				<table border=0 width="100%" cellpadding=0 cellspacing=0>
				<tr><td>
				<%=((TextParagraph) review.contents).getText()%>
				</td></tr>
				</table>						
				</center>
			<%
			}
		}
	}
}
catch( Exception e )
{
	Log.debug( "clans.jsp", e.toString() );
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
