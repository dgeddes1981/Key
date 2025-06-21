<%@ import="key.*" %>
<%@ import="key.te0.*" %>
<%@ import="key.web.*" %>
<%@ import="key.util.SMTP" %>
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
		
		if ( action.equalsIgnoreCase( "inventory" ) )
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
				Inventory inv = (Inventory) ( JSPUtils.getPlayer( uName ) ).getInventory();
				
				int count = inv.count();
			
				if (count > 0)
				{
					%>
					<b>You are currently holding:</b><p>
					<center>
					<table width="90%" border="0" cellPadding="2" cellSpacing="1">
					<tr bgcolor="#EEEEEE">
						<td align=center><b><font size=-1>OBJECT</font></b></td>
						<td align=center><b><font size=-1>DESCRIPTION</font></b></td>
						<td align=center><b><font size=-1>VALUE</font></b></td>
					</tr>
					<%
						int totvalue = 0;
						for( Enumeration e = inv.elements(); e.hasMoreElements(); )
						{
							Thing a = (Thing) e.nextElement();
							totvalue += a.getValue();
							%>
							<tr>
								<td><%=a.getName()%></td>
								<td><%=Converter.convert( a.getFullPortrait( null ) )%></td>
								<td align=center><%=a.getValue()%></td>
							</tr>
							<%
						}
					%>
					<tr bgcolor="#EEEEEE">
						<td align=center bgcolor="#FFFFFF">&nbsp;</td>
						<td align=center><b><font size=-1>TOTAL VALUE</font></b></td>
						<td align=center><b><font size=-1><%=totvalue%></font></b></td>
					</tr>					
					</table>
					</center>
					<br><b><%=count%> objects in total.</b>
					<%
				}
				else
				{
				%>
				<center><b>There are currently no objects in your Inventory.</b></center><br>
				<%
				}			
			
			
			}
						
		}
		else if ( action.equalsIgnoreCase( "rsubmit" ) )
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
						errorstr = "You must supply a Name for the Object in Review.";
					else if (contents == null || contents.trim().equalsIgnoreCase( "" ) )
						errorstr = "You must supply a Review.";
					else if (icon == null || icon.trim().equalsIgnoreCase( "" ) )
						icon = "/te0/images/icons/objects.gif";  //default object image
					
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
							
							Container tbin = (Container) te0.getSubmitted().getElement( "objects" );
							
							tbin.add( art );
							
							%>
							<p>
							Thank you for your submission.
							<p>
							It will now be reviewed by an Editor and published once approved.							
							<%

							Log.log( "te0", author + " submitted an object review" );

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
					<input type=hidden name=action value=rsubmit>
					<input type=hidden name=post value=true>
					<B>Object Name: </B><br>
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
		else if ( action.equalsIgnoreCase( "submit" ) )
		{
		%>
			<center><B>Online Object Submission</b></center>
		<%
			String type = request.getParameter( "type" );
			
			if ( type != null ) 
			{
				%>
				&nbsp;<br>Now describe your Object:
				<form method=post action="<%=request.getRequestURI()%>"> 
				<%
				
				int value = 1;  // default to a prop
				
				//  value 3 is reserved for EW style objects, once useCount is implemented
				
				if ( type.equalsIgnoreCase( "Nothing" ) ) value = 1;
				else if ( type.equalsIgnoreCase( "Seat" ) ) value = 2;
				else if ( type.equalsIgnoreCase( "Wear" ) ) value = 4;
				else if ( type.equalsIgnoreCase( "Use" ) ) value = 5;
				else if ( type.equalsIgnoreCase( "Wield" ) ) value = 6;
				else if ( type.equalsIgnoreCase( "Command" ) ) value = 7;
				
				
				if ( value > 0 )  // Prop or sub-classes
				{
				%>
				<p>
				<table>
				<tr><td align=right><small>Title:</small> </td><td><input type=text name=fullPortrait size=20 maxlength=80></td><td> <small>eg. a can of coke</small></td></tr>
				<tr><td align=right><small>Description:</small> </td><td><input type=text name=description size=20 maxlength=160></td><td> <small>eg. brown, sweet and full of caffeine goodness</small></td></tr>
				</table>
				<%
				}
				
				if ( value == 2 )  // Seat
				{
				%>
				<p>
				<table>
				<tr><td align=right><small>Seats how many people:</small> </td><td><input type=text name=seats size=5 maxlength=3></td></tr>
				</table>				
				<%
				}
				
				if ( value > 4 )  // Message or sub-classes
				{
				%>
				<p>
				<table>
				<tr><td align=right><small>Sound:</small> </td><td><input type=text name=sound size=20 maxlength=80></td><td> <small>eg. ! *GULP* !</small></td></tr>
				<tr><td align=right><small>Action:</small> </td><td><input type=text name=message size=20 maxlength=80></td><td> <small>eg. gulps down</small></td></tr>
				</table>
				<%
				}				
				
				if ( value == 4 || value == 6 )  // Wearable or WearableMessage
				{
				if (value==4) {%><p><small>Worn on:</small><%} else {%><p><small>Wielded at:</small><%}
				%>
<pre><input type=checkbox name=0> Not worn        <input type=checkbox name=1> Personage       <input type=checkbox name=2> Head
<input type=checkbox name=3> Hair            <input type=checkbox name=4> Ears            <input type=checkbox name=5> Face
<input type=checkbox name=6> Neck            <input type=checkbox name=7> Body (outer)    <input type=checkbox name=8> Body (middle)
<input type=checkbox name=9> Body (inner)    <input type=checkbox name=10> Arm (left)      <input type=checkbox name=11> Arm (right)
<input type=checkbox name=12> Waist accessory <input type=checkbox name=13> Waist           <input type=checkbox name=14> Legs (outer)
<input type=checkbox name=15> Legs (middle)   <input type=checkbox name=16> Legs (inner)    <input type=checkbox name=17> Hands
<input type=checkbox name=18> Wrist (left)    <input type=checkbox name=19> Wrist (right)   <input type=checkbox name=20> Finger (left)
<input type=checkbox name=21> Finger (right   <input type=checkbox name=22> Feet (outer)    <input type=checkbox name=23> Feet (middle)
<input type=checkbox name=24> Feet (inner)    <input type=checkbox name=25> Miscellaneous   <input type=checkbox name=26> Wield (left)
<input type=checkbox name=27> Wield (right)   <input type=checkbox name=28> Seat position   <input type=checkbox name=29> Pet</pre>

				<small>Covers these locations:</small>

<pre><input type=checkbox name=0> Not worn        <input type=checkbox name=1> Personage       <input type=checkbox name=2> Head
<input type=checkbox name=3> Hair            <input type=checkbox name=4> Ears            <input type=checkbox name=5> Face
<input type=checkbox name=6> Neck            <input type=checkbox name=7> Body (outer)    <input type=checkbox name=8> Body (middle)
<input type=checkbox name=9> Body (inner)    <input type=checkbox name=10> Arm (left)      <input type=checkbox name=11> Arm (right)
<input type=checkbox name=12> Waist accessory <input type=checkbox name=13> Waist           <input type=checkbox name=14> Legs (outer)
<input type=checkbox name=15> Legs (middle)   <input type=checkbox name=16> Legs (inner)    <input type=checkbox name=17> Hands
<input type=checkbox name=18> Wrist (left)    <input type=checkbox name=19> Wrist (right)   <input type=checkbox name=20> Finger (left)
<input type=checkbox name=21> Finger (right   <input type=checkbox name=22> Feet (outer)    <input type=checkbox name=23> Feet (middle)
<input type=checkbox name=24> Feet (inner)    <input type=checkbox name=25> Miscellaneous   <input type=checkbox name=26> Wield (left)
<input type=checkbox name=27> Wield (right)   <input type=checkbox name=28> Seat position   <input type=checkbox name=29> Pet</pre>
				<%
				}
				
				if ( value == 7 )  // CommandObject
				{
				%>
				<p>
				<small>Command(s) to perform:</small>
				<table>
				<tr><td><input type=text name=command1 size=20 maxlength=80></td></tr>
				<tr><td><input type=text name=command2 size=20 maxlength=80></td></tr>
				<tr><td><input type=text name=command3 size=20 maxlength=80></td></tr>
				<tr><td><input type=text name=command4 size=20 maxlength=80></td></tr>				
				</table>
				<%
				}				
				
				
				// 3 spawn/morph/useCount last table
				
				%>
				<p><center><input type=submit name=object value="Submit Object"></center>
				<input type=hidden name=action value=submit>
				</form>
				<%
			}
			else if ( request.getParameter( "object" ) != null )
			{
				Enumeration e = request.getParameterNames();
				if(e != null && e.hasMoreElements())
				{				
					StringBuffer sb = new StringBuffer();
					
					sb.append( "\nObject Submission" );
				
					String uName = (String) session.getValue( "name" );
					String fname = null;
					
					if (uName != null && uName.length() != 0)  // logged in
					{	
						fname = JSPUtils.getPlayer( uName ).getName();		
						sb.append( " from " + fname );
					}
					else
					{
						fname = "Anonymous";
					}
					
					sb.append( ":\n\n" );
					
					while(e.hasMoreElements())
					{
						String k = (String) e.nextElement();
						
						if( !k.equalsIgnoreCase("object") && !k.equalsIgnoreCase("action") )  // hidden input
						{
							sb.append( k );
							sb.append( " :\t" );
							sb.append( (String) request.getParameter( k ) );
							sb.append( "\n" );
						}
					}				
					
					SMTP mailer = new SMTP( (String) Key.instance().getConfig().getProperty( "emailServer" ) );

					mailer.send( "forest@realm.progsoc.uts.edu.au", "objects@realm.progsoc.uts.edu.au", "Object Submission", sb.toString() );
					
					Log.log( EmailSetter.EMAIL_LOG, "Object submission sent to objects@realm.progsoc.uts.edu.au from: " + request.getRemoteAddr() );
					Log.log( "te0", fname + " submitted an object" );				
				}
				%>
				<p>
				Thank you for your submission.
				<p>
				It will now be reviewed and a price will be determined , at which point you will be informed and given the chance to purchase this object.
				<%
			}
			else
			{
			%>
			&nbsp;<br>To design an Object that you would like to submit for creation on Forest, complete the following easy steps.<p>
			First choose an object type:
			<form method=post action="<%=request.getRequestURI()%>">
			<table>
			  <tr><td align=center><input type=submit name=type value="Nothing"></td><td>-</td><td> <small>does nothing<br>(eg. some belly button lint)</small></td></tr>
			  <tr><td align=center><input type=submit name=type value="Use"></td><td>-</td><td> <small>used on it's own, and is not worn, wielded or sat on<br>(eg. a can of coke)</small></td></tr>
			  <tr><td align=center><input type=submit name=type value="Wear"></td><td>-</td><td> <small>worn on the person somewhere<br>(eg. a coat)</small></td></tr>
			  <tr><td align=center><input type=submit name=type value="Wield"></td><td>-</td><td> <small>held somewhere on your body and used on another person<br>(eg a baseball bat)</small></td></tr>
			  <tr><td align=center><input type=submit name=type value="Seat"></td><td>-</td><td> <small>dropped and then sat on<br>(eg. a chair)</small></td></tr>
			  <tr><td align=center><input type=submit name=type value="Command"></td><td>-</td><td> <small>performs a command<br>(eg. a library card - "trans city/library")</small></td></tr>
			</table>
			<input type=hidden name=action value=submit>
			</form>	
			<%
			}
		}			
		else // review is the default
		{
			Container tbin = (Container) te0.getApproved().getElement( "objects" );
			
			Article review = (Article) tbin.getElementAt( 0 );
			
			if( review==null || review.isEmpty() )
			{
			%>
			<center>There is no current Object Review.</center>
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
						<font size="3" color="yellow">Object Review: <%=review.title%></font>
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
	Log.debug( "objects.jsp", e.toString() );
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
