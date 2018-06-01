package com.justwayward.reader.bean;

import java.io.Serializable;

/**
 * Created by 84514 on 2018/5/29.
 */

public class BookChapterComInsertReq implements Serializable {
    public String Action;
    public BookChapterComInsertReq.Data data;

    public static class Data {
        public String token;
        public String bookId;
        public int chapterID;
        public String info;
    }
}
