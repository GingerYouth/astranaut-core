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

/**
 * Simple pair in case someone needs to use (key, val) objects without creating a map.
 *
 * @param <K> The key type
 * @param <V> The value type
 *
 * @since 1.0
 */
public final class Pair<K, V> {
    /**
     * The key.
     */
    private final K key;

    /**
     * The value.
     */
    private final V val;

    /**
     * Constructor.
     * @param key The key
     * @param val The value
     */
    public Pair(final K key, final V val) {
        this.key = key;
        this.val = val;
    }

    /**
     * Returns the key.
     * @return The key
     */
    public K getKey() {
        return this.key;
    }

    /**
     * Returns the value.
     * @return The value
     */
    public V getValue() {
        return this.val;
    }
}