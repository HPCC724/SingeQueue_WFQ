package ch.ethz.systems.netbench.xpt.WFQ.SQSWFQ;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class SQSWFQOutputPortGenerator extends OutputPortGenerator {

    private final long sizeBytes;

    public SQSWFQOutputPortGenerator(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        SimulationLogger.logInfo("Port", "SQSWFQ(sizeBytes=" + sizeBytes + ")");
    }

    @Override
    public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
        return new SQSWFQOutputPort(ownNetworkDevice, towardsNetworkDevice, link, sizeBytes);
    }

}