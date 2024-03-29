package org.acm.seguin.uml.line;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class LabelSizeComputation {
	private Graphics g;
	private static LabelSizeComputation singleton;

	private LabelSizeComputation() {
		BufferedImage doubleBuffer = new BufferedImage(300, 25, BufferedImage.TYPE_INT_RGB);
		g = doubleBuffer.getGraphics();
	}

	public TextInfo compute(String text, Font font) {
		TextInfo result = new TextInfo();

		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		result.height = Math.max(1, fm.getHeight());
		if (text != null) {
			result.width = Math.max(1, fm.stringWidth(text));
		}
		else {
			result.width = 1;
		}

		result.ascent = fm.getAscent();

		return result;
	}


	public int computeHeight(String text, Font font) {
		TextInfo ti = compute(text, font);
		return ti.height;
	}


	public static LabelSizeComputation get() {
		if (singleton == null) {
			init();
		}

		return singleton;
	}


	private static synchronized void init() {
		if (singleton == null) {
			singleton = new LabelSizeComputation();
		}
	}
}
