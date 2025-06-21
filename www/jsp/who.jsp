<%@ import="key.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.web.*" %>
<%@ import="java.util.Enumeration" %>

<!--#INCLUDE file="../laf_top.html" --!>

<P>

<TABLE width=100% border=1>
<TR>
	<TH>Name</TH>
	<TH>Clan</TH>
	<TH>Idle</TH>
	<TH>Homepage</TH>
</TR>
<%
	int count = 0;
	for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
	{
		Player scan = (Player) e.nextElement();
		count++;
		
		{
			String name = scan.getName();
%>
	<TR>
	<TD><A href="/finger.jsp?player=<%=name%>"><%=name%></a></TD>
<%
		}
		
		{
			Clan c = scan.getClan();
			if( c != null )
			{
				String n = c.getName();
%>
	<TD><A href="/clanexamine.jsp?clan=<%=n%>"><%=n%></A></TD>
<%
			}
			else
			{
%>
	<TD>&nbsp;</TD>
<%
			}
		}
		
		{
			Duration d = scan.getIdle( new DateTime() );
%>
	<TD><%=d.toShortString()%></TD>
<%
		}
		
		{
			String s = scan.getWebpage().get();
			if( s != null )
			{
%>
	<TD><A HREF="<%=s%>"><%=s%></A></TD>
<%
			}
			else
			{
%>
	<TD>&nbsp;</TD>
<%
			}
		}
		
%>
</TR>
<%
	}
%>

</TABLE>
<P>
<CENTER>

	<% if( count == 1 ) { %>

<I>Only one person online at the moment</I>

	<% } else if( count == 0 ) { %>

<I>There aren't any people online at the moment</I>

	<% } else { %>

<I><%=count%> people online at the moment</I>

	<% } %>

</CENTER>
</P>
<BR>
<BR>

</BODY>
</HTML>
