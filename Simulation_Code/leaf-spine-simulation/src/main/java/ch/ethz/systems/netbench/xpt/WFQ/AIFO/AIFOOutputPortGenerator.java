package ch.ethz.systems.netbench.xpt.WFQ.AIFO;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class AIFOOutputPortGenerator extends OutputPortGenerator {

    private final long sizeBytes;

    public AIFOOutputPortGenerator(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        SimulationLogger.logInfo("Port", "AIFO(sizeBytes=" + sizeBytes + ")");
    }

    @Override
    public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
        return new AIFOOutputPort(ownNetworkDevice, towardsNetworkDevice, link, sizeBytes);
    }

}