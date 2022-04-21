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

package com.google.security.zynamics.bindiff.types;

import com.google.security.zynamics.bindiff.enums.ESide;
import com.google.security.zynamics.zylib.disassembly.IAddress;
import com.google.security.zynamics.zylib.general.Pair;
import java.util.Comparator;

// TODO(cblichmann): See com.google.security.zynamics.bindiff.project.matches.AddressPairComparator
public class AddressPairComparator implements Comparator<Pair<IAddress, IAddress>> {
  final ESide sortBySide;

  public AddressPairComparator(final ESide sortBySide) {
    this.sortBySide = sortBySide;
  }

  @Override
  public int compare(final Pair<IAddress, IAddress> addr1, final Pair<IAddress, IAddress> addr2) {
    final IAddress o1 = sortBySide == ESide.PRIMARY ? addr1.first() : addr1.second();
    final IAddress o2 = sortBySide == ESide.PRIMARY ? addr2.first() : addr2.second();

    return o1.compareTo(o2);
  }
}
