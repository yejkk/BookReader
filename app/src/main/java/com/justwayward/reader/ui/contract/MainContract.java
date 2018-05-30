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
package com.justwayward.reader.ui.contract;

import com.justwayward.reader.base.BaseContract;
import com.justwayward.reader.bean.BookCaseResp;
import com.justwayward.reader.bean.user.LoginReq;

/**
 * @author yuyh.
 * @date 2016/8/3.
 */
public interface MainContract {

    interface View extends BaseContract.BaseView {
        void loginSuccess();

        void loginFail();

        void registSuccess();

        void registFail();

        void syncBookShelfCompleted();

        void getRecommedList(BookCaseResp bookCaseRes);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void login(LoginReq loginReq);

        void regist(LoginReq loginReq);

        void syncBookShelf();

        void getRecommedList(BookCaseResp bookCaseRes);

        void setRecommedList();
    }

}
