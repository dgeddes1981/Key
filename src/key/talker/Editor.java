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
**  $Id: Editor.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  01Jun96     adamb        added all the commands in.
**                           the loop for some of them isn't
**                           available yet, but will be.
**
**  02Jun96     paulm        fixed a bug which meant blank
**                           lines were added to the end of
**                           the buffer during the edit
**
**  03Jun96     adamb        merged Paul's update with the 
**                           editor.
**
**  05Jun96     adamb        fixed the stringBuffer to StringTokenizer
**                           code so that blank lines are niether
**                           added nor deleted.
**
**  05Jun96     adamb        added the looping for some commands and
**                           also the elimination of the .g command.
**                           ie: .5 to goto line #5.
**                           Added .s(tats) command.
*/

package key;

import key.util.LinkedList;

import java.util.*;

/**
  *  Kind of hacked to edit paragraphs - doesn't do them
  *  properly, but then, what does?
 */
public class Editor
{
	public static final MultiParagraph intro;
	
	static
	{
		MultiParagraph.Generator i = new MultiParagraph.Generator();
		i.append( LineParagraph.LINE );
		i.append( new TextParagraph( TextParagraph.LEFT, "Entering the forest editor <footnote 10>:", 0, 0, 0, 0 ) );
		i.append( new TextParagraph( TextParagraph.LEFT, "If you're not sure how to use the editor, enter '.quit' immediately (without the quotes), and read 'help editor'", 2, 0, 0, 0 ) );
		i.append( BlankLineParagraph.BLANKLINE );
		intro = i.getParagraph();
	}
	
