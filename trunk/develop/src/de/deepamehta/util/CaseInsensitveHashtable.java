package de.deepamehta.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;



/**
 * 
 * @author enrico
 */
public class CaseInsensitveHashtable extends Hashtable {
	Hashtable upperKeysHash = new Hashtable();

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
		// if the key is not contained in the hashtable (null) it can't be removed
		// maltito, 9.10.2007
		if (upperKey != null) { 
			super.remove(upperKey);
		}
		return upperKeysHash.remove(upper);
	}

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
}
