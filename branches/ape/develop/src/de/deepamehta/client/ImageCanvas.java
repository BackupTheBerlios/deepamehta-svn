package de.deepamehta.client;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;



class ImageCanvas extends JComponent {

	private Image image;

	ImageCanvas() {
	}

	ImageCanvas(Image image) {
		this.image = image;
	}

	ImageCanvas(ImageIcon icon) {
		this.image = icon.getImage();
	}

	public void paint(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 0, this);
		}
	}

	void setImage(Image image) {
		this.image = image;
	}
}
