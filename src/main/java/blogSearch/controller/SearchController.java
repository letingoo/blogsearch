package blogSearch.controller;

import blogSearch.entity.Blog;
import blogSearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("/search/{keyword}")
    public List<Blog> search(@PathVariable("keyword") String keyword) {
        String[] keywords = keyword.split(" ");
        return searchService.searchBlogsFromES(keywords);
    }
}
