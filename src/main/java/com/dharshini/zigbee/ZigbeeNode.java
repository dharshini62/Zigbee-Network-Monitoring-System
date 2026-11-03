
package com.dharshini.zigbee;

public class ZigbeeNode {

    private String nodeId;
    private boolean online;
    private int rssi;
    private int packetsSent;
    private int packetsReceived;

    public ZigbeeNode(String nodeId, boolean online,
                      int rssi, int packetsSent,
                      int packetsReceived) {

    	if (nodeId == null || nodeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID required");
        }

        if (packetsSent < 0 || packetsReceived < 0
                || packetsReceived > packetsSent) {
            throw new IllegalArgumentException("Invalid packet counts");
        }

        this.nodeId = nodeId;
        this.online = online;
        this.rssi = rssi;
        this.packetsSent = packetsSent;
        this.packetsReceived = packetsReceived;
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isOnline() {
        return online;
    }

    public int getRssi() {
        return rssi;
    }

    public int getPacketsSent() {
        return packetsSent;
    }

    public int getPacketsReceived() {
        return packetsReceived;
    }

    public double getPacketDeliveryPercentage() {

        if (packetsSent == 0) {
            return 0.0;
        }

        return packetsReceived * 100.0 / packetsSent;
    }
}
