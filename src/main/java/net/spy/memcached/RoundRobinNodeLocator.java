package net.spy.memcached;


import com.google.common.collect.Iterators;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RoundRobinNodeLocator implements NodeLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobinNodeLocator.class);

    private final Object lock = new Object();
    private List<MemcachedNode> nodes;
    private Iterator<MemcachedNode> cyclingIter;
    public RoundRobinNodeLocator(List<MemcachedNode> nodes) {
        this.nodes = nodes;
        this.cyclingIter = Iterators.cycle(nodes);
    }

    @Override
    public MemcachedNode getPrimary(String k) {
        MemcachedNode primaryNode;
        synchronized (lock) {
            primaryNode = cyclingIter.next();
        }
        return primaryNode;
    }

    //todo : fix
    @Override
    public Iterator<MemcachedNode> getSequence(String k) {
        LOGGER.error("Requesting the Sequence/backup Node as primary node is not active");
//        MemcachedNode backUpNode = getPrimary(k);
        List<MemcachedNode> nodeList = new ArrayList<>();
//        nodeList.add(backUpNode);
        return nodeList.iterator();  //intentionally not using cycle iterator
    }

    @Override
    public Collection<MemcachedNode> getAll() {
        return nodes;
    }

    @Override
    public NodeLocator getReadonlyCopy() {   //intentionally not made read-only
        return this;
    }

    @Override
    public void updateLocator(List<MemcachedNode> nodes) {
        this.cyclingIter = Iterators.cycle(nodes);
        this.nodes = nodes;
    }

//    public void addToDisconnectedSocketAddress(SocketAddress socketAddress) {
////        log.error("Memcache Connection established: {}", socketAddress);
////        disconnectedSocketAddress.add(socketAddress);
//        updateCyclicIterator();
//    }
//
//    public void removeFromDisconnectedSocketAddress(SocketAddress socketAddress) {
////        log.error("Memcache Connection lost: {}", socketAddress);
//        disconnectedSocketAddress.remove(socketAddress);
//        updateCyclicIterator();
//    }

//    private void updateCyclicIterator() {
//        List<MemcachedNode> serviceableNodes = nodes.stream()
//                .filter(node -> !disconnectedSocketAddress.contains(node.getSocketAddress()))
//                .collect(Collectors.toList());
//
//        this.cyclingIter = Iterators.cycle(serviceableNodes);
//    }
}