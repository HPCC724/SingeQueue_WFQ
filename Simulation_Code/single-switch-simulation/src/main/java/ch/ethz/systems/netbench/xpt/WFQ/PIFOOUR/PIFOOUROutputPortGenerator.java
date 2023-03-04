package ch.ethz.systems.netbench.xpt.WFQ.PIFOOUR;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class PIFOOUROutputPortGenerator extends OutputPortGenerator {

    private final long sizeBytes;

    public PIFOOUROutputPortGenerator(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        SimulationLogger.logInfo("Port", "PIFOOUR(sizeBytes=" + sizeBytes + ")");
    }

    @Override
    public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
        return new PIFOOUROutputPort(ownNetworkDevice, towardsNetworkDevice, link, sizeBytes);
    }

}