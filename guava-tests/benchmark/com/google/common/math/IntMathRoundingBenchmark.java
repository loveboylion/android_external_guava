/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.math;

import static com.google.common.math.MathBenchmarking.ARRAY_MASK;
import static com.google.common.math.MathBenchmarking.ARRAY_SIZE;
import static com.google.common.math.MathBenchmarking.RANDOM_SOURCE;
import static com.google.common.math.MathBenchmarking.randomNonZeroBigInteger;
import static com.google.common.math.MathBenchmarking.randomPositiveBigInteger;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.common.math.IntMath;

import java.math.RoundingMode;

/**
 * Benchmarks for the rounding methods of {@code IntMath}.
 *
 * @author Louis Wasserman
 */
public class IntMathRoundingBenchmark extends SimpleBenchmark {
  private static final int[] positive = new int[ARRAY_SIZE];
  private static final int[] nonzero = new int[ARRAY_SIZE];
  private static final int[] ints = new int[ARRAY_SIZE];

  @Override
  protected void setUp() {
    for (int i = 0; i < ARRAY_SIZE; i++) {
      positive[i] = randomPositiveBigInteger(Integer.SIZE - 2).intValue();
      nonzero[i] = randomNonZeroBigInteger(Integer.SIZE - 2).intValue();
      ints[i] = RANDOM_SOURCE.nextInt();
    }
  }

  @Param({"DOWN", "UP", "FLOOR", "CEILING", "HALF_EVEN", "HALF_UP", "HALF_DOWN"})
  RoundingMode mode;

  public int timeLog2(int reps) {
    int tmp = 0;
    for (int i = 0; i < reps; i++) {
      int j = i & ARRAY_MASK;
      tmp += IntMath.log2(positive[j], mode);
    }
    return tmp;
  }

  public int timeLog10(int reps) {
    int tmp = 0;
    for (int i = 0; i < reps; i++) {
      int j = i & ARRAY_MASK;
      tmp += IntMath.log10(positive[j], mode);
    }
    return tmp;
  }

  public int timeSqrt(int reps) {
    int tmp = 0;
    for (int i = 0; i < reps; i++) {
      int j = i & ARRAY_MASK;
      tmp += IntMath.sqrt(positive[j], mode);
    }
    return tmp;
  }

  public int timeDivide(int reps) {
    int tmp = 0;
    for (int i = 0; i < reps; i++) {
      int j = i & ARRAY_MASK;
      tmp += IntMath.divide(ints[j], nonzero[j], mode);
    }
    return tmp;
  }

  public static void main(String[] args) {
    Runner.main(IntMathRoundingBenchmark.class, args);
  }
}
