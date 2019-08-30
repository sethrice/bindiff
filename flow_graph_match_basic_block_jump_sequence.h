#ifndef FLOW_GRAPH_MATCH_BASIC_BLOCK_JUMP_SEQUENCE_H_
#define FLOW_GRAPH_MATCH_BASIC_BLOCK_JUMP_SEQUENCE_H_

#include "third_party/zynamics/bindiff/flow_graph_match.h"

namespace security {
namespace bindiff {

class MatchingStepJumpSequence : public MatchingStepFlowGraph {
 public:
  MatchingStepJumpSequence()
      : MatchingStepFlowGraph("basicBlock: jump sequence matching",
                              "Basic Block: Jump Sequence") {}

  bool FindFixedPoints(FlowGraph* primary, FlowGraph* secondary,
                       const VertexSet& vertices1, const VertexSet& vertices2,
                       FixedPoint* fixed_point, MatchingContext* context,
                       MatchingStepsFlowGraph* matching_steps) override;

 private:
  void GetUnmatchedBasicBlocksByJumpSequence(const FlowGraph* flow_graph,
                                             const VertexSet& vertices,
                                             VertexIntMap* basic_blocks_map);
};

}  // namespace bindiff
}  // namespace security

#endif  // FLOW_GRAPH_MATCH_BASIC_BLOCK_JUMP_SEQUENCE_H_
