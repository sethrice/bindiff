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

package com.google.security.zynamics.zylib.io;

import java.io.IOException;

public class ProcessHelpers {
  public static void runBatchFile(final String filename) throws IOException, InterruptedException {
    final Process p = Runtime.getRuntime().exec(new String[] {"cmd", "/c", filename});

    p.waitFor();
  }
}
