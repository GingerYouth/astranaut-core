package org.cqfn.astranaut.core.database;

import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Node;

/**
 * Interface for database connection.
 *
 * @since 1.0
 * @param <I> Input node type
 * @param <R> Returning (database) node type
 */
public interface DbConnection<I, R> {
    /**
     * Remove all data from the database.
     */
    void drop();

    /**
     * Close the connection.
     */
    void close();

    /**
     * Convert the node to the DB vertex and put it to the database.
     *
     * @param node The node to add
     * @return Added converted vertex
     */
    R addVertex(I node);

    /**
     * Convert the DB vertex and get it as a {@link Node}.
     *
     * @param vertex The vertex to get
     * @return Converted node
     */
    Node getNode(R vertex, Factory factory);
}
