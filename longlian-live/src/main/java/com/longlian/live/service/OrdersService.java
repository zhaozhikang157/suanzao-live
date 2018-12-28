package com.longlian.live.service;

import com.longlian.dto.CourseRelayDto;
import com.longlian.model.Course;
import com.longlian.model.Orders;
import com.longlian.model.RelayIncome;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/15.
 */
public interface OrdersService {

    public void create(Orders orders);

    Orders  thirdPay(String orderNo, String payType, String tranNo) throws  Exception;

    Orders  thirdPayRelay(String orderNo, String payType, String tranNo) throws  Exception;


    Orders  updateBuyFlow(String orderNo, String payType) throws  Exception;

    Orders  updateBuyFlowError(String orderNo, String payType) throws  Exception;

    public  Orders getOrderById(long id);

    public  Orders getOrderByOrderNo(String orderNo);

    Orders getOrderByJoinCourseRecordId(long joinRecordId);

    List<Map> getTopUpRecord(Long id, long lastId);

    int  cancelById(long orderId);

    void handlerBuyCourse(Orders orders , Long invitationAppId);
    void handlerRelayCourse(Orders orders , Long invitationAppId,long courseId,RelayIncome relayIncome);
    void relayCourse(Orders orders , Long invitationAppId,long courseId);
    void handlerBuyRelayCourse(Orders orders, Long invitationAppId);

    void sendMsgAndFollow2(Orders orders , Course course , String cAct);
    void sendRelayMsg(Orders orders , Course course , String cAct);
    void sendMsgAndFollow(Orders orders , Course course , String cAct);
    //购买转播课发送消息
    void sendMsgRelayCoinPay(Orders orders , Course course,RelayIncome relayIncome,CourseRelayDto courseRelayDto);

    Orders setOrders(String orderType , String payType , long appId , String merId , BigDecimal amount , String tranNo ,long joinCourseId ,long iosPayTypeId, String remark);

    Orders setOrders(String orderType , String payType , long appId , String merId , BigDecimal amount , String tranNo ,long joinCourseId ,long iosPayTypeId, String remark,int courseType);

    Orders setBuyFlowOrder(String orderType , String payType , long appId , String merId , BigDecimal amount , String tranNo , String remark);
}
