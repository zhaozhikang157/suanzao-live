package com.huaxin.util.constant;

import java.math.BigDecimal;

public class Const {
	public  static  final String emp_init_password = "123456";//员工初始密码

	public static  final BigDecimal bank_out_hign_money_default = BigDecimal.valueOf(5000.00);//保证金默认提现最高金额
	
	public  static  final String app_user_init_password = "9493cda01ee388f02f750e65ebba95ab9493cda01e";//会员初始密码

	public static final long shou_zhi_account_shop_id = -6666;//收支账号SHOP_ID

	public static final long zi_jin_chi_account_shop_id = -8888;//资金池账号SHOP_ID

	public static final String bank_out_defaul_remark = "保证金待付提现";//提现备注


	public static final long ll369_shou_zhi_account_id = -6666;//龙链369收支账号ID

	public static final long ll360_zi_jin_chi_account_id = -8888;//龙链369资金池账号ID
	//public static final long ll360_zi_jin_chi_account_id = 12;

	public final static String ll369_app_home_last_update_date = "2016-09-06 12:00:00";	//APP端首页最后一次更新日期

	public final static int ll369_vip_days = 365 ;//会员天数
	public final static int ll369_vip_follow_wechat_delay_days = 30 ;//会员关注微信号奖励VIP天数
	public final static int ll369_vip_share_moment_delay_days = 1 ;//会员分享朋友圈奖励VIP天数
	public static final String ll369_bank_out_defaul_remark = "龙链钱包提现";//提现备注


	//龙链教育
	public static final long longlian_live_zi_jin_chi_account_id = -8888;//龙链视频直播资金池账号ID
	//public static final long longlian_live_zi_jin_chi_account_id = 12;//龙链视频直播资金池账号ID
	public static final String  longlian_live_bank_out_defaul_remark = "龙链酸枣在线钱包提现";//提现备注
}
