package com.jmonkey.office.lexi.support;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.UndoManager;

import com.jmonkey.export.Registry;
import com.jmonkey.office.lexi.support.editors.HTMLEditor;
import com.jmonkey.office.lexi.support.editors.RTFEditor;
import com.jmonkey.office.lexi.support.editors.TEXTEditor;

public abstract class Editor extends JPanel {
	public static final String[] VALID_CONTENT_TYPES = {"text/plain", "text/html", "text/rtf", "application/x-lexi"};
	private File _FILE = null;
	private Registry _OPTION_REGISTRY = null;
	private UndoManager _UNDO_MANAGER = null;

	protected final ActionListener _POPUP_LISTENER = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Copy")) {
						EditorActionManager.instance().getCopyAction();
					}
				if (e.getActionCommand().equals("Cut")) {
						EditorActionManager.instance().getCutAction();
					}
				if (e.getActionCommand().equals("Paste")) {
						EditorActionManager.instance().getPasteAction();
					}
				if (e.getActionCommand().equals("Undo")) {
						EditorActionManager.instance().getUndoAction();
					}
				if (e.getActionCommand().equals("Redo")) {
						EditorActionManager.instance().getRedoAction();
					}
				if (e.getActionCommand().equals("SelectAll")) {
						EditorActionManager.instance().getSelectAllAction();
					}
				if (e.getActionCommand().equals("SelectNone")) {
						EditorActionManager.instance().getSelectNoneAction();
					}

				}
			};

	public abstract class FRThread implements Runnable {
		protected File file = null;
		protected int position = 0;

		public FRThread(File file) {
			this.file = file;
		}

		public FRThread(File file, int position) {
			this.file = file;
			this.position = position;
		}

		public abstract void run();
	}

	public abstract class FWThread implements Runnable {
		protected File file = null;

		public FWThread(File file) {
			this.file = file;
		}

		public abstract void run();
	}
	public final void activate() {
		this.requestFocus();
		EditorActionManager.activate(this);
	}
	public abstract void append(File file) throws IOException;
	public final void deactivate() {
		EditorActionManager.deactivate(this);
	}
	public abstract void documentSetSelection(int start, int length, boolean wordsOnly);
	public abstract String getContentType();
	public abstract Element getCurrentParagraph();
	public abstract Element getCurrentRun();
	public static final Editor getEditorForContentType(String contentType) {
		if(contentType.equalsIgnoreCase(TEXTEditor.CONTENT_TYPE)) {
			return new TEXTEditor();
		} else if(contentType.equalsIgnoreCase(HTMLEditor.CONTENT_TYPE)) {
			return new HTMLEditor();
		} else if(contentType.equalsIgnoreCase(RTFEditor.CONTENT_TYPE)) {
			return new RTFEditor();
		} else {
			return new TEXTEditor();
		}
	}
	public static final Editor getEditorForExtension(String extension) {
		for(int txt = 0; txt < HTMLEditor.FILE_EXTENSIONS.length; txt++) {
			if(extension.equalsIgnoreCase(HTMLEditor.FILE_EXTENSIONS[txt])) {
				return new HTMLEditor();
			}
		}
		for(int txt = 0; txt < RTFEditor.FILE_EXTENSIONS.length; txt++) {
			if(extension.equalsIgnoreCase(RTFEditor.FILE_EXTENSIONS[txt])) {
				return new RTFEditor();
			}
		}
		for(int txt = 0; txt < TEXTEditor.FILE_EXTENSIONS.length; txt++) {
			if(extension.equalsIgnoreCase(TEXTEditor.FILE_EXTENSIONS[txt])) {
				return new TEXTEditor();
			}
		}
		return new TEXTEditor();
	}
	public final File getFile() {
		return _FILE;
	}
	public abstract String[] getFileExtensions();
	public abstract MutableAttributeSet getInputAttributes();
public final JPopupMenu getPopup() {
	JPopupMenu popUP = new JPopupMenu();
	Enumeration enum = getRegistry().getKeys("POPUP");
	while (enum.hasMoreElements()) {
		String key = (String) enum.nextElement();
		JMenuItem item = new JMenuItem(key);
		item.setActionCommand(getRegistry().getString("POPUP", key, key));
		item.addActionListener(_POPUP_LISTENER);
		popUP.add(item);
	}
	return popUP;
}
	protected final Registry getRegistry() {
		if (_OPTION_REGISTRY == null) {
			try {
				_OPTION_REGISTRY = Registry.loadForClass(this.getClass());
				if(!(_OPTION_REGISTRY.sizeOf("POPUP") > 0)) {
					_OPTION_REGISTRY.setProperty("POPUP", "Cut", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "Copy", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "Paste", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "----", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "Undo", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "Redo", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "----", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "SelectAll", "true");
					_OPTION_REGISTRY.setProperty("POPUP", "SelectNone", "true");
				}
			} catch (java.io.IOException ioe0) {
				System.err.println(ioe0.toString());
			}
		}
		return _OPTION_REGISTRY;
	}
	public final MutableAttributeSet getSimpleAttributeSet() {
		return new SimpleAttributeSet() {
	   			public AttributeSet getResolveParent() {
	   				return (getCurrentParagraph() != null) ? getCurrentParagraph().getAttributes() : null;
	   			}

	   			public Object clone() {
	   				return new SimpleAttributeSet(this);
	   			}
	   		};
	}
	public abstract JEditorPane getTextComponent();
	public final UndoManager getUndoManager() {
		if(_UNDO_MANAGER == null) {
			_UNDO_MANAGER = new UndoManager();
		}
		return _UNDO_MANAGER;
	}
	public abstract void hasBeenActivated(Editor editor);
	public abstract void hasBeenDeactivated(Editor editor);
	public final boolean hasFile() {
		return (_FILE != null);
	}
	public abstract void insert(File file, int position) throws IOException;
	public abstract boolean isChanged();
	public abstract boolean isEmpty();
	public abstract boolean isFormatted();
	public abstract boolean isNew();
	public abstract void read(File file) throws IOException;
	public abstract void setChanged(boolean changed);
	public abstract void setCurrentParagraph(Element paragraph);
	public abstract void setCurrentRun(Element run);
	public final void setFile(File file) {
		this._FILE = file;
	}
	public abstract void write(File file) throws IOException;
}
