package ch.ethz.systems.netbench.xpt.WFQTCP;
import ch.ethz.systems.netbench.core.network.Socket;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.ext.basic.IpPacket;
import ch.ethz.systems.netbench.core.network.Packet;

public class WFQTcpTransportLayer extends TransportLayer {

    public WFQTcpTransportLayer(int indentifier) {
        super(indentifier);
    }

//    @Override
//    public void startFlow(int destination, long flowSizeByte,float weight,int flow_num_set){
//        //long real_flowID = flowIdCounter*10+flow_num_set;
//        Socket socket = createSocket(flowIdCounter, destination, flowSizeByte,weight,flow_num_set);
//        flowIdToSocket.put(flowIdCounter, socket);
//        flowIdCounter++;
//        // Start the socket off as initiator
//        socket.markAsSender();
//        socket.start();
//    }

    @Override
    protected Socket createSocket(long flowId, int destinationId, long flowSizeByte) {
        return new WFQTcpSocket(this, flowId, this.identifier, destinationId, flowSizeByte,0,-1);
    }

    protected Socket createSocket(long flowId, int destinationId, long flowSizeByte,float weight,int flowset_num) {
        return new WFQTcpSocket(this, flowId, this.identifier, destinationId, flowSizeByte,weight,flowset_num);
    }

    @Override
    public void receive(Packet genericPacket) {

        FullExtTcpPacket packet = (FullExtTcpPacket) genericPacket;
        Socket socket = flowIdToSocket.get(packet.getFlowId());

        // If the socket does not yet exist, it is an incoming socket
        if (socket == null && !finishedFlowIds.contains(packet.getFlowId())) {

            // Create the socket instance in the other direction
            float weight = packet.getWeight();
            int flowset_num = packet.getFlowset_num();
            socket = createSocket(packet.getFlowId(), packet.getSourceId(),-1,weight,flowset_num);
            flowIdToReceiver.put(packet.getFlowId(), this);
            flowIdToSocket.put(packet.getFlowId(), socket);
        }

        // Give packet to socket (we do not care about stray packets)
        if (socket != null) {
            socket.handle(packet);
        }

    }
}
