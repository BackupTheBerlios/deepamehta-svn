package de.deepamehta.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;



/**
 * An hashtable whose keys are handled case-insensitively.
 * This is realized by maintaining a mirror-table with all-uppercase keys.
 *
 * @author	enrico
 */
public class CaseInsensitveHashtable extends Hashtable {



	/**
	 * The mirror-table.
	 */
	Hashtable upperKeysHash = new Hashtable();



	// ------------------------
	// --- Override Methods ---
	// ------------------------



	public Object get(Object key) {
		return upperKeysHash.get(upper(key));
	}

	public Object put(Object key, Object val) {
		Object upper = upper(key);
		if (upperKeysHash.containsKey(upper)) {
			super.remove(findUpperKey(upper));
		}
		super.put(key, val);
		return upperKeysHash.put(upper, val);
	}

	public void putAll(Map map) {
		Iterator i = map.keySet().iterator();
		while (i.hasNext()) {
			Object o = i.next();
			put(o, map.get(o));
		}
	}

	public boolean containsKey(Object key) {
		return upperKeysHash.containsKey(upper(key));
	}

	public Object remove(Object key) {
		Object upper = upper(key);
		Object upperKey = findUpperKey(upper);
		// If the key is not contained in the hashtable (null) it can't be removed.
		// maltito, 9.10.2007
		if (upperKey != null) { 
			super.remove(upperKey);
		}
		return upperKeysHash.remove(upper);
	}

	public synchronized int hashCode() {
		return upperKeysHash.hashCode();
	}

	public synchronized boolean equals(Object o) {
		CaseInsensitveHashtable ciht;
		if (o instanceof CaseInsensitveHashtable) {
			ciht = (CaseInsensitveHashtable) o;
		} else if (o instanceof Map) {
			ciht = new CaseInsensitveHashtable();
			ciht.putAll((Map) o);
		} else {
			return false;
		}
		return ciht.upperKeysHash.equals(upperKeysHash);
	}

	// When cloning this hashtable, the mirror-table must also be cloned.
	// Note: cloning is done in ApplicationService.removeUnchangedProperties().
	// jri, 2.11.2007
	public Object clone() {
		Object o = super.clone();
		((CaseInsensitveHashtable) o).upperKeysHash = (Hashtable) upperKeysHash.clone();
		return o;
	}



	// -----------------------
	// --- Private Methods ---
	// -----------------------



	private Object findUpperKey(Object upper) {
		Iterator iterator = this.keySet().iterator();
		while (iterator.hasNext()) {
			Object element = iterator.next();
			if (upper.equals(upper(element))) {
				return element;
			}
		}
		return null;
	}

	static private Object upper(Object key) {
		if (key instanceof String) {
			key = ((String) key).toUpperCase();
		}
		return key;
	}
}