	static public Paragraph edit( Player p, Paragraph buffer, InteractiveConnection ic, int maxLines, int maxBytes )
	{
		String startBuffer;
		int alignment = TextParagraph.LEFT;
		LinkedList lines = new LinkedList();
		int currentBytes=0;
		
		if( buffer instanceof TextParagraph )
			startBuffer = ((TextParagraph)buffer).getText();
		else
		{
			//ic.sendError( "This editor can only edit single text paragraphs at the moment - previous data cleared" );
			//startBuffer = "";
			ic.sendError( "This editor can only edit single text paragraphs." );
			return( buffer );
		}
		
		//
		// The StringTokenizer when delim return is set to true, returns in 1
		// entry the line and the next contains the delim.
		// If the line is (null) then the line is NOT returned, only the delim.
		// Therefore, if you use the line, you then have to remove the delim.
		// so if you get a delim, it isn't treated as a delim, it's treated as
		// a blank line.
		//
		
		StringTokenizer st = new StringTokenizer( startBuffer, "\n", true );
		if( !st.hasMoreTokens() )
			lines.append( "" );
		while( st.hasMoreTokens() ) 
		{
			String strToken=st.nextToken();
			if(!strToken.startsWith("\n"))
			{
				lines.append(strToken);
				if(st.hasMoreTokens()) 
				{
					strToken=st.nextToken();
				}
			} 
			else 
			{
				lines.append("");				// a CR in the text means a blank line
			}
		}
		
		ic.send( intro, false );
		ic.sendFeedback( "Limitations:  " + maxLines + " lines, " + maxBytes + " characters." );
		ic.sendLine();
		
		String entered=null;
		LinkedList.Iterator cline=lines.iterator();
		cline.last();
		
		// if there isn't a CR at the end, place one there.
		if(!((String)cline.element()).equals("")) 
		{
			cline.insertAfter("");
			cline.last();
		}
		
		display( ic, lines, alignment );
		ic.sendLine();
		
		currentBytes = countBytes( lines );
		
		while( true )
		{
			try
			{
				entered = ic.input( "+ " );
			}
			catch( NetworkException e ) 
			{ 
				System.out.println( "disconnect from editor" ); 
				return( buffer );
			} 
			
			if( entered.startsWith( "." ) ) 
			{
				//
				// first all the singlar commands not affected by numerical repeating 
				//

				if(entered.equals(".quit")) 
				{ 
					//
					// Quit - Abort and Exit 
					//
					ic.sendFeedback( "Aborting edit..." ); 
					return( buffer );
				}
				else if( entered.equals( ".end" ) )
				{
					//
					// End - Save and Exit
					//
					ic.sendFeedback("Saving editor buffer...");
					StringBuffer r = new StringBuffer( "" );
					for( Enumeration e = lines.elements(); e.hasMoreElements(); )
					{
						r.append( (String) e.nextElement() );
						if( e.hasMoreElements() )
							r.append( '\n' );
					}
					
					return( new TextParagraph( alignment, r.toString() ) );
				}
				else if( entered.equalsIgnoreCase( ".centrealign" ) || entered.equalsIgnoreCase( ".centeralign" ) )
				{
					alignment = TextParagraph.CENTERALIGNED;
					ic.sendFeedback( "This paragraph will now be centre aligned" );
				}
				else if( entered.equalsIgnoreCase( ".leftalign" ) )
				{
					alignment = TextParagraph.LEFT;
					ic.sendFeedback( "This paragraph will now be left aligned" );
				}
				else if(entered.startsWith(".l")) 
				{
					//
					// Look - view the current line
					//
					ic.sendRaw((String)cline.element());
				}
				else if( entered.startsWith( ".v" ) )
				{
					//
					// View - view the entire buffer
					//
					display( ic, lines, alignment );
				}
				else if(entered.startsWith(".w")) 
				{
					//
					// Wipe - Erase the entire buffer
					//
					cline.first();
					lines.clear();
					cline.first();
					lines.append( "" );
					cline.last();
					alignment = TextParagraph.LEFT;
					ic.sendFeedback( "Editing Buffer Cleared." );
					currentBytes = 0;
				}
				else if(entered.startsWith(".t")) 
				{
					//
					// Top - Go to the top of the buffer
					//
					cline.first();
					ic.sendFeedback( "At Top of Buffer." );
				}
				else if(entered.startsWith(".b")) 
				{
					//
					// Bottom - Go to the end of the buffer
					//
					cline.last();
					ic.sendFeedback( "At Bottom of Buffer." );
				}
				else if(entered.startsWith(".s"))
				{
					//
					// Stats - Some statistic about your edit buffer
					//
					ic.sendFeedback( "Total Number of lines: " + lines.count() +" (max " + maxLines + "), total bytes: " + currentBytes + " (max " + maxBytes + ")" );
				}
				else if(entered.startsWith(".g")) 
				{
					//
					// Go - Go to a given line No.
					//
					if(entered.length()>2) 
					{
						if(Character.isDigit(entered.charAt(2))) 
						{
							Integer lineNo;
							try 
							{
								lineNo=Integer.valueOf(entered.substring(2));
							}
							catch(java.lang.NumberFormatException e) 
							{ 
								ic.sendError("Invalid Line No: " + e.getMessage()); 
								continue;
							} 
								// Integer lineNo=Integer.valueOf(entered.substring(2));
							if(lines.count()>=lineNo.intValue()) 
							{
								cline.first();
								for(int i=0;i<lineNo.intValue()-1;i++) 
								{
									cline.next();
								}
								ic.sendRaw((String)cline.element());
							} 
							else 
							{
								ic.sendError("Line Number " + lineNo.intValue() + " out of range.  Total Lines in buffer: " + lines.count());
							}
						} 
						else 
						{
							ic.sendError("Usage: .g<n>  ... where <n> is the line no. to go to.");
						}
					} 
					else 
					{
						ic.sendError("Usage: .g<n>  ... where <n> is the line no. to go to.");
					}
				}
				else
				{
					//
					// Now for the numerical looping commands, ie .5d
					//
					int loop;
					String numberString=extractNumber(entered);
					if(numberString.length() > 0)
					{
						Integer intLoop=Integer.valueOf(numberString);
						loop=intLoop.intValue();
						if(numberString.length()+1==entered.length())
						{
							if(lines.count()>=loop) 
							{
								cline.first();
								for(int i=0;i<loop-1;i++) 
								{
									cline.next();
								}
								ic.sendRaw((String)cline.element());
							} 
							else 
							{
								ic.sendError("Line Number " + loop + " out of range.  Total Lines in buffer: " + lines.count());
							}
							loop=0;
						}
					}
					else
					{
						loop=1;
					}
					for(int i=0;i<loop;i++)
					{
						if( entered.endsWith( "+" ) )
						{
							//
							// Go forward 1 line
							//
							if( cline.isLast() )
							{
								ic.sendError( "Already at the end of the buffer - can't go forward" );
							}
							else
							{
								cline.next();
								ic.sendFeedback( (String) cline.element() );
							}
						}
						else if( entered.endsWith( "-" ) )
						{
							//
							// Go back one line
							//
							if( cline.isFirst() )
							{
								ic.sendError( "Already at the start of the buffer - can't go back" );
							}
							else
							{
								cline.previous();
								ic.sendFeedback( (String) cline.element() );
							}
						}
						else if( entered.endsWith( "d" ) || entered.endsWith( "del" ) )
						{
							//
							// delete - Delete the current line
							//
							if( cline.isLast() )
							{
								ic.sendError( "At end of buffer - cannot delete" );
							}
							else
							{
								cline.remove();
								ic.sendFeedback( "Line deleted" );
							}
						}
						else
						{
							//
							// Okay ... command not found/
							//
							ic.sendError("Command: " + entered + " not found.");
							break;
						}
					}
					
					currentBytes = countBytes( lines );
				}
			}
			else
			{
				if( lines.count() > maxLines )
				{
					ic.sendError( "Too many lines, line NOT added." );
				}
				else if( currentBytes + entered.length() > maxBytes )
				{
					ic.sendError( "Too many characters in buffer, line NOT added." );
				}
				else
				{
					cline.insertBefore( entered );
					ic.send( entered );
					currentBytes += entered.length();
				}
			}
		}
	}
	
	static private void display( InteractiveConnection ic, LinkedList lines, int alignment )
	{
		for( Enumeration e = lines.elements(); e.hasMoreElements(); ) 
		{
			String s = (String)e.nextElement();
			
			if( e.hasMoreElements() ) 
			{
				ic.sendFeedback( s );
			}
		}
	}
	
	static private int countBytes( LinkedList lines )
	{
		int b = 0;
		
		for( Enumeration e = lines.elements(); e.hasMoreElements(); )
		{
			String s = (String) e.nextElement();
			
			b += s.length();
		}
		
		return( b );
	}
	
	static private String extractNumber(String entered)
	{
		int i;
		
		for( i = 1; i < entered.length(); i++ )
		{
			if( !Character.isDigit(entered.charAt(i)) )
			{
				break;
			}
		}
		
		return( entered.substring( 1, i ) );
	}
}
