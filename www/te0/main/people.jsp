<%@ import="key.*" %>
<%@ import="key.te0.*" %>
<%@ import="key.web.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.talker.objects.Book" %>
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
		
		if ( action.equalsIgnoreCase( "who" ) )
		{
		%>
			<center>
			<%
				int count = 0;
				for( Enumeration e = key.players(); e.hasMoreElements(); )
				{
					Player scan = (Player) e.nextElement();
					
					Clan clan = scan.getClan();
					String clanname = null;
					if ( clan == null ) clanname = "";
					else clanname = clan.getName();
					
					String webpage = scan.getWebpage().get();
					if ( webpage == null ) webpage = "";
					
					if( count == 0 )
					{
					%>
						<table width="98%" border="0" cellPadding="1" cellSpacing="1">
						  <tr bgcolor="#EEEEEE">
							<td align=center><b><font size=-1>NAME</font></b></td>
							<td align=center><b><font size=-1>CLAN</font></b></td>
							<td align=center><b><font size=-1>IDLE</font></b></td>
							<td align=center><b><font size=-1>HOMEPAGE</font></b></td>
						  </tr>								
					<%
					}
					count++;						
					%>
						  <tr>
							<td><small><a href="/finger?name=<%=scan.getName()%>"><%=scan.getName()%></a></small></td>
							<td><small><%=clanname%></small></td>
							<td><small><%=scan.getIdle( new DateTime() ).toShortString()%></small></td>
							<td><a href="<%=webpage%>"><small><%=webpage%></small></a></td>
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
				<i>Only one person online at the moment.</i>
			<% } else if( count == 0 ) { %>
				<i>There aren't any people online at the moment.</i>
			<% } else { %>
				<i><%=count%> people online at the moment.</i>
			<% } %>
			<p>				
			</center>
		<%
		}
		else if ( action.equalsIgnoreCase( "finger" ) )
		{
			String supp = request.getParameter( "player" );
			
			Player p = null;
			String errorstr = null;
			String fname = null;
			
			if( supp != null )
			{
				p = JSPUtils.getPlayer( supp ) ;
				if ( p == null )
				{
					errorstr = "Could not find Player matching that name.";
					fname = supp;
				}
				else
				{
					fname = p.getName();
				}
			}
			else
			{
				fname = "";
			}
			
			%>
				<center>
				<p>
				Who do you wish to Finger:
				<p>
				<form method=post action="<%=request.getRequestURI()%>">
				<input type=text name=player maxlength="<%=Player.MAX_NAME%>" size="<%=Player.MAX_NAME%>" value="<%=fname%>">&nbsp;<input type=submit value="Finger">
				<input type=hidden name=action value=finger>
				</form>
				</center>
			<%			
			
			if ( p != null )
			{
				fname = p.getName();

				%>
				<%=Converter.convert( LineParagraph.LINE )%>
				<%=Converter.convert( p.getTitledName() )%><br>

				<%
					if( p.connected() )
					{
						%><%=Converter.convert( p.getFullName() , false )%> has been logged in for <%=p.loginStats.getTimeSinceConnection()%><br><%
					}
					else
					{
						DateTime dateTime = p.loginStats.getLastDisconnection();
			
						if( dateTime == null )
						{
							%><%=Converter.convert( p.getFullName() , false )%> has never logged in.<br><%
						}
						else
						{
							%><%=Converter.convert( p.getFullName() , false )%> was last seen at <%=dateTime.toString( p )%><br><%
						}
						
						DateTime lc = p.loginStats.getLastConnection();
						if( lc != null )
						{
							%><%=fname%> was last connected for <%=lc.difference( dateTime ).toTruncString()%><br><%
						}
					}
					
					if( !p.isHideTime() )
					{
						%><%=p.HisHer()%> total login time is <%=p.loginStats.getTotalConnectionTime()%><br><%
						String ol = p.getOldLoginTime();
						if( ol != null && ol.length() > 0 )
						{
							%><%=p.HisHer()%> pre-eclipse login time was <%=Converter.convert( ol )%><%
						}
					}

					if( !p.isEmailPrivate() )
					{
						String ea = p.getEmailAddress();
						%><%=p.HisHer()%> email address is: <a href="mailto:<%=ea%>"><%=ea%></a><br><%
					}
					
					{
						String wp = p.getWebpage().get();
						if( wp != null )
						{
							%><%=p.HisHer()%> web page is: <a href="<%=wp%>"><%=wp.substring(7)%></a><br><%
						}
					}
					
					{
						Paragraph plan = p.getPlan();
						
						if( plan != null && !plan.isEmpty() )
						{
							%><%=Converter.convert( p.planHeading )%><%
							%><%=Converter.convert( Grammar.substitute(p.getPlan()) )%><%
						}
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
						errorstr = "You must supply a Name for the Spod in Review.";
					else if (contents == null || contents.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Review.";
					else if (icon == null || icon.trim().equalsIgnoreCase( "" ) )
						icon = "/te0/images/icons/people.gif";  //default spod image
					
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
							
							Container tbin = (Container) te0.getSubmitted().getElement( "people" );
							
							tbin.add( art );
							
							%>
							<p>
							Thank you for your submission.
							<p>
							It will now be reviewed by an Editor and published once approved.							
							<%

							Log.log( "te0", author + " submitted a spod review" );

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
					<B>Spod Name: </B><br>
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
		else if ( action.equalsIgnoreCase( "edit" ) )
		{
			/*
			String uName = (String) session.getValue( "name" );
			
			if (uName == null || uName.length() == 0)  // not logged in
			{
			%>
				<center>You are not logged in.</center>
			<%
			}
			else
			{
				Player p = JSPUtils.getPlayer( uName ) ; // this player has successfully logged in - so assume p != null
				
				String errorstr = null;
				
				String id = null;
				String valueString = null;
				boolean showForm = false;
				
				// Here we have to intercept the form data and make sure it adheres to the key.commands
				// do checks on request.getParameter( "prefix" ) for valid prefix etc
				
				String gottitle = request.getParameter( "title" );
				if ( gottitle != null)
				{
					if( ",.:;".indexOf( gottitle.charAt( 0 ) ) == -1 )
						gottitle =  " " + gottitle;
				}								

				String gotprefix = request.getParameter( "prefix" );
				if ( gotprefix != null)
				{
					if( Key.shortcuts().getExactElement( gotprefix ) != null )
						gotprefix = "invalid";
						
					// *** multiple words
				}
				
				String gotlogin = request.getParameter( "loginMsg" );
				if ( gotlogin != null)
				{
					gotlogin = ": " + gotlogin;
				}
												
				%>
					<center><font size=+2><i><%=p.getName()%></i></font></center><p>
					<form method=post action="<%=request.getRequestURI()%>">
				
					<table>
					<tr><td><b>Prefix: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".prefix" , gotprefix );%></td></tr>
					<tr><td><b>Title: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".title" , gottitle );%></td></tr>
					</table>
					<p>
					<table>
					<tr><td><b>Also Known As: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".aka" , request.getParameter( "aka" ) );%></td></tr>
					<tr><td><b>Gender: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".gender" , request.getParameter( "gender" ) );%></td></tr>
					<tr><td><b>Age: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".age" , request.getParameter( "age" ) );%></td></tr>
					</table>
					<p>
					<table>
					<tr><td><b>From: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".whereFrom" , request.getParameter( "whereFrom" ) );%></td></tr>				
					<tr><td><b>ICQ Number: </b></td><td><%Edit.edit( out , null , "~" + p.getName() + ".icq" , request.getParameter( "icq" ) );%></td></tr>
					</table>
					<p>
					<b>Webpage: </b><%Edit.edit( out , null , "~" + p.getName() + ".homepage" , request.getParameter( "homepage" ) );%><p>					
					<b>Description: </b><br><%Edit.edit( out , null , "~" + p.getName() + ".description" , request.getParameter( "description" ) );%><p>
					<b>Plan: </b><br><%Edit.edit( out , null , "~" + p.getName() + ".plan" , request.getParameter( "plan" ) );%><p>
					<b>Login Message: </b><%Edit.edit( out , null , "~" + p.getName() + ".loginMsg" , gotlogin );%><br>
					<b>Logout Message: </b><%Edit.edit( out , null , "~" + p.getName() + ".logoutMsg" , request.getParameter( "logoutMsg" ) );%><br>
					<b>Idle Message: </b><%Edit.edit( out , null , "~" + p.getName() + ".idleMsg" , request.getParameter( "idleMsg" ) );%><br>
					<b>Block Message: </b><%Edit.edit( out , null , "~" + p.getName() + ".blockMsg" , request.getParameter( "blockMsg" ) );%><br>
					<b>Blocking Message: </b><%Edit.edit( out , null , "~" + p.getName() + ".blockingMsg" , request.getParameter( "blockingMsg" ) );%><br>
					
			
					</form>
				<%
			}		
			*/
		}
		else if ( action.equalsIgnoreCase( "books" ) )
		{
			/*  TODO.jsp  */
				Book spods = null;
			
				try {
					spods = (Book) new Search( "/var/books/spods", null ).result;
				}
				catch( InvalidSearchException ise ) {}
				
				if( spods==null )
				{
				%>
				<center>There is no Book of Spods.</center>
				<%
				}
				else
				{
					%><%=Converter.convert( spods.aspect() )%><%
				}					
		}			
		else // review is the default
		{
		
			Container tbin = (Container) te0.getApproved().getElement( "people" );
			
			Article review = (Article) tbin.getElementAt( 0 );
			
			if( review==null || review.isEmpty() )
			{
			%>
			<center>There is no current Spod Review.</center>
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
						<font size="3" color="yellow">Spod Review: <%=review.title%></font>
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
	Log.debug( "people.jsp", e.toString() );
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
