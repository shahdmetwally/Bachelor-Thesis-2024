package uml.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconManager {

  private static IconManager instance = new IconManager();

  private WeakHashMap resourceMap = new WeakHashMap();
  private HashMap imageMap = new HashMap();

  private IconManager() { }

  static public IconManager getInstance() {
    return instance;
  }

  protected Image loadImageResource(String resourceName) {

    Toolkit kit = Toolkit.getDefaultToolkit();
    URL url = getClass().getResource(resourceName);

    if(url != null) {

      try {

        return kit.createImage((java.awt.image.ImageProducer)url.getContent());
        
      } catch(Throwable t) { 
        t.printStackTrace();
      }

    }

    return null;

  }

  public void registerImageResource(Component comp, String resourceName) {
    
    Vector v = (Vector)resourceMap.get(comp);

    if(v == null) { 
      v = new Vector();
      resourceMap.put(comp, v);
    }

    if(!v.contains(resourceName))
      v.add(resourceName);

  }
 
  public Image getImageResource(Component comp, String resourceName) {
    
    if(resourceName.charAt(0) != '/')
      resourceName = '/' + resourceName;

    Image img = null;
    if((img = (Image)imageMap.get(resourceName)) == null) {

      MediaTracker tracker = new MediaTracker(comp);

      Vector v = (Vector)resourceMap.get(comp);
      if(v != null) {

        resourceMap.remove(comp);
      
        for(Iterator i = v.iterator(); i.hasNext();)
          trackImage(tracker, (String)i.next());

      } 

      if(v == null || !v.contains(resourceName))
        trackImage(tracker, resourceName);

      try {
        tracker.waitForAll();
      } catch(InterruptedException e) { }
      
      if(tracker.checkAll())
        img = (Image)imageMap.get(resourceName);

    }

    if(img == null)
      throw new RuntimeException("Resource not found " + resourceName);

    return img;

  }

  private final Image trackImage(MediaTracker tracker, String resourceName) {

    Image img = loadImageResource(resourceName);
    if(img != null) {
    
      tracker.addImage(img, 1);
      imageMap.put(resourceName, img);
      
    }
    
    return img;

  }

  public Icon getIconResource(Component comp, String resourceName) {

    if(comp == null)
      throw new RuntimeException("Null component!");

    if(resourceName == null)
      throw new RuntimeException("Null resource name!");

    return new ImageIcon(getImageResource(comp, resourceName));
  }

}
