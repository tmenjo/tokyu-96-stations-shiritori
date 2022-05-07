// SPDX-License-Identifier: MIT
// Copyright (c) 2022 Takashi Menjo

package org.example;

import org.example.Graph.Vertex;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Main {
    public static void main(String[] args) {
        final Graph graph = new Graph(new Scanner(System.in, StandardCharsets.UTF_8));
        System.out.println(ForkJoinPool.commonPool().invoke(new Walker(graph)));
    }

    private static class Walker extends RecursiveTask<List<Vertex>> {
        private final List<Vertex> track;
        private final Graph graph;
        private final Set<Vertex> vertices;

        public Walker(Graph graph) {
            this(Collections.emptyList(), graph, StreamSupport.stream(
                    Objects.requireNonNull(graph).spliterator(), false));
        }

        private Walker(List<Vertex> track, Graph graph, Stream<Vertex> stream) {
            this.track = Objects.requireNonNull(track);
            this.graph = Objects.requireNonNull(graph);
            this.vertices = Objects.requireNonNull(stream).collect(Collectors.toUnmodifiableSet());
        }

        @Override
        protected List<Vertex> compute() {
            // Fast return
            if (vertices.isEmpty()) {
                return track;
            }

            final List<Walker> tasks = new ArrayList<>(vertices.size());
            for (final Vertex vertex : vertices) {
                final List<Vertex> nextTrack =
                        Collections.unmodifiableList(concatenate(track, vertex));
                final Graph subGraph = graph.subGraph(vertex);
                final Stream<Vertex> nextVertices =
                        StreamSupport.stream(vertex.spliterator(), false).map(subGraph::find);
                tasks.add(new Walker(nextTrack, subGraph, nextVertices));
            }

            return invokeAll(tasks).stream()
                    .map(ForkJoinTask::join)
                    .max(Comparator.comparingInt(List::size))
                    .orElseThrow();
        }

        private static <T> List<T> concatenate(List<T> list, T newTail) {
            Objects.requireNonNull(list);
            Objects.requireNonNull(newTail);

            final List<T> newList = new ArrayList<>(list.size() + 1);
            newList.addAll(list);
            newList.add(newTail);
            return newList;
        }
    }
}
