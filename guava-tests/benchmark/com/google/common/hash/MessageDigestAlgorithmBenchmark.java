/*
 * Copyright (C) 2012 The Guava Authors
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

package com.google.common.hash;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Random;

/**
 * Benchmarks for comparing {@link MessageDigest}s and {@link HashFunction}s that wrap
 * {@link MessageDigest}s.
 *
 * <p>Parameters for the benchmark are:
 * <ul>
 * <li>size: The length of the byte array to hash.
 * <li>algorithm: the algorithm to hash with (e.g. MD5, SHA1, etc.).
 * <li>hashMethod: how to hash the data (using the Hashing API or the MessageDigest API).
 * </ul>
 *
 * @author Kurt Alfred Kluever
 */
public class MessageDigestAlgorithmBenchmark extends SimpleBenchmark {
  @Param({"10", "1000", "100000", "1000000"}) int size;
  @Param Algorithm algorithm;
  @Param HashMethod hashMethod;

  private enum HashMethod {
    MESSAGE_DIGEST_API() {
      @Override public byte[] hash(Algorithm algorithm, byte[] input) {
        MessageDigest md = algorithm.getMessageDigest();
        md.update(input);
        return md.digest();
      }
    },
    HASH_FUNCTION_API() {
      @Override public byte[] hash(Algorithm algorithm, byte[] input) {
        return algorithm.getHashFunction().hashBytes(input).asBytes();
      }
    };
    public abstract byte[] hash(Algorithm algorithm, byte[] input);
  }

  private enum Algorithm {
    MD5("MD5", Hashing.md5()),
    SHA_1("SHA-1", Hashing.sha1()),
    SHA_256("SHA-256", Hashing.sha256()),
    SHA_512("SHA-512", Hashing.sha512());

    private final String algorithmName;
    private final HashFunction hashFn;
    Algorithm(String algorithmName, HashFunction hashFn) {
      this.algorithmName = algorithmName;
      this.hashFn = hashFn;
    }
    public MessageDigest getMessageDigest() {
      try {
        return MessageDigest.getInstance(algorithmName);
      } catch (NoSuchAlgorithmException e) {
        throw new AssertionError(e);
      }
    }
    public HashFunction getHashFunction() {
      return hashFn;
    }
  }

  // Use a constant seed for all of the benchmarks to ensure apples to apples comparisons.
  private static final int RANDOM_SEED = new Random().nextInt();

  private byte[] testBytes;

  @Override public void setUp() {
    testBytes = new byte[size];
    new Random(RANDOM_SEED).nextBytes(testBytes);
  }

  public byte timeHashing(int reps) {
    byte result = 0x01;
    HashMethod hashMethod = this.hashMethod;
    Algorithm algorithm = this.algorithm;
    for (int i = 0; i < reps; i++) {
      result ^= hashMethod.hash(algorithm, testBytes)[0];
    }
    return result;
  }

  public static void main(String[] args) {
    Runner.main(MessageDigestAlgorithmBenchmark.class, args);
  }
}
