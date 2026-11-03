
package com.dharshini.zigbee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class NetworkMonitorTest {

    @Test
    void testPacketDelivery() {

        ZigbeeNode node =
                new ZigbeeNode("ZB01", true, -45, 100, 98);

        assertEquals(98.0,
                node.getPacketDeliveryPercentage(), 0.001);
    }

    @Test
    void testFailedNodeDetection() {

        ZigbeeNode node1 =
                new ZigbeeNode("ZB01", true, -45, 100, 98);

        ZigbeeNode node2 =
                new ZigbeeNode("ZB02", false, -100, 100, 0);

        NetworkMonitor monitor =
                new NetworkMonitor(Arrays.asList(node1, node2));

        assertEquals(1, monitor.getFailedNodeCount());
    }

    @Test
    void testOnlineNodeCount() {

        ZigbeeNode node1 =
                new ZigbeeNode("ZB01", true, -45, 100, 98);

        ZigbeeNode node2 =
                new ZigbeeNode("ZB02", false, -100, 100, 0);

        NetworkMonitor monitor =
                new NetworkMonitor(Arrays.asList(node1, node2));

        assertEquals(1, monitor.getOnlineNodeCount());
    }

    @Test
    void testNetworkReliability() {

        ZigbeeNode node1 =
                new ZigbeeNode("ZB01", true, -45, 100, 90);

        ZigbeeNode node2 =
                new ZigbeeNode("ZB02", true, -60, 100, 80);

        NetworkMonitor monitor =
                new NetworkMonitor(Arrays.asList(node1, node2));

        assertEquals(85.0,
                monitor.getNetworkReliability(), 0.001);
    }

    @Test
    void testWeakNodeDetection() {

        ZigbeeNode node =
                new ZigbeeNode("ZB01", true, -90, 100, 80);

        NetworkMonitor monitor =
                new NetworkMonitor(Arrays.asList(node));

        assertEquals(1, monitor.getWeakNodeCount());
    }

    @Test
    void testInvalidPacketCounts() {

        assertThrows(IllegalArgumentException.class,
                () -> new ZigbeeNode(
                        "ZB01", true, -45, 10, 15));
    }
}
