package zad1;

/**
 * Created by yaroslavkohun on 3/12/18.
 */
import java.io.File;
import java.nio.file.*;

public class Futil {
    public static void processDir(String startDir, String dataFile){
        new File(dataFile).delete();
        try{
            Files.walkFileTree(Paths.get(startDir), new MyFileVisitor(Paths.get(dataFile)));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
