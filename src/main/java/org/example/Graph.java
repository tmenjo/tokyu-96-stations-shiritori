// SPDX-License-Identifier: MIT
// Copyright (c) 2022 Takashi Menjo

package org.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class Graph {
    private final Set<Vertex> vertices;
    private final Map<UUID,Vertex> mappings;
    private final Map<Vertex,Set<Vertex>> edges;

    public Graph(Iterator<String> it) {
        Objects.requireNonNull(it);

        final Set<Vertex> newVertices = new HashSet<>();
        it.forEachRemaining(name -> newVertices.add(new Vertex(this, name)));
        this.vertices = Collections.unmodifiableSet(newVertices);
        this.mappings = Collections.unmodifiableMap(buildMappings(this.vertices));
        this.edges = Collections.unmodifiableMap(buildEdges(this.vertices));
    }

    private Graph(Set<Vertex> vertices) {
        Objects.requireNonNull(vertices);

        final Set<Vertex> newVertices = new HashSet<>();
        vertices.forEach(vertex -> newVertices.add(vertex.duplicate(this)));
        this.vertices = Collections.unmodifiableSet(newVertices);
        this.mappings = Collections.unmodifiableMap(buildMappings(this.vertices));
        this.edges = Collections.unmodifiableMap(buildEdges(this.vertices));
    }

    public Set<Vertex> vertices() {
        return vertices;
    }

    public Graph subGraph(Vertex vertexToBeRemoved) {
        Objects.requireNonNull(vertexToBeRemoved);
        Requires.requireSame(this, vertexToBeRemoved.graph);
        Requires.requireTrue(vertices.contains(vertexToBeRemoved));

        final Set<Vertex> newVertices = new HashSet<>(vertices);
        newVertices.remove(vertexToBeRemoved);
        return new Graph(newVertices);
    }

    public Vertex find(Vertex vertexInOtherGraph) {
        Objects.requireNonNull(vertexInOtherGraph);
        Requires.requireNotSame(this, vertexInOtherGraph.graph);

        return Requires.requireFound(mappings.get(vertexInOtherGraph.id));
    }

    private static Map<UUID,Vertex> buildMappings(Set<Vertex> vertices) {
        Objects.requireNonNull(vertices);

        final Map<UUID,Vertex> map = new HashMap<>(vertices.size());
        vertices.forEach(vertex -> map.put(vertex.id, vertex));
        return map;
    }

    private static Map<Vertex,Set<Vertex>> buildEdges(Set<Vertex> vertices) {
        Objects.requireNonNull(vertices);

        final Map<Vertex,Set<Vertex>> map = new HashMap<>(vertices.size());
        vertices.forEach(u -> map.put(u, new HashSet<>()));
        for (final Vertex u : vertices) {
            for (final Vertex v : vertices) {
                if (u.connectableTo(v)) {
                    map.get(u).add(v);
                }
            }
        }
        return map;
    }

    public static class Vertex {
        private static final Pattern katakana;

        private final Graph graph;
        private final String name;
        private final UUID id;

        static {
            katakana = Pattern.compile("\\A[\u30a0-\u30ff]+\\z");
        }

        private Vertex(Graph graph, String name, UUID id) {
            this.graph = Objects.requireNonNull(graph);
            this.name = Objects.requireNonNull(name);
            this.id = Objects.requireNonNull(id);
            requireKatakana(name);
        }

        public Vertex(Graph graph, String name) {
            this(graph, name, UUID.randomUUID());
        }

        public Set<Vertex> nextVertices() {
            return graph.edges.get(this);
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof Vertex v) {
                return id.equals(v.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

        private Vertex duplicate(Graph newGraph) {
            return new Vertex(Objects.requireNonNull(newGraph), name, id);
        }

        private boolean connectableTo(Vertex other) {
            Objects.requireNonNull(other);
            return ((this != other) && (this.lastChar() == other.firstChar()));
        }

        private char firstChar() {
            return name.charAt(0);
        }

        private char lastChar() {
            return name.charAt(name.length() - 1);
        }

        private static void requireKatakana(String s) {
            Requires.requireTrue(katakana.matcher(s).matches());
        }
    }
}
