package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpTransportLayerGenerator;

public class DCTCPLongtermTransportLayerGenerator extends WFQTcpTransportLayerGenerator {
    public DCTCPLongtermTransportLayerGenerator(){
        SimulationLogger.logInfo("Transport layer", "LongtermTCP");
    }

    @Override
    public TransportLayer generate(int identifier){
        return new DCTCPLongtermTransportLayer(identifier);
    }
}
