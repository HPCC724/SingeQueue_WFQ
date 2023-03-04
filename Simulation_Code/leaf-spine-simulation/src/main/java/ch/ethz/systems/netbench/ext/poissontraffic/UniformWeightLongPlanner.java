package ch.ethz.systems.netbench.ext.poissontraffic;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.ext.poissontraffic.flowsize.FlowSizeDistribution;
import ch.ethz.systems.netbench.xpt.longtermsocket.LongtermTransportLayer;
import ch.ethz.systems.netbench.xpt.longtermsocket.StartLongSocketEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class UniformWeightLongPlanner extends UniformWeightPlanner{

    private int LongflowIDCounter  = 1;
    private final long resttimeNs;
    private final long burst_bytes;
    //input the Flowsize Distribution ,but we donnot use it
    public UniformWeightLongPlanner (Map<Integer, TransportLayer> idToTransportLayerMap, FlowSizeDistribution flowSizeDistribution, int weight_num , int FlowNum, PairDistribution pairDistribution, String wdistribution,long resttimeNs,long burst_bytes){
        super(idToTransportLayerMap,flowSizeDistribution,weight_num,FlowNum,pairDistribution,wdistribution,false);
        this.resttimeNs = resttimeNs;
        this.burst_bytes = burst_bytes;
    }

    @Override
    public void createPlan_Incast(){
        double total_weight_long = Simulator.getConfiguration().getDoublePropertyWithDefault("total_weight_long",1.0);
        double[] weights = super.wd.get_weights_uniformlyset(super.TotalFlowNumber,total_weight_long);
        for(int i=0;i<weights.length;i++){
            double weight_current = weights[i];
            Pair<Integer, Integer> pair = choosePair();
            registerLongFlow(0, pair.getLeft(), pair.getRight(),(float) weight_current,LongflowIDCounter,this.resttimeNs,this.burst_bytes);
            LongflowIDCounter++;
        }
    }
    @Override
    public void createPlan_Outcast(long durationNs ){return;}

    public void registerLongFlow(long timeFromNs, int srcId,int dstId,float weight,int flow_set_num,long resttimeNs,long burst_bytes){
        if (srcId == dstId) {
            throw new RuntimeException("Invalid traffic pair; source (" + srcId + ") and destination (" + dstId + ") are the same.");
        } else if (idToTransportLayerMap.get(srcId) == null) {
            throw new RuntimeException("Source network device " + srcId + " does not have a transport layer.");
        } else if (idToTransportLayerMap.get(dstId) == null) {
            throw new RuntimeException("Destination network device " + dstId + ") does not have a transport layer.");
        } else if (timeFromNs < 0) {
            throw new RuntimeException("Cannot register a flow with a negative timestamp of " + timeFromNs);
        }
        if(LongtermTransportLayer.class.isInstance(idToTransportLayerMap.get(srcId)))
        {
            StartLongSocketEvent longevent = new StartLongSocketEvent(timeFromNs,(LongtermTransportLayer)idToTransportLayerMap.get(srcId),dstId,weight,flow_set_num,resttimeNs,burst_bytes);
            Simulator.registerEvent(longevent);
        }
    }
}
