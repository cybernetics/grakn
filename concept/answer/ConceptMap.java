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

package grakn.core.concept.answer;

import grakn.core.concept.Concept;
import graql.lang.pattern.variable.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConceptMap implements Answer {

    private final Map<Reference, ? extends Concept> concepts;

    public ConceptMap() {
        this(new HashMap<>());
    }

    public ConceptMap(Map<Reference, ? extends Concept> concepts) {
        this.concepts = concepts;
    }

    public boolean contains(Reference variable) {
        return concepts.containsKey(variable);
    }

    public Concept get(String variable) {
        return get(Reference.named(variable));
    }

    public Concept get(Reference variable) {
        return concepts.get(variable);
    }

    public Map<Reference, ? extends Concept> concepts() { return concepts; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConceptMap that = (ConceptMap) o;
        return Objects.equals(concepts, that.concepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concepts);
    }

    @Override
    public String toString() {
        return "ConceptMap{" + concepts + '}';
    }
}
