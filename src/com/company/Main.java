package com.company;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SearchCyrillicFiles searchCyrillicFiles = new SearchCyrillicFiles(Character.UnicodeBlock.CYRILLIC);
        String pathToFolder = ""; // change for work
        File folder = new File(pathToFolder);

        try {
            List<String> allFiles = searchCyrillicFiles.getListFiles(folder);
            System.out.print("All files: ");
            System.out.println(allFiles);

            List<String> allFilesOnlyExtension = searchCyrillicFiles.getFileOnlyExtension(folder, Arrays.asList("java", "ts"));
            System.out.print("All files only extension: ");
            System.out.println(allFilesOnlyExtension);

            List<String> filesWithCyrillic = searchCyrillicFiles.getCyrillicFilesWithLines(folder);
            System.out.print("Files with cyrillic: ");
            System.out.println(filesWithCyrillic);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
