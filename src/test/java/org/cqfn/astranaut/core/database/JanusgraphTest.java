package org.cqfn.astranaut.core.database;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.database.janusgraph.JGDbConnection;
import org.cqfn.astranaut.core.database.janusgraph.JGNode;
import org.cqfn.astranaut.core.example.LittleTrees;
import org.cqfn.astranaut.core.example.green.GreenFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("This tests should be run manually after DB server is up and " +
    "corresponding IP and port are specified")
@SuppressWarnings({
    "PMD.LongVariable", "PMD.AvoidUsingHardCodedIP"
})
public final class JanusgraphTest {
    private static final String IP = "172.28.162.16";

    private static final int PORT = 8182;

    private static final String DATA = "DATA";

    private static final String TYPE = "TYPE";

    private static final String VARIABLE = "Variable";

    /**
     * Test saving of the graph to the janusgraph database
     * based on {@link LittleTrees#createStatementListWithThreeChildren()}.
     */
    @Test
    void testJGDbVertexStatementListWithThreeChildren() {
        final JGDbConnection jgDbConnection = new JGDbConnection(JanusgraphTest.IP, JanusgraphTest.PORT);
        jgDbConnection.drop();
        final Node node = LittleTrees.createStatementListWithThreeChildren();
        final JGNode jgNode = new JGNode(node, true);
        jgDbConnection.addVertex(jgNode);
        Assertions.assertDoesNotThrow(() -> {
            final GraphTraversal<Vertex, Vertex> statementBlock = jgDbConnection.getG().V()
                .has(JanusgraphTest.TYPE, "StatementBlock");
            final GraphTraversal<Vertex, Vertex> sbChildren = statementBlock
                .outE().inV();
            final GraphTraversal<Vertex, Vertex> return_ = sbChildren
                .asAdmin().clone()
                .has(JanusgraphTest.TYPE, "Return");
            final GraphTraversal<Vertex, Vertex> returnVariableX = return_
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "x");
            final GraphTraversal<Vertex, Vertex> expStatement = sbChildren
                .asAdmin().clone()
                .has(JanusgraphTest.TYPE, "ExpressionStatement");
            final GraphTraversal<Vertex, Vertex> simpleAssignment = expStatement
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "SimpleAssignment");
            final GraphTraversal<Vertex, Vertex> literal1 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "IntegerLiteral")
                .has(JanusgraphTest.DATA, "1");
            final GraphTraversal<Vertex, Vertex> literal2 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "IntegerLiteral")
                .has(JanusgraphTest.DATA, "2");
            final GraphTraversal<Vertex, Vertex> variableY = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "y");
            final GraphTraversal<Vertex, Vertex> variableX = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "x");
            returnVariableX.next();
            literal1.next();
            literal2.next();
            variableY.next();
            variableX.next();
            final Node back = jgDbConnection.getNode(
                jgDbConnection.getG().V().has(JanusgraphTest.TYPE, "StatementBlock").next(),
                GreenFactory.INSTANCE
            );
            Assertions.assertTrue(back.deepCompare(node));
        });
        jgDbConnection.close();
    }

    /**
     * Test saving of the graph with action to the janusgraph database
     * based on {@link LittleTrees#createTreeWithDeleteAction()}.
     */
    @Test
    void testJGDbVertexTreeWithDeleteAction() {
        /**
         * ...
         * @checkstyle <LocalFinalVariableNameCheck> (50 lines)
         * ...
         */
        final JGDbConnection jgDbConnection = new JGDbConnection(JanusgraphTest.IP, JanusgraphTest.PORT);
        jgDbConnection.drop();
        final Node node = LittleTrees.createTreeWithDeleteAction();
        final JGNode jgNode = new JGNode(node, true);
        jgDbConnection.addVertex(jgNode);
        Assertions.assertDoesNotThrow(() -> {
            final GraphTraversal<Vertex, Vertex> statementBlock = jgDbConnection.getG().V()
                .has(JanusgraphTest.TYPE, "StatementBlock");
            final GraphTraversal<Vertex, Vertex> sbChildren = statementBlock
                .outE().inV();
            final GraphTraversal<Vertex, Vertex> delete = sbChildren
                .asAdmin().clone()
                .has(JanusgraphTest.TYPE, "Delete");
            final GraphTraversal<Vertex, Vertex> expStatementDeleted = delete
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "ExpressionStatement");
            final GraphTraversal<Vertex, Vertex> simpleAssignmentDeleted = expStatementDeleted
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "SimpleAssignment");
            final GraphTraversal<Vertex, Vertex> literal2Deleted = simpleAssignmentDeleted
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "IntegerLiteral")
                .has(JanusgraphTest.DATA, "2");
            final GraphTraversal<Vertex, Vertex> variableYDeleted = simpleAssignmentDeleted
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "y");
            final GraphTraversal<Vertex, Vertex> aReturn = sbChildren
                .asAdmin().clone()
                .has(JanusgraphTest.TYPE, "Return");
            final GraphTraversal<Vertex, Vertex> returnVariableX = aReturn
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "x");
            final GraphTraversal<Vertex, Vertex> expStatement = sbChildren
                .asAdmin().clone()
                .has(JanusgraphTest.TYPE, "ExpressionStatement");
            final GraphTraversal<Vertex, Vertex> simpleAssignment = expStatement
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "SimpleAssignment");
            final GraphTraversal<Vertex, Vertex> literal1 = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, "IntegerLiteral")
                .has(JanusgraphTest.DATA, "1");
            final GraphTraversal<Vertex, Vertex> variableX = simpleAssignment
                .asAdmin().clone()
                .outE().inV()
                .has(JanusgraphTest.TYPE, JanusgraphTest.VARIABLE)
                .has(JanusgraphTest.DATA, "x");
            returnVariableX.next();
            literal1.next();
            variableX.next();
            literal2Deleted.next();
            variableYDeleted.next();
            final Node back = jgDbConnection.getNode(
                jgDbConnection.getG().V().has(JanusgraphTest.TYPE, "StatementBlock").next(),
                GreenFactory.INSTANCE
            );
            Assertions.assertTrue(back.deepCompare(node));
        });
        jgDbConnection.close();
    }
}
