package com.huaxin.util.db;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by syl on 2017/1/10.
 * 1. 使用注解“@Repository”把该类放入到IOC容器中
 * 2. 使用注解“@Aspect”把该类声明为一个切面
 * 设置切面的优先级:
 * 3. 使用注解“@Order(number)”指定前面的优先级，值越小，优先级越高  (解决动态路由数据库设置Holder key顺序的坑)
 * 通过方法名读写分离
 */
/*@Order(1)
@Aspect
@Repository*/
public class DataSourceInterceptor {

    //需要从  slave数据读取数据（方法名以以下打头的方法）
    public  final  static  String[] SLAVE_METHOD_NAMES = {"find" , "select" , "query" , "get","load"};

   // @Before("execution(public *  com.llkeji.service.*.*(..))") //注解方式
    //采用声明式 xml 配置文件方式 在applicationcontext-mybatis
    public void dataSourceBefore(JoinPoint point) throws Exception{
        //判断是否在方法是否已经AOP注解数据源
        // 获取方法签名
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        Method instanceMethod = point.getTarget().getClass().getMethod(methodName, method.getParameterTypes());
       // DataSource methodAnnotation = AnnotationUtils.findAnnotation(instanceMethod, DataSource.class);
        ///Object[] arguments = point.getArgs();
        // Object result = method.invoke(method, arguments);
        if (instanceMethod.isAnnotationPresent(DataSource.class)) {
            DataSource datasource = instanceMethod.getAnnotation(DataSource.class);
            DynamicDataSourceKeyHolder.setDataSourceKey(datasource.value());
        }else {
            String dataSourceKey = getDateSourceKey(methodName);
            DynamicDataSourceKeyHolder.setDataSourceKey(dataSourceKey);
        }
    }

    /**
     * 获取数据源EKY
     * @param methodName
     * @return
     */
    public String getDateSourceKey(String methodName){
        if(isSlaveMethod(methodName)){
            return  DBSalveRandom.getInstance().getRandomDBServer();
            //return DynamicDataSourceKey.DS_SLAVE;
        }else{
            return DynamicDataSourceKey.DS_MASTER;
        }
    }

    /**
     * 根据方法名判断是否是需要从slave数据库读取
     * @param methodName
     * @return
     */
    public  boolean isSlaveMethod(String methodName){
        boolean slaveDskey = false;
        for (String slaveName : SLAVE_METHOD_NAMES){
            if(methodName.startsWith(slaveName)){
                return  true;
            }
        }
        return slaveDskey;
    }
}
