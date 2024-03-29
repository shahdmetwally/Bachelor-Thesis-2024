package org.acm.seguin.refactor.undo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Stack;

import org.acm.seguin.refactor.Refactoring;
import org.acm.seguin.uml.loader.ReloaderSingleton;
import org.acm.seguin.util.FileSettings;

public class UndoStack {

	private Stack stack;

	private static UndoStack singleton;

	public UndoStack() {
		if (!load()) {
			stack = new Stack();
		}
	}


	public boolean isStackEmpty() {
		return stack.isEmpty();
	}

	public UndoAction add(Refactoring ref) {
		UndoAction action = new UndoAction(ref.getDescription());
		stack.push(action);
		return action;
	}


	public UndoAction peek() {
		return (UndoAction) stack.peek();
	}


	public Iterator list() {
		return stack.iterator();
	}


	public void undo() {
		UndoAction action = (UndoAction) stack.pop();
		action.undo();
		ReloaderSingleton.reload();
	}


	public void done() {
		save();
	}


	public void delete() {
		File file = getFile();
		file.delete();
		stack = new Stack();
	}


	private File getFile() {
		File dir = new File(FileSettings.getSettingsRoot());
		return new File(dir, "undo.stk");
	}


	private void save() {
		try {
			File file = getFile();
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeObject(stack);
			output.flush();
			output.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
	}


	private boolean load() {
		try {
			File file = getFile();
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
			stack = (Stack) input.readObject();
			input.close();

			return true;
		}
		catch (FileNotFoundException fnfe) {
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace(System.out);
		}

		return false;
	}


	public static UndoStack get() {
		if (singleton == null) {
			singleton = new UndoStack();
		}

		return singleton;
	}
}
