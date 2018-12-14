import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main {
    public static void main(String[] args) throws IOException {
        SQLRequest sqlRequest = new SQLRequest();
        ArrayList<JSONObject> listRequestResponce = new ArrayList<JSONObject>();
        for (int i = 0; i < 20; ++i) {
            listRequestResponce.add(getJSONObject(sqlRequest.getCurrentURL()));
            sqlRequest.nextPage();
        }
        Set<Integer> ids = new HashSet<Integer>();
        for (JSONObject jsonObject : listRequestResponce) {
            JSONArray items = jsonObject.getJSONArray("items");
            for (int idx = 0; idx < items.length(); ++idx) {
                int id = items.getJSONObject(idx).getInt("id");
                ids.add(id);
            }
        }
        System.out.println(ids.size());

        int currentFileIdx = 0;
        for (JSONObject jsonObject : listRequestResponce) {
            FileWriter fileWriter = new FileWriter("json" + currentFileIdx++ + ".json");
            jsonObject.write(fileWriter);
            fileWriter.close();
        }

    }
    static JSONObject getJSONObject(String currentURL) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(currentURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "TestApp");
            con.setRequestMethod("GET");

            System.out.println("getResponseCode " + con.getResponseCode());
            System.out.println("con.getResponseMessage " + con.getResponseMessage());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();


            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                jsonObject = new JSONObject(inputLine);
                //JSONArray items = jsonObject.getJSONArray("items");
                /*for (Object item : items) {
                    System.out.println(item.toString());
                }*/

            }
            in.close();
            con.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
class SQLRequest {
    String basicURLVacancies = "https://api.hh.ru/vacancies?";
    int specializationId = 1;
    int perPage = 100;
    int page = 0;

    public String getCurrentURL() {
        return currentURL;
    }

    String currentURL = "";

    public SQLRequest() {
        this.build();
    }
    public void build() {
        currentURL = basicURLVacancies + "specialization=" + specializationId + "&" + "per_page=" + perPage;
    }
    public SQLRequest nextPage() {
        this.build();
        currentURL += "&page=" + ++page;
        return this;
    }
}