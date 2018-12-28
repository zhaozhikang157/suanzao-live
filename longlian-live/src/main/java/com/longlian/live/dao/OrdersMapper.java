package com.longlian.live.dao;

import com.longlian.model.Orders;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by U on 2016/8/1.
 */
public interface OrdersMapper {
    void create(Orders orders);
    Orders getOrderById(long id);
    Orders getOrderByOrderNo(String orderNo);
    List<Orders> getOrderByJoinCourseRecordId(long joinCourseRecordId);

    int updateRechargeById(Orders or);

    /** 查询当天申请进行中和申请中的提现金额
     * @param appId
     * @param nowTime
     * @return
     */
    BigDecimal findAmountByAppId(@Param("appId") long appId, @Param("nowTime") String nowTime);

    /**
     * 今日提现(成功)金额
     * @param appId
     * @param nowTime
     * @return
     */
    BigDecimal findAmountToday(@Param("appId") long appId, @Param("nowTime") String nowTime);

    /**
     * 查询所有申请中的提现的金额
     * @param appId
     * @param nowTime
     * @return
     */
    BigDecimal findAmountAllIng(@Param("appId") long appId, @Param("nowTime") String nowTime);


    List<Map> getTopUpRecord(@Param("appId") Long appId, @Param("lastId") long lastId);

    /*提现进行中的所有的钱*/
    BigDecimal findAccountMoney(long appId);

    int cancelById(long id);

    /**
     * 获取提现次数在当前月中,包含申请提现中的次数
     * @param startTime
     * @param endTime
     * @param appId
     * @return
     */
    Long getWithIngCount(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("appId")long appId);

    /**
     * 获取提现次数在当前月中,和成功提现的次数
     * @param startTime
     * @param endTime
     * @param appId
     * @return
     */
    Long getWithSuccessCount(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("appId")long appId);

    void updateOrdersByOrderNo(Orders orders);

}
