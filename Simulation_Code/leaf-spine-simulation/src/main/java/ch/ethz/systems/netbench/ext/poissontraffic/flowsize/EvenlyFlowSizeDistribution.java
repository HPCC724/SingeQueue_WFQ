package ch.ethz.systems.netbench.ext.poissontraffic.flowsize;

import ch.ethz.systems.netbench.core.log.SimulationLogger;

//add by WFQ,only generate flows with big and same size

public class EvenlyFlowSizeDistribution extends FlowSizeDistribution {
    public EvenlyFlowSizeDistribution(){
        super();
        SimulationLogger.logInfo("Flow planner flow size dist.", "Evenly Distribute of Flow");
    }

    @Override
    public long generateFlowSizeByte() {
        return 1460*6666666;
//        return 1460*10;
    }
}
