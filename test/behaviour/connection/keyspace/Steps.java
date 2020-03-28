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

package hypergraph.test.behaviour.connection.keyspace;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static grakn.common.util.Collections.list;
import static grakn.common.util.Collections.set;
import static hypergraph.test.behaviour.connection.ConnectionSteps.THREAD_POOL_SIZE;
import static hypergraph.test.behaviour.connection.ConnectionSteps.hypergraph;
import static hypergraph.test.behaviour.connection.ConnectionSteps.threadPool;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Steps {

    @When("connection create keyspace: {word}")
    public void connection_create_keyspace(String name) {
        connection_create_keyspaces(list(name));
    }

    @When("connection create keyspace(s):")
    public void connection_create_keyspaces(List<String> names) {
        for (String name : names) {
            hypergraph.keyspaces().create(name);
        }
    }

    @When("connection create keyspaces in parallel:")
    public void connection_create_keyspaces_in_parallel(List<String> names) {
        assertTrue(THREAD_POOL_SIZE >= names.size());

        CompletableFuture[] creations = new CompletableFuture[names.size()];
        int i = 0;
        for (String name : names) {
            creations[i++] = CompletableFuture.supplyAsync(() -> hypergraph.keyspaces().create(name), threadPool);
        }

        CompletableFuture.allOf(creations).join();
    }

    @When("connection delete keyspace(s):")
    public void connection_delete_keyspaces(List<String> names) {
        for (String keyspaceName : names) {
            hypergraph.keyspaces().get(keyspaceName).delete();
        }
    }

    @When("connection delete keyspaces in parallel:")
    public void connection_delete_keyspaces_in_parallel(List<String> names) {
        assertTrue(THREAD_POOL_SIZE >= names.size());

        CompletableFuture[] deletions = new CompletableFuture[names.size()];
        int i = 0;
        for (String name : names) {
            deletions[i++] = CompletableFuture.supplyAsync(
                    () -> {
                        hypergraph.keyspaces().get(name).delete();
                        return null;
                    },
                    threadPool
            );
        }

        CompletableFuture.allOf(deletions).join();
    }

    @Then("connection has keyspace(s):")
    public void connection_has_keyspaces(List<String> names) {
        assertEquals(set(names),
                     hypergraph.keyspaces().getAll().stream()
                             .map(keyspace -> keyspace.name())
                             .collect(Collectors.toSet()));
    }

    @Then("connection does not have keyspace(s):")
    public void connection_does_not_have_keyspaces(List<String> names) {
        for (String name : names) {
            assertNull(hypergraph.keyspaces().get(name));
        }
    }
}
