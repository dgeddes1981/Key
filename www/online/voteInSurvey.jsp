<%@ import="key.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.web.*" %>
<%@ import="key.talker.Survey" %>

<!--#INCLUDE file="../laf_top.html" --!>

<P>

<%
	Player p = Player.getCurrent();
	if( p == null )
	{
		%><B>You must log in before voting in the survey</B><%
	}
	else
	{
		String servId = request.getParameter( "name" );
		if( name != null && name.length() > 0 )
		{
			Container c = (Container) new Search( "/online/surveys/", null ).result;
			Survey s = c.getExactElement( servId );
			
			if( s != null )
			{
				%><B>Survey: </B><%=s.getId()%><P>
				<%=JSPUtils.convert( s.getTopic() );%><P>
			}
		}
		else
		{
			%><B>You must specify a 'name' parameter for the name of the survey to vote in</B><%
		}
%>
</BODY>
</HTML>
