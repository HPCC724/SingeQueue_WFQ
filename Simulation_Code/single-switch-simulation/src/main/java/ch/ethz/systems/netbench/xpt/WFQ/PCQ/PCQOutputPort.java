package ch.ethz.systems.netbench.xpt.WFQ.PCQ;

import ch.ethz.systems.netbench.core.Simulator;
import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.*;
import ch.ethz.systems.netbench.ext.basic.IpHeader;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;




public class PCQOutputPort extends OutputPort {


    public PCQOutputPort(NetworkDevice ownNetworkDevice, NetworkDevice targetNetworkDevice, Link link, long numQueues, long bytesPerRound) {
        super(ownNetworkDevice, targetNetworkDevice, link, new PCQQueue(numQueues, bytesPerRound, ownNetworkDevice.getIdentifier(), targetNetworkDevice.getIdentifier()));
    }

    /**
     * Enqueue the given packet.
     * Drops it if the queue is full (tail drop).
     *
     * @param packet    Packet instance
     */

    @Override
    public void enqueue(Packet packet) {

        PCQQueue q = (PCQQueue)getQueue();
        q.increaseTotalPackets();
        int QueuetoSend = q.offerPacket(packet);
        if (QueuetoSend != -1){
            if (!getIsSending()) {
                getLogger().logLinkUtilized(true);
                q.queueList.get(QueuetoSend).offer(packet);
//                q.logEnDeEvent((FullExtTcpPacket)packet);
                Simulator.registerEvent(new PacketDispatchedEvent(
                        0,
                        this
                ));
                setIsSending();
            }
            else {
                q.queueList.get(QueuetoSend).offer(packet);
            }
        }
    }

//    @Override
//    public void enqueue(Packet packet) {
//
//        PCQQueue q = (PCQQueue)getQueue();
//        q.increaseTotalPackets();
//        // If it is not sending, then the queue is empty at the moment,
//        // so this packet can be immediately send
//        if (!getIsSending()) {
//
//            q.UpdateBi((FullExtTcpPacket)packet);
//            q.logEnDeEvent((FullExtTcpPacket)packet);
//            // Link is now being utilized
//            getLogger().logLinkUtilized(true);
//
//            // Add event when sending is finished
//            Simulator.registerEvent(new PacketDispatchedEvent(
//                    (long)((double)packet.getSizeBit() / getLink().getBandwidthBitPerNs()),
//                    packet,
//                    this
//            ));
//
//            // It is now sending again
//            setIsSending();
//
//        } else { // If it is still sending, the packet is added to the queue, making it non-empty
//
//            boolean enqueued = false;
//            enqueued = getQueue().offer(packet);
//
//
//            if (enqueued){
//                increaseBufferOccupiedBits(packet.getSizeBit());
//                getLogger().logQueueState(getQueue().size(), getBufferOccupiedBits());
//            } else {
//                SimulationLogger.increaseStatisticCounter("PACKETS_DROPPED");
//
//                // Convert to IP packet
//                IpHeader ipHeader = (IpHeader) packet;
//                if (ipHeader.getSourceId() == this.getOwnId()) {
//                    SimulationLogger.increaseStatisticCounter("PACKETS_DROPPED_AT_SOURCE");
//                }
//            }
//        }
//
//    }
}
