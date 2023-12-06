package ca.jrvs.apps.practice;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class RegexExcImp implements RegexExc {

    @java.lang.Override
    public boolean matchJpeg(String filename) {

        Pattern pattern = Pattern.compile(".+\\.(jpe?g)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }

    @java.lang.Override
    public boolean matchIp(String ip) {
        Pattern pattern = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
        // for either number to be greater than 999 it must be 4 digits, regex takes care of that
    }

    @java.lang.Override
    public boolean isEmptyLine(String line) {
        if (line == null || line.trim().isEmpty() ) {
            return true;
        }
        return false;
    }
}