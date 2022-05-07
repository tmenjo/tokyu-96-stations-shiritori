// SPDX-License-Identifier: MIT
// Copyright (c) 2022 Takashi Menjo

package org.example;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.example.Requires.requireNotSame;
import static org.example.Requires.requireSame;
import static org.example.Requires.requireTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRequires {
    @Test
    void testRequireTrueReturns() {
        requireTrue(true);
    }

    @Test
    void testRequireTrueThrows() {
        assertThrows(IllegalArgumentException.class, () -> requireTrue(false));
    }

    @Test
    void testRequireSameNonNullReturns() {
        final String x = "foo";
        requireSame(x, x);
    }

    @Test
    void testRequireSameNullReturns() {
        requireSame(null, null);
    }

    @Test
    void testRequireSameEqualsThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                requireSame("foo", "FOO".toLowerCase()));
    }

    @Test
    void testRequireSameNotEqualsThrows() {
        assertThrows(IllegalArgumentException.class, () -> requireSame("foo", "bar"));
    }

    @Test
    void testRequireSameNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> requireSame("foo", null));
        assertThrows(IllegalArgumentException.class, () -> requireSame(null, "foo"));
    }

    @Test
    void testRequireNotSameEqualsReturns() {
        requireNotSame("foo", "FOO".toLowerCase());
    }

    @Test
    void testRequireNotSameNotEqualsReturns() {
        requireNotSame("foo", "bar");
    }

    @Test
    void testRequireNotSameNullReturns() {
        requireNotSame("foo", null);
        requireNotSame(null, "foo");
    }

    @Test
    void testRequireNotSameNonNullThrows() {
        final String x = "foo";
        assertThrows(IllegalArgumentException.class, () -> requireNotSame(x, x));
    }

    @Test
    void testRequireNotSameNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> requireNotSame(null, null));
    }

    @Test
    void testRequireFoundReturns() {
        final String x = "foo";
        assertSame(x, Requires.requireFound(x));
    }

    @Test
    void testRequireFoundThrows() {
        assertThrows(NoSuchElementException.class, () -> Requires.requireFound(null));
    }
}
