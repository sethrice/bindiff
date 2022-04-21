// Copyright 2011-2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.security.zynamics.bindiff.gui.tabpanels.viewtabpanel.subpanels;

import com.google.security.zynamics.bindiff.enums.EGraph;
import com.google.security.zynamics.bindiff.graph.SuperGraph;
import com.google.security.zynamics.bindiff.gui.tabpanels.viewtabpanel.ViewTabPanelFunctions;
import com.google.security.zynamics.bindiff.project.userview.ViewData;

public class SuperGraphPanel extends GraphPanel {
  public SuperGraphPanel(
      final ViewTabPanelFunctions controller, final ViewData view, final EGraph graphType) {
    super(
        controller, view.getGraphs().getDiff(), view, view.getGraphs().getSuperGraph(), graphType);
  }

  @Override
  public SuperGraph getGraph() {
    return (SuperGraph) super.getGraph();
  }
}
