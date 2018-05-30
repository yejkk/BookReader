/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justwayward.reader.ui.presenter;

import android.text.TextUtils;

import com.justwayward.reader.api.BookApi;
import com.justwayward.reader.base.Constant;
import com.justwayward.reader.base.RxPresenter;
import com.justwayward.reader.bean.BookCaseInsertRequest;
import com.justwayward.reader.bean.BookCaseRequest;
import com.justwayward.reader.bean.BookCaseResp;
import com.justwayward.reader.bean.BookDetail;
import com.justwayward.reader.bean.BookMixAToc;
import com.justwayward.reader.bean.Recommend;
import com.justwayward.reader.bean.base.BaseReponse;
import com.justwayward.reader.bean.user.Login;
import com.justwayward.reader.bean.user.LoginReq;
import com.justwayward.reader.manager.CollectionsManager;
import com.justwayward.reader.ui.contract.MainContract;
import com.justwayward.reader.utils.LogUtils;
import com.justwayward.reader.utils.SharedPreferencesUtil;
import com.justwayward.reader.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author yuyh.
 * @date 2016/8/3.
 */
public class MainActivityPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {

    private BookApi bookApi;
    public static boolean isLastSyncUpdateed = false;

    @Inject
    public MainActivityPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void login(LoginReq loginReq) {
        Subscription rxSubscription = bookApi.login(loginReq).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onNext(Login data) {
                        if (data != null && mView != null) {
                            if (TextUtils.equals(data.rescode, "200")) {
                                SharedPreferencesUtil.getInstance().putString(Constant.Token, data.token);
                                mView.loginSuccess();
                                if (TextUtils.equals(data.INFO, "manager")) {
                                    SharedPreferencesUtil.getInstance().putBoolean(Constant.Manager, true);
                                } else {
                                    SharedPreferencesUtil.getInstance().putBoolean(Constant.Manager, false);
                                }
                            } else {
                                mView.loginFail();
                            }

                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("login" + e.toString());
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void regist(LoginReq loginReq) {
        Subscription rxSubscription = bookApi.regist(loginReq).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onNext(Login data) {
                        if (data != null && mView != null) {
                            if (TextUtils.equals(data.rescode, "200")) {
                                mView.registSuccess();
                            } else {
                                mView.registFail();
                            }

                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("login" + e.toString());
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void syncBookShelf() {
        List<Recommend.RecommendBooks> list = CollectionsManager.getInstance().getCollectionList();
        BookCaseRequest bookCaseRequest = new BookCaseRequest();
        bookCaseRequest.Action = "GetUserBook";
        bookCaseRequest.token = SharedPreferencesUtil.getInstance().getString(Constant.Token, "");
        if (TextUtils.isEmpty(bookCaseRequest.token)) {
            return;
        }
        Subscription bookSubscription = bookApi.getBookCaseI(bookCaseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookCaseResp>() {
                    @Override
                    public void onNext(BookCaseResp bookCaseResp) {
                        for (Recommend.RecommendBooks bean : CollectionsManager.getInstance().getCollectionList()) {
                            if (!bookCaseResp.book.contains(bean._id)) {
                                CollectionsManager.getInstance().remove(bean._id);
                                bookCaseResp.book.remove(bean._id);
                            }
                        }
                        mView.getRecommedList(bookCaseResp);

                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.showError();
                    }
                });
        addSubscrebe(bookSubscription);
        List<Observable<BookMixAToc.mixToc>> observables = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Recommend.RecommendBooks bean : list) {
                if (!bean.isFromSD) {
                    Observable<BookMixAToc.mixToc> fromNetWork = bookApi.getBookMixAToc(bean._id, "chapters")
                            .map(new Func1<BookMixAToc, BookMixAToc.mixToc>() {
                                @Override
                                public BookMixAToc.mixToc call(BookMixAToc data) {
                                    return data.mixToc;
                                }
                            })
//                    .compose(RxUtil.<BookMixAToc.mixToc>rxCacheListHelper(
//                            StringUtils.creatAcacheKey("book-toc", bean._id, "chapters")))
                            ;
                    observables.add(fromNetWork);
                }
            }
        } else {
            ToastUtils.showSingleToast("书架空空如也...");
            mView.syncBookShelfCompleted();
            return;
        }
        isLastSyncUpdateed = false;
        Subscription rxSubscription = Observable.mergeDelayError(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookMixAToc.mixToc>() {
                    @Override
                    public void onNext(BookMixAToc.mixToc data) {
                        String lastChapter = data.chapters.get(data.chapters.size() - 1).title;
                        CollectionsManager.getInstance().setLastChapterAndLatelyUpdate(data.book, lastChapter, data.chaptersUpdated);
                    }

                    @Override
                    public void onCompleted() {
                        mView.syncBookShelfCompleted();
                        if (isLastSyncUpdateed) {
                            ToastUtils.showSingleToast("小説已更新");
                        } else {
                            ToastUtils.showSingleToast("你追的小説沒有更新");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.showError();
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getRecommedList(BookCaseResp bookCaseRes) {
        List<Observable<BookDetail>> observables = new ArrayList<>();
        for (String bookcase : bookCaseRes.book) {
            observables.add(bookApi.getBookDetail(bookcase));
        }
        Subscription rxSubscription = Observable.merge(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookDetail>() {
                    @Override
                    public void onNext(BookDetail data) {
                        Recommend.RecommendBooks recommendBooks = new Recommend.RecommendBooks();
                        recommendBooks.title = data.title;
                        recommendBooks._id = data._id;
                        recommendBooks.cover = data.cover;
                        recommendBooks.lastChapter = data.lastChapter;
                        recommendBooks.updated = data.updated;
                    }

                    @Override
                    public void onCompleted() {
                        mView.syncBookShelfCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.showError();
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void setRecommedList() {
        List<Recommend.RecommendBooks> list = CollectionsManager.getInstance().getCollectionList();
        BookCaseInsertRequest bookCaseInsertRequest = new BookCaseInsertRequest();
        bookCaseInsertRequest.Action = "InsertUserBook";
        bookCaseInsertRequest.token = SharedPreferencesUtil.getInstance().getString(Constant.Token, "");
        bookCaseInsertRequest.data = new ArrayList<BookCaseInsertRequest.Data>();
        for (int i = 0, length = list.size(); i < length; i++) {
            BookCaseInsertRequest.Data data = new BookCaseInsertRequest.Data();
            data.bookId = list.get(i)._id;
            bookCaseInsertRequest.data.add(data);
        }
        Subscription bookSubscription = bookApi.setBookCaseI(bookCaseInsertRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseReponse>() {
                    @Override
                    public void onNext(BaseReponse bookCaseResp) {
                        if (TextUtils.equals(bookCaseResp.rescode, "200")) {
                            ToastUtils.showSingleToast("书架已同步");
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
//                        mView.showError();
                    }
                });
        addSubscrebe(bookSubscription);
    }
}
