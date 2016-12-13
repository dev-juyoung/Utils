package com.jaychang.utils;

import android.support.annotation.CallSuper;
import android.widget.RelativeLayout;

import java.util.Stack;

public class UndoManager {
  private Stack<Operation> undoStack = new Stack<>();
  private Stack<Operation> redoStack = new Stack<>();

  private static UndoManager INSTANCE = null;

  private UndoManager() {
  }

  public static synchronized UndoManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new UndoManager();
    }
    return INSTANCE;
    RelativeLayout.LayoutParams
  }

  public void undo() {
    if (isUndoAvailable()) {
      Operation operation = undoStack.pop();
      operation.executeUndo();
    }
  }

  public void redo() {
    if (isRedoAvailable()) {
      Operation operation = redoStack.pop();
      operation.execute();
    }
  }

  public void clearRedoTasks() {
    redoStack.clear();
  }

  public void clearUndoTasks() {
    undoStack.clear();
  }

  public boolean isUndoAvailable() {
    return undoStack.size() > 0;
  }

  public boolean isRedoAvailable() {
    return redoStack.size() > 0;
  }

  public void addUndoTask(Operation operation) {
    undoStack.add(operation);
  }

  private void addRedoTask(Operation operation) {
    redoStack.add(operation);
  }

  public static class Operation {
    @CallSuper
    public void executeUndo() {
      getInstance().addRedoTask(this);
    }
    @CallSuper
    public void execute() {
      getInstance().addUndoTask(this);
    }
  }
}