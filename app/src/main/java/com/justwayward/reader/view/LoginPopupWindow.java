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
import com.justwayward.reader.bean.user.LoginReq;

/**
 * @author yuyh.
 * @date 16/9/5.
 */
public class LoginPopupWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Activity mActivity;

    private EditText user;
    private EditText pass;
    private EditText passRepeat;
    private Button loginBtn;
    private Button registBtn;
    private Button registBtnSuccess;
    private Button registback;

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
        passRepeat = (EditText) mContentView.findViewById(R.id.pass_repeat);

        loginBtn = (Button) mContentView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        registBtn = (Button) mContentView.findViewById(R.id.registBtn);
        registBtn.setOnClickListener(this);
        registBtnSuccess = (Button) mContentView.findViewById(R.id.registBtnSuccess);
        registBtnSuccess.setOnClickListener(this);
        registback = (Button) mContentView.findViewById(R.id.registback);
        registback.setOnClickListener(this);
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
        }else if(v.getId() == R.id.registBtn){
            loginBtn.setVisibility(View.GONE);
            registBtn.setVisibility(View.GONE);

            registBtnSuccess.setVisibility(View.VISIBLE);
            passRepeat.setVisibility(View.VISIBLE);
            registback.setVisibility(View.VISIBLE);
            user.setText("");
            pass.setText("");
            passRepeat.setText("");
            pop.setText("开始注册，完成上列信息");
            return;
        }else if(v.getId() == R.id.registBtnSuccess){
            String userid = user.getText().toString();
            String userpass = pass.getText().toString();
            String userpassRe = passRepeat.getText().toString();
            if(userid ==null || TextUtils.equals(userid , "") || userpass ==null || TextUtils.equals(userpass , "")
                    || userpassRe ==null || TextUtils.equals(userpassRe , "") ){
                pop.setText("请填写用户名密码");
                return;
            }
            if(!TextUtils.equals(userpass,userpassRe)){
                pop.setText("确认密码填写不一致");
                return;
            }
            pop.setText("注册中");
            LoginReq loginReq = new LoginReq();
            loginReq.UserName = userid;
            loginReq.UserPassword = userpass;
            loginReq.Action = "Regist";
            listener.onRegost(loginReq);
            return;
        }else if(v.getId() == R.id.registback){
            loginBtn.setVisibility(View.VISIBLE);
            registBtn.setVisibility(View.VISIBLE);

            registBtnSuccess.setVisibility(View.GONE);
            passRepeat.setVisibility(View.GONE);
            registback.setVisibility(View.GONE);
            pop.setText("");
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

    public void getRegistStatus(boolean success){
        if(success){
            registback.callOnClick();
            pop.setText("请登录");
        }else{
            pop.setText("注册失败，用户名重复");
        }
    }

    public interface LoginTypeListener {

        void onLogin(String userId, String userPass);
        void onRegost(LoginReq loginReq);
    }

    public void setLoginTypeListener(LoginTypeListener listener){
        this.listener = listener;
    }

}
