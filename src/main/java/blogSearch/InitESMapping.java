package blogSearch;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class InitESMapping {


    private static String ES_URL = "118.89.244.167";


    private static int ES_TCP_PORT = 9300;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Settings settings = Settings.builder().put("cluster.name", "elastic-cluster").build();
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(ES_URL), ES_TCP_PORT));

        String indexName = "blog-search";
        String typeName = "blog";
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().
                startObject()
                .startObject("settings")
                .startObject("analyzer")
                .startObject("ik")
                .field("tokenizer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject()
                .startObject("mappings")
                .startObject(typeName)
                .startObject("properties")
                .startObject("url").field("type", "keyword").field("index", "not_analyzed").endObject()
                .startObject("title").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("content").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .endObject()
                .endObject()
                .endObject().endObject();

        IndexResponse indexResponse = transportClient.prepareIndex(indexName, typeName).setSource(mappingBuilder).get();
        System.out.println(indexResponse.status());
    }

}
