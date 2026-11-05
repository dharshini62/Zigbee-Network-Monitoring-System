
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
        System.out.println("Online Nodes: " + monitor.getOnlineNodeCount());
        System.out.println("Failed Nodes: " + monitor.getFailedNodeCount());
        System.out.println("Weak Nodes: " + monitor.getWeakNodeCount());

        System.out.printf("Network Reliability: %.2f%%%n",
                monitor.getNetworkReliability());

        System.out.println("Network Status: " + monitor.getNetworkStatus());

        generateReport(nodes, monitor);
    }

    public static void generateReport(
            List<ZigbeeNode> nodes,
            NetworkMonitor monitor) throws IOException {

        double reliability = monitor.getNetworkReliability();

        String status = monitor.getNetworkStatus();

        String reliabilityColor =
                reliability >= 80 ? "#16A36A" :
                reliability >= 50 ? "#E5A12B" : "#DC5252";

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html lang='en'><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Zigbee Network Monitor</title>");

        html.append("<style>");

        html.append("*{box-sizing:border-box;margin:0;padding:0;}");

        html.append("body{font-family:'Segoe UI',Arial,sans-serif;");
        html.append("background:#F1F5F9;color:#172033;padding:25px;}");

        html.append(".container{max-width:1250px;margin:auto;}");

        html.append(".header{background:linear-gradient(120deg,#101B35,#173F68);");
        html.append("color:white;padding:32px;border-radius:18px;margin-bottom:25px;}");

        html.append(".brand{font-size:13px;color:#56D8C0;");
        html.append("font-weight:bold;letter-spacing:2px;}");

        html.append(".header h1{font-size:30px;margin:12px 0 8px;}");
        html.append(".subtitle{color:#B9C9DC;font-size:14px;}");

        html.append(".header-bottom{display:flex;justify-content:space-between;");
        html.append("align-items:center;gap:15px;flex-wrap:wrap;margin-top:20px;}");

        html.append(".live{background:#164D4A;color:#75E7C8;");
        html.append("padding:8px 15px;border-radius:20px;font-size:12px;font-weight:bold;}");

        html.append(".status{padding:10px 17px;border-radius:25px;");
        html.append("font-weight:bold;font-size:12px;display:inline-block;}");

        html.append(".status-good{background:#DCFCE7;color:#15803D;}");
        html.append(".status-warning{background:#FEF3C7;color:#A16207;}");
        html.append(".status-danger{background:#FEE2E2;color:#B91C1C;}");

        html.append(".section-title{font-size:19px;margin:25px 0 15px;}");
        
        html.append(".cards{display:grid;grid-template-columns:repeat(4,1fr);");
        html.append("gap:17px;}");

        html.append(".card{background:white;padding:23px;border-radius:15px;");
        html.append("box-shadow:0 4px 15px rgba(15,23,42,.05);");
        html.append("border:1px solid #E5EAF1;}");

        html.append(".card-label{font-size:13px;color:#64748B;}");
        html.append(".card-value{font-size:31px;font-weight:750;margin-top:12px;}");
        html.append(".card-note{font-size:12px;color:#94A3B8;margin-top:5px;}");

        html.append(".green{color:#16A36A;}");
        html.append(".red{color:#DC5252;}");
        html.append(".orange{color:#D99119;}");
        html.append(".blue{color:#3478CE;}");

        html.append(".panel{background:white;padding:25px;border-radius:15px;");
        html.append("border:1px solid #E5EAF1;margin-top:22px;}");

        html.append(".reliability-top{display:flex;justify-content:space-between;");
        html.append("align-items:center;flex-wrap:wrap;gap:10px;}");

        html.append(".reliability-value{font-size:28px;font-weight:bold;");
        html.append("color:").append(reliabilityColor).append(";}");

        html.append(".progress{height:13px;background:#E9EEF5;");
        html.append("border-radius:20px;margin-top:18px;overflow:hidden;}");

        html.append(".progress-bar{height:100%;width:");
        html.append(String.format(java.util.Locale.US, "%.2f", reliability));
        html.append("%;background:").append(reliabilityColor);
        html.append(";border-radius:20px;}");

        html.append(".table-wrap{overflow-x:auto;margin-top:18px;}");

        html.append("table{width:100%;border-collapse:collapse;");
        html.append("min-width:650px;}");

        html.append("th{background:#F1F5F9;color:#475569;");
        html.append("text-align:left;padding:15px;font-size:12px;}");

        html.append("td{padding:15px;border-bottom:1px solid #E9EEF5;");
        html.append("font-size:13px;}");

        html.append("tr:hover{background:#F8FAFC;}");

        html.append(".node-id{font-weight:bold;color:#245B96;}");

        html.append(".badge{padding:6px 11px;border-radius:20px;");
        html.append("font-size:11px;font-weight:bold;white-space:nowrap;}");

        html.append(".online{background:#DCFCE7;color:#15803D;}");
        html.append(".offline{background:#FEE2E2;color:#B91C1C;}");
        html.append(".weak{background:#FEF3C7;color:#A16207;}");

        html.append(".alert{margin-top:22px;padding:20px;border-radius:12px;");
        html.append("background:#FFF7ED;border:1px solid #FED7AA;}");
        
        html.append(".alert h3{color:#B45309;font-size:15px;margin-bottom:7px;}");
        html.append(".alert p{color:#7C4A19;font-size:13px;line-height:1.6;}");

        html.append(".footer{text-align:center;color:#94A3B8;");
        html.append("font-size:12px;margin:28px 0 10px;}");

        html.append("@media(max-width:850px){.cards{grid-template-columns:repeat(2,1fr);}}");
        html.append("@media(max-width:500px){body{padding:12px;}");
        html.append(".cards{grid-template-columns:1fr;}");
        html.append(".header h1{font-size:23px;}}");

        html.append("</style></head><body>");

        html.append("<div class='container'>");

        // HEADER
        html.append("<div class='header'>");
        html.append("<div class='brand'>ZIGBEE • WIRELESS SENSOR NETWORK</div>");
        html.append("<h1>Network Monitoring Dashboard</h1>");
        html.append("<div class='subtitle'>Real-time Network Health Analysis & Node Monitoring</div>");

        html.append("<div class='header-bottom'>");
        html.append("<span class='live'>● SIMULATED DATA SOURCE</span>");

        html.append("<span class='status ");
        html.append("NEEDS ATTENTION".equals(status) ? "status-danger" :
                "WARNING".equals(status) ? "status-warning" : "status-good");
        html.append("'>");
        html.append(status);
        html.append("</span>");

        html.append("</div></div>");

        // SUMMARY CARDS
        html.append("<h2 class='section-title'>Network Overview</h2>");
        html.append("<div class='cards'>");

        addCard(html, "TOTAL NODES", String.valueOf(nodes.size()),
                "blue", "Registered network nodes");

        addCard(html, "ONLINE NODES",
                String.valueOf(monitor.getOnlineNodeCount()),
                "green", "Active communication nodes");

        addCard(html, "FAILED NODES",
                String.valueOf(monitor.getFailedNodeCount()),
                "red", "Offline communication nodes");

        addCard(html, "WEAK NODES",
                String.valueOf(monitor.getWeakNodeCount()),
                "orange", "Weak signal / packet delivery");

        html.append("</div>");

        // RELIABILITY PANEL
        html.append("<div class='panel'>");
        html.append("<div class='reliability-top'>");
        html.append("<div><h2>Network Reliability</h2>");
        html.append("<p class='card-note'>Overall packet delivery performance</p></div>");

        html.append("<div class='reliability-value'>");
        html.append(String.format(java.util.Locale.US, "%.2f%%", reliability));
        html.append("</div></div>");

        html.append("<div class='progress'><div class='progress-bar'></div></div>");
        html.append("</div>");

        // NODE TABLE
        html.append("<div class='panel'>");
        html.append("<h2>Node Monitoring Details</h2>");
        html.append("<p class='card-note'>Individual node communication information</p>");

        html.append("<div class='table-wrap'><table>");
        html.append("<thead><tr>");
        html.append("<th>NODE ID</th>");
        html.append("<th>STATUS</th>");
        html.append("<th>RSSI</th>");
        html.append("<th>PACKETS SENT</th>");
        html.append("<th>PACKETS RECEIVED</th>");
        html.append("<th>DELIVERY %</th>");
        html.append("</tr></thead><tbody>");

        for (ZigbeeNode node : nodes) {

            boolean online = node.isOnline();

            boolean weak = online &&
                    (node.getRssi() <= -85 ||
                    node.getPacketDeliveryPercentage() < 50);

            String badgeClass = !online ? "offline" :
                    weak ? "weak" : "online";

            String badgeText = !online ? "OFFLINE" :
                    weak ? "WEAK SIGNAL" : "ONLINE";

            html.append("<tr>");

            html.append("<td class='node-id'>")
                    .append(node.getNodeId()).append("</td>");

            html.append("<td><span class='badge ")
                    .append(badgeClass).append("'>")
                    .append(badgeText).append("</span></td>");

            html.append("<td>").append(node.getRssi())
                    .append(" dBm</td>");

            html.append("<td>").append(node.getPacketsSent())
                    .append("</td>");

            html.append("<td>").append(node.getPacketsReceived())
                    .append("</td>");

            html.append("<td>").append(String.format(
                    java.util.Locale.US, "%.2f",
                    node.getPacketDeliveryPercentage()))
                    .append("%</td>");

            html.append("</tr>");
        }

        html.append("</tbody></table></div></div>");

        // ALERT PANEL
        html.append("<div class='alert'>");

        if (monitor.getFailedNodeCount() > 0) {

            html.append("<h3>⚠ Network Attention Required</h3>");
            html.append("<p>")
                    .append(monitor.getFailedNodeCount())
                    .append(" failed node(s) and ")
                    .append(monitor.getWeakNodeCount())
                    .append(" weak-signal node(s) detected. ");
            html.append("Please inspect the affected nodes and communication quality.</p>");

        } else if (monitor.getWeakNodeCount() > 0) {

            html.append("<h3>⚠ Network Warning</h3>");
            html.append("<p>Weak signal nodes detected. Check the communication quality.</p>");

        } else {

            html.append("<h3>✓ Network Healthy</h3>");
            html.append("<p>All monitored nodes are operating within the configured conditions.</p>");
        }

        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("Zigbee Network Monitoring System | DevOps Project");
        html.append("<br>Generated using Java & Apache Maven");
        html.append("</div>");

        html.append("</div></body></html>");

        Path path = Paths.get("reports", "network-health.html");

        Files.createDirectories(path.getParent());

        Files.write(path, html.toString().getBytes("UTF-8"));

        System.out.println("HTML Report Generated: "
                + path.toAbsolutePath());
    }

    private static void addCard(
            StringBuilder html,
            String label,
            String value,
            String color,
            String note) {

        html.append("<div class='card'>");
        html.append("<div class='card-label'>").append(label).append("</div>");
        html.append("<div class='card-value ").append(color).append("'>");
        html.append(value).append("</div>");
        html.append("<div class='card-note'>").append(note).append("</div>");
        html.append("</div>");
    }
}
