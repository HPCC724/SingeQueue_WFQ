package ch.ethz.systems.netbench.xpt.longtermsocket;

import ch.ethz.systems.netbench.core.network.Event;
import ch.ethz.systems.netbench.core.network.TransportLayer;

public class StartLongSocketEvent extends Event {

    private final LongtermTransportLayer longtermTransportLayer;
    int flowset_num;
    long resttimeNs;
    long burst_bytes;
    double weight;
    int targetId;

    public StartLongSocketEvent(long timeFromNs, LongtermTransportLayer longtermtransportLayer,int targetId,float weight,int flowset_num,long resttimeNs,long burst_bytes) {
        super(timeFromNs);
        this.longtermTransportLayer = longtermtransportLayer;
        this.flowset_num = flowset_num;
        this.resttimeNs = resttimeNs;
        this.burst_bytes = burst_bytes;
        this.weight = weight;
        this.targetId = targetId;
    }

    @Override
    public void trigger(){
        this.longtermTransportLayer.StartLongFlow(flowset_num,resttimeNs,burst_bytes,weight,targetId);
    }
}
