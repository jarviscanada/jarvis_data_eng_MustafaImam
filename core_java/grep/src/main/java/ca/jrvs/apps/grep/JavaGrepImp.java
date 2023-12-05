package ca.jrvs.app.grep;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;

public class JavaGrepImp extends JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

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
        javaGrepImp.getRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        }
        catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex)
        }
    }

}
