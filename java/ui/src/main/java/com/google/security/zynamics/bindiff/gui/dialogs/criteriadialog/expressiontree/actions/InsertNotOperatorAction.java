package com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.expressiontree.actions;

import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.expressiontree.ExpressionTreeActionProvider;
import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.operators.NotCriterion;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class InsertNotOperatorAction extends AbstractAction {
  private final ExpressionTreeActionProvider actionProvider;

  public InsertNotOperatorAction(final ExpressionTreeActionProvider actionProvider) {
    super("Insert NOT");

    this.actionProvider = actionProvider;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    actionProvider.insertCriterion(new NotCriterion());
  }
}
