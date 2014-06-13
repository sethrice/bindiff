#include "third_party/zynamics/bindiff/instruction.h"

#include <functional>

#include "third_party/boost/do_not_include_from_google3_only_third_party/boost/boost/algorithm/string.hpp"
#include "third_party/boost/do_not_include_from_google3_only_third_party/boost/boost/iterator/transform_iterator.hpp"
#include "third_party/zynamics/bindiff/log.h"

namespace {

typedef std::vector<int> Lengths;

// Calculate LCS row lengths given iterator ranges into two sequences.
// On completion, `lens` holds LCS lengths in the final row.
template <typename Iterator>
void LcsLens(Iterator xlo, Iterator xhi, Iterator ylo, Iterator yhi,
             Lengths& lens) {
  // Two rows of workspace.
  // Careful! We need the 1 for the leftmost column.
  Lengths curr(1 + distance(ylo, yhi), 0);
  Lengths prev(curr);

  for (Iterator x = xlo; x != xhi; ++x) {
    swap(prev, curr);
    int i = 0;
    for (Iterator y = ylo; y != yhi; ++y, ++i) {
      curr[i + 1] = *x == *y ? prev[i] + 1 : std::max(curr[i], prev[i + 1]);
    }
  }
  swap(lens, curr);
}

// Recursive LCS calculation. See Hirschberg for the theory!
// This is a divide and conquer algorithm. In the recursive case, we split the
// xrange in two. Then, by calculating lengths of LCSes from the start and end
// corners of the [xlo, xhi] x [ylo, yhi] grid, we determine where the yrange
// should be split.
// xo is the origin (element 0) of the xs sequence
// xlo, xhi is the range of xs being processed
// ylo, yhi is the range of ys being processed
// Parameter xs_in_lcs holds the members of xs in the LCS.
// TODO(cblichmann): Merge with the implementation in //security/vxclass/siggen
template <typename Iterator, typename OutIterator>
void ComputeLcs(Iterator xo, Iterator xlo, Iterator xhi, Iterator yo,
                Iterator ylo, Iterator yhi, OutIterator xout,
                OutIterator yout) {
  const unsigned nx = distance(xlo, xhi);
  if (nx == 0) {         // all done
  } else if (nx == 1) {  // single item in x range.
    // If it's in the yrange, mark its position in the LCS.
    const Iterator pos = find(ylo, yhi, *xlo);
    if (pos != yhi) {
      *xout++ = distance(xo, xlo);
      *yout++ = distance(yo, pos);
    }
  } else {  // split the xrange
    Iterator xmid = xlo + nx / 2;

    // Find LCS lengths at xmid, working from both ends of the range
    Lengths ll_b, ll_e;
    std::reverse_iterator<Iterator> hix(xhi), midx(xmid), hiy(yhi), loy(ylo);

    LcsLens(xlo, xmid, ylo, yhi, ll_b);
    LcsLens(hix, midx, hiy, loy, ll_e);

    // Find the optimal place to split the y range
    Lengths::const_reverse_iterator e = ll_e.rbegin();
    int lmax = -1;
    Iterator y = ylo, ymid = ylo;

    for (Lengths::const_iterator b = ll_b.begin(); b != ll_b.end(); ++b, ++e) {
      if (*b + *e > lmax) {
        lmax = *b + *e;
        ymid = y;
      }
      if (y != yhi) ++y;
    }

    // TODO(soerenme) test: We may need to implement an iterative solution to
    //     prevent stack overflows for large inputs. Split the range and
    //     recurse.
    ComputeLcs(xo, xlo, xmid, yo, ylo, ymid, xout, yout);
    ComputeLcs(xo, xmid, xhi, yo, ymid, yhi, xout, yout);
  }
}

template <typename Iterator, typename OutIterator>
void ComputeLcs(Iterator xbegin, Iterator xend, Iterator ybegin, Iterator yend,
                OutIterator xout, OutIterator yout) {
  if (xbegin == xend || ybegin == yend) {
    return;
  }

  // Optimize by eliminating common prefixes.
  Iterator start1 = xbegin;
  Iterator start2 = ybegin;
  for (; start1 != xend && start2 != yend && *start1 == *start2;
       ++start1, ++start2) {
    *xout++ = distance(xbegin, start1);
    *yout++ = distance(ybegin, start2);
  }

  // Early exit if one string is completely contained in the other.
  if (start1 == xend || start2 == yend) {
    return;
  }

  // Optimize by eliminating common suffixes.
  Iterator rstart1 = xend;
  --rstart1;
  Iterator rstart2 = yend;
  --rstart2;
  for (; rstart1 != start1 && rstart2 != start2 && *rstart1 == *rstart2;
       --rstart1, --rstart2) {
    // intentionally left blank
  }

  rstart1++;
  rstart2++;
  ComputeLcs(xbegin, start1, rstart1, ybegin, start2, rstart2, xout, yout);

  for (; rstart1 != xend && rstart2 != yend; ++rstart1, ++rstart2) {
    *xout++ = distance(xbegin, rstart1);
    *yout++ = distance(ybegin, rstart2);
  }
}

struct ProjectPrime : public std::unary_function<Instruction, size_t> {
  size_t operator()(const Instruction& instruction) const {
    return instruction.GetPrime();
  }
};

}  // namespace

