import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class ChangeGeoPoint {
    public static void main(String[] args) throws IOException {
        String dir = "temp_geo_data/";
        Path directory= Paths.get(dir);

        List<Path> files =  Files.walk(directory).collect(Collectors.toList());
        files.remove(0);
        for (Path file : files) {

            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int i = 0;
            StringBuilder resultString = new StringBuilder("");
            while (sc.hasNextLine()) {
                resultString.append(sc.nextLine());
            }

            JSONObject readedObject = new JSONObject(resultString.toString());
            System.out.println(readedObject);
            Object adr = readedObject.get("address");

            if (adr != JSONObject.NULL) {
                System.out.println(file);
                JSONObject newAddress = (JSONObject) adr;
                for (String s : newAddress.keySet()) {
                    System.out.println(s);
                }
                double lng = newAddress.getDouble("lng");
                double lat = newAddress.getDouble("lat");
                String result = "{\"lat\":" +lat +",\"lon\":" + lng +"}";
                JSONObject geo_point = new JSONObject(result);
                readedObject.put("location",geo_point);
                FileWriter fw = new FileWriter(
                        file.toString().replaceFirst(".json","")
                        + "_with_location.json");
                fw.write(readedObject.toString());
                fw.close();
            }

        }


    }
}
