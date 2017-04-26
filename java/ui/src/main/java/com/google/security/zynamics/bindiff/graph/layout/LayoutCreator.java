package com.google.security.zynamics.bindiff.graph.layout;

import com.google.common.base.Preconditions;
import com.google.security.zynamics.bindiff.enums.ECircularLayoutStyle;
import com.google.security.zynamics.bindiff.enums.ELayoutOrientation;
import com.google.security.zynamics.bindiff.enums.EOrthogonalLayoutStyle;
import com.google.security.zynamics.bindiff.graph.settings.GraphLayoutSettings;
import com.google.security.zynamics.zylib.gui.zygraph.layouters.CircularStyle;
import com.google.security.zynamics.zylib.gui.zygraph.layouters.HierarchicOrientation;
import com.google.security.zynamics.zylib.gui.zygraph.layouters.OrthogonalStyle;
import com.google.security.zynamics.zylib.yfileswrap.gui.zygraph.layouters.ZyGraphLayouter;

import y.layout.CanonicMultiStageLayouter;

public final class LayoutCreator {
  public static CanonicMultiStageLayouter getCircularLayout(final GraphLayoutSettings settings) {
    Preconditions.checkNotNull(settings);

    final ECircularLayoutStyle style = settings.getCircularLayoutStyle();
    final long minNodeDist = settings.getMinimumCircularNodeDistance();

    return ZyGraphLayouter.createCircularLayouter(
        CircularStyle.parseInt(ECircularLayoutStyle.getOrdinal(style)), minNodeDist);
  }

  public static CanonicMultiStageLayouter getHierarchicalLayout(
      final GraphLayoutSettings settings) {
    Preconditions.checkNotNull(settings);

    final boolean orthogonalEdgeRouting = settings.getHierarchicalOrthogonalEdgeRouting();
    final long minLayerDist = settings.getMinimumHierarchicLayerDistance();
    final long minNodeDist = settings.getMinimumHierarchicNodeDistance();
    final ELayoutOrientation orientation = settings.getHierarchicOrientation();
    final HierarchicOrientation zyOrientation =
        HierarchicOrientation.parseInt(ELayoutOrientation.getOrdinal(orientation));

    return ZyGraphLayouter.createIncrementalHierarchicalLayouter(
        orthogonalEdgeRouting, minLayerDist, minNodeDist, zyOrientation);
  }

  public static CanonicMultiStageLayouter getOrthogonalLayout(final GraphLayoutSettings settings) {
    Preconditions.checkNotNull(settings);

    final EOrthogonalLayoutStyle style = settings.getOrthogonalLayoutStyle();
    final long gridSize = settings.getMinimumOrthogonalNodeDistance();
    final boolean isVerticalOrientation =
        settings.getOrthogonalLayoutOrientation() == ELayoutOrientation.VERTICAL;

    final OrthogonalStyle zyStyle =
        OrthogonalStyle.parseInt(EOrthogonalLayoutStyle.getOrdinal(style));

    return ZyGraphLayouter.createOrthoLayouter(zyStyle, gridSize, isVerticalOrientation);
  }
}