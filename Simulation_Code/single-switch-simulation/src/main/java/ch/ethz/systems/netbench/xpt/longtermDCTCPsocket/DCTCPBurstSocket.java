package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.WFQDCTCP.WFQDCTcpSocket;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpSocket;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;

public class DCTCPBurstSocket extends WFQDCTcpSocket {
    private final DCTCPLongtermsocket DCTCPLongtermsocket;

    public DCTCPBurstSocket(TransportLayer transportLayer, long flowId, int sourceId, int destinationId, long flowSizeByte, float weight, int flowset_num, DCTCPLongtermsocket DCTCPLongtermsocket){
        super(transportLayer, flowId, sourceId, destinationId, flowSizeByte,weight,flowset_num);
        this.DCTCPLongtermsocket = DCTCPLongtermsocket;
    }

    public DCTCPBurstSocket(TransportLayer transportLayer, long flowId, int sourceId, int destinationId, long flowSizeByte, float weight, int flowset_num)
    {
        super(transportLayer, flowId, sourceId, destinationId, flowSizeByte,weight,flowset_num);
        this.DCTCPLongtermsocket = null;
    }

    @Override
    protected void handleAcknowledgment(FullExtTcpPacket packet){
        super.handleAcknowledgment(packet);
        if(this.DCTCPLongtermsocket != null){
            if(isAllFlowConfirmed()) {
                this.DCTCPLongtermsocket.Start_Rest();//register next burst event
            }
        }
    }

    @Override
    protected FullExtTcpPacket createPacket(
            long dataSizeByte,
            long sequenceNumber,
            long ackNumber,
            boolean ACK,
            boolean SYN,
            boolean ECE
    )
    {
        if(this.DCTCPLongtermsocket == null) {
            return super.createPacket(dataSizeByte,sequenceNumber,ackNumber,ACK,SYN,ECE);
        }
        else {
            float weight = super.getWeight();
            int flowset_num = super.getFlowset_num();
            return  new FullExtTcpPacket(flowId, dataSizeByte, sourceId, destinationId,
                    100, 80, 80, // TTL, source port, destination port
                    sequenceNumber, ackNumber, // Seq number, Ack number
                    false, false, ECE, // NS, CWR, ECE
                    false, ACK, false, // URG, ACK, PSH
                    false, SYN, false, // RST, SYN, FIN
                    0, // Window size
                    weight,
                    flowset_num,
                    true
            );
        }
    }

}
