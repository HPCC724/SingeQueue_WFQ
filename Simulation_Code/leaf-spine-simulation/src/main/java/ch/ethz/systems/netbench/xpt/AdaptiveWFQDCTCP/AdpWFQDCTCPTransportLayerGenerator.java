package ch.ethz.systems.netbench.xpt.AdaptiveWFQDCTCP;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.infrastructure.TransportLayerGenerator;
import ch.ethz.systems.netbench.xpt.AdaptiveWFQTCP.AdpWFQTCPTransportLayer;

public class AdpWFQDCTCPTransportLayerGenerator extends TransportLayerGenerator {
    public AdpWFQDCTCPTransportLayerGenerator() {
        // No parameters needed
        SimulationLogger.logInfo("Transport layer", "WFQTcp");
    }

    public TransportLayer generate(int identifier){
        return new AdpWFQDCTCPTransportLayer(identifier);
    }
}
