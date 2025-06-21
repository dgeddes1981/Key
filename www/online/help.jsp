<%@ import="key.*" %>
<%@ import="key.primitive.*" %>
<%@ import="key.web.*" %>
<%@ import="java.util.Enumeration" %>

<!--#INCLUDE file="../laf_top.html" --!>

<P>

<%
	Container helpC = (Container) Key.instance().getOnline().getElement( "help" );
	
	if( helpC == null )
	{
		%>
		<B>/online/help not found!</B>
		<HR>
		<%
		return;
	}
	
	String file = request.getParameter( "file" );
	if( file != null && file.length() > 0 )
	{
		Screen help = (Screen) helpC.getElement( file );

		if( help == null )
		{
			%><B>Help file '<%=file%>' not found!</B><%
			return;
		}
		
		out.println( Converter.convert( help.aspect() ) );

		%>
		<P>
		<CENTER>
		[ <A HREF="">Edit</A> | <A HREF="">Delete</A> ]
		</CENTER>
		<P>
		<%
		return;
	}
	
%>
<TABLE width=100% border=1>
<TR>
	<TH>name</TH>
	<TH>aliases</TH>
	<TH>author</TH>
	<TH>last modified</TH>
</TR>
<%
	
	int count = 0;
	
	for( Enumeration e = helpC.elements(); e.hasMoreElements(); )
	{
		count++;
		
		Screen help = (Screen) e.nextElement();
		String pName = help.getName();
		Object o = help.getKey();
		
		String aliases;
		if( o instanceof AliasListKey )
		{
			AliasListKey alk = (AliasListKey) o;
			if( alk.countSecondaries() > 0 )
				aliases = Grammar.commaSeperate( alk.secondaryKeys() );
			else
				aliases = "&nbsp;";
		}
		else
			aliases = "&nbsp;";
		
		String auth = help.getAuthor();
		if( auth == null || auth.length() == 0 )
			auth = "&nbsp;";
		
		%>
		<TR>
		<TD><A HREF="help.jsp?file=<%=pName%>"><%=pName%></A></TD>
		<TD><%=aliases%></TD>
		<TD><%=auth%></TD>
		<TD><%=help.getModified().toString( Player.getCurrent() )%></TD>
		</TR>
		<%
	}
%>

</TABLE>
<P>
<CENTER>

	<% if( count == 1 ) { %>

<I>Only one help file</I>

	<% } else if( count == 0 ) { %>

<I>No help files listed</I>

	<% } else { %>

<I><%=count%> help files</I>

	<% } %>

</CENTER>
</P>
<BR>
<BR>

</BODY>
</HTML>

