
package com.dharshini.zigbee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

    	int nodeCount = 5;

    	String parameter = System.getenv("NODE_COUNT");

    	if (parameter != null && !parameter.trim().isEmpty()) {
    	    nodeCount = Integer.parseInt(parameter);
    	}

        List<ZigbeeNode> nodes = new ArrayList<>();

        for (int i = 1; i <= nodeCount; i++) {

            boolean online = i % 5 != 0;
            int rssi;
            int received;

            if (!online) {
                rssi = -100;
                received = 0;
            } else if (i % 3 == 0) {
                rssi = -88;
                received = 55;
            } else {
                rssi = -45 - (i % 3) * 8;
                received = 90 + (i % 10);
            }

            nodes.add(new ZigbeeNode(
                    String.format("ZB%02d", i),
                    online,
                    rssi,
                    100,
                    received
            ));
        }

        NetworkMonitor monitor = new NetworkMonitor(nodes);

        System.out.println("ZIGBEE NETWORK MONITORING");
        System.out.println("=========================");
        System.out.println("Total Nodes: " + nodeCount);
        System.out.println("Online Nodes: "
                + monitor.getOnlineNodeCount());
        System.out.println("Failed Nodes: "
                + monitor.getFailedNodeCount());
        System.out.println("Weak Nodes: "
                + monitor.getWeakNodeCount());

        System.out.printf("Network Reliability: %.2f%%%n",
                monitor.getNetworkReliability());

        System.out.println("Network Status: "
                + monitor.getNetworkStatus());

        generateReport(nodes, monitor);

    }

    public static void generateReport(
            List<ZigbeeNode> nodes,
            NetworkMonitor monitor) throws IOException {

        StringBuilder html = new StringBuilder();

        html.append("<html><head><title>Zigbee Report</title>");
        html.append("<style>");
        html.append("body{font-family:Arial;margin:30px;}");
        html.append("table{border-collapse:collapse;width:100%;}");
        html.append("th,td{border:1px solid #ccc;padding:10px;}");
        html.append("th{background:#173b65;color:white;}");
        html.append("</style></head><body>");

        html.append("<h1>Zigbee Network Health Report</h1>");
        html.append("<p>Data Source: Simulated Nodes</p>");

        html.append("<h2>Network Summary</h2>");
        html.append("<p>Online Nodes: ")
                .append(monitor.getOnlineNodeCount()).append("</p>");

        html.append("<p>Failed Nodes: ")
                .append(monitor.getFailedNodeCount()).append("</p>");

        html.append("<p>Weak Nodes: ")
                .append(monitor.getWeakNodeCount()).append("</p>");

        html.append("<p>Network Reliability: ")
                .append(String.format("%.2f%%",
                        monitor.getNetworkReliability()))
                .append("</p>");

        html.append("<table><tr><th>Node ID</th>");
        html.append("<th>Status</th><th>RSSI</th>");
        html.append("<th>Packets Sent</th>");
        html.append("<th>Packets Received</th></tr>");

        for (ZigbeeNode node : nodes) {

            html.append("<tr><td>")
                    .append(node.getNodeId())
                    .append("</td><td>")
                    .append(node.isOnline() ? "ONLINE" : "OFFLINE")
                    .append("</td><td>")
                    .append(node.getRssi())
                    .append(" dBm</td><td>")
                    .append(node.getPacketsSent())
                    .append("</td><td>")
                    .append(node.getPacketsReceived())
                    .append("</td></tr>");
        }

        html.append("</table>");

        html.append("<h2>Network Status: ")
                .append(monitor.getNetworkStatus())
                .append("</h2></body></html>");

        Path path = Paths.get("reports", "network-health.html");
        Files.createDirectories(path.getParent());
        Files.write(path, html.toString().getBytes("UTF-8"));

        System.out.println("HTML Report Generated: "
                + path.toAbsolutePath());
    }
}
