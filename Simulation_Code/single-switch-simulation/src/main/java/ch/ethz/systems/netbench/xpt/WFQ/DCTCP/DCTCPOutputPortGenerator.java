package ch.ethz.systems.netbench.xpt.WFQ.DCTCP;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.run.infrastructure.OutputPortGenerator;

public class DCTCPOutputPortGenerator extends OutputPortGenerator {

    private final long sizeBytes;

    public DCTCPOutputPortGenerator(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        SimulationLogger.logInfo("Port", "DCTCP(sizeBytes=" + sizeBytes + ")");
    }

    @Override
    public OutputPort generate(NetworkDevice ownNetworkDevice, NetworkDevice towardsNetworkDevice, Link link) {
        return new DCTCPOutputPort(ownNetworkDevice, towardsNetworkDevice, link, sizeBytes);
    }

}