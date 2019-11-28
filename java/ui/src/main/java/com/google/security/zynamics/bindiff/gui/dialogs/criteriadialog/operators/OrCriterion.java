package com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.operators;

import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.criterion.AbstractCriterion;
import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.criterion.CriterionType;
import com.google.security.zynamics.bindiff.utils.ImageUtils;
import com.google.security.zynamics.zylib.gui.zygraph.nodes.CViewNode;
import com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.nodes.ZyGraphNode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class OrCriterion extends AbstractCriterion {
  private static final ImageIcon OR_ICON =
      ImageUtils.getImageIcon("data/selectbycriteriaicons/or.png");

  private final OrCriterionPanel panel = new OrCriterionPanel();

  @Override
  public String getCriterionDescription() {
    return "OR";
  }

  @Override
  public JPanel getCriterionPanel() {
    return panel;
  }

  @Override
  public Icon getIcon() {
    return OR_ICON;
  }

  @Override
  public CriterionType getType() {
    return CriterionType.OR;
  }

  @Override
  public boolean matches(final ZyGraphNode<? extends CViewNode<?>> node) {
    return true;
  }
}
