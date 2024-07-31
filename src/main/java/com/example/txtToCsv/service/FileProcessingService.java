package com.example.txtToCsv.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileProcessingService {

    private final Path uploadDirectory = Paths.get("uploads");

    public FileProcessingService() throws IOException {
        Files.createDirectories(uploadDirectory);
    }

    public String processFile(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String fileName = file.getOriginalFilename().replace(".txt", ".csv");
        Path outputPath = uploadDirectory.resolve(fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath.toFile()));

        String text;
        int n = 0;
        String example = "(\\d+\\.\\d+\\.\\d+\\.\\d+) \\[(.+?) (\\S+)] \"(.+?)\" (\\S+) (\\S+) (\\S+) (\\S+) (\\S+)";
        Pattern pattern = Pattern.compile(example);
        while ((text = br.readLine()) != null) {
            n++;
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String ip = matcher.group(1);
                String dateTime = matcher.group(2);
                String timeZone = matcher.group(3);
                String request = matcher.group(4);
                String status = matcher.group(5);
                String size = matcher.group(6);
                String symbols = matcher.group(7);
                String neverPlus = matcher.group(8);
                String numberX = matcher.group(9);

                String[] values = {ip, dateTime, timeZone, request, status, size, symbols, neverPlus, numberX};
                StringBuilder sb = new StringBuilder();
                for (String value : values) {
                    sb.append("\"").append(value.replace("\"", "\"\"")).append("\",");
                }
                sb.setLength(sb.length() - 1);
                bw.write(sb.toString());
                bw.newLine();
            } else {
                System.out.println("The string number " + n + " does not match the pattern: " + text);
            }
        }
        bw.close();
        br.close();
        return fileName;
    }

    public Path getFilePath(String fileName) {
        return uploadDirectory.resolve(fileName);
    }
}
