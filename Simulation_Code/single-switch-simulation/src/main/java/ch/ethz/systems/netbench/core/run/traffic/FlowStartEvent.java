package ch.ethz.systems.netbench.core.run.traffic;

import ch.ethz.systems.netbench.core.network.Event;
import ch.ethz.systems.netbench.core.network.TransportLayer;

public class FlowStartEvent extends Event {
    static float ave_flowsize_byte = 2369492.4f;
    static float median_flowsize_byte = 77380f;
    protected final TransportLayer transportLayer;
    protected final int targetId;
    protected final long flowSizeByte;

    //add by WFQ
    protected float weight = 0;
    protected int flowset_num = -1;

    /**
     * Create event which will happen the given amount of nanoseconds later.
     *
     * @param timeFromNowNs     Time it will take before happening from now in nanoseconds
     * @param transportLayer    Source transport layer that wants to send the flow to the target
     * @param targetId          Target network device identifier
     * @param flowSizeByte      Size of the flow to send in bytes
     */
    public FlowStartEvent(long timeFromNowNs, TransportLayer transportLayer, int targetId, long flowSizeByte) {
        super(timeFromNowNs);
        this.transportLayer = transportLayer;
        this.targetId = targetId;
        this.flowSizeByte = flowSizeByte;
    }

    /**
     * Create event which will happen the given amount of nanoseconds later.
     *
     * @param timeFromNowNs     Time it will take before happening from now in nanoseconds
     * @param transportLayer    Source transport layer that wants to send the flow to the target
     * @param targetId          Target network device identifier
     * @param flowSizeByte      Size of the flow to send in bytes
     * @param weight            weight of this flow
     */
    public FlowStartEvent(long timeFromNowNs, TransportLayer transportLayer, int targetId, long flowSizeByte,float weight,int flowset_num) {
        super(timeFromNowNs);
        this.transportLayer = transportLayer;
        this.targetId = targetId;
        this.flowSizeByte = flowSizeByte;
        this.weight = weight;
        this.flowset_num = flowset_num;
    }

    //modified by WFQ
    @Override
    public void trigger() {
        if(weight != 0){
            transportLayer.startFlow(this.targetId,this.flowSizeByte,this.weight,this.flowset_num);
//            transportLayer.startFlow(this.targetId,this.flowSizeByte,this.weight*this.flowSizeByte/median_flowsize_byte,this.flowset_num);
        }
        else
            transportLayer.startFlow(this.targetId, this.flowSizeByte);
    }


}
