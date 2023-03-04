package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

import ch.ethz.systems.netbench.core.network.Socket;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpTransportLayer;

import java.util.HashMap;
import java.util.Map;


//in this class: socket means burst socket, only Longsocket means Longterm Socket, and Longterm socket only excedds here
public class DCTCPLongtermTransportLayer extends WFQTcpTransportLayer {
    private Map <Integer, DCTCPLongtermsocket> flowIDToLongSocket;

    public DCTCPLongtermTransportLayer(int identifier){
        super(identifier);
        this.flowIDToLongSocket = new HashMap<Integer, DCTCPLongtermsocket>();
    }
    @Override
    protected Socket createSocket(long flowId, int destinationId, long flowSizeByte,float weight,int flowset_num) {
        if(this.flowIDToLongSocket.containsKey(flowset_num)){
            DCTCPLongtermsocket DCTCPLongtermsocket = this.flowIDToLongSocket.get(flowset_num);
            return new DCTCPBurstSocket(this, flowId, this.identifier, destinationId, flowSizeByte,weight,flowset_num, DCTCPLongtermsocket);
        }
        else {
            return new DCTCPBurstSocket(this, flowId, this.identifier, destinationId, flowSizeByte,weight,flowset_num);
        }
    }

    protected DCTCPLongtermsocket createLongSocket(int LongtermID, long resttimeNs, long burst_bytes, double weight, int dstID ){
        return new DCTCPLongtermsocket(LongtermID,resttimeNs,burst_bytes,weight,this,dstID,this.identifier);
    }

    public void StartLongFlow(int flowset_num,long resttimeNs,long burst_bytes,double weight,int destination)
    {
        DCTCPLongtermsocket DCTCPLongtermsocket = createLongSocket(flowset_num,resttimeNs,burst_bytes,weight,destination);
        flowIDToLongSocket.put(flowset_num, DCTCPLongtermsocket);
        DCTCPLongtermsocket.Start_Burst();
    }

    public DCTCPLongtermsocket getLongtermSocket(int ID){
        return this.flowIDToLongSocket.get(ID);
    }
}

