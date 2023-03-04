package ch.ethz.systems.netbench.xpt.WFQ.SQWFQ;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQWFQQueue implements Queue{

    public final ArrayBlockingQueue fifo;

    private final Map flowBytesSent;

    private double R = 1.25; // bytes per ns = 10/8
//    private double R = 5;

    private long currentRound;

    private long queuelength;

    private Lock reentrantLock;

    private int ownId;

    private int targetId;

    private boolean islogswitch = false;

    private long QueueOccupied;

    private  int count;

    private int samplecount;

    private long realround;

    //add by LeafSpine
    private SQWFQOutputPort OwnerPort = null;


    public SQWFQQueue(long queuelength, int targetId, int ownId){
        long perQueueCapacity = 8192;
        this.ownId = ownId;
        this.targetId = targetId;

        this.currentRound = 0;

        this.flowBytesSent = new HashMap();

        this.queuelength = queuelength;
        this.fifo = new ArrayBlockingQueue((int)perQueueCapacity);
        this.reentrantLock = new ReentrantLock();

        this.QueueOccupied = 0;
        this.count = 0;
        this.realround = 0;
        this.samplecount = Simulator.getConfiguration().getIntegerPropertyWithDefault("samplecount",1);

//        if(ownId>=144 && targetId>=144){
//            islogswitch = true;
//        }
    }

    //add by SpineLeaf
    public void setOwnerPort(SQWFQOutputPort ownerPort){
        this.OwnerPort = ownerPort;
    }

    @Override
    public boolean offer(Object o){
        this.reentrantLock.lock();
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        boolean result = true;

        try {
            p.addPath(this.OwnerPort.getOwnId());
            if(p.isSYN() || p.isACK()){
//                if(p.isSYN()){
//                    float weight_origin = p.getWeight();
//                    float weight = (float) this.OwnerPort.getFlowWeight(p.getFlowId(),weight_origin,p.isACK(),p.isSYN());
//                }
                long sbytesEstimate = QueueOccupied + p.getSizeBit()/8;
                if (sbytesEstimate <= queuelength){
                    result = true;
                    QueueOccupied = sbytesEstimate;
//                    if(p.getDiffFlowId3().equals("17309")){
//                        SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),0,currentRound,0,0,0,Simulator.getCurrentTime(),BufferUtil());
//                    }
                    if (islogswitch) {
                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
                    }
                }
                else {
                    result = false;
//                    if(p.getDiffFlowId3().equals("17309")){
//                        if (fullDrop(p)) {
//                            SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),0,currentRound,0,0,1,Simulator.getCurrentTime(),BufferUtil());
//                        } else {
//                            SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),0,currentRound,0,0,2,Simulator.getCurrentTime(),BufferUtil());
//                        }
//                    }
                    if (islogswitch) {
                        if (fullDrop(p)) {
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
                        } else {
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
                        }
                    }
                }
            }
            else {
                String Id = p.getDiffFlowId3();

                float weight = p.getWeight();

//                this.OwnerPort.getFlowWeight(p.getFlowId(),weight,p.isACK(),p.isSYN());

                //add by LeafSpine
//                float weight_origin = p.getWeight();
//                float weight = (float) this.OwnerPort.getFlowWeight(p.getFlowId(),weight_origin,p.isACK(),p.isSYN());
//                float weight = (float) this.OwnerPort.newGetWeight(Id,Simulator.getCurrentTime());
//                if(islogswitch){
//                    SimulationLogger.log2Weight(ownId, targetId,p.getDiffFlowId3(),weight_origin,weight,Simulator.getCurrentTime());
//                }
                long bid = (long) (this.currentRound * this.R * weight);
                if (flowBytesSent.containsKey(Id)) {
                    if (bid < (Long) flowBytesSent.get(Id)) {
                        bid = (Long) flowBytesSent.get(Id);
                    }
                }
                bid = bid + (p.getSizeBit() / 8);

                long finishTime = (long)((bid-this.currentRound*weight*this.R)/(this.R*weight));
//                long packetRound = (long) ((double)bid / (this.R * weight));
//                PriorityHeader header = (PriorityHeader) p;
//                header.setPriority(packetRound);
                if ((finishTime) > this.queuelength / this.R) {
                    result = false; // Packet dropped since computed round is too far away
//                    if(p.getDiffFlowId3().equals("17309")){
//                        if (fullDrop(p)) {
//                            SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),weight,currentRound,bid,finishTime,1,Simulator.getCurrentTime(),BufferUtil());
//                        } else {
//                            SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),weight,currentRound,bid,finishTime,2,Simulator.getCurrentTime(),BufferUtil());                        }
//                    }
                    if (islogswitch) {
                        if (fullDrop(p)) {
                            SimulationLogger.logPromoteWeight(p.getFlowId(), p.getFlowset_num(), Simulator.getCurrentTime(), weight, 0, 0, p.getSequenceNumber(), 1);
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
                        } else {
                            SimulationLogger.logPromoteWeight(p.getFlowId(), p.getFlowset_num(), Simulator.getCurrentTime(), weight, 0, 0, p.getSequenceNumber(), 2);
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
                        }
                    }
                } else {
                    long bytesEstimate = QueueOccupied + p.getSizeBit() / 8;
                    if (bytesEstimate <= queuelength) {
                        result = true;
//                        if(p.getDiffFlowId3().equals("17309")){
//                            SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),weight,currentRound,bid,finishTime,0,Simulator.getCurrentTime(),BufferUtil());
//                        }
                        QueueOccupied = bytesEstimate;
                        flowBytesSent.put(Id, bid);
                        if (islogswitch) {
                            SimulationLogger.logPromoteWeight(p.getFlowId(), p.getFlowset_num(), Simulator.getCurrentTime(), weight, 0, 0, p.getSequenceNumber(), 0);
                            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
                        }
                    } else {
                        result = false;
//                        if(p.getDiffFlowId3().equals("17309")){
//                            if (fullDrop(p)) {
//                                SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),weight,currentRound,bid,finishTime,1,Simulator.getCurrentTime(),BufferUtil());
//                            } else {
//                                SimulationLogger.logFailFlow(p.isSYN(),p.isACK(),ownId,targetId,p.getDiffFlowId3(),p.getSequenceNumber(),p.getWeight(),weight,currentRound,bid,finishTime,2,Simulator.getCurrentTime(),BufferUtil());                        }
//                        }
                        if (islogswitch) {
                            if (fullDrop(p)) {
                                SimulationLogger.logPromoteWeight(p.getFlowId(), p.getFlowset_num(), Simulator.getCurrentTime(), weight, 0, 0, p.getSequenceNumber(), 1);
                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
                            } else {
                                SimulationLogger.logPromoteWeight(p.getFlowId(), p.getFlowset_num(), Simulator.getCurrentTime(), weight, 0, 0, p.getSequenceNumber(), 2);
                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception SQWFQ offer: " + e.getMessage() + e.getLocalizedMessage());
        } finally {
            this.reentrantLock.unlock();
            return result;
        }
    }

//    @Override
//    public boolean offer(Object o){
//        this.reentrantLock.lock();
//        FullExtTcpPacket p = (FullExtTcpPacket) o;
//        boolean result = true;
//
//        try {
//            String Id = p.getDiffFlowId3();
//            this.currentRound = Simulator.getCurrentTime();
//            float weight = p.getWeight();
//            long bid = (long)(this.currentRound * this.R * weight);
//            if(flowBytesSent.containsKey(Id)){
//                if(bid < (Long)flowBytesSent.get(Id)){
//                    bid = (Long)flowBytesSent.get(Id);
//                }
//            }
//            bid = bid + (p.getSizeBit()/8);
//
//            long packetRound = (long) (bid/(this.R*weight));
//            if((packetRound - this.currentRound) > this.queuelength/this.R){
//                result = false; // Packet dropped since computed round is too far away
//                if (islogswitch) {
//                    if (fullDrop(p)) {
//                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                    } else {
//                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                    }
//                }
//            } else {
//                long bytesEstimate = QueueOccupied + p.getSizeBit()/8;
//                if (bytesEstimate <= queuelength){
//                    result = true;
//                    fifo.offer(p);
//                    QueueOccupied = bytesEstimate;
//                    flowBytesSent.put(Id, bid);
//                    if (islogswitch) {
//                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
//                    }
//                }
//                else {
//                    result = false;
//                    if (islogswitch) {
//                        if (fullDrop(p)) {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                        } else {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Exception SQWFQ offer: " + e.getMessage() + e.getLocalizedMessage());
//        } finally {
//            this.reentrantLock.unlock();
//            return result;
//        }
//    }

    @Override
    public Packet poll(){
        this.reentrantLock.lock();
        try {
            Packet packet = (Packet) fifo.poll();
            if (islogswitch) {
                SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) packet).getDiffFlowId3(), ((FullExtTcpPacket) packet).getSequenceNumber(), currentRound, Simulator.getCurrentTime(), packet.getSizeBit() / 8, BufferUtil());
            }
            FullExtTcpPacket p = (FullExtTcpPacket) packet;
            if (!p.isSYN() && !p.isACK()){
                updateRound(packet);
            }
            QueueOccupied -= packet.getSizeBit()/8;
            return packet;
        } catch (Exception e){
            return null;
        } finally {
            this.reentrantLock.unlock();
        }
    }

//    public void updateRound(Packet p){
//        PriorityHeader header = (PriorityHeader) p;
//        long rank = header.getPriority();
//        if(rank>this.currentRound) {
//            this.currentRound = rank;
//        }
//    }

    public void updateRound(Packet p){
        this.realround += (p.getSizeBit()/8*this.queuelength*1.0/this.QueueOccupied)/R;
        if(this.count==0){
            this.currentRound = realround;
        }
        this.count = this.count+1;
        if(this.count==samplecount){
            this.count = 0;
        }
//        this.currentRound += (p.getSizeBit()/8*this.queuelength*1.0/this.QueueOccupied)/R;
//        this.currentRound += (p.getSizeBit()/8*this.queuelength*1.0/this.QueueOccupied)/R/this.OwnerPort.weightTotal;
//        SimulationLogger.log2Weight(ownId, targetId,"p.getDiffFlowId3()",0,(float) this.OwnerPort.weightTotal,Simulator.getCurrentTime());
    }

    public void logEnDeEvent(FullExtTcpPacket p){
        if (islogswitch) {
            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
//            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), 1);
            SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), ((FullExtTcpPacket) p).getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, (p.getSizeBit() / 8) * 1.0 / this.queuelength);
        }
    }

    public boolean fullDrop(FullExtTcpPacket p){
        boolean result = true;
        if (p.getSizeBit()/8+QueueOccupied <= this.queuelength){
            result = false;
        }
        return result;
    }

    public double BufferUtil(){
        double util = QueueOccupied*1.0/this.queuelength;
        return util;
    }


    @Override
    public int size() {
        return fifo.size();
    }

    @Override
    public boolean isEmpty() {
        return fifo.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Object[] toArray(Object[] objects) {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection collection) {
        return false;
    }

    @Override
    public void clear() { }

    @Override
    public boolean retainAll(Collection collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection collection) {
        return false;
    }

    @Override
    public boolean containsAll(Collection collection) {
        return false;
    }

    @Override
    public Object remove() {
        return null;
    }

    @Override
    public Object element() {
        return null;
    }

    @Override
    public Object peek() {
        return null;
    }
}
