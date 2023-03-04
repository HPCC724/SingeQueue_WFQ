package ch.ethz.systems.netbench.ext.poissontraffic;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.traffic.TrafficPlanner;
import ch.ethz.systems.netbench.ext.poissontraffic.flowsize.FlowSizeDistribution;
import ch.ethz.systems.netbench.ext.poissontraffic.flowsize.pFabricWebSearchAlbert;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Random;

public class UniformWeightPlanner extends TrafficPlanner {
    //copy pair distribute from poisson planner
    public enum PairDistribution {
        ALL_TO_ALL,
        ALL_TO_ALL_FRACTION,
        ALL_TO_ALL_SERVER_FRACTION,
        PARETO_SKEW_DISTRIBUTION,
        PAIRINGS_FRACTION,
        DUAL_ALL_TO_ALL_FRACTION,
        DUAL_ALL_TO_ALL_SERVER_FRACTION,
        Incast,
        Side_To_Side
    }

    private final FlowSizeDistribution flowSizeDistribution;

    private FlowSizeDistribution poissonDistributionforOutcast;
    private final Random ownIndependentRng;
    private final RandomCollection<Pair<Integer, Integer>> randomPairGenerator;
    private final RandomCollection<Pair<Integer, Integer>> randomPairGenerator2;
    protected final int TotalFlowNumber;
    private final int WeightNumber;
    protected final WeightDistribution wd;
    private final PairDistribution pairDistribution;

    public UniformWeightPlanner(Map<Integer, TransportLayer> idToTransportLayerMap, FlowSizeDistribution flowSizeDistribution,int weight_num ,int FlowNum,PairDistribution pairDistribution,String wdistribution,boolean needRng) {
        super(idToTransportLayerMap);
        this.flowSizeDistribution = flowSizeDistribution;
        this.poissonDistributionforOutcast = null;
        this.TotalFlowNumber = FlowNum;
        this.WeightNumber = weight_num;
        if(needRng){
            this.ownIndependentRng = Simulator.selectIndependentRandom("uniform_arrival"+Integer.toString(FlowNum));
            this.randomPairGenerator = new RandomCollection<>(Simulator.selectIndependentRandom("pair_probabilities_draw"+Integer.toString(FlowNum)));
            this.randomPairGenerator2 = new RandomCollection<>(Simulator.selectIndependentRandom("pair_probabilities_draw2"+Integer.toString(FlowNum)));
            this.poissonDistributionforOutcast = new  pFabricWebSearchAlbert(2);
        }
        else {
            this.ownIndependentRng = Simulator.selectIndependentRandom("uniform_arrival_long"+Integer.toString(FlowNum));
            this.randomPairGenerator = new RandomCollection<>(Simulator.selectIndependentRandom("pair_probabilities_draw_long"+Integer.toString(FlowNum)));
            this.randomPairGenerator2 = new RandomCollection<>(Simulator.selectIndependentRandom("pair_probabilities_draw2_long"+Integer.toString(FlowNum)));
        }
        this.pairDistribution = pairDistribution;
        switch (pairDistribution) {

            case ALL_TO_ALL:
                this.setPairProbabilitiesAllToAll();
                break;
            case Incast:
                this.setPairIncast();
                break;
            case Side_To_Side:
                this.setPairSide();
                break;
            default:
                throw new IllegalArgumentException("Invalid pair distribution given: " + pairDistribution + ".");

        }
        this.wd = new WeightDistribution(wdistribution,WeightNumber,needRng);
        SimulationLogger.logInfo("Flow planner", "Unifrom_Weight_Traffic(flownumber=" + this.TotalFlowNumber + ", pairDistribution=" + pairDistribution + ")");

    }

    //copy form poisson planner
    private void setPairProbabilitiesAllToAll() {

        System.out.print("Generating all-to-all pair probabilities between all nodes with a transport layer...");

        // Uniform probability for every server pair
        double pdfNumBytes = 1.0 / (this.idToTransportLayerMap.size() * (this.idToTransportLayerMap.size() - 1));

        // Add uniform probability for every pair
        for (Integer src : this.idToTransportLayerMap.keySet()){
            for (Integer dst : this.idToTransportLayerMap.keySet()){
                if(!src.equals(dst)) {
                    this.randomPairGenerator.add(pdfNumBytes, new ImmutablePair<>(src, dst));
                }
            }
        }

        System.out.println(" done.");
    }

