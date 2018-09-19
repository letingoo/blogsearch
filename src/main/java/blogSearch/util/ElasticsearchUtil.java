package blogSearch.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchUtil {

    @Value("${elasticsearch.url}")
    private static String ES_URL;

    @Value("${elasticsearch.tcp-port}")
    private static int ES_TCP_PORT;

    @Bean(name = "transportClient")
    public TransportClient transportClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "elastic-cluster").build();
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(ES_URL), ES_TCP_PORT));
        return transportClient;
    }
}
