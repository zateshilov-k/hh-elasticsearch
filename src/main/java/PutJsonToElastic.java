import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class PutJsonToElastic {
    public static void main(String[] args) throws IOException {
        String esIp = "172.17.0.2";


        RestClient lowRestClient = RestClient.builder(
                new HttpHost(esIp, 9200, "http")).build();

        RestHighLevelClient client =
                new RestHighLevelClient(lowRestClient);

        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        IndexResponse ir = client.index(request);
        System.err.println(ir);
        lowRestClient.close();
    }
}
