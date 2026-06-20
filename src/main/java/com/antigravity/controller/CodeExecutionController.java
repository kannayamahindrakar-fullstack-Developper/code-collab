package com.antigravity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/execute")
public class CodeExecutionController {

    @PostMapping("/java")
    public ResponseEntity<Map<String, Object>> executeJava(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String input = payload.get("input");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "output", "No code provided"));
        }

        try {
            // Remove package declarations as we are compiling in a flat temp directory
            code = code.replaceAll("(?m)^\\s*package\\s+[a-zA-Z0-9_.]+;\\s*$", "");

            // Extract the public class name
            String className = "Main";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("public\\s+class\\s+([A-Za-z0-9_]+)").matcher(code);
            if (m.find()) {
                className = m.group(1);
            }

            // Create a temporary directory
            Path tempDir = Files.createTempDirectory("java-exec-");
            File sourceFile = new File(tempDir.toFile(), className + ".java");
            Files.writeString(sourceFile.toPath(), code);

            // Compile the Java file
            Process compileProcess = new ProcessBuilder("javac", className + ".java")
                    .directory(tempDir.toFile())
                    .redirectErrorStream(true)
                    .start();
            
            boolean compiled = compileProcess.waitFor(5, TimeUnit.SECONDS);
            if (!compiled || compileProcess.exitValue() != 0) {
                String errorOutput = readProcessOutput(compileProcess);
                return ResponseEntity.ok(Map.of("success", false, "output", "Compilation Error:\n" + errorOutput));
            }

            // Run the compiled class
            Process runProcess = new ProcessBuilder("java", className)
                    .directory(tempDir.toFile())
                    .redirectErrorStream(true)
                    .start();
            
            if (input != null && !input.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                    writer.write(input);
                    writer.flush();
                }
            }

            boolean finished = runProcess.waitFor(5, TimeUnit.SECONDS);
            String runOutput = readProcessOutput(runProcess);

            if (!finished) {
                runProcess.destroyForcibly();
                return ResponseEntity.ok(Map.of("success", false, "output", "Execution timed out after 5 seconds.\nOutput so far:\n" + runOutput));
            }

            return ResponseEntity.ok(Map.of("success", runProcess.exitValue() == 0, "output", runOutput));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "output", "Server error during execution: " + e.getMessage()));
        }
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
}
