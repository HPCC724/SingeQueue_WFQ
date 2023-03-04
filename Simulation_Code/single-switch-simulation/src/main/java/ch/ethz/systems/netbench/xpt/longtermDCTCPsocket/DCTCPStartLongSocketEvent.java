package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

import ch.ethz.systems.netbench.core.network.Event;

public class DCTCPStartLongSocketEvent extends Event {

    private final DCTCPLongtermTransportLayer DCTCPLongtermTransportLayer;
    int flowset_num;
    long resttimeNs;
    long burst_bytes;
    double weight;
    int targetId;

    public DCTCPStartLongSocketEvent(long timeFromNs, DCTCPLongtermTransportLayer longtermtransportLayerDCTCP, int targetId, float weight, int flowset_num, long resttimeNs, long burst_bytes) {
        super(timeFromNs);
        this.DCTCPLongtermTransportLayer = longtermtransportLayerDCTCP;
        this.flowset_num = flowset_num;
        this.resttimeNs = resttimeNs;
        this.burst_bytes = burst_bytes;
        this.weight = weight;
        this.targetId = targetId;
    }

    @Override
    public void trigger(){
        this.DCTCPLongtermTransportLayer.StartLongFlow(flowset_num,resttimeNs,burst_bytes,weight,targetId);
    }
}
