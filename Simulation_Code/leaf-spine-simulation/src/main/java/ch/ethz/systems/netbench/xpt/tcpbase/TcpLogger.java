package ch.ethz.systems.netbench.xpt.tcpbase;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.log.LogFailureException;
import ch.ethz.systems.netbench.core.log.LoggerCallback;
import ch.ethz.systems.netbench.core.log.SimulationLogger;

import java.io.BufferedWriter;
import java.io.IOException;

public class TcpLogger implements LoggerCallback {

    private final long flowId;
    private long maxFlowlet;
    private final BufferedWriter congestionWindowWriter;
    private final BufferedWriter packetBurstGapWriter;
    private final BufferedWriter maxFlowletWriter;
    private final boolean logPacketBurstGapEnabled;
    private final boolean logCongestionWindowEnabled;
    private final boolean isReceiver;
    //WFQ_add
    private final BufferedWriter PacketIATWriter;
    private final boolean logPacketIATEnabled;

    private final boolean log_flowset_num_enabled;

    private final BufferedWriter Flowset_Writer;

    private final BufferedWriter InflightWriter;

    private final BufferedWriter TimeoutWriter;
    private final BufferedWriter AckedWriter;

    public TcpLogger(long flowId, boolean isReceiver) {
        this.flowId = flowId;
        this.maxFlowlet = 0;
        this.congestionWindowWriter = SimulationLogger.getExternalWriter("congestion_window.csv.log");
        this.packetBurstGapWriter = SimulationLogger.getExternalWriter("packet_burst_gap.csv.log");
        this.maxFlowletWriter = SimulationLogger.getExternalWriter("max_flowlet.csv.log");
        //WFQ_add_IAT_logger
        this.PacketIATWriter = SimulationLogger.getExternalWriter("flow_IAT.csv.log");
        this.logPacketIATEnabled = Simulator.getConfiguration().getBooleanPropertyWithDefault("enable_log_packet_IAT", true);
        //WFQ flowID_flowset_num
//        this.log_flowset_num_enabled = Simulator.getConfiguration().getPropertyOrFail("transport_layer").equals("wfq_tcp");
        this.log_flowset_num_enabled = true;
        this.Flowset_Writer = SimulationLogger.getExternalWriter("flowset_num_flowID.csv.log");
        this.InflightWriter = SimulationLogger.getExternalWriter("Inflight_Bytes.csv.log");
        this.TimeoutWriter = SimulationLogger.getExternalWriter("Timeout_Events.csv.log");
        this.AckedWriter = SimulationLogger.getExternalWriter("Acked_Events.csv.log");

        this.logPacketBurstGapEnabled = Simulator.getConfiguration().getBooleanPropertyWithDefault("enable_log_packet_burst_gap", true);
        this.logCongestionWindowEnabled = Simulator.getConfiguration().getBooleanPropertyWithDefault("enable_log_congestion_window", true);
        this.isReceiver = isReceiver;
        SimulationLogger.registerCallbackBeforeClose(this);
    }

    /**
     * Log the congestion window of a specific flow at a certain point in time.
     *
     * @param congestionWindow      Current size of congestion window
     */
    public void logCongestionWindow(double congestionWindow) {
        if (logCongestionWindowEnabled) {
            try {
                congestionWindowWriter.write(flowId + "," + congestionWindow + "," + Simulator.getCurrentTime() + "\n");
            } catch (IOException e) {
                throw new LogFailureException(e);
            }
        }
    }

    /**
     * Log the maximum flowlet identifier observed acknowledged.
     *
     * @param flowlet   Flowlet identifier
     */
    public void logMaxFlowlet(long flowlet) {
        assert(flowlet >= maxFlowlet);
        maxFlowlet = flowlet;
    }

    /**
     * Log the packet burst gap (ns).
     *
     * @param gapNs Packet burst gap in nanoseconds
     */
    public void logPacketBurstGap(long gapNs) {
        try {
            if (logPacketBurstGapEnabled) {
                packetBurstGapWriter.write(gapNs + "\n");
            }
        } catch (IOException e) {
            throw new LogFailureException(e);
        }
    }

    /**
     * log the flow IAT WFQ
     *
     */
    public void logPacketIAT(int transportID, long sequenceNumber,long DataSizeByte,int flowset_num,double weight){
        if(logPacketIATEnabled && isReceiver)
        {
            try{
                PacketIATWriter.write(transportID+","+flowId+","+sequenceNumber+","+DataSizeByte+","+Simulator.getCurrentTime()+","+flowset_num+","+weight+"\n");
            } catch (IOException e){
                throw new LogFailureException(e);
            }
        }
    }

    public void logTimeOutEvent(int transportID,int flowset_num,long sequenceNumber,long newRTT){
        try {
            TimeoutWriter.write(flowId+","+flowset_num+","+transportID+","+sequenceNumber+","+newRTT+","+Simulator.getCurrentTime()+"\n");
        }catch (IOException e){
            throw new LogFailureException(e);
        }
    }

    public void logAckedEvent(int transportID,long sequenceNumber,int flowset_num){
        try{
            AckedWriter.write(flowId+","+sequenceNumber+","+flowset_num+","+transportID+","+Simulator.getCurrentTime()+"\n");
        } catch (IOException e) {
            throw new LogFailureException(e);
        }
    }

    //log flowID to flowsetnum
    public void logFlowID_Setnum(long flowId,int flowset_num,float weight,long flowsiezeByte){
        if(this.log_flowset_num_enabled)
        {
            try{
                this.Flowset_Writer.write(flowId+","+flowset_num+","+weight+","+flowsiezeByte+"\n");
            }
            catch (IOException e){
                throw new LogFailureException(e);
            }
        }
    }

    public void logInflightBytes(long inflight_bytes)
    {
        try{
            this.InflightWriter.write(flowId+","+inflight_bytes+","+Simulator.getCurrentTime()+"\n");
        }catch (IOException e){
            throw new LogFailureException(e);
        }
    }



    @Override
    public void callBeforeClose() {
        try {
            if (!isReceiver) {
                maxFlowletWriter.write(flowId + "," + maxFlowlet + "\n");
            }
        } catch (IOException e) {
            throw new LogFailureException(e);
        }
    }

}
