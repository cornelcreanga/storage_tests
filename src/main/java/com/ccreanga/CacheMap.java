package com.ccreanga;

import java.util.Arrays;
import java.util.Map;

public class CacheMap {
    /** The array of keys. */
    protected transient byte[][] key;
    /** The array of values. */
    protected transient byte[][] value;
    /** The mask for wrapping a position counter. */
    protected transient int mask;
    /** Whether this set contains the key zero. */
    protected transient boolean containsNullKey;
    /** The current table size. */
    protected transient int n;

    protected transient int maxFill;
    /** Number of entries in the set (including the key zero, if present). */
    protected int size;
    /** The acceptable load factor. */
    protected final float f;


    public static float DEFAULT_LOAD_FACTOR = .75f;
    public static int DEFAULT_INITIAL_SIZE = 16;
    /**
     * Creates a new hash map.
     *
     * <p>
     * The actual table size will be the least power of two greater than
     * <code>expected</code>/<code>f</code>.
     *
     * @param expected
     *            the expected number of elements in the hash set.
     * @param f
     *            the load factor.
     */
    @SuppressWarnings("unchecked")
    public CacheMap(final int expected, final float f) {
        if (f <= 0 || f > 1)
            throw new IllegalArgumentException(
                    "Load factor must be greater than 0 and smaller than or equal to 1");
        if (expected < 0)
            throw new IllegalArgumentException(
                    "The expected number of elements must be nonnegative");
        this.f = f;
        n = arraySize(expected, f);
        mask = n - 1;
        maxFill = maxFill(n, f);
        key =  new byte[n + 1][];
        value = new byte[n + 1][];
    }

    public CacheMap(final int expected) {
        this(expected, DEFAULT_LOAD_FACTOR);
    }

