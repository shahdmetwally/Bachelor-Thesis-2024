package com.taursys.model;

import java.text.Format;
import com.taursys.model.ValueHolder;
import javax.swing.event.ChangeListener;

public interface TextModel {

  public String getText() throws ModelException;


  public void setText(String text) throws ModelException;

  public Format getFormat();

  public void setFormat(Format format);

  public String getFormatPattern();

  public void setFormatPattern(String newPattern);


  public void setValueHolder(ValueHolder newValueHolder);

  public com.taursys.model.ValueHolder getValueHolder();

  public void setPropertyName(String newPropertyName);

  public String getPropertyName();

  public void removeChangeListener(ChangeListener l);

  public void addChangeListener(ChangeListener l);
}
