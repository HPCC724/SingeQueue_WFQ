package ch.ethz.systems.netbench.xpt.WFQDCTCP;

import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.newreno.newrenodctcp.NewRenoDctcpSocket;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;

public class WFQDCTcpSocket extends NewRenoDctcpSocket {
    private float weight;
    private int flowset_num;
    public WFQDCTcpSocket(TransportLayer transportLayer, long flowId, int sourceId, int destinationId, long flowSizeByte, float weight, int flowset_num) {
        super(transportLayer, flowId, sourceId, destinationId, flowSizeByte);
        this.weight = weight;
        this.flowset_num = flowset_num;
    }

    public int getFlowset_num() {
        return this.flowset_num;
    }

    public float getWeight() {
        return weight;
    }

    //need createPacket
    //@Override
    protected FullExtTcpPacket createPacket(
            long dataSizeByte,
            long sequenceNumber,
            long ackNumber,
            boolean ACK,
            boolean SYN,
            boolean ECE
    )
    {
        float weight = this.weight;
        int flowset_num = this.flowset_num;
        return  new FullExtTcpPacket(flowId, dataSizeByte, sourceId, destinationId,
                100, 80, 80, // TTL, source port, destination port
                sequenceNumber, ackNumber, // Seq number, Ack number
                false, false, ECE, // NS, CWR, ECE
                false, ACK, false, // URG, ACK, PSH
                false, SYN, false, // RST, SYN, FIN
                0, // Window size
                weight,
                flowset_num,
                false
        );
    }


}
