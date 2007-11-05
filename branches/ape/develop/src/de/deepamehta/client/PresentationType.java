package de.deepamehta.client;

import de.deepamehta.PresentableType;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Color;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;



/**
 * Type model as instantiated at client side.
 * <P>
 * <HR>
 * Last functional change: 7.4.2003 (2.0a18-pre9)<BR>
 * Last documentation update: 30.7.2001 (2.0a11)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public final class PresentationType extends PresentableType {



	// **************
	// *** Fields ***
	// **************



	// ### Note: these 2 fields are redundant, they are updated together.
	// Initialized by set() and by the private initIcon() methods of the subtypes.
	protected Icon typeIcon;
	protected Image typeImage;

	// only used for association types
	protected Color typeColor;



	// ********************
	// *** Constructors ***
	// ********************



	public PresentationType(PresentableType type, PresentationService ps) {
		super(type);
		// --- type definition ---
		this.typeDefinition = DeepaMehtaClientUtils.createPresentationPropertyDefinitions(
			type.getTypeDefinition().elements());
		// --- type icon ---
		this.typeIconfile = type.getTypeIconfile();
		initIcon(ps);
		// --- association type color ---
		// Note: typeColor is only initialized for association types.
		// For topic types typeColor remains uninitialized (null)
		String color = type.getAssocTypeColor();
		if (color != null) {
			this.typeColor = DeepaMehtaUtils.parseHexColor(color);
		}
		// --- flags ---
		this.disabled = type.isDisabled();
		this.hiddenTopicNames = type.getHiddenTopicNames();
		this.isSearchType = type.isSearchType();
	}

	/**
	 * Stream constructor.
	 * <P>
	 * References checked: 29.12.2001 (2.0a14-pre5)
	 *
	 * @see		PresentationType#PresentationType(DataInputStream, PresentationService)
	 */
	PresentationType(DataInputStream in, PresentationService ps) throws IOException {
		// ### compare to PresentableType.write() ### PresentableType has no stream constructor
		// --- id, name, version ---
		this.id = in.readUTF();
		this.name = in.readUTF();
		this.version = in.readInt();
		// --- type definition ---
		int fieldCount = in.readInt();
		for (int i = 0; i < fieldCount; i++) {
			typeDefinition.addElement(new PresentationPropertyDefinition(in));
		}
		// --- type icon ---
		this.typeIconfile = in.readUTF();
		initIcon(ps);
		// --- association type color ---
		String color = in.readUTF();
		// Note: typeColor is only initialized for association types.
		// For topic types typeColor remains uninitialized (null)
		if (!color.equals("")) {
			this.typeColor = DeepaMehtaUtils.parseHexColor(color);
		}
		// --- flags ---
		this.disabled = in.readBoolean();
		this.hiddenTopicNames = in.readBoolean();
		this.isSearchType = in.readBoolean();
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Initializer.
	 */
	/* ### public void set(PresentationType typeTopic) {
		this.id = typeTopic.id;
		this.name = typeTopic.name;
		this.version = typeTopic.version;
		// Note: associations are not set!
		this.typeDefinition = typeTopic.typeDefinition;
		this.typeIconfile = typeTopic.typeIconfile;		// ### was commented
		this.assocTypeColor = typeTopic.assocTypeColor;
		this.disabled = typeTopic.disabled;
		this.hiddenTopicNames = typeTopic.hiddenTopicNames;
		// ###
		this.typeIcon = typeTopic.typeIcon;
		this.typeImage = typeTopic.typeImage;
		this.typeColor = typeTopic.typeColor;
	} */

	// ---

	public Image getImage() {
		return typeImage;
	}

	public Icon getIcon() {
		return typeIcon;
	}

	public int getImageSize() {
		return IMAGE_SIZE;
	}

	public Color getColor() {
		return typeColor;
	}

	// ---

	public boolean hasImage() {
		return typeImage != null;
	}

	/**
	 * @see		GraphPanel#nodeClicked
	 * @see		GraphPanel#edgeClicked
	 */
	/* ### public boolean isDisabled() {
		return disabled;
	} */

	/**
	 * @see		GraphPanel#paintNode
	 */
	/* ### public boolean hiddenTopicNames() {
		return hiddenTopicNames;
	} */



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#PresentationType(PresentableType, PresentationService)
	 * @see		#PresentationType(DataInputStream, PresentationService)
	 */
	private void initIcon(PresentationService ps) {
		typeImage = ps.getImage(FILESERVER_ICONS_PATH + getTypeIconfile());
		typeIcon = new ImageIcon(typeImage);
	}
}
