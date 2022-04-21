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

#include "third_party/zynamics/bindiff/match/basic_block_hash.h"

#include "third_party/absl/strings/str_cat.h"
#include "third_party/zynamics/bindiff/match/flow_graph.h"

namespace security::bindiff {

bool MatchingStepHashBasicBlock ::FindFixedPoints(
    FlowGraph* primary, FlowGraph* secondary, const VertexSet& vertices1,
    const VertexSet& vertices2, FixedPoint* fixed_point,
    MatchingContext* context, MatchingStepsFlowGraph* matching_steps) {
  VertexIntMap vertex_map_1;
  VertexIntMap vertex_map_2;
  GetUnmatchedBasicBlocksByHash(primary, vertices1, &vertex_map_1);
  GetUnmatchedBasicBlocksByHash(secondary, vertices2, &vertex_map_2);
  return FindFixedPointsBasicBlockInternal(primary, secondary, &vertex_map_1,
                                           &vertex_map_2, fixed_point, context,
                                           matching_steps);
}

void MatchingStepHashBasicBlock::GetUnmatchedBasicBlocksByHash(
    const FlowGraph* flow_graph, const VertexSet& vertices,
    VertexIntMap* basic_blocks_map) {
  basic_blocks_map->clear();
  for (auto vertex : vertices) {
    if (!flow_graph->GetFixedPoint(vertex) &&
        flow_graph->GetInstructionCount(vertex) >= min_instructions_) {
      basic_blocks_map->emplace(flow_graph->GetHash(vertex), vertex);
    }
  }
}

}  // namespace security::bindiff
