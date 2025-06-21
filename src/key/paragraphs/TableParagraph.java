package key;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 */
public final class TableParagraph extends Paragraph
{
		//  META: this is a security risk -
		//   do not allow direct access to the column array.
	Column columns[];
	private Vector rows;
	private String footer;
	
	TableParagraph( Generator p )
	{
		columns = p.columns;
		rows = p.rows;
		rows.trimToSize();
		footer = p.footer;
	}
	
	public Column getColumnAt( int x )
	{
		return( columns[x] );
	}

	public int countColumns()
	{
		return( columns.length );
	}

	TableParagraph( TableParagraph p )
	{
		columns = p.columns;
		rows = p.rows;
		footer = p.footer;
	}
	
	public Enumeration elements()
	{
		return( rows.elements() );
	}
	
	public int count()
	{
		return( rows.size() );
	}
	
	public String getFooter()
	{
		return( footer );
	}
	
		//  this isn't a security violation as it doesn't
		//  modify this table
	public Paragraph substitute( String[] codes )
	{
		String s = Grammar.substitute( footer, codes );
		
		if( s != footer )
		{
			TableParagraph tp = new TableParagraph( this );
			tp.footer = s;
			tp.footer = Grammar.substitute( footer, codes );
			return( tp );
		}
		else
			return( this );
	}
	
	public String toString()
	{
		return( "TableParagraph" );
	}
	
	public static class Column implements java.io.Serializable
	{
		private String heading;
		private int width;

		public Column( String heading, int width )
		{
			this.heading = heading;
			this.width = width;
		}

		public int getWidth()
			{ return( width ); }
		
		public String getHeading()
			{ return( heading ); }
	}
	
	public static class Generator
	{
		Column columns[];
		Vector rows;
		String footer;
		
		public Generator()
		{
			columns = new Column[0];
			
			rows = new Vector( 60, 60 );
			footer = "";
		}
		
		public Generator( TableParagraph p )
		{
			columns = p.columns;
			rows = p.rows;
			footer = p.footer;
		}
		
		public Generator( Column columnDefinition[] )
		{
			columns = columnDefinition;
			
			rows = new Vector( 60, 60 );
		}
		
		public void setFooter( String f )
		{
			footer = f;
		}
		
		public void appendRow( String[] rowValues )
		{
			if( rowValues.length != columns.length )
				throw new UnexpectedResult( "number of row values is not equal to the number of headings" );
			
			rows.addElement( rowValues );
		}
		
		public int count()
		{
			return( rows.size() );
		}
		
		public TableParagraph getParagraph()
		{
			return( new TableParagraph( this ) );
		}
		
		public int countColumns()
		{
			return( columns.length );
		}

	}
}
