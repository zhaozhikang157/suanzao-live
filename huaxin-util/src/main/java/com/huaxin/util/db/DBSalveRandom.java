package com.huaxin.util.db;

import com.huaxin.util.Utility;
import com.huaxin.util.spring.CustomizedPropertyConfigurer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 采用 双检查锁机制
 * Created by syl on 2017/12/25.
 */
public class DBSalveRandom {
    //使用volatile关键字保其可见性
    volatile private static DBSalveRandom instance = null;
    volatile private static List<String > dbServers = null;
    private static int connectionSlaveWeight = 1;
    private static int connectionSlave2Weight = 1;
    private DBSalveRandom(){}

    /**
     * 获取实例对象
     * @return
     */
    public static DBSalveRandom getInstance() {
        try {
            if(instance != null){//懒汉式

            }else{
                //创建实例之前可能会有一些准备性的耗时工作
                Thread.sleep(100);
                synchronized (DBSalveRandom.class) {
                    if(instance == null){//二次检查
                        instance = new DBSalveRandom();
                        dbServers = new LinkedList<String>();//采用链表的数据结构，获取性能更优
                        //处理权重
                        if(!Utility.isNullorEmpty(CustomizedPropertyConfigurer.getContextProperty("connection.slave.weight"))){
                            connectionSlaveWeight = Utility.parseInt((String)CustomizedPropertyConfigurer.getContextProperty("connection.slave.weight"));
                        }
                        if(!Utility.isNullorEmpty(CustomizedPropertyConfigurer.getContextProperty("connection.slave2.weight"))){
                            connectionSlave2Weight = Utility.parseInt((String)CustomizedPropertyConfigurer.getContextProperty("connection.slave2.weight"));
                        }
                        for(int i =0 ;i < instance.connectionSlaveWeight ; i++){
                            dbServers.add(DynamicDataSourceKey.DS_SLAVE);
                        }
                        for(int i =0 ;i < instance.connectionSlave2Weight; i++){
                            dbServers.add(DynamicDataSourceKey.DS_SLAVE2);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 随机获取
     * @return
     */
    public  String getRandomDBServer()
    {
        int index = ThreadLocalRandom.current().nextInt(dbServers.size());
        return dbServers.get(index);
    }

    public static void main(String[] args) {
        for (int i =0 ;i<10 ;i++){
            String db  =  DBSalveRandom.getInstance().getRandomDBServer();
            System.out.println(db);
        }
    }
}
