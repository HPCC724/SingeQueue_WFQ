package ch.ethz.systems.netbench.xpt.longtermsocket;
import java.util.HashMap;
import java.util.Map;
import ch.ethz.systems.netbench.core.network.Socket;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpSocket;
import ch.ethz.systems.netbench.xpt.WFQTCP.WFQTcpTransportLayer;


//in this class: socket means burst socket, only Longsocket means Longterm Socket, and Longterm socket only excedds here
public class LongtermTransportLayer extends WFQTcpTransportLayer {
    private Map <Integer,Longtermsocket> flowIDToLongSocket;

    public LongtermTransportLayer(int identifier){
        super(identifier);
        this.flowIDToLongSocket = new HashMap<Integer,Longtermsocket>();
    }
    @Override
    protected Socket createSocket(long flowId, int destinationId, long flowSizeByte,float weight,int flowset_num) {
        if(this.flowIDToLongSocket.containsKey(flowset_num)){
            Longtermsocket longtermsocket = this.flowIDToLongSocket.get(flowset_num);
            return new BurstSocket(this, flowId, this.identifier, destinationId, flowSizeByte,weight,flowset_num,longtermsocket);
        }
        else {
            return new BurstSocket(this, flowId, this.identifier, destinationId, flowSizeByte,weight,flowset_num);
        }
    }

    protected Longtermsocket createLongSocket(int LongtermID, long resttimeNs, long burst_bytes, double weight, int dstID ){
        return new Longtermsocket(LongtermID,resttimeNs,burst_bytes,weight,this,dstID,this.identifier);
    }

    public void StartLongFlow(int flowset_num,long resttimeNs,long burst_bytes,double weight,int destination)
    {
        Longtermsocket longtermsocket = createLongSocket(flowset_num,resttimeNs,burst_bytes,weight,destination);
        flowIDToLongSocket.put(flowset_num,longtermsocket);
        longtermsocket.Start_Burst();
    }

    public Longtermsocket getLongtermSocket(int ID){
        return this.flowIDToLongSocket.get(ID);
    }
}

