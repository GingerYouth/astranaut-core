/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.core.example;

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.algorithms.DifferenceTreeBuilder;
import org.cqfn.astranaut.core.example.green.ExpressionStatement;
import org.cqfn.astranaut.core.example.green.IntegerLiteral;
import org.cqfn.astranaut.core.example.green.Return;
import org.cqfn.astranaut.core.example.green.SimpleAssignment;
import org.cqfn.astranaut.core.example.green.StatementBlock;
import org.cqfn.astranaut.core.example.green.Variable;

/**
 * Little trees for testing purposes.
 *
 * @since 1.1.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class LittleTrees {
    /**
     * Private constructor.
     */
    private LittleTrees() {
    }

    /**
     * Creates a node that represents variable access.
     * @param name Variable name
     * @return Resulting node
     */
    public static Node createVariable(final String name) {
        Node result = EmptyTree.INSTANCE;
        final Variable.Constructor ctor = new Variable.Constructor();
        ctor.setData(name);
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents integer literal.
     * @param value Integer value
     * @return Resulting node
     */
    public static Node createIntegerLiteral(final int value) {
        Node result = EmptyTree.INSTANCE;
        final IntegerLiteral.Constructor ctor = new IntegerLiteral.Constructor();
        ctor.setData(String.valueOf(value));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents assignment operator.
     * @param left Left operand (assignable expression)
     * @param right Right operand (expression)
     * @return Resulting node
     */
    public static Node createAssignment(final Node left, final Node right) {
        Node result = EmptyTree.INSTANCE;
        final SimpleAssignment.Constructor ctor = new SimpleAssignment.Constructor();
        ctor.setChildrenList(Arrays.asList(left, right));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Represents expression as a statement.
     * @param expression Expression
     * @return Resulting node
     */
    public static Node wrapExpressionWithStatement(final Node expression) {
        Node result = EmptyTree.INSTANCE;
        final ExpressionStatement.Constructor ctor = new ExpressionStatement.Constructor();
        ctor.setChildrenList(Collections.singletonList(expression));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents return statement.
     * @param expression Expression to return (if exist)
     * @return Resulting node
     */
    public static Node createReturnStatement(final Node expression) {
        Node result = EmptyTree.INSTANCE;
        final Return.Constructor ctor = new Return.Constructor();
        if (expression != null) {
            ctor.setChildrenList(Collections.singletonList(expression));
        }
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a node that represents block of statements.
     * @param statements List of statements
     * @return Resulting node
     */
    public static Node createStatementBlock(final Node... statements) {
        Node result = EmptyTree.INSTANCE;
        final StatementBlock.Constructor ctor = new StatementBlock.Constructor();
        ctor.setChildrenList(Arrays.asList(statements));
        if (ctor.isValid()) {
            result = ctor.createNode();
        }
        return result;
    }

    /**
     * Creates a tree that has a "delete" action in it.
     * @return Root node
     */
    public static Node createTreeWithDeleteAction() {
        final Node victim = wrapExpressionWithStatement(
            createAssignment(
                createVariable("y"),
                createIntegerLiteral(2)
            )
        );
        final DifferenceTreeBuilder builder = new DifferenceTreeBuilder(
            createStatementBlock(
                wrapExpressionWithStatement(
                    createAssignment(
                        createVariable("x"),
                        createIntegerLiteral(1)
                    )
                ),
                    victim,
                createReturnStatement(
                    createVariable("x")
                )
            )
        );
        builder.deleteNode(victim);
        return builder.getRoot();
    }
}
