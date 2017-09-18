
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JniProducerCaller
 {
	public native void		startBenchmark();

    public static void main(String[] args)
    {
     System.loadLibrary("benchmarkProducer");
     JniProducerCaller sample = new JniProducerCaller();
     sample.startBenchmark();
   }
 }