    private void setPairIncast(){
        System.out.print("Generating incast pair probabilities between all nodes with a transport layer...");
        double pdfNumBytes = 1.0 /  (this.idToTransportLayerMap.size() - 1);
        int numofserver = this.idToTransportLayerMap.keySet().size();
        int dst = 11;//temp

        for(Integer src:this.idToTransportLayerMap.keySet()){
            if(!src.equals(dst)){
                this.randomPairGenerator.add(pdfNumBytes,new ImmutablePair<>(src,dst));
            }
        }
    }

    private void setPairSide(){
        System.out.print("Generating incast pair probabilities between all nodes with a transport layer...");
        double pdfNumBytes = 1.0 /  ((this.idToTransportLayerMap.size()/2)*(this.idToTransportLayerMap.size()/2));
        int side_server_num = this.idToTransportLayerMap.size()/2;
        for(Integer src:this.idToTransportLayerMap.keySet()){
            if(src < side_server_num)
            {
                for(Integer dst:this.idToTransportLayerMap.keySet()){
                    if(dst < side_server_num)
                        continue;
                    else{
                        this.randomPairGenerator.add(pdfNumBytes,new ImmutablePair<>(src,dst));
                    }
                }
            }
            else {
                for(Integer dst:this.idToTransportLayerMap.keySet()){
                    if(dst >= side_server_num)
                        continue;
                    else{
                        this.randomPairGenerator2.add(pdfNumBytes,new ImmutablePair<>(src,dst));
                    }
                }
            }
        }
    }

    @Override
    public void createPlan(long durationNs) {
        switch(this.pairDistribution){
            case Incast:
                this.createPlan_Incast();
//                this.createPlan_Outcast(durationNs);
                break;
            case Side_To_Side:
                this.createPlan_Side();
                break;
            default:
                System.out.println("do not support all to all now");
        }

    }
    public void createPlan_Incast() {
//        double[] weights = this.wd.get_weights_linearly(this.TotalFlowNumber);
        double total_weight = Simulator.getConfiguration().getDoublePropertyWithDefault("total_weight",1.0);
        double[] weights = this.wd.get_weights_uniformlyset(this.TotalFlowNumber,total_weight);
        for(int i=0;i<weights.length;i++){
            double weight_current = weights[i];
            Pair<Integer, Integer> pair = choosePair();
            registerFlow(0, pair.getLeft(), pair.getRight(), flowSizeDistribution.generateFlowSizeByte(),(float) weight_current,0);
        }
    }

    public void createPlan_Outcast(long durationNs){
        double total_weight =1.0;
        double[] weights = this.wd.get_weights_uniformlyset(500,total_weight);

        long lambdaFlowStartsPerSecond = 100;
        long time = 0;
        int x = 0;
        long sum = 0;
        // Generate flow start events until the duration
        // of the experiment has lapsed
        long nextProgressLog = durationNs / 10;
        int weight_index = 0;
        while (time <= durationNs) {
            long interArrivalTime = 1000000;
            sum += interArrivalTime;
            // Register flow
            Pair<Integer, Integer> pair = choosePair();
            double weight_current = weights[weight_index];
            registerFlow(time,  pair.getRight(), pair.getLeft(),this.poissonDistributionforOutcast.generateFlowSizeByte(),(float) weight_current,0);
            weight_index = (weight_index+100)%500;
            // Advance time to next arrival
            time += interArrivalTime;
            x++;
            if (time > nextProgressLog) {
                nextProgressLog += durationNs / 10;
            }
        }
    }

    public void createPlan_Side(){
//        for side 1
        double[] weights = this.wd.get_weights_uniformly(this.TotalFlowNumber/2);
        for(int i=0;i<weights.length;i++){
            double weight_current = weights[i];
            Pair<Integer, Integer> pair = choosePair();
            registerFlow(0, pair.getLeft(), pair.getRight(), flowSizeDistribution.generateFlowSizeByte(),(float) weight_current,0);
        }
        for(int i=0;i<weights.length;i++){
            double weight_current = weights[i];
            Pair<Integer, Integer> pair = choosePair2();
            registerFlow(0, pair.getLeft(), pair.getRight(), flowSizeDistribution.generateFlowSizeByte(),(float) weight_current,0);
        }
    }

    //copy from poisson planner
    protected Pair<Integer, Integer> choosePair() {
        return this.randomPairGenerator.next();
    }
    private Pair<Integer, Integer> choosePair2() {
        return this.randomPairGenerator2.next();
    }


}
