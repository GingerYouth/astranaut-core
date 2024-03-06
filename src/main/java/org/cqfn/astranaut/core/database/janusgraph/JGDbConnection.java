package org.cqfn.astranaut.core.database.janusgraph;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.io.binary.TypeSerializerRegistry;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyVertexProperty;
import org.apache.tinkerpop.gremlin.util.ser.GraphBinaryMessageSerializerV1;
import org.cqfn.astranaut.core.Builder;
import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.database.DbConnection;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

/**
 * Janusgraph DB connection implementation.
 *
 * @since 1.1.0
 */
@SuppressWarnings({
    "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
})
public class JGDbConnection implements DbConnection<JGNode, Vertex> {
    /**
     * The cluster.
     */
    private Cluster cluster;

    /**
     * The graphtraversal.
     */
    private GraphTraversalSource g;

    /**
     * Constructor.
     *
     * @param ip IP to connect
     * @param port Port to connect
     */
    public JGDbConnection(final String ip, final int port) {
        this.connect(ip, port);
    }

    /**
     * Get graph traversal source.
     *
     * @return {@link GraphTraversalSource}
     */
    public GraphTraversalSource getG() {
        return g;
    }

    /**
     * Connect to the DB.
     *
     * @param ip IP to connect
     * @param port Port to connect
     */
    public void connect(final String ip, final int port) {
        cluster = connectToJgDatabase(ip, port);
        g = getGraphTraversalSource(cluster);
    }

    @Override
    public void drop() {
        g.V().drop().iterate();
    }

    @Override
    public void close() {
        cluster.close();
    }

    /**
     * Add root node to the database.
     *
     * @param node The node to add
     * @return Added {@link Vertex} - converted node
     */
    @Override
    public Vertex addVertex(final JGNode node) {
        final GraphTraversal<Vertex, Vertex> thisVertexGT = g.addV();
        for (final Map.Entry<JGNode.PName, Object> entry: node.properties.entrySet()) {
            thisVertexGT.property(entry.getKey().toString(), entry.getValue());
        }
        final Vertex thisVertex = thisVertexGT.next();
        for (int i = 0; i < node.children.size(); i++) {
            final GraphTraversal<Vertex, Edge> edgeGT = g.V(thisVertex).addE("ast");
            final Vertex childVertex = addVertex(node.children.get(i), i);
            edgeGT.to(childVertex).next();
        }
        return Objects.requireNonNull(thisVertex);
    }

    /**
     * Add child node to the database with the specified index.
     *
     * @param node The node to add
     * @param index The node's index among children
     * @return Added {@link Vertex} - converted node
     */
    public Vertex addVertex(final JGNode node, final int index) {
        final GraphTraversal<Vertex, Vertex> thisVertexGT = g.addV();
        for (final Map.Entry<JGNode.PName, Object> entry: node.properties.entrySet()) {
            thisVertexGT.property(entry.getKey().toString(), entry.getValue());
        }
        thisVertexGT.property("INDEX", index);
        final Vertex thisVertex = thisVertexGT.next();
        for (int i = 0; i < node.children.size(); i++) {
            final GraphTraversal<Vertex, Edge> edgeGT = g.V(thisVertex).addE("ast");
            final Vertex childVertex = addVertex(node.children.get(i), i);
            edgeGT.to(childVertex).next();
        }
        return Objects.requireNonNull(thisVertex);
    }

    @Override
    public Node getNode(final Vertex vertex, final Factory factory) {
        final List<Vertex> childrenVertices = this.getG().V(vertex.id()).outE().inV().toList();
        final Vector<Node> childrenVector = new Vector<>();
        childrenVector.setSize(childrenVertices.size());
        for (final Vertex childVertex: childrenVertices) {
            final Node child = getNode(childVertex, factory);
            if (childVertex.property("INDEX") instanceof EmptyVertexProperty) {
                childrenVector.add(child);
            } else {
                childrenVector.add((Integer) childVertex.property("INDEX").value(), child);
            }
        }
        final Map<JGNode.PName, Object> properties = new HashMap<>();
        final Iterator<VertexProperty<Object>> vpIterator = vertex.properties();
        while (vpIterator.hasNext()) {
            final VertexProperty<Object> vertexProperty = vpIterator.next();
            properties.put(JGNode.PName.valueOf(vertexProperty.label()), vertexProperty.value());
        }
        final JGNode node = new JGNode(properties, factory);
        final Builder builder = factory.createBuilder((String) vertex.property("TYPE").value());
        builder.setFragment(node.getFragment());
        builder.setData(node.getData());
        childrenVector.removeIf(Objects::isNull);
        //TODO:: this fails because of actions. need to process them separately
        builder.setChildrenList(childrenVector);
        return builder.createNode();
    }

    /**
     * Connect to the Janusgraph DB.
     *
     * @param ipaddress IP to connect
     * @param port Port to connect
     * @return Connection {@link Cluster}
     */
    private static Cluster connectToJgDatabase(final String ipaddress, final int port) {
        final TypeSerializerRegistry serializer = TypeSerializerRegistry.build()
            .addRegistry(JanusGraphIoRegistry.instance())
            .create();
        final Cluster.Builder builder = Cluster.build();
        builder.addContactPoint(ipaddress);
        builder.port(port);
        builder.serializer(new GraphBinaryMessageSerializerV1(serializer));
        return builder.create();
    }

    /**
     * Get {@link GraphTraversalSource} using specified {@link Cluster}.
     *
     * @param cluster The cluster to get traversal source
     * @return Graph traversal source {@link GraphTraversalSource}
     */
    private static GraphTraversalSource getGraphTraversalSource(final Cluster cluster) {
        return traversal().withRemote(DriverRemoteConnection.using(cluster));
    }
}
