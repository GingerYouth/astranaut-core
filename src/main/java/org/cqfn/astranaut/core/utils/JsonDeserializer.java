/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astranaut.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.Builder;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Factory;
import org.cqfn.astranaut.core.FactorySelector;
import org.cqfn.astranaut.core.Node;

/**
 * Converts a string that contains a JSON object to a tree.
 *
 * @since 1.0.2
 */
public class JsonDeserializer {
    /**
     * The 'language' string.
     */
    private static final String STR_LANGUAGE = "language";

    /**
     * The 'root' string.
     */
    private static final String STR_ROOT = "root";

    /**
     * The 'type' string.
     */
    private static final String STR_TYPE = "type";

    /**
     * The 'data' string.
     */
    private static final String STR_DATA = "data";

    /**
     * The 'children' string.
     */
    private static final String STR_CHILDREN = "children";

    /**
     * String contains JSON object.
     */
    private final String source;

    /**
     * The factory selector.
     */
    private final FactorySelector selector;

    /**
     * The suitable factory for node creation.
     */
    private Factory factory;

    /**
     * Constructor.
     * @param source String that contains JSON object
     * @param selector The factory selector
     */
    public JsonDeserializer(final String source, final FactorySelector selector) {
        this.source = source;
        this.selector = selector;
    }

    /**
     * Converts the source string contains JSON object to a syntax tree.
     * @return Root node
     */
    public Node convert() {
        Node result = EmptyTree.INSTANCE;
        final JsonElement element = new Gson().fromJson(this.source, JsonElement.class);
        if (element.isJsonObject()) {
            final JsonObject obj = element.getAsJsonObject();
            String language = "";
            if (obj.has(JsonDeserializer.STR_LANGUAGE)) {
                language = obj.get(JsonDeserializer.STR_LANGUAGE).getAsString();
            }
            this.factory = this.selector.select(language);
            if (obj.has(JsonDeserializer.STR_ROOT)) {
                result = this.convertElement(obj.get(JsonDeserializer.STR_ROOT));
            }
        }
        return result;
    }

    /**
     * Converts JSON element to node.
     * @param element JSON element
     * @return A node
     */
    private Node convertElement(final JsonElement element) {
        Node result = EmptyTree.INSTANCE;
        if (element.isJsonObject()) {
            final JsonObject obj = element.getAsJsonObject();
            result = this.convertObject(obj);
        }
        return result;
    }

    /**
     * Converts JSON object to node.
     * @param obj JSON element
     * @return A node
     */
    private Node convertObject(final JsonObject obj) {
        Node result = EmptyTree.INSTANCE;
        if (obj.has(JsonDeserializer.STR_TYPE)) {
            final String type = obj.get(JsonDeserializer.STR_TYPE).getAsString();
            final Builder builder = this.factory.createBuilder(type);
            this.fillNodeBuilder(obj, builder);
            if (builder.isValid()) {
                result = builder.createNode();
            }
        }
        return result;
    }

    /**
     * Fills the node builder.
     * @param obj JSON element
     * @param builder The node builder
     */
    private void fillNodeBuilder(final JsonObject obj, final Builder builder) {
        if (obj.has(JsonDeserializer.STR_DATA)) {
            builder.setData(obj.get(JsonDeserializer.STR_DATA).getAsString());
        }
        if (obj.has(JsonDeserializer.STR_CHILDREN)) {
            final JsonElement children = obj.get(JsonDeserializer.STR_CHILDREN);
            if (children.isJsonArray()) {
                final List<Node> list = new LinkedList<>();
                for (final JsonElement child : children.getAsJsonArray()) {
                    list.add(this.convertElement(child));
                }
                builder.setChildrenList(list);
            }
        }
    }
}
