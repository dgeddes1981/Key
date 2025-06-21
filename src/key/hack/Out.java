/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
**
**  $Id: Out.java,v 1.2 2000/03/28 16:36:54 subtle Exp $
**
**  $Log: Out.java,v $
**  Revision 1.2  2000/03/28 16:36:54  subtle
**  blah
**
**  Revision 1.1  2000/03/22 21:17:57  subtle
**  Out finished and is no longer cd
**
**
*/

package key;

import key.*;
import java.io.*;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Out extends Command
{
	public Out()
	{
		setKey( "out" );
		usage = "<command line>";
	}

	/**
	  *  Um.  You need to know what you're doing to use this.  This command can
	  *  also completely bypass beyond if you're really clever.  If you're know
	  *  enough to do this you probably deserve beyond anyway.
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( ic instanceof SocketIC )
		{
			ic.sendFeedback( "The world seems to scream as you tear a small hole in the pattern and step out.  You may type Java directly." );
			
				//  Sends a notification
			for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
			{
				Player g = (Player) e.nextElement();

				if( g != p && g.isBeyond() )
					g.sendFeedback( "The pattern ripples slightly." );
			}
			
			PrintWriterPrintStream pwps = new PrintWriterPrintStream( new PrintWriter( ((SocketIC)ic).output ) );
			bsh.Interpreter interp = new bsh.Interpreter( ((SocketIC)ic).inputStream, pwps, pwps, true );
			
			try
			{
				interp.eval( "import key.*;" );
				interp.eval( "in( String c ) { Player.getCurrent().command( c, Player.getCurrent().getConnection(), false ); }" );
				interp.eval( "show();" );
				interp.eval( "print( \"Your environment should now be set up.\" );" );
				interp.eval( "in( \"say I'm out, dude\" );" );
			}
			catch( Exception ex )
			{
				ic.printStackTrace( ex );
			}
			
			interp.run();
		}
		else
			ic.sendFailure( "Wrong connection type" );
	}
	
		//  A delegation class for PrintStream.  This should be automatic
		//  (if we had a Real Compiler)
	class PrintWriterPrintStream extends java.io.PrintStream
	{
		PrintWriter pw;
		
		PrintWriterPrintStream( PrintWriter p )
		{
				//  just garbage
			super( new ByteArrayOutputStream() );
			pw = p;
		}
		
		public void flush() { pw.flush(); }
		public void close() { pw.close(); }
		public boolean checkError() { return( pw.checkError() ) ; }
		public void write(int b) { pw.write( b ); }
		public void write(byte buf[], int off, int len)
		{
			for( int i = off; i < off+len; i++ )
				pw.write( (char) buf[i] );
		}
		public void print(boolean b) { pw.print( b ); }
		public void print(char c) { pw.print( c ); }
		public void print(int i) { pw.print( i ); }
		public void print(long l) { pw.print( l ); }
		public void print(float f) { pw.print( f ); }
		public void print(double d) { pw.print( d ); }
		public void print(char s[]) { pw.print( s ); }
		public void print(String s) { pw.print( s ); }
		public void print(Object obj) { pw.print( obj ); }
		public void println() { pw.println(); }
		public void println(boolean x) { pw.println( x ); }
		public void println(char x) { pw.println( x ); }
		public void println(int x) { pw.println( x ); }
		public void println(long x) { pw.println( x ); }
		public void println(float x) { pw.println( x ); }
		public void println(double x) { pw.println( x ); }
		public void println(char x[]) { pw.println( x ); }
		public void println(String x) { pw.println( x ); }
		public void println(Object x) { pw.println( x ); }
	}
}
