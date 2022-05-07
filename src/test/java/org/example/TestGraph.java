// SPDX-License-Identifier: MIT
// Copyright (c) 2022 Takashi Menjo

package org.example;

import org.example.Graph.Vertex;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestGraph {
    @Test
    void testGraphNull() {
        assertThrows(NullPointerException.class, () -> new Graph(null));
    }

    @Test
    void testGraphNonKatakana() {
        assertThrows(IllegalArgumentException.class, () ->
                new Graph(Collections.singletonList("あおばだい").iterator()));
    }

    @Test
    void testGraphEmpty() {
        final Graph graph = new Graph(Collections.emptyIterator());
        assertEquals(0, graph.numVertices());

        final Iterator<Vertex> it = graph.iterator();
        assertNotNull(it);
        assertFalse(it.hasNext());
    }

    @Test
    void testGraphOneVertex() {
        final Graph graph = new Graph(Collections.singletonList("アオバダイ").iterator());
        assertEquals(1, graph.numVertices());

        assertThrows(NullPointerException.class, () -> graph.find(null));

        final Iterator<Vertex> graphIterator = graph.iterator();
        assertNotNull(graphIterator);
        assertTrue(graphIterator.hasNext());
        final Vertex vertex = graphIterator.next();
        assertNotNull(vertex);
        assertFalse(graphIterator.hasNext());

        assertEquals(vertex, vertex); // to check the equal() method
        assertDoesNotThrow(vertex::hashCode);
        assertEquals("アオバダイ", vertex.toString());

        final Iterator<Vertex> vertexIterator = vertex.iterator();
        assertNotNull(vertexIterator);
        assertFalse(vertexIterator.hasNext());

        // cannot pass any vertex the graph contains to the find() method
        assertThrows(IllegalArgumentException.class, () -> graph.find(vertex));

        final Graph subGraph = graph.subGraph(vertex);
        assertNotNull(subGraph);
        assertEquals(0, subGraph.numVertices());
        final Iterator<Vertex> subGraphIterator = subGraph.iterator();
        assertNotNull(subGraphIterator);
        assertFalse(subGraphIterator.hasNext());

        // subgraph does not contain the vertex passed to the previous subGraph() method call
        assertThrows(NoSuchElementException.class, () -> subGraph.find(vertex));

        // any subGraph() call does not change the original graph
        assertEquals(1, graph.numVertices());
        assertSame(vertex, graph.iterator().next());
    }

    @Test
    void testGraphTwoVertices() {
        final Graph g0 = new Graph(Arrays.asList("アオバダイ", "イケガミ").iterator());
        assertEquals(2, g0.numVertices());

        final SortedSet<Vertex> sorted = new TreeSet<>(Comparator.comparing(Vertex::toString));
        final Iterator<Vertex> git0 = g0.iterator();
        assertNotNull(git0);
        git0.forEachRemaining(sorted::add);
        final Iterator<Vertex> sortedIterator = sorted.iterator();
        assertNotNull(sortedIterator);
        assertTrue(sortedIterator.hasNext());
        final Vertex v1 = sortedIterator.next();
        assertNotNull(v1);
        assertTrue(sortedIterator.hasNext());
        final Vertex v2 = sortedIterator.next();
        assertNotNull(v2);
        assertFalse(sortedIterator.hasNext());

        assertEquals("アオバダイ", v1.toString());
        assertEquals("イケガミ", v2.toString());

        // the vertex "アオバダイ" connects to the vertex "イケガミ"
        final Iterator<Vertex> vit1 = v1.iterator();
        assertNotNull(vit1);
        assertTrue(vit1.hasNext());
        assertSame(v2, vit1.next());
        assertFalse(vit1.hasNext());
        // but the reverse is not so
        final Iterator<Vertex> vit2 = v2.iterator();
        assertNotNull(vit2);
        assertFalse(vit2.hasNext());

        // cannot pass any vertex the graph contains to the find() method
        assertThrows(IllegalArgumentException.class, () -> g0.find(v1));
        assertThrows(IllegalArgumentException.class, () -> g0.find(v2));

        // first subgraph
        final Graph g1 = g0.subGraph(v1);
        assertNotNull(g1);
        assertEquals(1, g1.numVertices());
        assertThrows(NoSuchElementException.class, () -> g1.find(v1));
        final Vertex v12 = g1.find(v2);
        assertNotNull(v12);
        assertNotSame(v2, v12);
        assertEquals(v2, v12);
        assertEquals(v2.hashCode(), v12.hashCode());
        assertEquals(v2.toString(), v12.toString());
        final Iterator<Vertex> git1 = g1.iterator();
        assertNotNull(git1);
        assertTrue(git1.hasNext());
        assertSame(v12, git1.next());
        assertFalse(git1.hasNext());
        final Iterator<Vertex> vit12 = v12.iterator();
        assertNotNull(vit12);
        assertFalse(vit12.hasNext());

        // second subgraph
        final Graph g2 = g0.subGraph(v2);
        assertNotNull(g2);
        assertEquals(1, g2.numVertices());
        assertThrows(NoSuchElementException.class, () -> g2.find(v2));
        final Vertex v21 = g2.find(v1);
        assertNotNull(v21);
        assertNotSame(v1, v21);
        assertEquals(v1, v21);
        assertEquals(v1.hashCode(), v21.hashCode());
        assertEquals(v1.toString(), v21.toString());
        final Iterator<Vertex> git2 = g2.iterator();
        assertNotNull(git2);
        assertTrue(git2.hasNext());
        assertSame(v21, git2.next());
        assertFalse(git2.hasNext());
        final Iterator<Vertex> vit21 = v21.iterator();
        assertNotNull(vit21);
        assertFalse(vit21.hasNext());
    }
}
