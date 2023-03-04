package ch.ethz.systems.netbench.xpt.WFQ.PCQ;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class PCQQueue implements Queue {

    public final ArrayList<ArrayBlockingQueue> queueList;
    private final Map flowBytesSent;

    private final Map <Integer, Long> FIFOBytesSend;

    private final Map<Integer, Long> FIFOBytesOccupied;//<yuxin> Occupancy of the queue in Bytes
    private long bytesPerRound;// <yuxin> in theory, bytesPerRound = size of a FIFO in Bytes
    private long currentRound;
    private long servingQueue;

    private long taildrop;

    private long rounddrop;

    private long totalpackets;

//    private long queueLength = 15000;
    private ReentrantLock reentrantLock;
    private int ownId;

    private int targetId;

    private boolean islogswitch;

    private boolean head_bpr_limit;

    public PCQQueue(long numQueues, long bytesPerRound, int ownId, int targetId){
        long perQueueCapacity = 8192;// <yuxin> physical size of a FIFO in packets
        this.FIFOBytesOccupied = new HashMap();
        this.FIFOBytesSend = new HashMap();
        this.queueList = new ArrayList((int)numQueues);
        ArrayBlockingQueue fifo;
        for (int i=0; i<(int)numQueues; i++){
            fifo = new ArrayBlockingQueue((int)perQueueCapacity);
            queueList.add(fifo);
            FIFOBytesOccupied.put(i, (long)0);
            FIFOBytesSend.put(i, (long)0);
        }

        this.flowBytesSent = new HashMap();
        this.bytesPerRound = bytesPerRound;
        this.currentRound = 0;
        this.servingQueue = 0;
        this.taildrop = 0;
        this.rounddrop = 0;
        this.totalpackets = 0;
        this.reentrantLock = new ReentrantLock();
        this.ownId = ownId;
        this.targetId = targetId;
        if (ownId == 10 && targetId == 11){
            islogswitch = true;
        }
        else if (ownId == 16 && targetId == 17){
            islogswitch = true;
        }
        else {
            islogswitch = false;
        }
        this.head_bpr_limit = Simulator.getConfiguration().getBooleanPropertyWithDefault("headqueue_bpr_limit", false);
    }

    public int offerPacket(Object o){

        this.reentrantLock.lock();
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        int result = -1;

        try {

            // Compute the packet bid (when will the last byte be transmitted) as the max. between the current round (in bytes) and the last bid of the flow
            float weight = p.getWeight();// <yuxin> flow weight
//            float weight = 1;
            long bid = (long)(this.currentRound * this.bytesPerRound * weight);

            if(flowBytesSent.containsKey(p.getDiffFlowId3())){
                if(bid < (Long)flowBytesSent.get(p.getDiffFlowId3())){
                    bid = (Long)flowBytesSent.get(p.getDiffFlowId3());
                }
            }
            bid = bid + (p.getSizeBit()/8);

            //float weight = p.getWeight();// <yuxin> flow weight
            long packetRound = (long) (bid/(this.bytesPerRound*weight));

            if((packetRound - this.currentRound) > queueList.size() - 1){
                result = -1; // Packet dropped since computed round is too far away
                if (islogswitch) {
                    if (fullDrop(p)) {
                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0,p.getWeight());
                    } else {
                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1,p.getWeight());
                    }
                }
                rounddrop += 1;
            } else {
                int QueueToSend = (int)packetRound%(queueList.size());
                long FIFOSizeEstimate = p.getSizeBit()/8 + FIFOBytesOccupied.get(QueueToSend);
                if (FIFOSizeEstimate > this.bytesPerRound){
                    result = -1;//<yuxin> Packet dropped because of tail drop
                    if (islogswitch) {
                        if (fullDrop(p)) {
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0,p.getWeight());
                        } else {
                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1,p.getWeight());
                        }
                    }
                    taildrop += 1;
                }
                else{
                    result = QueueToSend;
                    if (islogswitch) {
                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8,p.getWeight());
//                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), 0);
                    }
                    flowBytesSent.put(p.getDiffFlowId3(), bid);
                    FIFOBytesOccupied.put(QueueToSend, FIFOSizeEstimate);
                    long FIFOBytesSendEstimate = p.getSizeBit()/8 + FIFOBytesSend.get(QueueToSend);
                    FIFOBytesSend.put(QueueToSend, FIFOBytesSendEstimate);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Probably the bid size has been exceeded, transmit less packets ");
            System.out.println("Exception PCQ offer: " + e.getMessage() + e.getLocalizedMessage());
        } finally {
            this.reentrantLock.unlock();
            return result;
        }
    }

    @Override
    public boolean offer(Object o){
        return  false;
    }

