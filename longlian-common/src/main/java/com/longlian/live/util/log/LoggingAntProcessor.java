package com.longlian.live.util.log;

import com.huaxin.regexp.ELExpressionParser;
import com.huaxin.regexp.ExpFetcher;
import com.huaxin.regexp.RegexpAnalyser;
import com.huaxin.util.StringUtil;

import java.lang.reflect.Method;

/**
 * syl
 */
public class LoggingAntProcessor {
	
	/**
	 * 渲染日志模板
	 * @return
	 */
	public String renderTemplate(final LogRequestInfo requestInfo,Method method,final Object arguments[],final Object returned) {
		String rendered = null;
		Log annotation = method.getAnnotation(Log.class);
		if(annotation!=null){
			String template = annotation.content();
			RegexpAnalyser analyser = new RegexpAnalyser();
			analyser.setText(template);
			rendered = analyser.replace(new String[]{"\\{[a-zA-Z0-9\\._#$]+\\}"}, new ExpFetcher(){
				@Override
				public String parse(String pattern) {
					// TODO Auto-generated method stub
					return renderByVariables(pattern,requestInfo,arguments,returned);
				}
				
			});
		}
		return rendered;
	}
	
	/**
	 * 根据变量渲染
	 * @param pattern
	 * @return
	 */
	public static String renderByVariables(String pattern,LogRequestInfo requestInfo,Object arguments[],Object returned){
		String patternPlain = pattern.substring(1,pattern.length()-1);
		String renderedField = null;
		ELExpressionParser el = new ELExpressionParser();
		Object contextObject = requestInfo;
		String sp[] = patternPlain.split("\\.");
		if(sp[0].startsWith("$")){//$1为第一个参数，$2为第二个，以此类推
			patternPlain = "";
			int index = Integer.parseInt(sp[0].substring(1,sp[0].length()));
			contextObject = arguments[index-1];
		}else if(sp[0].startsWith("#")){//#为返回值
			patternPlain = "";
			contextObject = returned;
		}
		if("".equals(patternPlain)){
			for(int i=1;i<sp.length;i++){
				patternPlain+=sp[i];
				if(i!=sp.length-1){
					patternPlain+=".";
				}
			}
		}
		renderedField = StringUtil.getString(el.evaluate(contextObject, patternPlain));
		return renderedField;
	}
	
}
