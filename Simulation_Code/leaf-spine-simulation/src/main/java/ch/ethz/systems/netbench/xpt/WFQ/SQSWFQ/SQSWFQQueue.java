package ch.ethz.systems.netbench.xpt.WFQ.SQSWFQ;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.WFQ.PCQ.PCQOutputPort;
import ch.ethz.systems.netbench.xpt.WFQ.SQWFQ.SQWFQOutputPort;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQSWFQQueue implements Queue{

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

    private double alpha;//move average factor

    private double rho;//control factor

    private final Map<String, Long> FlowBytesArrived;

    private final Map<String, Long> FlowPacketsArrived;

    private final Map<String, Long> FlowTimeInterval;

    private final Map<String, Long> FlowTimeLastArrive;

    private SQSWFQOutputPort OwnerPort = null;

    public SQSWFQQueue(long queuelength, int targetId, int ownId){
        long perQueueCapacity = 8192;
        this.ownId = ownId;
        this.targetId = targetId;

        this.flowBytesSent = new HashMap();
        this.FlowBytesArrived = new HashMap();
        this.FlowPacketsArrived = new HashMap();
        this.FlowTimeInterval = new HashMap();
        this.FlowTimeLastArrive = new HashMap();
        this.currentRound = 0;

        this.queuelength = queuelength;
        this.fifo = new ArrayBlockingQueue((int)perQueueCapacity);
        this.reentrantLock = new ReentrantLock();

        this.QueueOccupied = 0;
        this.rho = Simulator.getConfiguration().getDoublePropertyWithDefault("esprho",0.1);
        this.alpha = Simulator.getConfiguration().getDoublePropertyWithDefault("alpha_factor", 0.2);
        this.count = 0;
        this.realround = 0;
        this.samplecount = Simulator.getConfiguration().getIntegerPropertyWithDefault("samplecount",1);

//        if(ownId>=144 && targetId>=144){
//            islogswitch = true;
//        }
    }

    public void setOwnerPort(SQSWFQOutputPort ownerPort){
        this.OwnerPort = ownerPort;
    }


    @Override
    public boolean offer(Object o){
        this.reentrantLock.lock();
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        boolean result = true;

        try {
            p.PathIDs.add(this.OwnerPort.getOwnId());
            UpdateST(p);//<yuxin> update s and t
            if(p.isSYN() || p.isACK()){
//            if(!FlowTimeInterval.containsKey(p.getDiffFlowId3()) ||FlowTimeInterval.get(p.getDiffFlowId3())<0){
                long sbytesEstimate = QueueOccupied + p.getSizeBit()/8;
                if (sbytesEstimate <= queuelength){
                    result = true;
                    QueueOccupied = sbytesEstimate;
                    if (islogswitch) {
                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
                    }
                }
                else {
                    result = false;
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
//                 float weight_origin = p.getWeight();
//                 float weight = (float) this.OwnerPort.getFlowWeight(p.getFlowId(),weight_origin,p.isACK(),p.isSYN());
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
                double s = FlowBytesArrived.get(Id)*1.0/FlowPacketsArrived.get(Id);
                double t = FlowTimeInterval.get(Id);
                double speed = s/t;
                double prediction = this.R*weight;
                double AlphaFactor;
//                if(weight != 1 && p.getFlowId() == 6){
//                    System.out.println(this.ownId+","+this.OwnerPort.getTargetId()+","+p.getFlowId()+","+weight_origin+","+weight+","+this.OwnerPort.FlowIds+","+this.OwnerPort.weightTotal);
//                }
                if(speed < prediction){
                    //System.out.println("slow");
                    AlphaFactor = 1;
                }
                else {
                    //System.out.println("fast");
                    AlphaFactor = speed/prediction;
                    AlphaFactor *= rho;
                    if (AlphaFactor < 1){
                        AlphaFactor = 1;
                    }
                }
                double PromoteWeight = weight*AlphaFactor;
                if(PromoteWeight > 1){//<yuxin> can't exceed 1
                    PromoteWeight = 1;
                }
                long finishTime = (long)((bid-this.currentRound*weight*this.R)/(this.R*PromoteWeight));
//                PriorityHeader header = (PriorityHeader) p;
//                header.setPriority(finishTime+this.currentRound);
                if (finishTime > this.queuelength / this.R) {
                    result = false; // Packet dropped since computed round is too far away
                    if (islogswitch) {
                        if (fullDrop(p)) {
                            SimulationLogger.logPromoteWeight(p.getFlowId(),p.getFlowset_num(),Simulator.getCurrentTime(),weight,PromoteWeight,t,p.getSequenceNumber(),1);
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
                        } else {
                            SimulationLogger.logPromoteWeight(p.getFlowId(),p.getFlowset_num(),Simulator.getCurrentTime(),weight,PromoteWeight,t,p.getSequenceNumber(),2);
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
                        }
                    }
                } else {
                    long bytesEstimate = QueueOccupied + p.getSizeBit() / 8;
                    if (bytesEstimate <= queuelength) {
                        result = true;
                        QueueOccupied = bytesEstimate;
                        flowBytesSent.put(Id, bid);
                        if (islogswitch) {
                            SimulationLogger.logPromoteWeight(p.getFlowId(),p.getFlowset_num(),Simulator.getCurrentTime(),weight,PromoteWeight,t,p.getSequenceNumber(),0);
                            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
                        }
                    } else {
                        result = false;
                        if (islogswitch) {
                            if (fullDrop(p)) {
                                SimulationLogger.logPromoteWeight(p.getFlowId(),p.getFlowset_num(),Simulator.getCurrentTime(),weight,PromoteWeight,t,p.getSequenceNumber(),1);
                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
                            } else {
                                SimulationLogger.logPromoteWeight(p.getFlowId(),p.getFlowset_num(),Simulator.getCurrentTime(),weight,PromoteWeight,t,p.getSequenceNumber(),2);
                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception SQSWFQ offer: " + e.getMessage() + e.getLocalizedMessage());
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
//            UpdateST(p);//<yuxin> update s and t
//            if(p.isSYN() || p.isACK()){
//                long sbytesEstimate = QueueOccupied + p.getSizeBit()/8;
//                if (sbytesEstimate <= queuelength){
//                    result = true;
//                    fifo.offer(p);
//                    QueueOccupied = sbytesEstimate;
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
//            else {
//                String Id = p.getDiffFlowId3();
//                this.currentRound = Simulator.getCurrentTime();
//                float weight = p.getWeight();
//                long bid = (long) (this.currentRound * this.R * weight);
//                if (flowBytesSent.containsKey(Id)) {
//                    if (bid < (Long) flowBytesSent.get(Id)) {
//                        bid = (Long) flowBytesSent.get(Id);
//                    }
//                }
//                bid = bid + (p.getSizeBit() / 8);
//                double s = FlowBytesArrived.get(Id)*1.0/FlowPacketsArrived.get(Id);
//                double t = FlowTimeInterval.get(Id);
//                double speed = s/t;
//                double prediction = this.R*weight;
//                double AlphaFactor;
//                if(speed < prediction){
//                    //System.out.println("slow");
//                    AlphaFactor = 1;
//                }
//                else {
//                    //System.out.println("fast");
//                    AlphaFactor = speed/prediction;
//                    AlphaFactor *= rho;
//                    if (AlphaFactor < 1){
//                        AlphaFactor = 1;
//                    }
//                }
//                double PromoteWeight = weight*AlphaFactor;
//                if(PromoteWeight > 1){//<yuxin> can't exceed 1
//                    PromoteWeight = 1;
//                }
//                long packetRound = (long) (bid / (this.R * PromoteWeight));
//                if ((packetRound - this.currentRound) > this.queuelength / this.R) {
//                    result = false; // Packet dropped since computed round is too far away
//                    if (islogswitch) {
//                        if (fullDrop(p)) {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                        } else {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                        }
//                    }
//                } else {
//                    long bytesEstimate = QueueOccupied + p.getSizeBit() / 8;
//                    if (bytesEstimate <= queuelength) {
//                        result = true;
//                        fifo.offer(p);
//                        QueueOccupied = bytesEstimate;
//                        flowBytesSent.put(Id, bid);
//                        if (islogswitch) {
//                            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
//                        }
//                    } else {
//                        result = false;
//                        if (islogswitch) {
//                            if (fullDrop(p)) {
//                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                            } else {
//                                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Exception SQSWFQ offer: " + e.getMessage() + e.getLocalizedMessage());
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


    public void UpdateST(FullExtTcpPacket p){
        String Id = p.getDiffFlowId3();
        if (!p.isSYN() && !p.isACK()) { // update packet size
            if(FlowBytesArrived.containsKey(Id)){
                long Bytes = FlowBytesArrived.get(Id) + p.getSizeBit()/8;
                FlowBytesArrived.put(Id, Bytes);
                long Packets = FlowPacketsArrived.get(Id) + 1;
                FlowPacketsArrived.put(Id, Packets);
            }
            else{
                FlowBytesArrived.put(Id, p.getSizeBit()/8);
                FlowPacketsArrived.put(Id, (long)(1));
            }
        }
        if(!FlowTimeInterval.containsKey(Id)){//update interval
            FlowTimeInterval.put(Id, (long)(-2));
            FlowTimeLastArrive.put(Id, Simulator.getCurrentTime());
        }
        else if((FlowTimeInterval.get(Id) == (long)(-2))){
            FlowTimeInterval.put(Id, (long)(-1));
            FlowTimeLastArrive.put(Id, Simulator.getCurrentTime());
        }
        else if((FlowTimeInterval.get(Id) == (long)(-1))){
            long time =Simulator.getCurrentTime();
            long Interval = time - FlowTimeLastArrive.get(Id);
            FlowTimeInterval.put(Id, Interval);
            FlowTimeLastArrive.put(Id, time);
        }
        else{
            long time =Simulator.getCurrentTime();
            long LastInterval = FlowTimeInterval.get(Id);
            long Interval = (long)((1-alpha)*LastInterval + alpha*(time - FlowTimeLastArrive.get(Id)));
            FlowTimeInterval.put(Id, Interval);
            FlowTimeLastArrive.put(Id, time);
        }
    }

//    public void UpdateST(FullExtTcpPacket p){
//        String Id = p.getDiffFlowId3();
//        if (p.isSYN() == true){ //<yuxin> if is SYN, initialize flowtimeinterval as -2, Bytes and Packets as 0
//            FlowTimeInterval.put(Id, (long)(-1));//<yuxin> tell next packet that you are first
//            FlowTimeLastArrive.put(Id, Simulator.getCurrentTime());
//            FlowBytesArrived.put(Id, (long)(0));
//            FlowPacketsArrived.put(Id, (long)(0));
//        } else if (p.isACK()) {
//            FlowTimeInterval.put(Id, (long)(-1));
//            FlowTimeLastArrive.put(Id, Simulator.getCurrentTime());
//            FlowBytesArrived.put(Id, (long)(0));
//            FlowPacketsArrived.put(Id, (long)(0));
//        } else{//<yuxin> data packets
//            long Bytes = FlowBytesArrived.get(Id) + p.getSizeBit()/8;
//            FlowBytesArrived.put(Id, Bytes);
//            long Packets = FlowPacketsArrived.get(Id) + 1;
//            FlowPacketsArrived.put(Id, Packets);
//            if(FlowTimeInterval.get(Id) == (long)(-1)){//<yuxin> second date packet, compute interval at first time
//                long time =Simulator.getCurrentTime();
//                long Interval = time - FlowTimeLastArrive.get(Id);
//                FlowTimeInterval.put(Id, Interval);
//                FlowTimeLastArrive.put(Id, time);
//            }
//            else{//<yuxin> other data packets, compute interval use EMA
//                long time =Simulator.getCurrentTime();
//                long LastInterval = FlowTimeInterval.get(Id);
//                long Interval = (long)((1-alpha)*LastInterval + alpha*(time - FlowTimeLastArrive.get(Id)));
//                FlowTimeInterval.put(Id, Interval);
//                FlowTimeLastArrive.put(Id, time);
//            }
//        }
//    }


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
