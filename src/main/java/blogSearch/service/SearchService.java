package blogSearch.service;

import blogSearch.entity.Blog;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private TransportClient transportClient;

    public List<Blog> searchBlogsFromES(String[] keywords) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (String keyword : keywords) {
            QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("content", keyword);
            queryBuilder.should(matchQueryBuilder);
        }

        logger.info(queryBuilder.toString());
        SearchResponse searchResponse =
                transportClient.prepareSearch("blog-search").
                        setTypes("blog").setQuery(queryBuilder).get();
        List<Blog> result = new LinkedList<>();
        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> map = hit.getSourceAsMap();
            Blog blog = new Blog();
            blog.setTitle((String)map.get("title"));
            blog.setUrl((String)map.get("url"));
            result.add(blog);
        }
        return result;
    }
}
