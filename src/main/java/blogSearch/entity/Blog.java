package blogSearch.entity;

import lombok.Getter;
import lombok.Setter;

public class Blog {

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String content;


    public Blog(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public Blog() {

    }

}
