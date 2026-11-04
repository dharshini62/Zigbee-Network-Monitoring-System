package com.dharshini.zigbee;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ZigbeeController {

    @GetMapping("/network")
    public Map<String, Object> getNetworkData() {

        List<Map<String, Object>> nodes = new ArrayList<>();

        nodes.add(createNode("ZB01", "ONLINE", -53, 100, 91));
        nodes.add(createNode("ZB02", "ONLINE", -61, 100, 92));
        nodes.add(createNode("ZB03", "ONLINE", -88, 100, 55));
        nodes.add(createNode("ZB04", "ONLINE", -53, 100, 94));
        nodes.add(createNode("ZB05", "OFFLINE", -100, 100, 0));

        int received = 332;
        int sent = 500;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalNodes", nodes.size());
        result.put("onlineNodes", 4);
        result.put("failedNodes", 1);
        result.put("reliability", received * 100.0 / sent);
        result.put("nodes", nodes);

        return result;
    }

    private Map<String, Object> createNode(
            String id, String status, int rssi,
            int sent, int received) {

        Map<String, Object> node = new LinkedHashMap<>();

        node.put("nodeId", id);
        node.put("status", status);
        node.put("rssi", rssi);
        node.put("packetsSent", sent);
        node.put("packetsReceived", received);
        node.put("packetDelivery",
                received * 100.0 / sent);

        return node;
    }
}