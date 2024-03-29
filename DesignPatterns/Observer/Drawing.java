package CH.ifa.draw.framework;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import CH.ifa.draw.util.Storable;


public interface Drawing
		extends Storable, FigureChangeListener, Serializable {
	public void release();
	public FigureEnumeration figures();
	public FigureEnumeration figuresReverse();
	public Figure findFigure(int x, int y);
	public Figure findFigure(Rectangle r);
	public Figure findFigureWithout(int x, int y, Figure without);
	public Figure findFigure(Rectangle r, Figure without);
	public Figure findFigureInside(int x, int y);
	public Figure findFigureInsideWithout(int x, int y, Figure without);
	public void addDrawingChangeListener(DrawingChangeListener listener);
	public void removeDrawingChangeListener(DrawingChangeListener listener);
	public Enumeration drawingChangeListeners();
	public Figure add(Figure figure);
	public void addAll(Vector newFigures);
	public Figure remove(Figure figure);
	public Figure orphan(Figure figure);
	public void orphanAll(Vector newFigures);
	public void removeAll(Vector figures);
	public void replace(Figure figure, Figure replacement);
	public void sendToBack(Figure figure);
	public void bringToFront(Figure figure);
	public void draw(Graphics g);
	public void figureInvalidated(FigureChangeEvent e);
	public void figureRequestUpdate(FigureChangeEvent e);
	public void figureRequestRemove(FigureChangeEvent e);
	public void lock();
	public void unlock();

}
