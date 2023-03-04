package ch.ethz.systems.netbench.xpt.longtermDCTCPsocket;

//a longterm socket own a new reno socket (Overrided as BurstSocket)at one time , when a new reno socket finished , it inform Longterm Socket

//a longterm socket is identified by a num (flow set num?)

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.TransportLayer;
import ch.ethz.systems.netbench.xpt.newreno.newrenotcp.NewRenoTcpSocket;

import java.util.Random;

public class DCTCPLongtermsocket {
    private final int LongtermID;
    private NewRenoTcpSocket newRenoTcpSocket;
    private long resttimeNs;        //expectated rest time      //maybe crate a object to return resttimes for every socket
    private long burst_bytes;

    private final double weight;

    private final TransportLayer transportLayer;

    private final int dstID;

    private final int srcID;

    private final Random RestTimeRandom ;

    public DCTCPLongtermsocket(int LongtermID, long resttimeNs, long burst_bytes, double weight, TransportLayer transportLayer, int dstID, int srcID)
    {
        this.LongtermID = LongtermID;
        this.resttimeNs = resttimeNs;
        this.burst_bytes = burst_bytes;
        this.transportLayer = transportLayer;
        this.dstID = dstID;
        this.srcID = srcID;
        this.weight = weight;
        this.RestTimeRandom = Simulator.selectIndependentRandom("RestRandomTime"+this.LongtermID);
    }

    //trigger by burst start event
    public void Start_Burst(){
        this.transportLayer.startFlow(this.dstID,burst_bytes,(float) this.weight,this.LongtermID);
    }
    //trigger by burst_socket over event
    public void Start_Rest(){
        long RealrestTime = Math.abs(RestTimeRandom.nextLong()%(this.resttimeNs))+this.resttimeNs/2;
//        long RealrestTime = RestTimeRandom.nextLong()%(this.resttimeNs)+this.resttimeNs/2;    //get a random rest time, expectation is resttimeNs,if Random of python is uniform
        Simulator.registerEvent(new DCTCPStartBurstEvent(RealrestTime,this.transportLayer,this.dstID,this.burst_bytes,(float)this.weight,this.LongtermID));
    }
}
