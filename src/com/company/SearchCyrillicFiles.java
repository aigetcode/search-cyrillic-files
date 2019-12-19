package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class SearchCyrillicFiles {

    private Character.UnicodeBlock unicodeBlock;
    private Charset encoding = StandardCharsets.UTF_8;
    private List<String> prohibitedExtension = new ArrayList<>(Arrays.asList("png", "jpg", "jar", "jasper", "jpeg", "eot", "woff", "gif",
            "ico", "ttf", "svn-base", "zip", "map", "swf", "mp3", "ogg", "woff2",
            "gzip", "otf", "ai", "tgz", "vtt", "enc", "pfx", "gz", "html", "json", "class", "exe", "pb"));

    SearchCyrillicFiles(Character.UnicodeBlock unicodeBlock) {
        this.unicodeBlock = unicodeBlock;
    }

    public List<String> getListFiles(File folder) throws IOException {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                getListFiles(fileEntry);
            } else {
                String fileExtension = getFileExtension(fileEntry);
                if (!prohibitedExtension.contains(fileExtension)) {
                    handleFile(files, fileEntry, encoding);
                }
            }
        }
        return files;
    }

    public List<String> getFileOnlyExtension(File folder, List<String> extension) throws IOException {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                getListFiles(fileEntry);
            } else {
                String fileExtension = getFileExtension(fileEntry);
                if (extension.contains(fileExtension)) {
                    handleFile(files, fileEntry, encoding);
                }
            }
        }
        return files;
    }

    public List<String> getCyrillicFilesWithLines(File folder) throws IOException {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                getCyrillicFilesWithLines(fileEntry);
            } else {
                String fileExtension = getFileExtension(fileEntry);
                if (!prohibitedExtension.contains(fileExtension)) {
                    searchCyrillicByLine(files, fileEntry);
                }
            }
        }
        return files;
    }

    private void handleFile(List<String> files, File file, Charset encoding) throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             Reader buffer = new BufferedReader(reader)) {

            handleCharacters(files, file, buffer);
        }
    }

    private void handleCharacters(List<String> files, File file, Reader reader) throws IOException {
        int r;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            String character = String.valueOf(ch);
            if (!character.isEmpty()) {
                Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
                if (block != null
                        && Character.UnicodeBlock.of(ch).equals(unicodeBlock)
                        && files.indexOf(file.getName()) == -1) {
                    files.add(file.getName());
                }
            }
        }
    }

    private void searchCyrillicByLine(List<String> files, File file) throws IOException {
        List<Integer> linesNumbers = null;
        try (LineNumberReader numberRdr = new LineNumberReader(Files.newBufferedReader(Paths.get(file.getPath()), encoding))) {
            linesNumbers = numberRdr.lines()
                    .filter(w -> {
                        char[] charArray = w.toCharArray();
                        for (char character : charArray) {
                            Character.UnicodeBlock block = Character.UnicodeBlock.of(character);
                            if (block != null && Character.UnicodeBlock.of(character).equals(unicodeBlock)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .map(w -> numberRdr.getLineNumber())
                    .collect(toList());
        } catch(Exception e) {
            String fileExtension = getFileExtension(file);
            prohibitedExtension.add(fileExtension);
        }

        if (linesNumbers != null && !linesNumbers.isEmpty()) {
            files.add(file.getName() + " - " + linesNumbers);
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        List<String> extension = new ArrayList<>();

        for (int i = fileName.length() - 1; i >= 0; i--) {
            if (fileName.charAt(i) != '.') {
                extension.add(String.valueOf(fileName.charAt(i)));
            } else {
                Collections.reverse(extension);
                StringBuilder ext = new StringBuilder();
                extension.forEach(ext::append);
                return ext.toString();
            }
        }
        return "";
    }
}
