package ch.ethz.systems.netbench.xpt.WFQTCP;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.infrastructure.TransportLayerGenerator;

public class WFQTcpTransportLayerGenerator extends TransportLayerGenerator {

    public WFQTcpTransportLayerGenerator() {
        // No parameters needed
        SimulationLogger.logInfo("Transport layer", "WFQTcp");
    }

    public TransportLayer generate(int identifier){
        return new WFQTcpTransportLayer(identifier);
    }
}
