package key.io;

/**
  *  This interface indicates that only one of this object
  *  (per-value) should exist in the sytem at any point
  *  in time.  It is used by KeyInputStream to replace
  *  loaded objects with the ones that are already in memory,
  *  in an attempt to conserve memory.
 */
public interface Replaceable
{
	public Object getReplacement();
}
