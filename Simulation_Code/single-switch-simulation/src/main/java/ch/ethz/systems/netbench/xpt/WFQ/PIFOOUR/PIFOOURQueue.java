package ch.ethz.systems.netbench.xpt.WFQ.PIFOOUR;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PIFOOURQueue extends PriorityBlockingQueue implements Queue {

    private final long queuelength;
    private Lock reentrantLock;
    private int ownId;
    private int targetId;

    /*STFQ Attributes*/
    private final Map last_finishTime;
    private long round;

    private long QueueOccupied;

    private boolean islogswitch;

    public PIFOOURQueue(long queuelength, int targetId, int ownId){
        this.ownId = ownId;
        this.targetId = targetId;

        this.queuelength = queuelength;
        this.reentrantLock = new ReentrantLock();

        /*STFQ Attributes*/
        this.last_finishTime = new HashMap();
        this.round = 0;
        this.QueueOccupied = 0;

        if (ownId == 10 && targetId == 11){
            islogswitch = true;
        }
        else if (ownId == 16 && targetId == 17){
            islogswitch = true;
        }
        else {
            islogswitch = false;
        }
    }

    /*Rank computation following STFQ as proposed in the PIFO paper*/
    public long computeRank(Packet o){
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        String Id = p.getDiffFlowId3();
        long startTime = this.round;
        if(last_finishTime.containsKey(Id)){
            if((long)last_finishTime.get(Id) > round){
                startTime = (long)last_finishTime.get(Id);
            }
        }

        float weight = ((FullExtTcpPacket)p).getWeight();
//        float weight = 1;
        long finishingTime_update = (long)(startTime + (p.getSizeBit()/(8*weight)));
        last_finishTime.put(Id, finishingTime_update);
        return finishingTime_update;
    }

    public long updatePort(Packet o){
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        String Id = p.getDiffFlowId3();
        long startTime = this.round;
        if(last_finishTime.containsKey(Id)){
            if((long)last_finishTime.get(Id) > round){
                startTime = (long)last_finishTime.get(Id);
            }
        }

        float weight = ((FullExtTcpPacket)p).getWeight();
        long finishingTime_update = (long)(startTime + (p.getSizeBit()/(8*weight)));
        last_finishTime.put(Id, finishingTime_update);
        this.round = finishingTime_update;
        return finishingTime_update;
    }
    /*Round is the virtual start time of the last dequeued packet across all flows*/
    public void updateRound(Packet p){
        PriorityHeader header = (PriorityHeader) p;
        long rank = header.getPriority();
        this.round = rank;
    }

    public Packet offerPacket(Object o, int ownID) {

        this.reentrantLock.lock();

        /*Rank computation*/
        FullExtTcpPacket packet = (FullExtTcpPacket) o;
        long rank = this.computeRank(packet);

        PriorityHeader header = (PriorityHeader) packet;
        header.setPriority((long)rank); // This makes no effect since each switch recomputes the ranks

        boolean success = true;
        try {
            /* As the original PBQ is has no limited size, the packet is always inserted */
            success = super.offer(packet); /* This method will always return true */
            if (islogswitch) {
                SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) packet).getDiffFlowId3(), packet.getSequenceNumber(), round, Simulator.getCurrentTime(), packet.getSizeBit() / 8,packet.getWeight());
            }
            QueueOccupied += packet.getSizeBit()/8;
            boolean dropflag = false;

            /* We control the size by removing the extra packet */
            while (QueueOccupied > queuelength){
                dropflag = true;
                Object[] contentPIFO = this.toArray();
                Arrays.sort(contentPIFO);
                packet = (FullExtTcpPacket) contentPIFO[this.size()-1];
                SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) packet).getDiffFlowId3(), packet.getSequenceNumber(), round, Simulator.getCurrentTime(), packet.getSizeBit() / 8, 0,packet.getWeight());
                String Id = packet.getDiffFlowId3();
                float weight = packet.getWeight();
//                float weight = 1;
                last_finishTime.put(Id, (long)((long)last_finishTime.get(Id) - (packet.getSizeBit()/(8*weight))));
                QueueOccupied -= packet.getSizeBit()/8;
                this.remove(packet);
            }
            if(dropflag){
                return packet;
            }
            return null;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public Object poll() {
        this.reentrantLock.lock();
        try {
            Packet packet = (Packet) super.poll(); // As the super queue is unbounded, this method will always return true
            if (islogswitch) {
                SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) packet).getDiffFlowId3(), ((FullExtTcpPacket) packet).getSequenceNumber(), round, Simulator.getCurrentTime(), packet.getSizeBit() / 8, BufferUtil());
            }

            // Update round number
            this.updateRound(packet);
            QueueOccupied -= packet.getSizeBit()/8;
            return packet;
        } catch (Exception e){
            return null;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    public void logEnDeEvent(FullExtTcpPacket p){
        if (islogswitch) {
            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), round, Simulator.getCurrentTime(), p.getSizeBit() / 8,p.getWeight());
            SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), ((FullExtTcpPacket) p).getSequenceNumber(), round, Simulator.getCurrentTime(), p.getSizeBit() / 8, (p.getSizeBit()/8)*1.0/this.queuelength);
        }
    }

    public double BufferUtil(){
        double util = QueueOccupied*1.0/this.queuelength;
        return util;
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

}

