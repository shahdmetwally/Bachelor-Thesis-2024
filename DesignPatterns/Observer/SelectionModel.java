package diagram;

import java.util.Iterator;
public interface SelectionModel {

  public boolean contains(Figure figure);
  public boolean contains(Link link);
  public void add(Figure figure);
  public void add(Figure f, boolean reset);
  public void remove(Figure figure);
  public void clear();
  public int size();
  public Iterator iterator();
  public Object[] toArray(Object[] array);
  public void addSelectionListener(DiagramSelectionListener listener);
  public void removeSelectionListener(DiagramSelectionListener listener);

}
