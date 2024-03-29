/**
 * HTMLComponentFactory - Factory which creates XMLComponents from an HTML Doc.
 *
 * Copyright (c) 2000
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.html;

import java.util.*;
import java.text.*;
import org.w3c.dom.*;
import com.taursys.xml.*;
import com.taursys.html.*;
import com.taursys.model.*;
import com.taursys.debug.*;
import com.taursys.dom.*;

public class HTMLComponentFactory extends ComponentFactory {
  private static HTMLComponentFactory factory;

  public HTMLComponentFactory() {
    super();
  }

  public static HTMLComponentFactory getInstance() {
    if (factory == null) {
      factory = new HTMLComponentFactory();
    }
    return factory;
  }

  protected void initTagTable() {
    Vector suggestions = new Vector();
    suggestions.add(HTMLAnchorURL.class.getName());
    tagTable.put("a", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLSelect.class.getName());
    tagTable.put("select", suggestions);

    suggestions = new Vector();
    suggestions.add(TextField.class.getName());
    tagTable.put("span", suggestions);

    suggestions = new Vector();
    suggestions.add(TextField.class.getName());
    tagTable.put("td", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLTextArea.class.getName());
    tagTable.put("textarea", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLInputText.class.getName());
    tagTable.put("input-hidden", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLInputText.class.getName());
    tagTable.put("input-password", suggestions);

    suggestions = new Vector();
    suggestions.add(Button.class.getName());
    suggestions.add(Trigger.class.getName());
    tagTable.put("input-submit", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLInputText.class.getName());
    tagTable.put("input-text", suggestions);

    suggestions = new Vector();
    suggestions.add(HTMLCheckBox.class.getName());
    tagTable.put("input-checkbox", suggestions);
  }

  public Vector getSuggestedComponents(Element element) {
    String tagName = element.getTagName();
    if (tagName.equals("input"))
      tagName += "-" + element.getAttribute("type");
    String id = element.getAttribute("id");
    return getSuggestedComponents(tagName, id, element);
  }

  protected Component createComponentForElement(String id, Element element,
      ValueHolder[] holders) {
    if (id == null)
      throw new IllegalArgumentException(
          "Null id passed to createComponentForElement");

    int pos = id.indexOf(ID_DELIMITER);
    if (pos < 1)
      return null;
    String alias = id.substring(0, pos);
    pos += 2;
    if (id.length() <= pos)
      return null;
    String propertyName = id.substring(pos);
    pos = propertyName.indexOf(ID_DELIMITER);
    if (pos != -1)
      propertyName = propertyName.substring(0, pos);

    ValueHolder holder = null;
    for (int i = 0; i < holders.length; i++) {
      holder = holders[i];
      if (alias.equals(holder.getAlias()))
        break;
    }
    if (holder == null)
      return null;

    Vector suggestions = getSuggestedComponents(element);
    if (suggestions.size() == 0)
      return null;
    Component component = null;
    try {
      component = (Component)
          Class.forName((String)suggestions.get(0)).newInstance();
    } catch (Exception ex) {
      Debug.error("Error during create component: " + ex.getMessage(), ex);
      return null;
    }

    if (component instanceof AbstractField) {
      ((AbstractField)component).setId(id);
      ((AbstractField)component).setValueHolder(holder);
      ((AbstractField)component).setPropertyName(propertyName);
      String parameter = element.getAttribute("name");
      if (parameter != null && parameter.length() == 0)
        parameter = null;
      ((AbstractField)component).setParameter(parameter);
      setupFormat((AbstractField)component, element);
      return component;
    } else if (component instanceof Template) {
      ((Template)component).setId(id);
      if (holder instanceof CollectionValueHolder) {
        ((Template)component).setCollectionValueHolder((CollectionValueHolder)holder);
        return component;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private void setupFormat(AbstractField field, Element element) {
    String formatInfo = element.getAttribute("value");
    if (formatInfo == null || formatInfo.length() == 0)
      formatInfo = element.getAttribute("href");
    if (formatInfo == null || formatInfo.length() == 0)
      formatInfo = DOM_1_20000929_DocumentAdapter.getElementText(element);
    if (formatInfo != null && formatInfo.length() > 0) {
      if (formatInfo.startsWith("DATE:")) {
        field.setFormat(new java.text.SimpleDateFormat());
        field.setFormatPattern(formatInfo.substring(5));
      } else if (formatInfo.startsWith("NUMBER:")) {
        field.setFormat(new DecimalFormat());
        field.setFormatPattern(formatInfo.substring(7));
      } else if (formatInfo.startsWith("MSG:")) {
        field.setFormat(new MessageFormat(""));
        field.setFormatPattern(formatInfo.substring(4));
      }
    }
  }
}
