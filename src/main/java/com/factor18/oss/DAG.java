package com.factor18.oss;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DAG<V extends Vertex<P>, P> {
    public DAG() {
        this.traverser = new Traverser();
    }

    public boolean addVertex(V vertex) {
        if(this.vertices.contains(vertex)) {
            return false;
        } else {
            this.vertices.add(vertex);
            return true;
        }
    }

    public boolean addEdge(V source, P sourcePort, V target, P targetPort) {
        return addEdge(new Edge<>(source, target, sourcePort, targetPort));
    }

    public boolean addEdge(Edge<V, P> edge) {
        if(connectionExists(edge.getSource(), edge.getTarget(), edge.getSourcePort(), edge.getTargetPort())) {
            return false;
        } else {
            edges.add(edge);
            if(isCyclic()) {
                edges.remove(edge);
                return false;
            } else {
                return true;
            }
        }
    }

    public Set<V> getVertices() {
        return vertices;
    }

    public Set<Edge<V, P>> getEdges() {
        return edges;
    }

    public Iterator<V> getTopologicalIterator() {
        try {
            return this.traverser.getTopologicalIterator(this);
        } catch (CyclicDependencyException e) {
            // It will never come here
            return null;
        }
    }

    private boolean isCyclic() {
        return this.traverser.isCyclic(this);
    }

    private boolean connectionExists(V source, V target, P sourcePort, P targetPort) {
        if(vertices.contains(source) && vertices.contains(target) && source.getOutPorts().contains(sourcePort) && target.getInPorts().contains(targetPort)) {
            return edges.stream().anyMatch((e) ->
                    e.getSource().equals(source)
                            && e.getTarget().equals(target)
                            && e.getSourcePort().equals(sourcePort)
                            && e.getTargetPort().equals(targetPort)
            );
        } else {
            return false;
        }
    }

    public Set<V> getInVertices(V vertex) {
        return edges.stream().filter((e) -> e.getSource().equals(vertex)).map(Edge::getSource).collect(Collectors.toSet());
    }

    public Set<V> getOutVertices(V vertex) {
        return edges.stream().filter((e) -> e.getSource().equals(vertex)).map(Edge::getTarget).collect(Collectors.toSet());
    }

    public Set<Edge<V, P>> getInEdges(V vertex, P port) {
        return edges.stream().filter((e) -> e.getTarget().equals(vertex) && e.getTargetPort().equals(port)).collect(Collectors.toSet());
    }

    public Set<Edge<V, P>> getOutEdges(V vertex, P port) {
        return edges.stream().filter((e) -> e.getSource().equals(vertex) && e.getSourcePort().equals(port)).collect(Collectors.toSet());
    }

    public Set<Edge<V, P>> getAllInEdges(V vertex) {
        return edges.stream().filter((e) -> e.getTarget().equals(vertex)).collect(Collectors.toSet());
    }

    public Set<Edge<V, P>> getAllOutEdges(V vertex) {
        return edges.stream().filter((e) -> e.getSource().equals(vertex)).collect(Collectors.toSet());
    }

    private Traverser traverser;
    private Set<Edge<V, P>> edges = Sets.newHashSet();
    private Set<V> vertices = Sets.newHashSet();

    private class Traverser {

        private void traverse(V vertex, List<V> traversed, List<V> order) throws CyclicDependencyException {
            if(traversed.contains(vertex)) throw new CyclicDependencyException(vertex.getName());
            traversed.add(vertex);

            Set<V> outgoing = getOutVertices(vertex);

            if(outgoing.size() != 0) {
                for (V v: outgoing) {
                    traverse(v, Lists.newArrayList(traversed), order);
                }
            }

            if(!order.contains(vertex)) order.add(vertex);
        }

        public Iterator<V> getTopologicalIterator(DAG<V, P> dag) throws CyclicDependencyException {
            Set<V> vertices = dag.getVertices();
            ArrayList<V> order = Lists.newArrayList();
            for (V vertex: vertices) {
                traverse(vertex, Lists.newArrayList(), order);
            }
            return Lists.reverse(order).iterator();
        }

        public boolean isCyclic(DAG<V, P> dag) {
            try {
                getTopologicalIterator(dag);
                return false;
            } catch (CyclicDependencyException e) {
                return true;
            }
        }
    }
}
