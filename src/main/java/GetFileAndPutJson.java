import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class GetFileAndPutJson {
    public static void main(String[] args) {

        String dir;
        String esIp;
        if (args.length == 0) {
            dir = "/home/headon/ParseHHnew/test_dir/";
            esIp = "172.17.0.2";
        } else if (args.length == 2){
            dir = args[0];
            esIp = args[1];
        } else {
            System.out.println("Wrong input");
            return;
        }
        System.out.println("Dir : "+dir);
        System.out.println("ES IP:"+esIp);


        Path testDir = Paths.get(dir);
        try {
            System.out.println(testDir.toRealPath());
        } catch (IOException e) {
            System.err.println("Wrong path to watched directory (maybe dir doesn't exists)");
            e.printStackTrace();
            return;
        }

        try {
            WatchService watcher = testDir.getFileSystem().newWatchService();
            testDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                System.out.println("Listening to new files in dir:" + testDir);
                WatchKey watchKey = watcher.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Created: " + event.context().toString());
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Modify: " + event.context().toString());
                        try (RestClient lowRestClient = RestClient.builder(
                                new HttpHost(esIp, 9200, "http")).build();)
                        {
                            RestHighLevelClient client =
                                    new RestHighLevelClient(lowRestClient);


                            processFile(testDir + testDir.getFileSystem().getSeparator()+
                                    event.context().toString(), client);
                        }
                    }
                }
                watchKey.reset();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    private static void processFile(String fileName, RestHighLevelClient client) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        JSONObject obj = new JSONObject(new JSONTokener(reader));
        JSONArray items = obj.getJSONArray("Items");

        System.out.println("JSONArray size :" + items.length());

        for (Object item : items) {
            int id = ((JSONObject)item).getInt("id");
            addLocationAndWriteToFile((JSONObject)item,id, client);
        }
    }
    public static void addLocationAndWriteToFile(JSONObject readedObject, int id,
                                                 RestHighLevelClient client) throws IOException {
        Object adr = readedObject.get("address");
        if (adr != JSONObject.NULL) {
            JSONObject newAddress = (JSONObject) adr;
            if (newAddress.get("lng") != JSONObject.NULL && newAddress.get("lat") != JSONObject.NULL) {
                double lng = newAddress.getDouble("lng");
                double lat = newAddress.getDouble("lat");
                String result = "{\"lat\":" + lat + ",\"lon\":" + lng + "}";
                JSONObject geo_point = new JSONObject(result);
                readedObject.put("location", geo_point);
            }
        }
        putToEs(readedObject,id,client);
    }

    private static void putToEs(JSONObject readedObject, int id, RestHighLevelClient client) {
        String idStr = Integer.toString(id);
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                idStr);
        request.source(readedObject.toString(), XContentType.JSON);
        IndexResponse ir = null;
        try {
            ir = client.index(request);
        } catch (IOException e) {
            System.err.println("Failed to put json to ES");
            System.err.println("Json id:" + id);
            e.printStackTrace();
        }
        System.out.println(ir);
    }
}
