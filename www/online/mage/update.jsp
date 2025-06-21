<%@import="java.io.*"%>
<HTML>
<BODY>
<H1>Update from CVS via the web</H1>
<HR>
<%
	if( request.getParameter( "do" ) != null )
	{
			// start command running
		Process proc = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c","cd www;cvs update"});
		
		// get command's output stream and
		// put a buffered reader input stream on it
		
		InputStream istr = proc.getInputStream();
		InputStream estr = proc.getErrorStream();
		BufferedReader br =
			new BufferedReader(new InputStreamReader(istr));
		BufferedReader ebr =
			new BufferedReader(new InputStreamReader(estr));
		
		// read output lines from command
		%><H2>stdout</H2><BR><PRE><%
		
		String str;
		while ((str = br.readLine()) != null)
		{
			%><%=str%><BR><%
		}
		
		%></PRE><P><HR><H2>stderr</H2><BR><PRE><%
		
		while ((str = ebr.readLine()) != null)
		{
			%><%=str%><BR><%
		}
		
		%></PRE><P><HR><P><%
		
		// wait for command to terminate

		try
		{
			proc.waitFor();
		}
		catch (InterruptedException e)
		{
			%><%="<B>process was interrupted</B><BR>"%><%
		}

		// check its exit value

		if (proc.exitValue() != 0)
		{
			%><%="<B>exit value was non-zero</B><BR>"%><%
		}
		
		// close stream
		
		br.close();
	}
	else
	{
	%>
		This script will execute a 'cvs update' on the www directory of realm.
		<P>
		<A HREF="<%=request.getRequestURI() + "?do=true"%>">Do CVS Update</A>
		<P>
	<%
	}
%>
</BODY>
</HTML>
