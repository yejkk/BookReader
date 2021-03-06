/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justwayward.reader.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.justwayward.reader.api.BookApi;
import com.justwayward.reader.base.Constant;
import com.justwayward.reader.base.RxPresenter;
import com.justwayward.reader.bean.BookChapterComInsertReq;
import com.justwayward.reader.bean.BookChapterComReq;
import com.justwayward.reader.bean.BookChapterComRespo;
import com.justwayward.reader.bean.BookMixAToc;
import com.justwayward.reader.bean.BookPublish;
import com.justwayward.reader.bean.BookPublishRequest;
import com.justwayward.reader.bean.BookRead;
import com.justwayward.reader.bean.BookReadVip;
import com.justwayward.reader.bean.BookSource;
import com.justwayward.reader.bean.ChapterRead;
import com.justwayward.reader.bean.base.BaseReponse;
import com.justwayward.reader.ui.contract.BookReadContract;
import com.justwayward.reader.utils.LogUtils;
import com.justwayward.reader.utils.RxUtil;
import com.justwayward.reader.utils.SharedPreferencesUtil;
import com.justwayward.reader.utils.StringUtils;
import com.justwayward.reader.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author lfh.
 * @date 2016/8/7.
 */
public class BookReadPresenter extends RxPresenter<BookReadContract.View>
        implements BookReadContract.Presenter<BookReadContract.View> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookReadPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    @Override
    public void getBookMixAToc(final String bookId, String viewChapters) {
        String key = StringUtils.creatAcacheKey("book-toc", bookId, viewChapters);
        Observable<BookMixAToc.mixToc> fromNetWork = bookApi.getBookMixAToc(bookId, viewChapters)
                .map(new Func1<BookMixAToc, BookMixAToc.mixToc>() {
                    @Override
                    public BookMixAToc.mixToc call(BookMixAToc data) {
                        return data.mixToc;
                    }
                })
                .compose(RxUtil.<BookMixAToc.mixToc>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable
                .concat(RxUtil.rxCreateDiskObservable(key, BookMixAToc.mixToc.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookMixAToc.mixToc>() {
                    @Override
                    public void onNext(BookMixAToc.mixToc data) {
                        List<BookMixAToc.mixToc.Chapters> list = data.chapters;
                        if (list != null && !list.isEmpty() && mView != null) {
                            mView.showBookToc(list);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                        mView.netError(0);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void showIsPublish(BookPublishRequest bookPublishRequest) {
        Subscription rxSubscription = bookApi.publish(bookPublishRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookPublish>() {
                    @Override
                    public void onNext(BookPublish data) {
                        if (data != null && mView != null) {
                            if (SharedPreferencesUtil.getInstance().getBoolean(Constant.Manager, false)) {
                                if (TextUtils.equals(data.rescode, "200")) {
                                    mView.showPublish(3);
                                } else {
                                    mView.showPublish(1);
                                }
                            } else {
                                if (TextUtils.equals(data.rescode, "200")) {
                                    mView.showPublish(2);
                                } else {
                                    mView.showPublish(0);
                                }
                            }
                        } else {
                            mView.showPublish(0);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void sendPublish(BookPublishRequest bookPublishRequest) {
        Subscription rxSubscription = bookApi.publish(bookPublishRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookPublish>() {
                    @Override
                    public void onNext(BookPublish data) {
                        if (data != null && mView != null) {
                            if (SharedPreferencesUtil.getInstance().getBoolean(Constant.Manager, false)) {
                                ToastUtils.showToast("出版成功");
                            } else {
                                ToastUtils.showToast("购买成功");
                            }
                        } else {
                            mView.showPublish(0);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                    }
                });
        addSubscrebe(rxSubscription);
    }


    @Override
    public void getChapterRead(String url, final int chapter, final String bookId) {
        Subscription rxSubscription = bookApi.getBBookSource("summary", bookId)
                .flatMap(new Func1<List<BookSource>, Observable<BookReadVip>>() {
                    @Override
                    public Observable<BookReadVip> call(List<BookSource> bookSources) {
                        return bookApi.getABookRead(bookSources.get(0)._id, "chapters");
                    }
                })
                .flatMap(new Func1<BookReadVip, Observable<ChapterRead>>() {
                    @Override
                    public Observable<ChapterRead> call(BookReadVip bookReadVip) {
                        Log.i("yxtest", bookReadVip.chapters.get(chapter).link);
                        return bookApi.getChapterRead(bookReadVip.chapters.get(chapter).link);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChapterRead>() {
                    @Override
                    public void onNext(ChapterRead data) {
                        if (data.chapter != null && mView != null) {
                            mView.showChapterRead(data.chapter, chapter);
                        } else {
                            mView.netError(chapter);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                        mView.netError(chapter);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getChapterCommenRead(final int chapter, final String bookId) {
        BookChapterComReq bookChapterComReq = new BookChapterComReq();
        bookChapterComReq.Action = "GetBookChapterCom";
        bookChapterComReq.bookId = bookId;
        bookChapterComReq.chapterID = chapter;
        Subscription rxSubscription = bookApi.getBookChapterComReq(bookChapterComReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookChapterComRespo>() {
                    @Override
                    public void onNext(BookChapterComRespo data) {
                        mView.showChapterCommon(data);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.netError(chapter);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void sendChapterCommen(final int chapter, final String bookId, final String info) {
        BookChapterComInsertReq bookChapterComInsertReq = new BookChapterComInsertReq();
        bookChapterComInsertReq.Action = "InsertBookChapterCom";
        BookChapterComInsertReq.Data data =new BookChapterComInsertReq.Data();
        data.bookId = bookId;
        data.chapterID = chapter;
        data.info = info;
        data.token = SharedPreferencesUtil.getInstance().getString(Constant.Token,"");
        Subscription rxSubscription = bookApi.setBookChapterComReq(bookChapterComInsertReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseReponse>() {
                    @Override
                    public void onNext(BaseReponse data) {
                        ToastUtils.showToast("发表评论成功");
                        mView.showChapterCommon();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.netError(chapter);
                    }
                });
        addSubscrebe(rxSubscription);
    }
}