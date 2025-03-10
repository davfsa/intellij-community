// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.find.impl;

import com.intellij.find.FindBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.usageView.UsageViewBundle;
import com.intellij.usages.TextChunk;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.*;
import java.awt.*;

class TextSearchRenderer extends JPanel implements ListCellRenderer<UsageInfo2UsageAdapter> {
  private final ColoredListCellRenderer<UsageInfo2UsageAdapter> myUsageRenderer = new ColoredListCellRenderer<>() {
    @Override
    protected void customizeCellRenderer(@NotNull JList<? extends UsageInfo2UsageAdapter> list,
                                         UsageInfo2UsageAdapter value, int index, boolean selected, boolean hasFocus) {
      if (!value.isValid()) {
        myUsageRenderer.append(" " + UsageViewBundle.message("node.invalid") + " ", SimpleTextAttributes.ERROR_ATTRIBUTES);
      }
      TextChunk[] text = value.getPresentation().getText();

      // skip line number / file info
      for (int i = 1; i < text.length; ++i) {
        TextChunk textChunk = text[i];
        myUsageRenderer.append(textChunk.getText(), getAttributes(textChunk, selected));
      }

      myUsageRenderer.setIcon(AllIcons.Nodes.TextArea);
      //noinspection UseDPIAwareInsets
      myUsageRenderer.setIpad(new Insets(0, 0, 0, getIpad().right));
      setBorder(null);
    }

    @NotNull
    private SimpleTextAttributes getAttributes(@NotNull TextChunk textChunk, boolean selected) {
      SimpleTextAttributes attributes = textChunk.getSimpleAttributesIgnoreBackground();
      if (!(attributes.getFontStyle() == Font.BOLD)) return attributes;

      return new SimpleTextAttributes(null, attributes.getFgColor(), attributes.getWaveColor(),
                                      attributes.getStyle() & ~SimpleTextAttributes.STYLE_BOLD |
                                      (selected ? SimpleTextAttributes.STYLE_SEARCH_MATCH : SimpleTextAttributes.STYLE_PLAIN));
    }
  };

  private final ColoredListCellRenderer<UsageInfo2UsageAdapter> myFileAndLineNumber;

  TextSearchRenderer(@NotNull GlobalSearchScope scope) {
    setLayout(new BorderLayout());
    myFileAndLineNumber = new FileAndLineTextRenderer(scope);
    add(myUsageRenderer, BorderLayout.CENTER);
    add(myFileAndLineNumber, BorderLayout.EAST);
    setBorder(JBUI.Borders.empty(2, 2, 2, 0));
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends UsageInfo2UsageAdapter> list,
                                                UsageInfo2UsageAdapter value, int index, boolean isSelected, boolean cellHasFocus) {
    myUsageRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    myFileAndLineNumber.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    setBackground(myUsageRenderer.getBackground());
    if (!isSelected) {
      Color color = VfsPresentationUtil.getFileBackgroundColor(value.getUsageInfo().getProject(), value.getFile());
      setBackground(color);
      myUsageRenderer.setBackground(color);
      myFileAndLineNumber.setBackground(color);
    }
    getAccessibleContext().setAccessibleName(
      FindBundle.message("find.popup.found.element.accesible.name", myUsageRenderer.getAccessibleContext().getAccessibleName(),
                         myFileAndLineNumber.getAccessibleContext().getAccessibleName()));
    return this;
  }

  @Override
  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null) {
      accessibleContext = new StateSetAccessibleJPanel();
    }
    return accessibleContext;
  }

  class StateSetAccessibleJPanel extends AccessibleJPanel {
    @Override
    public AccessibleRole getAccessibleRole() {
      return AccessibleRole.UNKNOWN;
    }

    @Override
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet stateSet = new AccessibleStateSet();
      stateSet.addAll(myUsageRenderer.getAccessibleContext().getAccessibleStateSet().toArray());
      stateSet.addAll(myFileAndLineNumber.getAccessibleContext().getAccessibleStateSet().toArray());
      return stateSet;
    }

    @Override
    public int getAccessibleIndexInParent() {
      return 0;
    }

    @Override
    public int getAccessibleChildrenCount() {
      return 0;
    }

    @Override
    public Accessible getAccessibleChild(int i) {
      return null;
    }
  }
}

