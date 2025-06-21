
package key.web;

import com.mortbay.Base.*;
import com.mortbay.HTML.*;
import com.mortbay.HTTP.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LaF extends Page
{
	public static boolean alternate;
	
	public static Composite contact = new Composite();
	public static Composite alternateContact = new Composite();
	public static PathMap sectionMap = new PathMap();
	public static Hashtable headingMap = new Hashtable();
	
	static
	{
		alternate=false;
		contact.add(new Link("http://www.mortbay.com",
			 "<B>Jetty by Mort Bay Consulting</B>"));
		contact.add("<BR>AU: Pty. Ltd. ACN 069204815");
		contact.add("<BR>GB: Ltd. No 3396994");
		contact.add("<BR>");
		contact.add(new Link("http://www.mortbay.com",
					  "http://www.mortbay.com"));
		contact.add("<BR>");
		contact.add(new Link("mailto:mortbay@mortbay.com",
					 "mortbay@mortbay.com"));

		alternateContact.add(new Link("mailto:mortbay@mortbay.com",
			 "<B>Mort Bay Consulting</B>"));
		alternateContact.add( " AU: Pty. Ltd. ACN 069204815");
		alternateContact.add( " GB: Ltd. No 3396994 ");
		alternateContact.add(new Link("http://www.mortbay.com",
						  "http://www.mortbay.com"));

		sectionMap.put("/Jetty/Info","Info");
		sectionMap.put("/Jetty/Demo","Demo");
		sectionMap.put("/Jetty/Program","Program");
		sectionMap.put("/Jetty/Config","Config");
		sectionMap.put("/Jetty/Home","Home");

		headingMap.put("Info","Jetty Information");
		headingMap.put("Demo","Jetty Demonstrations");
		headingMap.put("Program","Jetty Development");
		headingMap.put("Config","Jetty Configuration");
	}
	
	static Element mbImage=null;
	static Element logo=null;
	
	private static final String[][] navigation =
	{
		{"Up",null},
		{"Home","/"},
		{"Info","/Jetty/Info"},
		{"Demo","/Jetty/Demo"},
		{"Program","/Jetty/Program"},
		{"Config","/Jetty/Config"},
		{"Help",null},
	};	
	

	Composite header=new Composite();
	Composite content=new Composite();
	Composite footer=new Composite();
	
	public LaF()
	{
		properties.put(Home,"/");
		setBackGroundColor("white");
		setBase("_top",null);
		
		setSection(Header,header);
		setSection(Content,content);
		setSection(Footer,footer);
		
		add(header);
		nest(content);
		if (!alternate)
			nest(new Block(Block.Center));
	}
	
	/** Complete Header and footer setion
	 * Called just before the page is output (after all content added).
	 */
	public void completeSections()
	{
		// Finish adding to content
		unnest();
		add(footer);
		
		// Determine Section
		String section = (String)properties().get(Section);
		String sectionPath = null;
		
		HttpServletRequest request =
			(HttpServletRequest)properties().get(Request);

		if (request!=null)
		{
			sectionPath = sectionMap.longestMatch(request.getServletPath()+
							  request.getPathInfo());
			
			if (section==null && sectionPath!=null)
			section = (String)sectionMap.get(sectionPath);
		}
				
		// check heading
		String heading = (String)properties().get(Heading);
		if (heading==null ||
			heading.length()==0 ||
			heading.equals(Page.NoTitle))
		{
			if (section!=null)
			heading = (String)headingMap.get(section);
			if (heading==null)
			heading = "Jetty Demo";
		}
		
		// check up
		String up = (String)properties().get(Up);
		if (up==null && sectionPath!=null)
		properties().put(Up,sectionPath);
	
		// check images and applets 
		if (mbImage==null)
		{
		mbImage = new Image("FileBase","/Images/mbLogoSmall.gif")
			.border(0);
		mbImage = new Link("/",mbImage);
		}
		
		if( logo==null )
		{
			logo=mbImage;
		}

		// Build header
		Table grid = new Table(0);
		grid.width("100%");
		grid.newRow();
		grid.newCell().cell().add("Home".equals(section)?
					  (Element)logo:
					  (Element)mbImage)
		.left().width("30%").bottom();
		grid.newCell().cell()
		.nest(new Block(Block.Bold))
		.center().width("40%").bottom();
	
		if ("Home".equals(section))
		{
			grid.cell()
				.add(new Font(3,true).face("Helvetica")
				.add("Mort Bay Consulting's<P>"))
				.add(new Font(4,true).face("Helvetica")
				.add("JETTY<BR>Java HTTP Server"));
		}
		else
		{
			grid.cell()
				.add(new Font(4,true).face("Helvetica")
				.add(heading));
		}
		
		grid.newCell().cell()
		.nest(new Font(2).face("Helvetica"))
		.right().width("30%").bottom();

		Composite topRight = new Composite();
		if ("Home".equals(section))
		grid.cell().add(contact);
		else
		grid.cell().add(topRight);
		
		header.add(grid);
		header.add(Break.rule);

		// build footer & finish top right header
		footer.add(Break.rule);
		footer.nest(new Block(Block.Center));

		for( int i =0;i<navigation.length;i++ )
		{
			String label = navigation[i][0];
			String url = navigation[i][1];
			if (url==null)
				url=(String)properties().get(label);
			if (url!=null && url.length()>0)
			{
				if (label.equals(section))
				{
				footer.add("<B>");
				topRight.add("<B>");
				}
			
				footer.add(new Link(url,label));
				topRight.add(new Link(url,label));

				if (label.equals(section))
				{
				footer.add("</B>");
				topRight.add("</B>");
				}
			
				footer.add("&nbsp;");
				topRight.add("<BR>");
			}
		}
	}

	public FrameSet frameSet()
	{
		FrameSet frameSet=new FrameSet( "LaF.Frames", null, "150,*,70" );
		frameSet.nameFrame(Page.Header,0,0).scrolling(false);
		frameSet.nameFrame(Page.Content,0,1);
		frameSet.nameFrame(Page.Footer,0,2).scrolling(false);
		frameSet.border(false,0,null);
		
		return frameSet;
	}
}