//    @Override
//    public boolean offer(Object o){
//
//        this.reentrantLock.lock();
//        FullExtTcpPacket p = (FullExtTcpPacket) o;
//        boolean result = true;
//
//        try {
//
//            // Compute the packet bid (when will the last byte be transmitted) as the max. between the current round (in bytes) and the last bid of the flow
//            float weight = p.getWeight();// <yuxin> flow weight
////            float weight = 1;
//            long bid = (long)(this.currentRound * this.bytesPerRound * weight);
//
//            if(flowBytesSent.containsKey(p.getDiffFlowId3())){
//                if(bid < (Long)flowBytesSent.get(p.getDiffFlowId3())){
//                    bid = (Long)flowBytesSent.get(p.getDiffFlowId3());
//                }
//            }
//            bid = bid + (p.getSizeBit()/8);
//
//            //float weight = p.getWeight();// <yuxin> flow weight
//            long packetRound = (long) (bid/(this.bytesPerRound*weight));
//
//            if((packetRound - this.currentRound) > queueList.size() - 1){
//                result = false; // Packet dropped since computed round is too far away
//                if (islogswitch) {
//                    if (fullDrop(p)) {
//                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                    } else {
//                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                    }
//                }
//                rounddrop += 1;
//            } else {
//                int QueueToSend = (int)packetRound%(queueList.size());
//                long FIFOSizeEstimate = p.getSizeBit()/8 + FIFOBytesOccupied.get(QueueToSend);
//                if (FIFOSizeEstimate > this.bytesPerRound){
//                    result = false;//<yuxin> Packet dropped because of tail drop
//                    if (islogswitch) {
//                        if (fullDrop(p)) {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0);
//                        } else {
//                            SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1);
//                        }
//                    }
//                    taildrop += 1;
//                }
//                else{
//                    result = queueList.get(QueueToSend).offer(p);
//                    if (islogswitch) {
//                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8);
////                        SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), 0);
//                    }
//                    if (!result) {
//                        System.out.println("!!!maybe perQueueCapacity should be larger");
//                    } else {
//                        flowBytesSent.put(p.getDiffFlowId3(), bid);
//                        FIFOBytesOccupied.put(QueueToSend, FIFOSizeEstimate);
//                        long FIFOBytesSendEstimate = p.getSizeBit()/8 + FIFOBytesSend.get(QueueToSend);
//                        FIFOBytesSend.put(QueueToSend, FIFOBytesSendEstimate);
//                    }
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Probably the bid size has been exceeded, transmit less packets ");
//            System.out.println("Exception PCQ offer: " + e.getMessage() + e.getLocalizedMessage());
//        } finally {
//            this.reentrantLock.unlock();
//            return result;
//        }
//    }

    @Override
    public Packet poll(){
        this.reentrantLock.lock();
        try {
            Packet p = null;
            while (p == null){
                if (this.size() != 0) {
                    if (!queueList.get((int) this.servingQueue).isEmpty()) {
                        p = (Packet) queueList.get((int) this.servingQueue).poll();
                        if (islogswitch) {
                            SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), ((FullExtTcpPacket) p).getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, BufferUtil());
                        }
                        if(!head_bpr_limit) {
                            long FIFOSizeDecreaseEstimate = FIFOBytesOccupied.get((int) this.servingQueue) - p.getSizeBit() / 8;
                            FIFOBytesOccupied.put((int) this.servingQueue, FIFOSizeDecreaseEstimate);//<yuxin> decrease when send a packet
                        }
                        return p;
                    } else {
                        SimulationLogger.logDropRate(ownId, targetId, currentRound, taildrop * 1.0 / totalpackets, rounddrop * 1.0 / totalpackets, (taildrop + rounddrop) * 1.0 / totalpackets);
                        if(head_bpr_limit) {
                            FIFOBytesOccupied.put((int) this.servingQueue, (long) 0);
                        }
                        SimulationLogger.logFIFOsend(ownId, targetId, currentRound, this.FIFOBytesSend.get((int)servingQueue));
                        FIFOBytesSend.put((int)servingQueue, (long)0);
                        this.servingQueue = (this.servingQueue + 1) % this.queueList.size();
                        this.currentRound++;
                    }
                }
            }
            return null;
        }
        finally {
            this.reentrantLock.unlock();
        }
    }

    public boolean fullDrop(FullExtTcpPacket p){
        boolean result = true;
        for (int i=0; i<this.queueList.size(); i++){
            if (p.getSizeBit()/8+FIFOBytesOccupied.get(i) <= this.bytesPerRound){
                result = false;
                break;
            }
        }
        return result;
    }

    public void logEnDeEvent(FullExtTcpPacket p){
        if (islogswitch) {
            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8,p.getWeight());
//            SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), currentRound, Simulator.getCurrentTime(), 1);
            SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), ((FullExtTcpPacket) p).getSequenceNumber(), currentRound, Simulator.getCurrentTime(), p.getSizeBit() / 8, (p.getSizeBit() / 8) * 1.0 / (this.queueList.size() * this.bytesPerRound));
        }
    }

    public double BufferUtil(){
        long occupy = 0;
        for(int i=0; i<this.queueList.size(); i++){
            occupy += FIFOBytesOccupied.get(i);
        }
        double util = occupy*1.0/(this.queueList.size()*this.bytesPerRound);
        return util;
    }

    @Override
    public int size() {
        int size = 0;
        for (int q=0; q<queueList.size(); q++){
            size = size + queueList.get(q).size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        boolean empty = true;
        for (int q=0; q<queueList.size(); q++){
            if(!queueList.get(q).isEmpty()){
                empty = false;
                break;// <yuxin> reduce the number of searches
            }
        }
        return empty;
    }

    public long increaseTotalPackets(){
        totalpackets += 1;
        return totalpackets;
    }

    public void UpdateBi(FullExtTcpPacket p){//<yuxin> update Bi for outputport
        long bid = (long)(this.currentRound * this.bytesPerRound * p.getWeight());

        if(flowBytesSent.containsKey(p.getDiffFlowId3())){
            if(bid < (Long)flowBytesSent.get(p.getDiffFlowId3())){
                bid = (Long)flowBytesSent.get(p.getDiffFlowId3());
            }
        }
        bid = bid + (p.getSizeBit()/8);
        flowBytesSent.put(p.getDiffFlowId3(), bid);
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
