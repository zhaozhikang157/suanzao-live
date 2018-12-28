package com.longlian.live.service;

import com.longlian.dto.ActResultDto;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created by wtc on 2016/6/6.
 */
public interface AlipayService {

    /**
     * 支付宝回调
     * @param request
     * @return
     * @throws Exception
     */
    ActResultDto alipayAppCalBack(HttpServletRequest request)throws Exception;

    ActResultDto getVIPAliPay(String orderNumber, BigDecimal amount)throws  Exception;
}
