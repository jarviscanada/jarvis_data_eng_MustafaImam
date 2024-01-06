package ca.jrvs.apps.grep;


import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path; // this a path interface
import java.nio.file.Paths; // this is a utility class that contains methods to convert strings into Path objects
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends ca.jrvs.apps.grep.JavaGrepImp {

    private static final Logger logger = LoggerFactory.getLogger(JavaGrepLambdaImp.class);

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrepLambda regex rootPath outFile");
        }
        // use default logger config
        BasicConfigurator.configure();

        JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
        javaGrepLambdaImp.setRegex(args[0]);
        javaGrepLambdaImp.setRootPath(args[1]);
        javaGrepLambdaImp.setOutFile(args[2]);

        try {
            javaGrepLambdaImp.process();
        }
        catch (Exception ex) {
            logger.error("Error: Unable to process", ex);
        }
    }

    /**
     * Top level search workflow
     *
     * @throws IOException
     */
    @Override
    
    public void process() throws IOException {
        List<String> matchedLines = new ArrayList<>();
//        System.out.println("The above works!");
        Stream<File> filesStream = listFiles(getRootPath()).stream();
//        System.out.println("The above works!");
        filesStream.forEach(file -> readLines(file).stream().filter(this::containsPattern).forEach(matchedLines::add) );

        writeToFile(matchedLines);
        
    }

    /**
     * Traverse a given directory and return all files
     *
     * @param rootDir input directory
     * @return files under the rootDir
     * return a list of all files in this directory
     */
    @Override
    public List<File> listFiles(String rootDir) {
        List<File> fileList = new ArrayList<>(); // this is what we will return
        Path root = Paths.get(rootDir); // now we have the rootDir as a path object
        try (Stream<Path> stream = Files.walk(root)) {
            stream.forEach(item -> {
                File file = item.toFile();
                if (file.isFile()) {
                    fileList.add(file);
                }
            } );
        }
        catch ( IOException e) {
            logger.error("IOException!!!");

        }
//        System.out.println("This is the filelist from listFiles: " + fileList);
        return fileList;
    }



    /**
     * Read a file and return all the lines
     * Explain FileReader, BufferedReader, and character encoding
     *
     * @param inputFile file to be read
     * @return lines
     * @throws IllegalArgumentException if a given inputFile is not a file
     *                                  I have to find a way to go through every line somehow
     */
    @Override
    public List<String> readLines(File inputFile) {
        // i need to read the file line by line and then put each line into a list
        List<String> allLines = new ArrayList<>();
        // Files lines: returns a stream of all the lines, with each line as a string in the stream
        try (Stream<String> lines = Files.lines(inputFile.toPath())) {
//            System.out.println("stream lines" + lines);
            lines.forEach(line -> allLines.add(line));
        }
        catch (IOException e) {
            logger.error("IOException!!! from readLines");
        }
        return allLines;
    }

    /**
     * Write Lines to a file
     * Explore: FileOutputStream, OutputStreamWriter, and BufferedWriter
     *
     * @param lines matched Line
     * @throws IOException if write failed
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {
        // get the file
        File outputFile = new File(getOutFile());
        Stream<String> linesStream = lines.stream();

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
            linesStream.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

        }
        catch (IOException e) {
            logger.error("There is an error writing to file: " + outputFile);

        }
    }
}
