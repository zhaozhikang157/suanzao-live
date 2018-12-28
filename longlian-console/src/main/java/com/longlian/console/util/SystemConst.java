package com.longlian.console.util;

import com.huaxin.util.Utility;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2017/6/19.
 */
public class SystemConst {
    //#龙链运营部所有人员发送提前10分钟开课提醒的人员
    public static Set<String> initDepartmentOfOperation(){
        Set<String> ll_department_of_operation_user_ids_set = new HashSet<String>();
        String ll_department_of_operation_user_ids = CustomizedPropertyConfigurer.getContextProperty("ll_department_of_operation_user_ids");
        StringTokenizer st=new StringTokenizer(ll_department_of_operation_user_ids , ",");
        while ( st.hasMoreElements() ){
            //System.out.println(st.nextToken());
            try {
                ll_department_of_operation_user_ids_set.add(st.nextToken());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return  ll_department_of_operation_user_ids_set;
    }
}
