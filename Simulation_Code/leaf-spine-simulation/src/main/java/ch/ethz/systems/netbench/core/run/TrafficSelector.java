package ch.ethz.systems.netbench.core.run;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.config.exceptions.PropertyValueInvalidException;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.ext.poissontraffic.*;
import ch.ethz.systems.netbench.core.run.traffic.TrafficPlanner;
import ch.ethz.systems.netbench.ext.trafficpair.TrafficPairPlanner;
import ch.ethz.systems.netbench.ext.poissontraffic.flowsize.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class TrafficSelector {

    /**
     * Select the traffic planner which creates and registers the start
     * of flows during the run.
     *
     * Selected using following property:
     * traffic=...
     *
     * @param idToTransportLayer    Node identifier to transport layer mapping
     *
     * @return Traffic planner
     */
    static TrafficPlanner selectPlanner(Map<Integer, TransportLayer> idToTransportLayer) {

        switch (Simulator.getConfiguration().getPropertyOrFail("traffic")) {

            case "poisson_arrival":

                FlowSizeDistribution flowSizeDistribution;
                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_flow_size_dist")) {

                    case "pfabric_data_mining_lower_bound": {
                        flowSizeDistribution = new PFabricDataMiningLowerBoundFSD();
                        break;
                    }
                    case "pfabric_data_mining_upper_bound": {
                        flowSizeDistribution = new PFabricDataMiningUpperBoundFSD();
                        break;
                    }

                    case "pfabric_web_search_lower_bound": {
                        flowSizeDistribution = new PFabricWebSearchLowerBoundFSD();
                        break;
                    }

                    case "pfabric_data_mining_albert": {
                        flowSizeDistribution = new pFabricDataMiningAlbert();
                        break;
                    }

                    case "pfabric_web_search_albert": {
                        flowSizeDistribution = new pFabricWebSearchAlbert();
                        break;
                    }

                    case "pfabric_web_search_upper_bound": {
                        flowSizeDistribution = new PFabricWebSearchUpperBoundFSD();
                        break;
                    }
                    case "pareto": {
                        flowSizeDistribution = new ParetoFSD(
                                Simulator.getConfiguration().getDoublePropertyOrFail("traffic_flow_size_dist_pareto_shape"),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_pareto_mean_kilobytes")
                        );
                        break;
                    }

                    case "uniform": {
                        flowSizeDistribution = new UniformFSD(
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_uniform_mean_bytes")
                        );
                        break;
                    }



                    default: {
                        throw new PropertyValueInvalidException(
                                Simulator.getConfiguration(),
                                "traffic_flow_size_dist"
                        );
                    }

                }

                // Attempt to retrieve pair probabilities file
                String pairProbabilitiesFile = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_file", null);

                if (pairProbabilitiesFile != null) {

                    // Create poisson arrival plan from file
	                return new PoissonArrivalPlanner(
	                        idToTransportLayer,
	                        Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
	                        flowSizeDistribution,
                            Simulator.getConfiguration().getPropertyOrFail("traffic_probabilities_file")
	                );

                } else {

                    // If we don't supply the pair probability file we fallback to all-to-all
                    String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator", "all_to_all");

                    switch (generativePairProbabilities) {
                        case "all_to_all":

                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL
                            );

                        case "all_to_all_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_FRACTION
                            );

                        case "all_to_all_server_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_SERVER_FRACTION
                            );

                        case "pairings_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PAIRINGS_FRACTION
                            );
                            
                        case "skew_pareto_distribution":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getDoublePropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PARETO_SKEW_DISTRIBUTION
                            );

                        case "dual_all_to_all_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_FRACTION
                            );

                        case "dual_all_to_all_server_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_SERVER_FRACTION
                            );

                        default:
                            throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");

                    }


                }


            //WFQ
            case "perflow_weight_poisson_arrival":

                FlowSizeDistribution flowSizeDistribution_perflow;
                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_flow_size_dist")) {

                    case "pfabric_data_mining_lower_bound": {
                        flowSizeDistribution = new PFabricDataMiningLowerBoundFSD();
                        break;
                    }
                    case "pfabric_data_mining_upper_bound": {
                        flowSizeDistribution = new PFabricDataMiningUpperBoundFSD();
                        break;
                    }

                    case "pfabric_web_search_lower_bound": {
                        flowSizeDistribution = new PFabricWebSearchLowerBoundFSD();
                        break;
                    }

                    case "pfabric_data_mining_albert": {
                        flowSizeDistribution = new pFabricDataMiningAlbert();
                        break;
                    }

                    case "pfabric_web_search_albert": {
                        flowSizeDistribution = new pFabricWebSearchAlbert();
                        break;
                    }

                    case "pfabric_web_search_upper_bound": {
                        flowSizeDistribution = new PFabricWebSearchUpperBoundFSD();
                        break;
                    }
                    case "pareto": {
                        flowSizeDistribution = new ParetoFSD(
                                Simulator.getConfiguration().getDoublePropertyOrFail("traffic_flow_size_dist_pareto_shape"),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_pareto_mean_kilobytes")
                        );
                        break;
                    }

                    case "uniform": {
                        flowSizeDistribution = new UniformFSD(
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_uniform_mean_bytes")
                        );
                        break;
                    }



                    default: {
                        throw new PropertyValueInvalidException(
                                Simulator.getConfiguration(),
                                "traffic_flow_size_dist"
                        );
                    }

                }

                // Attempt to retrieve pair probabilities file
                String pairProbabilitiesFile_perflow = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_file", null);

                if (pairProbabilitiesFile_perflow != null) {

                    // Create poisson arrival plan from file
                    return null;

                } else {

                    // If we don't supply the pair probability file we fallback to all-to-all
                    String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator", "all_to_all");

                    switch (generativePairProbabilities) {
                        case "all_to_all":

                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL
                            );

                        case "all_to_all_fraction":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_FRACTION
                            );

                        case "all_to_all_server_fraction":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_SERVER_FRACTION
                            );

                        case "pairings_fraction":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PAIRINGS_FRACTION
                            );

                        case "skew_pareto_distribution":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getDoublePropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PARETO_SKEW_DISTRIBUTION
                            );

                        case "dual_all_to_all_fraction":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_FRACTION
                            );

                        case "dual_all_to_all_server_fraction":
                            return new PerFlowWeightPoissonPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_SERVER_FRACTION
                            );

                        default:
                            throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");

                    }


                }

            case "traffic_pair":

                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_pair_type")) {

                    case "all_to_all":
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                TrafficPairPlanner.generateAllToAll(
                                        Simulator.getConfiguration().getGraphDetails().getNumNodes()
                                ),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );

                    case "stride":
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                TrafficPairPlanner.generateStride(
                                        Simulator.getConfiguration().getGraphDetails().getNumNodes(),
                                        Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_pair_stride")
                                ),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );

                    case "stride_server":
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                TrafficPairPlanner.generateStride(
                                        Simulator.getConfiguration().getGraphDetails().getNumServers(),
                                        Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_pair_stride")
                                ),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );

                    case "custom":
                        List<Integer> list = Simulator.getConfiguration().getDirectedPairsListPropertyOrFail("traffic_pairs");
                        List<TrafficPairPlanner.TrafficPair> pairs = new ArrayList<>();
                        for (int i = 0; i < list.size(); i += 2) {
                            pairs.add(new TrafficPairPlanner.TrafficPair(list.get(i), list.get(i + 1)));
                        }
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                pairs,
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );
                }

            case "traffic_arrivals_string":
                return new FromStringArrivalPlanner(idToTransportLayer, Simulator.getConfiguration().getPropertyOrFail("traffic_arrivals_list"));

                //add by WFQ
            case "uniformly_weight":{
                FlowSizeDistribution flowSizeDis;
                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_flow_size_dist")) {
                    case "evenly": {
                        flowSizeDis = new EvenlyFlowSizeDistribution();
                        break;
                    }
                    default:{
                        throw new PropertyValueInvalidException(
                                Simulator.getConfiguration(),
                                "traffic_flow_size_dist");
                    }
                }

                String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator", "all_to_all");
                int flownum = Simulator.getConfiguration().getIntegerPropertyOrFail("flow_num");
                int weightnum = Simulator.getConfiguration().getIntegerPropertyOrFail("weight_num");
                String weightdist = Simulator.getConfiguration().getPropertyOrFail("weight_distribution");
                switch (generativePairProbabilities){
                    case "all_to_all":
                        return new UniformWeightPlanner(idToTransportLayer, flowSizeDis,weightnum, flownum, UniformWeightPlanner.PairDistribution.ALL_TO_ALL,weightdist,true);
                    case "incast":
                        return new UniformWeightPlanner(idToTransportLayer, flowSizeDis, weightnum ,flownum, UniformWeightPlanner.PairDistribution.Incast,weightdist,true);
                    case "side_to_side":
                        return new UniformWeightPlanner(idToTransportLayer, flowSizeDis, weightnum ,flownum, UniformWeightPlanner.PairDistribution.Side_To_Side,weightdist,true);
                        default:
                        throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");
                }
            }
            default:
                throw new PropertyValueInvalidException(
                        Simulator.getConfiguration(),
                        "traffic"
                );

        }


    }

    static TrafficPlanner selectPlannerLong(Map<Integer, TransportLayer> idToTransportLayer)
    {
        switch (Simulator.getConfiguration().getPropertyOrFail("traffic_long")){
            case "uniformly_weight":{


                String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator_long", "incast");
                int flownum = Simulator.getConfiguration().getIntegerPropertyOrFail("flow_num_long");
                int weightnum = Simulator.getConfiguration().getIntegerPropertyOrFail("weight_num_long");
                String weightdist = Simulator.getConfiguration().getPropertyOrFail("weight_distribution_long");
                long resttimeNs = Simulator.getConfiguration().getLongPropertyOrFail("resttimeNs_long");
                long burst_bytes = Simulator.getConfiguration().getLongPropertyOrFail("burst_bytes_long");
                switch (generativePairProbabilities){
                    case "incast":
                        return new UniformWeightLongPlanner(idToTransportLayer, null, weightnum ,flownum, UniformWeightPlanner.PairDistribution.Incast,weightdist,resttimeNs,burst_bytes);
                    default:
                        throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");
                }
            }
            default:
                throw new PropertyValueInvalidException(
                        Simulator.getConfiguration(),
                        "traffic"
                );
        }
    }


    //add by WFQ,only to introduce weight set
    static TrafficPlanner selectPlanner(Map<Integer, TransportLayer> idToTransportLayer,int flowset_num,int multiple) {

        switch (Simulator.getConfiguration().getPropertyOrFail("traffic")) {

            case "poisson_arrival":

                FlowSizeDistribution flowSizeDistribution;
                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_flow_size_dist")) {

                    case "pfabric_data_mining_lower_bound": {
                        flowSizeDistribution = new PFabricDataMiningLowerBoundFSD();
                        break;
                    }
                    case "pfabric_data_mining_upper_bound": {
                        flowSizeDistribution = new PFabricDataMiningUpperBoundFSD();
                        break;
                    }

                    case "pfabric_web_search_lower_bound": {
                        flowSizeDistribution = new PFabricWebSearchLowerBoundFSD(flowset_num);
                        break;
                    }

                    case "pfabric_data_mining_albert": {
                        flowSizeDistribution = new pFabricDataMiningAlbert();
                        break;
                    }

                    case "pfabric_web_search_albert": {
                        flowSizeDistribution = new pFabricWebSearchAlbert(flowset_num);
                        break;
                    }

                    case "pfabric_web_search_upper_bound": {
                        flowSizeDistribution = new PFabricWebSearchUpperBoundFSD();
                        break;
                    }
                    case "pareto": {
                        flowSizeDistribution = new ParetoFSD(
                                Simulator.getConfiguration().getDoublePropertyOrFail("traffic_flow_size_dist_pareto_shape"),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_pareto_mean_kilobytes")
                        );
                        break;
                    }

                    case "uniform": {
                        flowSizeDistribution = new UniformFSD(
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_flow_size_dist_uniform_mean_bytes")
                        );
                        break;
                    }

                    default: {
                        throw new PropertyValueInvalidException(
                                Simulator.getConfiguration(),
                                "traffic_flow_size_dist"
                        );
                    }

                }

                // Attempt to retrieve pair probabilities file
                String pairProbabilitiesFile = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_file", null);

                if (pairProbabilitiesFile != null) {

                    // Create poisson arrival plan from file
                    return new PoissonArrivalPlanner(
                            idToTransportLayer,
                            Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                            flowSizeDistribution,
                            Simulator.getConfiguration().getPropertyOrFail("traffic_probabilities_file")
                    );

                } else {

                    // If we don't supply the pair probability file we fallback to all-to-all
                    String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator", "all_to_all");

                    switch (generativePairProbabilities) {
                        //modified by WFQ
                        case "all_to_all":

                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL,
                                    flowset_num,
                                    multiple
                            );

                        case "all_to_all_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_FRACTION,
                                    flowset_num,
                                    multiple
                            );

                        case "all_to_all_server_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.ALL_TO_ALL_SERVER_FRACTION,
                                    flowset_num,
                                    multiple
                            );

                        case "pairings_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PAIRINGS_FRACTION,
                                    flowset_num,
                                    multiple
                            );

                        case "skew_pareto_distribution":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getDoublePropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.PARETO_SKEW_DISTRIBUTION,
                                    flowset_num,
                                    multiple
                            );

                        case "dual_all_to_all_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_FRACTION,
                                    flowset_num,
                                    multiple
                            );

                        case "dual_all_to_all_server_fraction":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.DUAL_ALL_TO_ALL_SERVER_FRACTION,
                                    flowset_num,
                                    multiple
                            );

                        case "incast":
                            return new PoissonArrivalPlanner(
                                    idToTransportLayer,
                                    Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_lambda_flow_starts_per_s"),
                                    flowSizeDistribution,
                                    PoissonArrivalPlanner.PairDistribution.Incast,
                                    flowset_num,
                                    multiple
                            );
                        default:
                            throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");

                    }


                }

            case "traffic_pair":

                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_pair_type")) {

                    case "all_to_all":
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                TrafficPairPlanner.generateAllToAll(
                                        Simulator.getConfiguration().getGraphDetails().getNumNodes()
                                ),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );

                    case "stride":
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                TrafficPairPlanner.generateStride(
                                        Simulator.getConfiguration().getGraphDetails().getNumNodes(),
                                        Simulator.getConfiguration().getIntegerPropertyOrFail("traffic_pair_stride")
                                ),
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );

                    case "custom":
                        List<Integer> list = Simulator.getConfiguration().getDirectedPairsListPropertyOrFail("traffic_pairs");
                        List<TrafficPairPlanner.TrafficPair> pairs = new ArrayList<>();
                        for (int i = 0; i < list.size(); i += 2) {
                            pairs.add(new TrafficPairPlanner.TrafficPair(list.get(i), list.get(i + 1)));
                        }
                        return new TrafficPairPlanner(
                                idToTransportLayer,
                                pairs,
                                Simulator.getConfiguration().getLongPropertyOrFail("traffic_pair_flow_size_byte")
                        );
                }

            case "traffic_arrivals_string":
                return new FromStringArrivalPlanner(idToTransportLayer, Simulator.getConfiguration().getPropertyOrFail("traffic_arrivals_list"));
            //add by WFQ
            case "uniformly_weight":{
                FlowSizeDistribution flowSizeDis;
                switch (Simulator.getConfiguration().getPropertyOrFail("traffic_flow_size_dist")) {
                    case "evenly": {
                        flowSizeDis = new EvenlyFlowSizeDistribution();
                        break;
                    }
                    default:{
                        throw new PropertyValueInvalidException(
                                Simulator.getConfiguration(),
                                "traffic_flow_size_dist");
                    }
                }

                String generativePairProbabilities = Simulator.getConfiguration().getPropertyWithDefault("traffic_probabilities_generator", "all_to_all");
                int flownum = Simulator.getConfiguration().getIntegerPropertyOrFail("flow_num");
                int weightnum = Simulator.getConfiguration().getIntegerPropertyOrFail("weight_num");      //now flow==weight num
                String weightdist = Simulator.getConfiguration().getPropertyOrFail("weight_distribution");
                switch (generativePairProbabilities){
                    case "all_to_all":
                        return new UniformWeightPlanner(idToTransportLayer, flowSizeDis,weightnum, flownum, UniformWeightPlanner.PairDistribution.ALL_TO_ALL,weightdist,true);
                    case "incast":
                        return new UniformWeightPlanner(idToTransportLayer, flowSizeDis,weightnum, flownum, UniformWeightPlanner.PairDistribution.Incast,weightdist,true);
                    default:
                        throw new PropertyValueInvalidException(Simulator.getConfiguration(), "traffic_probabilities_generator");
                }
            }
            default:
                throw new PropertyValueInvalidException(
                        Simulator.getConfiguration(),
                        "traffic"
                );

        }

    }

}
