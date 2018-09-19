import blogSearch.StartApplication;
import blogSearch.TimeTask.CrawlBlog;
import blogSearch.entity.Blog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApplication.class)
public class TestCrawl {

    @Autowired
    private CrawlBlog crawlBlog;

    @Test
    public void testCrawlBlog() {
        List<Blog> list = crawlBlog.crawl();
        System.out.println(list.size());
    }
}
