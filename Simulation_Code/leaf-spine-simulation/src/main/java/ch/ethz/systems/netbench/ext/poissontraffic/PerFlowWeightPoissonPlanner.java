package ch.ethz.systems.netbench.ext.poissontraffic;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.ext.poissontraffic.flowsize.FlowSizeDistribution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Random;

public class PerFlowWeightPoissonPlanner extends PoissonArrivalPlanner{
    public PerFlowWeightPoissonPlanner(Map<Integer, TransportLayer> idToTransportLayerMap, double lambdaFlowStartsPerSecond, FlowSizeDistribution flowSizeDistribution, PairDistribution pairDistribution) {
        super(idToTransportLayerMap,lambdaFlowStartsPerSecond,flowSizeDistribution,pairDistribution);
    }

    @Override
    public void createPlan(long durationNs){
        System.out.print("Creating arrival plan...");

        // Statistics tracking
        long time = 0;
        int x = 0;
        long sum = 0;
        Random weight_probability = Simulator.selectIndependentRandom("weight_probability");
        // Generate flow start events until the duration
        // of the experiment has lapsed
        long nextProgressLog = durationNs / 10;
        while (time <= durationNs) {
            long interArrivalTime = (long) (-Math.log(super.ownIndependentRng.nextDouble()) / (super.lambdaFlowStartsPerSecond / 1e9));

            // Uniform arrival
            //
            // long interArrivalTime = (long) (1 / (lambdaFlowStartsPerSecond / 1e9));

            // Add to sum for later statistics
            sum += interArrivalTime;

            // Register flow
            Pair<Integer, Integer> pair = super.choosePair();
            long size = super.flowSizeDistribution.generateFlowSizeByte();
//            registerFlow(time, pair.getLeft(), pair.getRight(), flowSizeDistribution.generateFlowSizeByte(),(float)(weight/(lambdaFlowStartsPerSecond*0.061904f)),flowset_num);
//            registerFlow(time, pair.getLeft(), pair.getRight(),size ,size/1460,0);
//            registerFlow(time, pair.getLeft(), pair.getRight(),size ,1,0);

            registerFlow(time, pair.getLeft(), pair.getRight(),size ,0.02f,0);

            //0.189559392 = median_flowsize_byte/1250000
            // Advance time to next arrival
            time += interArrivalTime;
            x++;

            if (time > nextProgressLog) {
                System.out.print(" " + (100 * time / durationNs) + "%...");
                nextProgressLog += durationNs / 10;
            }

        }

        System.out.println(" done.");

        // Log plan created
        System.out.println("Poisson Arrival plan created.");
        System.out.println("Number of flows created: " + x + ".");
        System.out.println("Mean inter-arrival time: " + (sum / x) + " (expectation: "
                + (1 / (lambdaFlowStartsPerSecond / 1e9)) + ")");
        SimulationLogger.logInfo("Flow planner number flows", String.valueOf(x));
        SimulationLogger.logInfo("Flow planner mean inter-arrival time", String.valueOf((sum / x)));
        SimulationLogger.logInfo("Flow planner expected inter-arrival time", String.valueOf((1 / (lambdaFlowStartsPerSecond / 1e9))));
        SimulationLogger.logInfo("Flow planner poisson rate lambda (flow starts/s)", String.valueOf(lambdaFlowStartsPerSecond));

    }
}