CacheEntry::CacheEntry(size_t prime, const std::string& operands)
    : operands_(operands), prime_(prime) {}

bool operator<(const CacheEntry& left, const CacheEntry& right) {
  return left.prime_ == right.prime_ ? left.operands_ < right.operands_
                                     : left.prime_ < right.prime_;
}

bool operator==(const CacheEntry& one, const CacheEntry& two) {
  return one.prime_ == two.prime_ && one.operands_ == two.operands_;
}

// Cache must have a lifetime longer than instruction, otherwise cache_entry_
// will point to invalid memory.
Instruction::Instruction(Cache* cache, Address address,
                         const std::string& mnemonic, uint32_t prime,
                         const std::string& operands)
    : cache_entry_(0), address_(address) {
  // The differ standalone version is multi threaded so be careful with static
  // variables and the like.
  CHECK(cache != nullptr);

  CacheEntry entry(prime, operands);
  if (cache->cache_.find(entry) == cache->cache_.end()) {
    CHECK(cache->cache_.insert(entry).second);
  }
  cache_entry_ = &(*cache->cache_.find(entry));

  Cache::PrimeToMnemonic::const_iterator i =
      cache->prime_to_mnemonic_.find(prime);
  if (i != cache->prime_to_mnemonic_.end()) {
    if (!i->second.empty() && i->second != mnemonic) {
      LOG(INFO) << "prime collision detected! mnemonics '" << i->second
                << "' and '" << mnemonic << "'";
    }
  } else {
    cache->prime_to_mnemonic_[prime] = mnemonic;
  }
}

Address Instruction::GetAddress() const { return address_; }

uint32_t Instruction::GetPrime() const { return cache_entry_->prime_; }

std::string Instruction::GetOperandString() const {
  return cache_entry_->operands_;
}

std::string Instruction::GetMnemonic(const Cache* cache) const {
  Cache::PrimeToMnemonic::const_iterator i =
      cache->prime_to_mnemonic_.find(GetPrime());
  assert(i != cache->prime_to_mnemonic_.end());
  return i->second;
}

// TODO(soerenme) Try a binary search first as addresses should normally be
//     sorted in ascending order if that search fails fall back to linear scan.
const Instruction* GetInstruction(
    const Instructions& instructions, const Address instruction_address) {
  for (Instructions::const_iterator i = instructions.begin(),
       end = instructions.end(); i != end; ++i) {
    const Instruction* instruction = &*i;
    if (instruction->GetAddress() == instruction_address)
      return instruction;
  }
  assert(false && "this should never happen");
  return 0;
}

void ComputeLcs(const Instructions::const_iterator& instructions1_begin,
                const Instructions::const_iterator& instructions1_end,
                const Instructions::const_iterator& instructions2_begin,
                const Instructions::const_iterator& instructions2_end,
                InstructionMatches& matches) {
  typedef std::list<size_t> List;
  List matches1, matches2;
  typedef boost::transform_iterator<
      ProjectPrime, Instructions::const_iterator> Iterator;
  ComputeLcs(Iterator(instructions1_begin, ProjectPrime()),
             Iterator(instructions1_end, ProjectPrime()),
             Iterator(instructions2_begin, ProjectPrime()),
             Iterator(instructions2_end, ProjectPrime()),
             std::back_inserter(matches1),
             std::back_inserter(matches2));

  for (List::const_iterator i = matches1.begin(), j = matches2.begin();
       i != matches1.end() && j != matches2.end(); ++i, ++j) {
    matches.push_back(std::make_pair(
        &*(instructions1_begin + *i), &*(instructions2_begin + *j)));
  }

  // Shrink-to-fit idiom: This reduces the capacity of the vector to the
  // required minimum.
  InstructionMatches(matches).swap(matches);
}
