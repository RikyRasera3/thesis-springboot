package it.thesis.springboot.controller;

import it.thesis.springboot.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static it.thesis.springboot.controller.Path.SERVER_PATH;

@RestController
@RequestMapping(value = SERVER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @GetMapping
    public ResponseEntity<Map<String, String>> getServerStatus() {
        logger.info("Received request for server status");
        var response = new HashMap<String, String>();
        response.put("status", "OK");
        response.put("message", "Server is running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        logger.info("Received ping request");
        var response = new HashMap<String, String>();
        response.put("message", "pong");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-results/{file}")
    public ResponseEntity<Resource> getTestResults(@PathVariable FileType file) {
        String fileType = file.getType();
        logger.info("Getting {} test file content", fileType);
        String filename = fileType + "-scenario.json";
        var filePath = new File("grafana/results/" + filename);

        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            logger.error("File not found: {}", filePath.getAbsolutePath());
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"springboot-" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}