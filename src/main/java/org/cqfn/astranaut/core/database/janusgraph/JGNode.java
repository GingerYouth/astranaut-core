package org.cqfn.astranaut.core.database.janusgraph;


import org.cqfn.astranaut.core.CommonFragment;
import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.Fragment;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.Position;
import org.cqfn.astranaut.core.Type;
import org.cqfn.astranaut.core.database.DbNode;

import java.util.*;

@SuppressWarnings({
    "PMD.OnlyOneConstructorShouldDoInitialization", "PMD.ConstructorShouldDoInitialization",
    "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
})
public class JGNode implements DbNode {
    final Map<PName, Object> properties = new HashMap<>();

    final List<JGNode> children = new ArrayList<>();

    private Factory factory;

    public enum PName {
        ROOT, BEGIN, END, SOURCE, TYPE, DATA, CHILD_COUNT, INDEX, UUID
    }

    public JGNode(final Node node, final boolean root) {
        final Position begin = node.getFragment().getBegin();
        final Position end = node.getFragment().getEnd();
        if (root) {
            this.properties.put(PName.ROOT, true);
        }
        this.properties.put(PName.BEGIN, String.valueOf(begin.getIndex()));
        this.properties.put(PName.END, String.valueOf(end.getIndex()));
        this.properties.put(PName.SOURCE, node.getFragment().getSource().getFragmentAsString(begin, end));
        this.properties.put(PName.DATA, node.getData());
        this.properties.put(PName.TYPE, node.getType().getName());
        this.properties.put(PName.CHILD_COUNT, String.valueOf(node.getChildCount()));
        this.properties.put(PName.UUID, UUID.randomUUID().toString()); // TODO:: think about collisions
        for (final Node child: node.getChildrenList()) {
            this.children.add(new JGNode(child, false));
        }
    }

    public JGNode(final Map<PName, Object> vertexProperties, final Factory factory) {
        this.properties.putAll(vertexProperties);
        this.factory = factory;
    }

    @Override
    public Fragment getFragment() {
        return new CommonFragment(
            (String) this.properties.get(PName.SOURCE),
            Integer.parseInt((String) this.properties.get(PName.BEGIN)),
            Integer.parseInt((String) this.properties.get(PName.BEGIN))
        );
    }

    @Override
    public Type getType() {
        return this.factory.getType((String) this.properties.get(PName.TYPE));
    }

    @Override
    public String getTypeName() {
        return this.getType().getName();
    }

    @Override
    public String getData() {
        return (String) this.properties.get(PName.DATA);
    }

    @Override
    public int getChildCount() {
        return Integer.parseInt((String) this.properties.get(PName.CHILD_COUNT));
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

}