    public CacheMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }



    private int realSize() {
        return containsNullKey ? size - 1 : size;
    }
    private void ensureCapacity(final int capacity) {
        final int needed = arraySize(capacity, f);
        if (needed > n)
            rehash(needed);
    }
    private void tryCapacity(final long capacity) {
        final int needed = (int) Math.min(
                1 << 30,
                Math.max(2, nextPowerOfTwo((long) Math.ceil(capacity
                        / f))));
        if (needed > n)
            rehash(needed);
    }

    private int insert(final byte[] k, final byte[] v) {
        int pos;
        if (((k) == null)) {
            if (containsNullKey)
                return n;
            containsNullKey = true;
            pos = n;
        } else {
            byte[] curr;
            final byte[][] key = this.key;
            // The starting point.
            if (!((curr = key[pos = (mix((k)
                    .hashCode())) & mask]) == null)) {
                if (((curr).equals(k)))
                    return pos;
                while (!((curr = key[pos = (pos + 1) & mask]) == null))
                    if (((curr).equals(k)))
                        return pos;
            }
        }
        key[pos] = k;
        value[pos] = v;
        if (size++ >= maxFill)
            rehash(arraySize(size + 1, f));

        return -1;
    }
    public byte[] put(final byte[] k, final byte[] v) {
        final int pos = insert(k, v);
        if (pos==-1)
            return null;
        final byte[] oldValue = value[pos];
        value[pos] = v;
        return oldValue;
    }
    /**
     * Shifts left entries with the specified hash code, starting at the
     * specified position, and empties the resulting free entry.
     *
     * @param pos
     *            a starting position.
     */
    protected final void shiftKeys(int pos) {
        // Shift entries with the same hash.
        int last, slot;
        byte[] curr;
        final byte[][] key = this.key;
        for (;;) {
            pos = ((last = pos) + 1) & mask;
            for (;;) {
                if (((curr = key[pos]) == null)) {
                    key[last] = (null);
                    value[last] = null;
                    return;
                }
                slot = (mix((curr).hashCode()))
                        & mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot
                        && slot > pos)
                    break;
                pos = (pos + 1) & mask;
            }
            key[last] = curr;
            value[last] = value[pos];
        }
    }

    @SuppressWarnings("unchecked")
    public byte[] get(final Object k) {
        if ((((byte[]) k) == null))
            return containsNullKey ? value[n]:null;
        byte[] curr;
        final byte[][] key = this.key;
        int pos;
        // The starting point.
        if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix((k)
                .hashCode())) & mask]) == null))
            return null;
        if (((k).equals(curr)))
            return value[pos];
        // There's always an unused entry.
        while (true) {
            if (((curr = key[pos = (pos + 1) & mask]) == null))
                return null;
            if (((k).equals(curr)))
                return value[pos];
        }
    }
    @SuppressWarnings("unchecked")
    public boolean containsKey(final Object k) {
        if ((((byte[]) k) == null))
            return containsNullKey;
        byte[] curr;
        final byte[][] key = this.key;
        int pos;
        // The starting point.
        if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix((k)
                .hashCode())) & mask]) == null))
            return false;
        if (((k).equals(curr)))
            return true;
        // There's always an unused entry.
        while (true) {
            if (((curr = key[pos = (pos + 1) & mask]) == null))
                return false;
            if (((k).equals(curr)))
                return true;
        }
    }
    public boolean containsValue(final Object v) {
        final byte[] value[] = this.value;
        final byte[] key[] = this.key;
        if (containsNullKey
                && ((value[n]) == null ? (v) == null : (value[n]).equals(v)))
            return true;
        for (int i = n; i-- != 0;)
            if (!((key[i]) == null)
                    && ((value[i]) == null ? (v) == null : (value[i]).equals(v)))
                return true;
        return false;
    }
    /*
     * Removes all elements from this map.
     * 
     * <P>To increase object reuse, this method does not change the table size.
     * If you want to reduce the table size, you must use {@link #trim()}.
     */
    public void clear() {
        if (size == 0)
            return;
        size = 0;
        containsNullKey = false;
        Arrays.fill(key, (null));
        Arrays.fill(value, null);
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }



    public boolean trim() {
        final int l = arraySize(size, f);
        if (l >= n || size > maxFill(l, f))
            return true;
        try {
            rehash(l);
        } catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    public boolean trim(final int n) {
        final int l = nextPowerOfTwo((int) Math.ceil(n / f));
        if (l >= n || size > maxFill(l, f))
            return true;
        try {
            rehash(l);
        } catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }
    /**
     * Rehashes the map.
     *
     * <P>
     * This method implements the basic rehashing strategy, and may be overriden
     * by subclasses implementing different rehashing strategies (e.g.,
     * disk-based rehashing). However, you should not override this method
     * unless you understand the internal workings of this class.
     *
     * @param newN
     *            the new size
     */
    @SuppressWarnings("unchecked")
    protected void rehash(final int newN) {
        final byte[] key[] = this.key;
        final byte[] value[] = this.value;
        final int mask = newN - 1; // Note that this is used by the hashing
        // macro
        final byte[] newKey[] =  new byte[newN + 1][];
        final byte[] newValue[] = new byte[newN + 1][];
        int i = n, pos;
        for (int j = realSize(); j-- != 0;) {
            while (((key[--i]) == null));
            if (!((newKey[pos = (mix((key[i])
                    .hashCode())) & mask]) == null))
                while (!((newKey[pos = (pos + 1) & mask]) == null));
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[n];
        n = newN;
        this.mask = mask;
        maxFill = maxFill(n, f);
        this.key = newKey;
        this.value = newValue;
    }



    public static int maxFill( final int n, final float f ) {
		/* We must guarantee that there is always at least
		 * one free entry (even with pathological load factors). */
        return Math.min( (int)Math.ceil( n * f ), n - 1 );
    }
    public static int arraySize( final int expected, final float f ) {
        final long s = Math.max( 2, nextPowerOfTwo( (long)Math.ceil( expected / f ) ) );
        if ( s > (1 << 30) ) throw new IllegalArgumentException( "Too large (" + expected + " expected elements with load factor " + f + ")" );
        return (int)s;
    }
    public static long nextPowerOfTwo( long x ) {
        if ( x == 0 ) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return ( x | x >> 32 ) + 1;
    }
    public static int nextPowerOfTwo( int x ) {
        if ( x == 0 ) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return ( x | x >> 16 ) + 1;
    }
    public final static int mix( final int x ) {
        final int h = x * INT_PHI;
        return h ^ (h >>> 16);
    }
    private static final int INT_PHI = 0x9E3779B9;
}
