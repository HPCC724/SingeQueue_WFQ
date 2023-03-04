package ch.ethz.systems.netbench.xpt.longtermsocket;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpTransportLayerGenerator;

public class LongtermTransportLayerGenerator extends WFQTcpTransportLayerGenerator {
    public LongtermTransportLayerGenerator(){
        SimulationLogger.logInfo("Transport layer", "LongtermTCP");
    }

    @Override
    public TransportLayer generate(int identifier){
        return new LongtermTransportLayer(identifier);
    }
}
