package ch.ethz.systems.netbench.xpt.longtermsocket;

import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.traffic.FlowStartEvent;

public class StartBurstEvent extends FlowStartEvent {
    public StartBurstEvent(long timeFromNowNs, TransportLayer transportLayer, int targetId, long flowSizeByte, float weight, int flowset_num) {
        super(timeFromNowNs,transportLayer,targetId,flowSizeByte,weight,flowset_num);
    }
    @Override
    public void trigger(){
        int longtermID = super.flowset_num;
        ((LongtermTransportLayer)super.transportLayer).getLongtermSocket(longtermID).Start_Burst();
    }
}
