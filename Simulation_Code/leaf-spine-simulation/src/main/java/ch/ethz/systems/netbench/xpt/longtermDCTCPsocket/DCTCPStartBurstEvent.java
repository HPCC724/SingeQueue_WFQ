package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.traffic.FlowStartEvent;

public class DCTCPStartBurstEvent extends FlowStartEvent {
    public DCTCPStartBurstEvent(long timeFromNowNs, TransportLayer transportLayer, int targetId, long flowSizeByte, float weight, int flowset_num) {
        super(timeFromNowNs,transportLayer,targetId,flowSizeByte,weight,flowset_num);
    }
    @Override
    public void trigger(){
        int longtermID = super.flowset_num;
        ((DCTCPLongtermTransportLayer)super.transportLayer).getLongtermSocket(longtermID).Start_Burst();
    }
}
