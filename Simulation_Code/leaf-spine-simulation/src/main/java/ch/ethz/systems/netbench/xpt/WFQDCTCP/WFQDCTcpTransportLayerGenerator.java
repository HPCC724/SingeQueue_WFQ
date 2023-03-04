package ch.ethz.systems.netbench.xpt.WFQDCTCP;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.infrastructure.TransportLayerGenerator;

public class WFQDCTcpTransportLayerGenerator extends TransportLayerGenerator {
    public WFQDCTcpTransportLayerGenerator() {
        // No parameters needed
        SimulationLogger.logInfo("Transport layer", "WFQDCTcp");
    }

    public TransportLayer generate(int identifier){
        return new WFQDCTcpTransportLayer(identifier);
    }
}
