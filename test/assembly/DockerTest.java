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

package grakn.core.test.assembly;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;

public class DockerTest {
    private static final Logger LOG = LoggerFactory.getLogger(DockerTest.class);
    private final ProcessExecutor executor;
    private static final int graknPort = 1729;

    public DockerTest() {
        executor = new ProcessExecutor().readOutput(true);
    }

    @Test
    public void bootup() throws InterruptedException, TimeoutException, IOException {
        String imagePath = Paths.get("assemble-docker.tar").toAbsolutePath().toString();
        ProcessResult result = execute("docker", "load", "-i", imagePath);
        LOG.info(result.outputString());
        StartedProcess graknProcess = executor.command(
                "docker", "run", "--name", "grakn",
                "--rm", "-t", "-p", String.format("%d:%d", graknPort, graknPort),
                "bazel:assemble-docker"
        ).start();
        waitUntilReady();
        assertTrue("Grakn Core failed to start", graknProcess.getProcess().isAlive());
        graknProcess.getProcess().destroy();
    }

    private void waitUntilReady() throws InterruptedException {
        int attempt = 0;
        while (!isGraknServerReady() && attempt < 25) {
            Thread.sleep(1000);
            attempt++;
        }
        if (!isGraknServerReady()) {
            throw new RuntimeException("Grakn server didn't boot up in the allotted time");
        }
    }

    private static boolean isGraknServerReady() {
        try {
            Socket s = new Socket("localhost", graknPort);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private ProcessResult execute(String... cmd) throws InterruptedException, TimeoutException, IOException {
        ProcessResult result = executor.command(cmd).redirectError(System.err).redirectOutput(System.out).execute();
        if (result.getExitValue() != 0) {
            LOG.error("An error has occurred.");
            LOG.error(" - cmd: " + Arrays.toString(cmd));
            LOG.error(" - output: " + result.outputString());
            throw new RuntimeException();
        } else return result;
    }
}
