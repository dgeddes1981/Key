<%@ import="key.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.web.*" %>

<!--#INCLUDE file="../laf_top.html" --!>

The current date is <%= new DateTime().toString() %>.
<P>

<%
	Player p = JSPUtils.getPlayer( out, request.getParameter( "player" ) );
	if( p != null )
	{
		%><%=Converter.convert( p.getPrefix() + p.getTitledName() )%><BR><%
		
		if( p.connected() )
		{
			%><%=p.getFullName()%> has been logged in for <%=p.loginStats.getTimeSinceConnection()%><BR><%
		}
		else
		{
			DateTime dateTime = p.loginStats.getLastDisconnection();

			if( dateTime == null )
			{
				%><%=p.getFullName()%> has never logged in.<BR><%
			}
			else
			{
				%><%=p.getFullName()%> was last seen at <%=dateTime.toString( p )%><BR><%
			}
			
			DateTime lc = p.loginStats.getLastConnection();
			if( lc != null )
			{
				%><%=p.getName()%> was last connected for <%=lc.difference( dateTime ).toTruncString()%><BR><%
			}
		}

		if( !p.isHideTime() )
		{
			%><%=p.HisHer()%> total login time is <%=p.loginStats.getTotalConnectionTime()%><BR><%
			String ol = p.getOldLoginTime();
			if( ol != null && ol.length() > 0 )
			{
				%><%=p.HisHer()%> pre-eclipse login time was <%=p.getOldLoginTime()%><BR><%
			}
		}
		
		if( !p.isEmailPrivate() )
		{
			String ea = p.getEmailAddress();
			%><%=p.HisHer()%> email address is: <a ="mailto:<%=ea%>"><%=ea%></a><BR><%
		}
		
		{
			String wp = p.getWebpage().get();
			if( wp != null )
			{
				%><%=p.HisHer()%> web page is: <a href="<%=wp%>"><%=wp%></a><BR><%
			}
		}
		
		{
			Paragraph plan = p.getPlan();
			
			if( plan != null && !plan.isEmpty() )
			{
				%><%=Converter.convert( p.planHeading )%><%
				%><%=Converter.convert( p.getPlan() )%><%
				%><%=Converter.convert( LineParagraph.LINE )%><%
			}
		}
%>
<%
	}
%>
</BODY>
</HTML>
