package com.justwayward.reader.bean;

import java.io.Serializable;

/**
 * Created by 84514 on 2018/5/29.
 */

public class BookCaseInsertRequest implements Serializable {
    public String Action;
    public BookCaseInsertRequest.Data data;

    public static class Data {
        public String token;
        public String bookId;
    }
}
