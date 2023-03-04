package ch.ethz.systems.netbench.xpt.AdaptiveWFQTCP;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.core.run.infrastructure.BaseInitializer;
import ch.ethz.systems.netbench.ext.ecmp.EcmpSwitch;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpSocket;
import ch.ethz.systems.netbench.xpt.ports.FIFO.FIFOOutputPort;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class AdpWFQTCPSocket extends WFQTcpSocket{
    public AdpWFQTCPSocket(TransportLayer transportLayer, long flowId, int sourceId, int destinationId, long flowSizeByte, float weight, int flowset_num) {
        super(transportLayer, flowId, sourceId, destinationId, flowSizeByte,weight,flowset_num);
    }

    @Override
    protected void handleAcknowledgment(FullExtTcpPacket packet){
        super.handleAcknowledgment(packet);
        if(isAllFlowConfirmed()){
            this.UpdateWeightPool(packet);
        }
    }

    public void UpdateWeightPool(FullExtTcpPacket packet) {
        ArrayList<Integer> PathIds = packet.getPathIDs();
        long flowid = packet.getFlowId();
        BaseInitializer initializer = Simulator.getInitializer();
        Map<Integer, NetworkDevice> integerNetworkDeviceMap = initializer.getIdToNetworkDevice();
        int src = this.sourceId;
        int dst = this.destinationId;
        for(int i=0;i< PathIds.size();i++){
            int deviceid = PathIds.get(i);
            int nextid;
            if(deviceid == src || deviceid==dst)
                continue;
            if(i != PathIds.size()-1)
                nextid = PathIds.get(i+1);
            else
                nextid = src;
            NetworkDevice device = integerNetworkDeviceMap.get(deviceid);
            if(EcmpSwitch.class.isInstance(device)){
                OutputPort port = device.getTargetIdToOutputPort().get(nextid);
                if(FIFOOutputPort.class.isInstance(port))
                    continue;
                port.DecreaseTotalWeight(packet.weight,flowid);
            }
        }

        ArrayList<Integer> PathIdsData = (ArrayList<Integer>)Simulator.getActiveFlowPath().get(packet.getFlowId());
        for(int i=0;i< PathIdsData.size();i++){
            int deviceid = PathIdsData.get(i);
            int nextid;
            if(deviceid == src || deviceid==dst)
                continue;
            if(i != PathIdsData.size()-1)
                nextid = PathIdsData.get(i+1);
            else
                nextid = dst;
            NetworkDevice device = integerNetworkDeviceMap.get(deviceid);
            if(EcmpSwitch.class.isInstance(device)){
                OutputPort port = device.getTargetIdToOutputPort().get(nextid);
                if(FIFOOutputPort.class.isInstance(port))
                    continue;
                port.DecreaseTotalWeight(packet.weight,flowid);
            }
        }
        Simulator.RemoveFlow(packet.getFlowId());
    }
}
