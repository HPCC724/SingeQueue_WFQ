package ch.ethz.systems.netbench.xpt.WFQ.SQWFQ;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class SQWFQOutputPortGenerator extends OutputPortGenerator {

    private final long sizeBytes;

    public SQWFQOutputPortGenerator(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        SimulationLogger.logInfo("Port", "SQWFQ(sizeBytes=" + sizeBytes + ")");
    }

    @Override
    public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
        return new SQWFQOutputPort(ownNetworkDevice, towardsNetworkDevice, link, sizeBytes);
    }

}