/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package grakn.core.common.iterator;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

// TODO: verify (and potentially fix) this class is able to handle null objects
public class DistinctIterator<T> implements ResourceIterator<T> {

    private final ResourceIterator<T> iterator;
    private final Set<T> consumed;
    private T next;

    public DistinctIterator(ResourceIterator<T> iterator) {
        this(iterator, new HashSet<>());
    }

    public DistinctIterator(ResourceIterator<T> iterator, Set<T> duplicates) {
        this.iterator = iterator;
        this.consumed = duplicates;
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return (next != null) || fetchAndCheck();
    }

    private boolean fetchAndCheck() {
        while (iterator.hasNext() && consumed.contains(next = iterator.next())) next = null;
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        final T result = next;
        consumed.add(next);
        next = null;
        return result;
    }

    @Override
    public void recycle() {
        iterator.recycle();
    }
}
