import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ParserStatistics {
    public static void main(String[] args) throws IOException {
        String sourceDirectory = "parse_statistic/";
        Path directory= Paths.get(sourceDirectory);

        List<Path> files =  Files.walk(directory).collect(Collectors.toList());
        files.remove(0);

        FileWriter fw = new FileWriter("parse_statistic.csv");
        fw.write("Found,Collected,GoodRequestCounter,BadRequestCounter");
        fw.write("\n");
        int iteration = 0;
        for (Path fileName : files) {
            System.out.println(fileName);
            JSONObject currentJson = new JSONObject();

            BufferedReader reader = new BufferedReader(new FileReader(fileName.toString()));
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            int found = obj.getInt("Found");
            int collected = obj.getInt("Collected");
            //JSONObject col = obj.getJSONObject("Statistic");
            int goodRequestCounter = obj.getInt("GoodRequestCounter");
            int badRequestCounter = obj.getInt("BadRequestCounter");
            fw.write(found+","+collected+","+goodRequestCounter+","+badRequestCounter+"");
            fw.write("\n");
        }
        fw.close();
    }
}
