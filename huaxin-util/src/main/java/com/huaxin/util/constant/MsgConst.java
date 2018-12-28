package com.huaxin.util.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

/**
 * Created by liuhan on 2017-02-23.
 */
@Component
public class MsgConst {
    private static final String split = "{}";

    private static String website;
    @Value("${website:}")
    public void setWebsite(String website) {
        MsgConst.NEW_COUSER_REMIND_URL = website + NEW_COUSER_REMIND_URL;
        MsgConst.website = website;
    }

    public static final String NEW_COUSER_REMIND_CONTENT = "恭喜您！开课成功，请于{}准时开课。";
    public static final String NEW_COUSER_REMIND_FOLLOW_USER_CONTENT = "你关注的{}老师直播间最近新开一门课程";
    public static String NEW_COUSER_REMIND_URL =  "/weixin/courseInfo?id={}";

    public static final String NEW_FOLLOW_REMIND_TEACHER_CONTENT = "你有新学员{}关注您，请在个人中心查看";
    public static final String NEW_PAY_REMIND_TEACHER_CONTENT = "你有新学员付费，请在我的收益查看";
    public static final String NEW_PAY_RELAY_TEACHER_CONTENT = "恭喜您！您的《{}》课程已被转播成功，请在我的收益内查看详细信息";
    public static final String RELAY_COUSER_REMIND_FOLLOW_USER_CONTENT = "您的《{}》课程已成功上架到转播市场，快去转播市场看看吧";
    public static final String RELAY_COURSE_SUCCESS = "您关注的{}老师已成功开通一节转播课，快去转播市场看看吧";
    public static final String FOLLOW_TEACHER_RELAY_COURSE_SUCCESS = "您关注的{}老师已成功转播一节课，快去看看吧";
    public static final String NEW_PAY_RELAY_TEACHER_HALF = "你有新的分成收入，请在我的收益查看";
    public static final String DIVIDE_INTO_ACCOUNT = "《{}》课程分成收益已到账，请在我的收益内查看详细信息";

    public static final String FOLLOW_REMIND_STUDENT_CONTENT = "你关注了{}老师";

    public static final String APPLY_ROOM_REMIND_STUDENT_CONTENT = "你于{}成功开通直播间";
    public static final String APPLY_ROOM_REMIND_STUDENT_CONTENT_RP = "你于{}重新申请开通直播间";

    public static final String PAY_REMIND_STUDENT_CONTENT= "成功购买《{}》课程，费用{}学币";
    public static final String RELAY_REMIND_TEACHER_CONTENT= "恭喜您！您的《{}》课程已经被转播成功，请在我的收益内查看详细信息";
    public static final String RELAY_REMIND_STUDENT_CONTENT= "恭喜您！您的《{}》课程已经转播成功，请在我的直播间内查看详细信息";
    public static final String RELAY_REMIND_STUDENT_CONTENT_YIELD= "有学员成功购买了《{}》课程，分成收入{}学币";
    public static final String INVITATION_REWARD_CONTENT= "成功分销了《{}》课程，奖励费用{}学币";

    public static final String SHARE_COURSE_REMIND_TEACHER_CONTENT= "你有新朋友分销了您的课程{}";

    public static final String SHARE_ROOM_REMIND_TEACHER_CONTENT= "你有新朋友分销了您的直播间";
    public static final String COURSE_COMMENT_REMIND= "您有新学员评价你的《{}》课程，请在课程详情中查看";
    public static final String TEACHER_COURSE_COMMENT_REMIND= "您在线直播《{}》课程，与{}开始";
    public static final String STUDENT_COURSE_COMMENT_REMIND= "您购买的{}《{}》课程{}开课，注意查收";

    public static final String YELLOW_COMMENT_REMIND= "您在直播《{}》课程期间出现违反法律的行为，该节课程已被冻结，如有疑问，请联系官方客服人员。";


    public static final String MONEY_RECHAGE_PAY_REMIND_CONTENT= "钱包充值成功，费用{}元";
    public static final String LEARD_COINPAY_RECHAGE_PAY_REMIND_CONTENT= "账户充值{}学币成功";
    public static final String TEACHER_WITHDRAW_RERUEN_PROXY_REMIND_CONTENT= "您的机构旗下{}老师在酸枣平台提现{}个枣币，您将获取其中的的{}%作为收益，我们已将{}个枣币发送至您的账户，请在个人中心“我的收益”中查收";
    public static final String BUY_FLOW_CONTENT= "您已成功充值价值{}元{}流量，有效期至{} ";

    /**
     * 替换model字符串中的{}符号
     * @param model
     * @param args
     * @return
     */
    public static String replace(String model , String ... args) {
        StringTokenizer st = new StringTokenizer(model , split);
        int b = st.countTokens();
        //解决{}在头部或者尾部时
        if (b != 1) {
            b -= 1;
        }
        StringBuffer sb = new StringBuffer();
        int i =  0;
        while (st.hasMoreElements()     ){
            sb.append(st.nextToken());
            if (i < args.length  && i < b  )
                sb.append(args[i]);
            i++;
        }
        return sb.toString();
    }

    public static void main(String args[]) {
//       System.out.println( replace(BUY_FLOW_CONTENT , "ddadafd","dafdasfdasf","dafdasf"));
    }
}
