package com.justwayward.reader.bean;

import com.justwayward.reader.bean.base.Base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 84514 on 2018/5/27.
 */

public class BookReadVip implements Serializable {
    public String _id;
    public String source;
    public String link;
    public String name;
    /**
     * title : 1，相亲
     * link : http://novel.hongxiu.com/a/1218893/12118325.html
     * unreadble : false
     */

    public List<BookReadVip.Chapters> chapters;

    public static class Chapters {
        public String title;
        public String link;
        public boolean unreadble;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public boolean isUnreadble() {
            return unreadble;
        }

        public void setUnreadble(boolean unreadble) {
            this.unreadble = unreadble;
        }
    }
}
