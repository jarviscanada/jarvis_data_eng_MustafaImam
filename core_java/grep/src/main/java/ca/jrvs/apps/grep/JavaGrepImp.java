package ca.jrvs.apps.grep;

import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;



public class JavaGrepImp implements ca.jrvs.apps.grep.JavaGrep {

    final Logger logger = LoggerFactory.getLogger(ca.jrvs.apps.grep.JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    // Getters and Setters
    public String getRegex() { return regex; }
    public void setRegex(String regex) { this.regex = regex; }
    public String getOutFile() { return outFile; }
    public void setOutFile(String outFile) { this.outFile = outFile; }
    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }


    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        // use default logger config
        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        }
        catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex);
        }
    }

    // all the implements methods

    /**
     * Top level search workflow
     * @throws IOException
     */
    // works!
    @Override
    public void process() throws IOException {
        // Implementation of process method
        List<String> matchedLines = new ArrayList<>();
        for (File file : listFiles(getRootPath())) {
            for (String line : readLines(file)) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        System.out.println(matchedLines);
        writeToFile(matchedLines);
    }

    /**
     * Traverse a given directory and return all files
     * @param rootDir input directory
     * @return files under the rootDir
     * return a list of all files in this directory
     */
    // listFiles works!
    @Override
    public List<File> listFiles(String rootDir) {
//        System.out.println(rootDir + "bruhh");
        List<File> fileList = new ArrayList<>();
        File directory = new File(rootDir);
        // Check if rootDir is a directory
        if (directory.isDirectory()) {

            // Call the recursive function to add files to fileList
            addFilesToList(directory, fileList);
//            System.out.println(fileList + " eberytime a file is added");
        } else {
            throw new IllegalArgumentException("The provided path is not a directory: " + rootDir);
        }
//        System.out.println("the final result of the files " + fileList);
        return fileList;

    };

    private void addFilesToList(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    addFilesToList(file, fileList); // Recursive call
                }
            }
        }

    }


    /**
     * Read a file and return all the lines
     * Explain FileReader, BufferedReader, and character encoding
     * @param inputFile file to be read
     * @return lines
     * @throws IllegalArgumentException if a given inputFile is not a file
     * I have to find a way to go through every line somehow
     */

    // readLine works!
    @Override
    public List<String> readLines(File inputFile) {
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException(inputFile + "is not a file");
        }

        // List to populate
        List<String> lines = new ArrayList<>();
        try ( BufferedReader reader = new BufferedReader(new FileReader(inputFile)) ) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + inputFile, e);
        }
        catch ( IOException  e ) {
            logger.error("An I/O exception occurred", e);
        }
//        System.out.println("returning the lines in a file: " + lines.get(1));
        return lines;
    };

    /**
     * check if a Line contains the regex pattern (passed by user)
     * @param line input string
     * @return true if there is a match
     */
    // containsPattern works!
    @Override
    public boolean containsPattern(String line) {

        try {
            String patternString = this.getRegex();
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(line);
//            if (matcher.find() == true)
//                --> want to use matcher.find() again but that looks for another match, and there may nt be
//                System.out.println("Did you find: " + matcher.matches());
//            }

            return matcher.matches();
        }
        catch (PatternSyntaxException e) {
            logger.error("This pattern is throwing an error");
            return false;


        }
    };

    /**
     * Write Lines to a file
     * Explore: FileOutputStream, OutputStreamWriter, and BufferedWriter
     * @param lines matched Line
     * @throws IOException if write failed
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {

        // get the file
        File outputFile = new File(getOutFile()); // we dont use this.getOutFile since this is only used in the local scope if there is a name conflict

        try (
        // output system setup
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile); // Here we are essentially naming a file
        // to write into. This stream is bytes only, when you write to the file it must be using a stream that writes
        // in bytes OR we wrap this with another stream that converts normal characters into bytes.
        // When you create a new instance of FileOutputStream, you specify a File object or a file path. This sets up a
        // stream through which data can be written to the file specified. The data written through a FileOutputStream
        // is in the form of bytes.

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); //  It takes a byte stream
        // (like FileOutputStream) and converts the characters into bytes according to the specified charset encoding
        // This is necessary because the data in files is ultimately stored as bytes, but you often work with characters
        // in a higher-level programming language like Java.

        BufferedWriter writer = new BufferedWriter(outputStreamWriter); // It holds the characters in a buffer. When the
        // buffer is full, or when the flush() or close() method is called, it writes the data to the underlying output
        // stream (in this case, the OutputStreamWriter). This reduces the number of I/O operations by grouping
        // characters together, which is more efficient than writing each character individually to the disk.

        ) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();

            }
        } catch (IOException e) {
            logger.error("There is an error writing to file: " + outputFile);

        }
    };




}
