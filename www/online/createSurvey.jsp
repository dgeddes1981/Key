<%@ import="key.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.web.*" %>
<%@ import="key.talker.*" %>

<!--#INCLUDE file="../laf_top.html" --!>

<P>

<%
	String name = request.getParameter( "name" );
	String modify = request.getParameter( "modify" );
	
	String description = request.getParameter( "description" );
	if( name != null && name.length() > 0 && modify == null )
	{
		Survey s = (Survey) Factory.makeAtom( Survey.class, name );
		Container c = (Container) new Search( "/online/surveys", null ).result;
		try
		{
			c.add( s );
			
			%><B>Survey <%=s.getId()%> created</B><%
		}
		catch( Exception e )
		{
			e.printStackTrace( out );
		}
	}
	else
	{
		//Survey s = (Survey) Factory.makeAtom( Survey.class, name );
		
		%>
		<FORM METHOD="POST" ACTION=<%=response.encodeURL( request.getRequestURI() )%>">
		<P>
		<TABLE>
		<TR>
			<TD>Survey Identifier<BR>(unique name):</TD>
			<TD><INPUT max=15 name="name"></TD>
		</TR>
		<TR>
		<TD>Survey topic:</TD><TD><INPUT width=60 name="topic"></TD>
		</TR>
		<TD valign=top>Survey description:</TD>
		<TD>
			<TEXTAREA rows=10 cols=60 name="description"></TEXTAREA>
		</TD>
		</TR>
		<TR>
		<TD valign=top>Survey Options:</TD>
		<TD>
			1: <INPUT width=60 name="option1"><BR>
			2: <INPUT width=60 name="option2"><BR>
			3: <INPUT width=60 name="option3"><BR>
			4: <INPUT width=60 name="option4"><BR>
			5: <INPUT width=60 name="option5"><BR>
			6: <INPUT width=60 name="option6"><BR>
			7: <INPUT width=60 name="option7"><BR>
		</TD>
		</TR>
		</TABLE>
		<P>
		<INPUT TYPE=Submit VALUE="Create Survey">
		</FORM>
		<%
	}
%>
</BODY>
</HTML>
