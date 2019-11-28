package com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.conditions.nodecolor;

import com.google.common.base.Preconditions;
import com.google.security.zynamics.bindiff.graph.AbstractGraphsContainer;
import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.conditions.ConditionCriterion;
import com.google.security.zynamics.bindiff.utils.ImageUtils;
import com.google.security.zynamics.zylib.gui.zygraph.nodes.CViewNode;
import com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.nodes.ZyGraphNode;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ColorCriterion extends ConditionCriterion {
  private static final ImageIcon COLOR_CONDITION_ICON =
      ImageUtils.getImageIcon("data/selectbycriteriaicons/color-condition.png");

  private final ColorCriterionPanel panel;

  private final AbstractGraphsContainer graphs;

  public ColorCriterion(final AbstractGraphsContainer graphs) {
    Preconditions.checkNotNull(graphs);

    panel = new ColorCriterionPanel(this);

    this.graphs = graphs;
  }

  private JPanel getCriterionPanel(final AbstractGraphsContainer graphs) {
    panel.updateColors(graphs);

    return panel;
  }

  @Override
  public JPanel getCriterionPanel() {
    return getCriterionPanel(graphs);
  }

  public Color getColor() {
    return panel.getColor();
  }

  @Override
  public String getCriterionDescription() {
    return String.format("Nodes with Color %06X", getColor().getRGB() & 0xFFFFFF);
  }

  @Override
  public Icon getIcon() {
    return COLOR_CONDITION_ICON;
  }

  @Override
  public boolean matches(final ZyGraphNode<? extends CViewNode<?>> node) {
    return node.getRawNode().getColor().equals(getColor());
  }

  public void update() {
    notifyListeners();
  }
}
