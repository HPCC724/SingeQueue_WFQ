package ch.ethz.systems.netbench.xpt.AdaptiveWFQTCP;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.infrastructure.TransportLayerGenerator;

public class AdpWFQTCPTransportLayerGenerator extends TransportLayerGenerator {
    public AdpWFQTCPTransportLayerGenerator() {
        // No parameters needed
        SimulationLogger.logInfo("Transport layer", "WFQTcp");
    }

    public TransportLayer generate(int identifier){
        return new AdpWFQTCPTransportLayer(identifier);
    }
}
