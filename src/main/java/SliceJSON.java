import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SliceJSON {
    public static void main(String[] args) throws IOException {

        String sourceDirectory = "source_data/";
        Path directory= Paths.get(sourceDirectory);

        List<Path> files =  Files.walk(directory).collect(Collectors.toList());
        files.remove(0);

        int iteration = 0;
        for (Path fileName : files) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName.toString()));

            JSONObject  obj = new JSONObject(new JSONTokener(reader));
            JSONArray items = obj.getJSONArray("Items");

            System.out.println("JSONArray size :" + items.length());

            for (Object item : items) {
                int id = ((JSONObject)item).getInt("id");
                addLocationAndWriteToFile((JSONObject)item,id);
            }
            iteration++;
            System.out.println("Proceed: " + (float)iteration/(float)files.size() + "%");

        }




        }
    public static void addLocationAndWriteToFile(JSONObject readedObject, int id) throws IOException {
        Object adr = readedObject.get("address");
        String outFileName = "location_data/"+id+".json";
        if (adr != JSONObject.NULL) {
            JSONObject newAddress = (JSONObject) adr;
            if (newAddress.get("lng") != JSONObject.NULL && newAddress.get("lat") != JSONObject.NULL) {
                double lng = newAddress.getDouble("lng");
                double lat = newAddress.getDouble("lat");
                String result = "{\"lat\":" +lat +",\"lon\":" + lng +"}";
                JSONObject geo_point = new JSONObject(result);
                readedObject.put("location",geo_point);
                FileWriter fw = new FileWriter(outFileName);
                fw.write(readedObject.toString());
                fw.close();
            } else {
                FileWriter fw = new FileWriter(outFileName);
                fw.write(readedObject.toString());
                fw.close();
            }
        } else {
            FileWriter fw = new FileWriter(outFileName);
            fw.write(readedObject.toString());
            fw.close();
        }
    }
    }


