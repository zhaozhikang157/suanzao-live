package com.huaxin.util.bank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class BankCardNoCheckUtil {
	
	/**
	 * 判断银行账号是否合规，目前规则 ：银行账号 非0开头的12-10位数字
	 * @param cardNo
	 * @return
	 */
	public static boolean checkCardNo(String cardNo) {
		if(StringUtils.isBlank(cardNo)) return false ;
    	// 银行账号 非0开头的12-10位数字
    	cardNo = cardNo.replaceAll("\\s*", ""); 
		Pattern p = Pattern.compile("^[1-9]\\d{11,19}$"); 
		Matcher m = p.matcher(cardNo); 
		boolean res = m.matches(); 
		return res;
	}

}
