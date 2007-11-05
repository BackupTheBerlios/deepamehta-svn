package de.deepamehta.artfacts;

import de.deepamehta.DeepaMehtaConstants;



public interface Artfacts extends DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// -------------------
	// --- Topic Types ---
	// -------------------



	static final String TOPICTYPE_ARTIST = "tt-af-artist";
	static final String TOPICTYPE_ARTWORK = "tt-af-artwork";
	static final String TOPICTYPE_GALLERY = "tt-af-gallery";
	static final String TOPICTYPE_EXHIBITION = "tt-af-exhibition";



	// ------------------
	// --- Properties ---
	// ------------------



	public static final String PROPERTY_ID = "ID";
	public static final String PROPERTY_BIRTH_LOCATION = "Birth Location";
	public static final String PROPERTY_FOUNDATION_YEAR = "Foundation Year";
	public static final String PROPERTY_TITLE = "Title";
	// ### public static final String PROPERTY_BEGIN = "Begin";
	public static final String PROPERTY_END = "End";
}
