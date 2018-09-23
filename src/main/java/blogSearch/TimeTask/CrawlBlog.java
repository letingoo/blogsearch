package blogSearch.TimeTask;

import blogSearch.entity.Blog;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 定时任务 抓取 blog上的内容存入es
 */
@Component
public class CrawlBlog {

    @Value("${targetUrl}")
    private String TARGET_URL;

    @Autowired
    private TransportClient transportClient;

    private Logger logger = LoggerFactory.getLogger(CrawlBlog.class);

    /**
     * 每天0点0分0秒开始爬取blog数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void crawlBlogAndSave() {
        List<Blog> blogList = crawl();
        try {
            deleteAllFromES();
            saveToES(blogList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        logger.info("crawl blogs at " + date.toString());
    }


    public List<Blog> crawl() {

        List<Blog> blogList = new LinkedList<>();
        try {
            Document document = Jsoup.connect(TARGET_URL).get();
            Elements articles = document.select("article.post");
            for (Element article : articles) {
                String title = article.select("h1 > a").text();
                String url = TARGET_URL + article.select("h1 > a").first().attr("href").toString();
                Blog blog = new Blog(url, title);
                blogList.add(blog);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            crawlDetail(blogList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blogList;
    }


    /**
     * 抓取正文
     * @param blogList
     * @throws IOException
     */
    public void crawlDetail(List<Blog> blogList) throws IOException {
        for (Blog blog : blogList) {
            String url = blog.getUrl();
            Document document = Jsoup.connect(url).get();
            String content = document.select("div.entry").text();
            blog.setContent(content);
        }
    }


    public void deleteAllFromES() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient).filter(QueryBuilders.matchAllQuery())
                .source("blog-search").get();
    }


    /**
     * 数据存入ES
     * @param blogList
     */
    public void saveToES(List<Blog> blogList) throws IOException {
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        for (Blog blog : blogList) {
            bulkRequest.add(transportClient.prepareIndex("blog-search", "blog")
            .setSource(XContentFactory.jsonBuilder()
                .startObject()
                .field("url", blog.getUrl())
                .field("title", blog.getTitle())
                .field("content", blog.getContent())
            .endObject()));
        }

        BulkResponse bulkResponse = bulkRequest.get();
    }

}