//package ch.ethz.systems.netbench.xpt.WFQ.PIFOOUR;
//
//import ch.ethz.systems.netbench.core.network.Packet;
//import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
//import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;
//
//import java.util.*;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.PriorityBlockingQueue;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//
//public class PIFOOURQueue implements Queue {
//
//    private final PriorityBlockingQueue pifo;
//
//    private final long queuelength;
//    private Lock reentrantLock;
//    private int ownId;
//    private int targetId;
//
//    /*STFQ Attributes*/
//    private final Map last_finishTime;
//    private long round;
//
//    private long QueueOccupied;
//
//    public PIFOOURQueue(long queuelength, int targetId, int ownId){
//        long perQueueCapacity = 320;
//        this.ownId = ownId;
//        this.targetId = targetId;
//
//        this.queuelength = queuelength;
//        this.pifo = new PriorityBlockingQueue ((int)perQueueCapacity);
//        this.reentrantLock = new ReentrantLock();
//
//        /*STFQ Attributes*/
//        this.last_finishTime = new HashMap();
//        this.round = 0;
//        this.QueueOccupied = 0;
//    }
//
//    /*Rank computation following STFQ as proposed in the PIFO paper*/
//    public long computeRank(Packet o){
//        FullExtTcpPacket p = (FullExtTcpPacket) o;
//        String Id = p.getDiffFlowId3();
//        long startTime = this.round;
//        if(last_finishTime.containsKey(Id)){
//            if((long)last_finishTime.get(Id) > round){
//                startTime = (long)last_finishTime.get(Id);
//            }
//        }
//
//        float weight = ((FullExtTcpPacket)p).getWeight();
////        float weight = 1;
//        long finishingTime_update = (long)(startTime + (p.getSizeBit()/(8*weight)));
//        last_finishTime.put(Id, finishingTime_update);
//        return finishingTime_update;
//    }
//
//    public long updatePort(Packet o){
//        FullExtTcpPacket p = (FullExtTcpPacket) o;
//        String Id = p.getDiffFlowId3();
//        long startTime = this.round;
//        if(last_finishTime.containsKey(Id)){
//            if((long)last_finishTime.get(Id) > round){
//                startTime = (long)last_finishTime.get(Id);
//            }
//        }
//
//        float weight = ((FullExtTcpPacket)p).getWeight();
//        long finishingTime_update = (long)(startTime + (p.getSizeBit()/(8*weight)));
//        last_finishTime.put(Id, finishingTime_update);
//        this.round = finishingTime_update;
//        return finishingTime_update;
//    }
//    /*Round is the virtual start time of the last dequeued packet across all flows*/
//    public void updateRound(Packet p){
//        PriorityHeader header = (PriorityHeader) p;
//        long rank = header.getPriority();
//        this.round = rank;
//    }
//
//    @Override
//    public boolean offer(Object o){
//
//        this.reentrantLock.lock();
//
//        /*Rank computation*/
//        FullExtTcpPacket packet = (FullExtTcpPacket) o;
//        long rank = this.computeRank(packet);
//
//        PriorityHeader header = (PriorityHeader) packet;
//        header.setPriority((long)rank); // This makes no effect since each switch recomputes the ranks
//
//        boolean success = true;
//        try {
//            /* As the original PBQ is has no limited size, the packet is always inserted */
//            success = pifo.offer(packet); /* This method will always return true */
//            QueueOccupied += packet.getSizeBit()/8;
//            boolean dropflag = false;
//
//            /* We control the size by removing the extra packet */
//            while (QueueOccupied > queuelength){
//                dropflag = true;
//                Object[] contentPIFO = pifo.toArray();
//                Arrays.sort(contentPIFO);
//                packet = (FullExtTcpPacket) contentPIFO[this.size()-1];
//                String Id = packet.getDiffFlowId3();
//                float weight = packet.getWeight();
////                float weight = 1;
//                last_finishTime.put(Id, (long)((long)last_finishTime.get(Id) - (packet.getSizeBit()/(8*weight))));
//                QueueOccupied -= packet.getSizeBit()/8;
//                pifo.remove(packet);
//            }
//            if(dropflag){
//                return false;
//            }
//            return true;
//        } finally {
//            this.reentrantLock.unlock();
//        }
//    }
//
//    @Override
//    public Object poll() {
//        this.reentrantLock.lock();
//        try {
//            Packet packet = (Packet) pifo.poll(); // As the super queue is unbounded, this method will always return true
//
//            // Update round number
//            this.updateRound(packet);
//            QueueOccupied -= packet.getSizeBit()/8;
//            return packet;
//        } catch (Exception e){
//            return null;
//        } finally {
//            this.reentrantLock.unlock();
//        }
//    }
//
//    @Override
//    public int size() {
//        return pifo.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return pifo.isEmpty();
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return false;
//    }
//
//    @Override
//    public Iterator iterator() {
//        return null;
//    }
//
//    @Override
//    public Object[] toArray() {
//        return new Object[0];
//    }
//
//    @Override
//    public Object[] toArray(Object[] objects) {
//        return new Object[0];
//    }
//
//    @Override
//    public boolean add(Object o) {
//        return false;
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return false;
//    }
//
//    @Override
//    public boolean addAll(Collection collection) {
//        return false;
//    }
//
//    @Override
//    public void clear() { }
//
//    @Override
//    public boolean retainAll(Collection collection) {
//        return false;
//    }
//
//    @Override
//    public boolean removeAll(Collection collection) {
//        return false;
//    }
//
//    @Override
//    public boolean containsAll(Collection collection) {
//        return false;
//    }
//
//    @Override
//    public Object remove() {
//        return null;
//    }
//
//    @Override
//    public Object element() {
//        return null;
//    }
//
//    @Override
//    public Object peek() {
//        return null;
//    }
//}
