package CH.ifa.draw.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Iconkit {
	private Hashtable           fMap;
	private Vector              fRegisteredImages;
	private Component           fComponent;
	private final static int    ID = 123;
	private static Iconkit      fgIconkit = null;
	private static boolean      fgDebug = false;

	public Iconkit(Component component) {
		fMap = new Hashtable(53);
		fRegisteredImages = new Vector(10);
		fComponent = component;
		fgIconkit = this;
	}

	public static Iconkit instance() {
		return fgIconkit;
	}

	public void loadRegisteredImages(Component component) {
		if (fRegisteredImages.size() == 0)
			return;

		MediaTracker tracker = new MediaTracker(component);

		Enumeration k = fRegisteredImages.elements();
		while (k.hasMoreElements()) {
			String fileName = (String) k.nextElement();
			if (basicGetImage(fileName) == null) {
				tracker.addImage(loadImage(fileName), ID);
			}
		}
		fRegisteredImages.removeAllElements();

		try {
			tracker.waitForAll();
		} catch (Exception e) {  }
	}

	public void registerImage(String fileName) {
		fRegisteredImages.addElement(fileName);
	}

	public Image registerAndLoadImage(Component component, String fileName) {
		registerImage(fileName);
		loadRegisteredImages(component);
		return getImage(fileName);
	}

	public Image loadImage(String filename) {
		if (fMap.containsKey(filename))
			return (Image) fMap.get(filename);
		Image image = loadImageResource(filename);
		if (image != null)
			fMap.put(filename, image);
		return image;
	}

	public Image loadImageResource(String resourcename) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			java.net.URL url = getClass().getResource(resourcename);
			if (fgDebug)
				System.out.println(resourcename);
			return toolkit.createImage((ImageProducer) url.getContent());
		} catch (Exception ex) {
			return null;
		}
	}

	public Image getImage(String filename) {
		Image image = basicGetImage(filename);
		if (image != null)
			return image;
		loadRegisteredImages(fComponent);
		return basicGetImage(filename);
	}

	private Image basicGetImage(String filename) {
		if (fMap.containsKey(filename))
			return (Image) fMap.get(filename);
		return null;
	}
}
