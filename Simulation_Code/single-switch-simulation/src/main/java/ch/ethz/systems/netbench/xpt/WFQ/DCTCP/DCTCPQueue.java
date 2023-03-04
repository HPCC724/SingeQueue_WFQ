package ch.ethz.systems.netbench.xpt.WFQ.DCTCP;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DCTCPQueue implements Queue{

    public final ArrayBlockingQueue fifo;

    private long queuelength;

    private Lock reentrantLock;

    private int ownId;

    private int targetId;

    private boolean islogswitch;

    private long QueueOccupied;


    public DCTCPQueue(long queuelength, int targetId, int ownId){
        long perQueueCapacity = 8192;
        this.ownId = ownId;
        this.targetId = targetId;

        this.queuelength = queuelength;
        this.fifo = new ArrayBlockingQueue((int)perQueueCapacity);
        this.reentrantLock = new ReentrantLock();

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

    @Override
    public boolean offer(Object o){
        this.reentrantLock.lock();
        FullExtTcpPacket p = (FullExtTcpPacket) o;
        boolean result = true;

        try {
            long bytesEstimate = QueueOccupied + p.getSizeBit()/8;
            if (bytesEstimate <= queuelength){
                result = true;
                QueueOccupied = bytesEstimate;
                if (islogswitch) {
                    SimulationLogger.logEnqueueEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), 0, Simulator.getCurrentTime(), p.getSizeBit() / 8,p.getWeight());
                }
            }
            else {
                result = false;
                if (islogswitch) {
                    if (fullDrop(p)) {
                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), 0, Simulator.getCurrentTime(), p.getSizeBit() / 8, 0,p.getWeight());
                    } else {
                        SimulationLogger.logDropEvent(ownId, targetId, ((FullExtTcpPacket) p).getDiffFlowId3(), p.getSequenceNumber(), 0, Simulator.getCurrentTime(), p.getSizeBit() / 8, 1,p.getWeight());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception DCTCP offer: " + e.getMessage() + e.getLocalizedMessage());
        } finally {
            this.reentrantLock.unlock();
            return result;
        }
    }

    @Override
    public Packet poll(){
        this.reentrantLock.lock();
        try {
            Packet packet = (Packet) fifo.poll();
            if (islogswitch) {
                SimulationLogger.logDequeueEvent(ownId, targetId, ((FullExtTcpPacket) packet).getDiffFlowId3(), ((FullExtTcpPacket) packet).getSequenceNumber(), 0, Simulator.getCurrentTime(), packet.getSizeBit() / 8, BufferUtil());
            }
            QueueOccupied -= packet.getSizeBit()/8;
            return packet;
        } catch (Exception e){
            return null;
        } finally {
            this.reentrantLock.unlock();
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
