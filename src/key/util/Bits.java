package key.util;

import java.io.*;

public final class Bits
	implements Cloneable,
	java.io.Serializable
{
    private final static int BITS_PER_UNIT = 6;
    private final static int MASK = (1<<BITS_PER_UNIT)-1;
    private long bits[];
	
	private int lowestSet;
	
    /**
     * Convert bitIndex to a subscript into the bits[] array.
     */
    private static int subscript(int bitIndex)
	{
		return bitIndex >> BITS_PER_UNIT;
    }
	
    /**
     * Convert a subscript into the bits[] array to a (maximum) bitIndex.
     */
    private static int bitIndex(int subscript)
	{
		return (subscript << BITS_PER_UNIT) + MASK;
    }
	
    /**
     * Creates an empty set.
     */
    public Bits()
	{
		this(1 << BITS_PER_UNIT);
    }

    /**
     * Creates an empty set with the specified size.
     * @param nbits the size of the set
     */
    public Bits(int nbits)
	{
		/* nbits can't be negative; size 0 is OK */
		if (nbits < 0)
		{
			throw new NegativeArraySizeException(Integer.toString(nbits));
		}
		
		/* On wraparound, truncate size; almost certain to o-flo memory. */
		if (nbits + MASK < 0)
		{
			nbits = Integer.MAX_VALUE - MASK;
		}
		
		/* subscript(nbits + MASK) is the length of the array needed to hold nbits */
		bits = new long[subscript(nbits + MASK)];
		
		lowestSet = Integer.MAX_VALUE;
    }

    /**
     * Ensures that the Bits can hold at least an nth bit.
     * This cannot leave the bits array at length 0.
     * @param	nth	the 0-origin number of the bit to ensure is there.
     */
    private void ensureCapacity(int nth)
	{
		/* Doesn't need to be synchronized because it's an internal method. */
		int required = subscript(nth) + 1;	/* +1 to get length, not index */
		if (required > bits.length)
		{
			/* Ask for larger of doubled size or required size */
			int request = Math.max(2 * bits.length, required);
			long newBits[] = new long[request];
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			bits = newBits;
		}
    }

    /**
     * Sets a bit.
     * @param bit the bit to be set
     */
    public void set(int bit)
	{
		if (bit < 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(bit));
		}
		
		synchronized (this)
		{
			ensureCapacity(bit);
			bits[subscript(bit)] |= (1L << (bit & MASK));
			
			if( bit < lowestSet )
				lowestSet = bit;
		}
    }

    /**
     * Clears a bit.
     * @param bit the bit to be cleared
     */
    public void clear(int bit)
	{
		if( bit < 0 )
			throw new IndexOutOfBoundsException( Integer.toString(bit) );
		
		if( subscript(bit) >= bits.length )
			return;
		
		synchronized( this )
		{
			ensureCapacity(bit);
			bits[subscript(bit)] &= ~(1L << (bit & MASK));

			if( bit == lowestSet )
			{
				lowestSet = Integer.MAX_VALUE;
			}
		}
    }
	
	public int firstSet()
	{
		if( lowestSet == Integer.MAX_VALUE )
		{
			int bitsLength = bits.length;
			int i;
			
			for( i = 0; i < bitsLength; i++ )
			{
				if( bits[i] != 0 )
				{
					long bi = bits[i];
					long j;
					int added = i*64;
					
					int c = 32;
					
					do
					{
						j = (bi << c);
						
						if( (bi << c) == 0 )
							added += c;
						else
							bi = j;
						
						c >>= 1;
					} while( c != 0 );
					
					return( added );
					//lowestSet = added;
					//break;
				}
			}
			
			lowestSet = Integer.MAX_VALUE;
		}
		
		return( lowestSet );
	}
	
    /**
     * Gets a bit.
     * @param bit the bit to be gotten
     */
    public boolean get(int bit)
	{
		if (bit < 0)
		{
			throw new IndexOutOfBoundsException(Integer.toString(bit));
		}
		boolean result = false;
		synchronized (this)
		{
			int n = subscript(bit);		/* always positive */
			if (n < bits.length)
			{
				result = ((bits[n] & (1L << (bit & MASK))) != 0);
			}
		}
		return result;
    }

    /**
     * Logically ANDs this bit set with the specified set of bits.
     * @param set the bit set to be ANDed with
     */
    public void and(Bits set) {
	/*
	 * Need to synchronize  both this and set.
	 * This might lead to deadlock if one thread grabs them in one order
	 * while another thread grabs them the other order.
	 * Use a trick from Doug Lea's book on concurrency,
	 * somewhat complicated because Bits overrides hashCode().
	 */
	if (this == set) {
	    return;
	}
	Bits first = this;
	Bits second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int bitsLength = bits.length;
		int setLength = set.bits.length;
		int n = Math.min(bitsLength, setLength);
		for (int i = n ; i-- > 0 ; ) {
		    bits[i] &= set.bits[i];
		}
		for (; n < bitsLength ; n++) {
		    bits[n] = 0;
		}
	    }
	}
    }

    /**
     * Logically ORs this bit set with the specified set of bits.
     * @param set the bit set to be ORed with
     */
    public void or(Bits set) {
	if (this == set) {
	    return;
	}
	/* See the note about synchronization in and(), above. */
	Bits first = this;
	Bits second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int setLength = set.bits.length;
		if (setLength > 0) {
		    ensureCapacity(bitIndex(setLength-1));
		}
		for (int i = setLength; i-- > 0 ;) {
		    bits[i] |= set.bits[i];
		}
	    }
	}
    }

    /**
     * Logically XORs this bit set with the specified set of bits.
     * @param set the bit set to be XORed with
     */
    public void xor(Bits set) {
	/* See the note about synchronization in and(), above. */
	Bits first = this;
	Bits second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int setLength = set.bits.length;
		if (setLength > 0) {
		    ensureCapacity(bitIndex(setLength-1));
		}
		for (int i = setLength; i-- > 0 ;) {
		    bits[i] ^= set.bits[i];
		}
	    }
	}
    }

    /**
     * Gets the hashcode.
     */
    public int hashCode() {
	long h = 1234;
	synchronized (this) {
	    for (int i = bits.length; --i >= 0; ) {
		h ^= bits[i] * (i + 1);
	    }
	}
	return (int)((h >> 32) ^ h);
    }
    
    /**
     * Calculates and returns the set's size in bits.
     * The maximum element in the set is the size - 1st element.
     */
    public int size() {
	/* This doesn't need to be synchronized, since it just reads a field. */
	return bits.length << BITS_PER_UNIT;
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to compare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Bits)) {
	    if (this == obj) {
		return true;
	    }
	    Bits set = (Bits) obj;
	    /* See the note about synchronization in and(), above. */
	    Bits first = this;
	    Bits second = set;
	    if (System.identityHashCode(first) > System.identityHashCode(second)) {
		first = set;
		second = this;
	    }
	    synchronized (first) {
		synchronized (second) {
		    int bitsLength = bits.length;
		    int setLength = set.bits.length;
		    int n = Math.min(bitsLength, setLength);
		    for (int i = n ; i-- > 0 ;) {
			if (bits[i] != set.bits[i]) {
			    return false;
			}
		    }
		    if (bitsLength > n) {
			for (int i = bitsLength ; i-- > n ;) {
			    if (bits[i] != 0) {
				return false;
			    }
			}
		    } else if (setLength > n) {
			for (int i = setLength ; i-- > n ;) {
			    if (set.bits[i] != 0) {
				return false;
			    }
			}
		    }
		}
	    }
	    return true;
	}
	return false;
    }

    /**
     * Clones the Bits.
     */
    public Object clone() {
	Bits result = null;
	synchronized (this) {
	    try {
		result = (Bits) super.clone();
	    } catch (CloneNotSupportedException e) {
		// this shouldn't happen, since we are Cloneable
		throw new InternalError();
	    }
	    result.bits = new long[bits.length];
	    System.arraycopy(bits, 0, result.bits, 0, result.bits.length);
	}
	return result;
    }

    /**
     * Converts the Bits to a String.
     */
    public String toString()
	{
	int count = 0;
	
	synchronized (this)
	{
	    int limit = size();
		
	    for (int i = 0 ; i < limit ; i++)
		{
			if (get(i))
			count++;
	    }
	}
	return ("" + count);
    }

	/*
	public static void main( String args[] )
	{
		Bits bits = new Bits();
		int r = 0;
		DataInputStream dis = new DataInputStream( System.in );
		
		System.out.println( "bitting program" );
		
		do
		{
			System.out.print( "enter bit, or '?': " );
			
			try
			{
				String s = dis.readLine();
				
				if( s.charAt(0) == '?' )
				{
					System.out.println( bits.toString() );
					continue;
				}
				
				r = Integer.parseInt( s );
			}
			catch( Exception e )
			{
				System.out.println( e.toString() );
				continue;
			}
			
			if( bits.get( r ) )
			{
				bits.clear( r );
				System.out.println( "Cleared bit " + r );
			}
			else
			{
				bits.set( r );
				System.out.println( "Set bit " + r );
			}
			
			System.out.println( "First bit set is " + bits.firstSet() );
		} while( r >= 0 );
	}
	*/
}
