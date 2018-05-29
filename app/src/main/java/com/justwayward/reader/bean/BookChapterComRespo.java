package com.justwayward.reader.bean;

import com.justwayward.reader.bean.base.BaseReponse;

import java.util.List;

/**
 * Created by 84514 on 2018/5/29.
 */

public class BookChapterComRespo extends BaseReponse {

    public List<BookChapter> data;

    public static class BookChapter {
        public String userName;
        public String info;
    }
}
