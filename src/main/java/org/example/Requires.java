// SPDX-License-Identifier: MIT
// Copyright (c) 2022 Takashi Menjo

package org.example;

import java.util.NoSuchElementException;

public class Requires {
    public static void requireTrue(boolean x) {
        if (!x) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireNotSame(Object expected, Object actual) {
        if (expected == actual) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> T requireFound(T x) {
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
}
