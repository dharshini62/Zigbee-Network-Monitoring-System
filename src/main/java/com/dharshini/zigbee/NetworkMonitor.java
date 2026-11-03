package com.dharshini.zigbee;

import java.util.ArrayList;
import java.util.List;
public class NetworkMonitor {

    private List<ZigbeeNode> nodes;

    public NetworkMonitor(List<ZigbeeNode> nodes) {

        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one node is required");
        }

        this.nodes = new ArrayList<>(nodes);
    }

    public long getOnlineNodeCount() {
        return nodes.stream()
                .filter(ZigbeeNode::isOnline)
                .count();
    }

    public long getFailedNodeCount() {
        return nodes.stream()
                .filter(node -> !node.isOnline())
                .count();
    }

    public long getWeakNodeCount() {
        return nodes.stream()
                .filter(node -> node.isOnline()
                    && (node.getRssi() <= -85
                    || node.getPacketDeliveryPercentage() < 50))
                .count();
    }

    public double getNetworkReliability() {

        long totalSent = nodes.stream()
                .mapToLong(ZigbeeNode::getPacketsSent)
                .sum();

        long totalReceived = nodes.stream()
                .mapToLong(ZigbeeNode::getPacketsReceived)
                .sum();

        if (totalSent == 0) {
            return 0.0;
        }

        return totalReceived * 100.0 / totalSent;
    }

    public String getNetworkStatus() {

        if (getFailedNodeCount() > 0) {
            return "NEEDS ATTENTION";
        }

        if (getWeakNodeCount() > 0) {
            return "WARNING";
        }

        return "HEALTHY";
    }
}
