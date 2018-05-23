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
package com.justwayward.reader.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.justwayward.reader.R;

/**
 * @author yuyh.
 * @date 16/9/5.
 */
public class LoginPopupWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Activity mActivity;

    private EditText user;
    private EditText pass;
    private Button loginBtn;
    private TextView pop;

    LoginTypeListener listener;


    public LoginPopupWindow(Activity activity) {
        mActivity = activity;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        mContentView = LayoutInflater.from(activity).inflate(R.layout.layout_login_popup_window, null);
        setContentView(mContentView);

        user = (EditText) mContentView.findViewById(R.id.userId);
        pass = (EditText) mContentView.findViewById(R.id.pass);
        loginBtn = (Button) mContentView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        pop = (TextView) mContentView.findViewById(R.id.promptText);

        setAnimationStyle(R.style.LoginPopup);
        setFocusable(true);


    }

    private void lighton() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        mActivity.getWindow().setAttributes(lp);
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }

//    @Override
//    public void showAsDropDown(View anchor, int xoff, int yoff) {
//        lightoff();
//        super.showAsDropDown(anchor, xoff, yoff);
//    }
//
//    @Override
//    public void showAtLocation(View parent, int gravity, int x, int y) {
//        lightoff();
//        super.showAtLocation(parent, gravity, x, y);
//    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginBtn){
            String userid = user.getText().toString();
            String userpass = pass.getText().toString();
            if(userid ==null || TextUtils.equals(userid , "") || userpass ==null || TextUtils.equals(userpass , "")){
                pop.setText("请输入用户名密码");
                return;
            }
            pop.setText("登陆中");
            listener.onLogin(userid,userpass);
            return;
        }
    }

    public void getLoginStatus(boolean success){
        if(success){
            this.dismiss();
        }else{
            pop.setText("用户名密码错误");
        }
    }

    public interface LoginTypeListener {

        void onLogin(String userId, String userPass);

    }

    public void setLoginTypeListener(LoginTypeListener listener){
        this.listener = listener;
    }

}
