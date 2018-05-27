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
package com.justwayward.reader.bean.user;

import com.justwayward.reader.bean.base.Base;

import java.io.Serializable;

/**
 * @author yuyh.
 * @date 16/9/4.
 */
public class Login implements Serializable {


    /**
     {"INFO": "None", "token": "dd48a44d-5e3e-4846-b2bf-4b390666de35", "rescode": "200"}
     */

    public String token;


    public String rescode;

    public String INFO;




}
