
package com.dharshini.zigbee;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/jenkins")
public class JenkinsStatusController {

    @GetMapping("/status")
    public Map<String, Object> getJenkinsStatus() {

        String url = "http://localhost:8085/job/"
                + "Zigbee-Network-Monitoring-Pipeline/lastBuild/api/json";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> result = new LinkedHashMap<>();

        try {

            Map<String, Object> jenkinsData =
                    restTemplate.getForObject(url, Map.class);

            result.put("jobName",
                    jenkinsData.get("fullDisplayName"));

            result.put("buildNumber",
                    jenkinsData.get("number"));

            result.put("buildStatus",
                    jenkinsData.get("result"));

            result.put("building",
                    jenkinsData.get("building"));

            result.put("duration",
                    jenkinsData.get("duration"));

            result.put("message",
                    "Connected to actual Jenkins server");

        } catch (Exception e) {

            result.put("jobName",
                    "Zigbee-Network-Monitoring-Pipeline");

            result.put("buildNumber", "Unavailable");
            result.put("buildStatus", "UNAVAILABLE");
            result.put("message",
                    "Unable to connect to Jenkins");

        }

        return result;
    }
}
