package ch.ethz.systems.netbench.ext.poissontraffic.flowsize;

import ch.ethz.systems.netbench.core.Simulator;

import java.util.Random;

public abstract class FlowSizeDistribution {

    Random independentRng;

    FlowSizeDistribution() {
        this.independentRng = Simulator.selectIndependentRandom("flow_size");
    }

    //modified by WFQ,generate diffrent flows for diffrent weights
    FlowSizeDistribution(int flowset_num) {
        this.independentRng = Simulator.selectIndependentRandom("flow_size"+Integer.toString(flowset_num));
    }

    public abstract long generateFlowSizeByte();
}
