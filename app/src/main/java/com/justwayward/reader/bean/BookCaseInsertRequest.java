package com.justwayward.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 84514 on 2018/5/29.
 */

public class BookCaseInsertRequest implements Serializable {
    public String Action;
    public String token;
    public List<Data> data;

    public static class Data {
        public String bookId;
    }
}
