package com.longlian.live.util.yunxin;

import com.longlian.live.util.expressionJ.EquationUtil;
import com.longlian.live.util.expressionJ.Stat;
import org.da.expressionj.expr.parser.EquationParser;
import org.da.expressionj.expr.parser.ParseException;
import org.da.expressionj.model.Equation;
import org.da.expressionj.model.Expression;
import org.da.expressionj.util.ExpressionCombiner;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 */
public class ExpressionTest {
    private EquationUtil equationUtil;

    @Before
    public void before() throws InstantiationException, IllegalAccessException {
        equationUtil = new EquationUtil();
        equationUtil.register(Stat.class);
    }

    @Test
    public void f() throws ParseException {

        // 从配置中直接读取
        String expr = "sum(a)";

        // 从通过配置动态获取
        Map<String, Object> o = new HashMap<String, Object>();
        o.put("a", new double[]{1, 2, 3, 4, 5});

        Object result = equationUtil.evaluate(expr, o);
        System.out.println(result);

    }

    @Test
    public void t() throws ParseException {
        String expr = "a + 1";
        Map<String, Object> o =new HashMap<String, Object>();
        o.put("a", 2);
        Object result = equationUtil.evaluate(expr, o);
        System.out.println(result);
    }
    @Test
    public void m() throws ParseException, ScriptException {
        //String expr = "a>4 || b>9 ";
        String expr = "courseAmout>=0 && coursePayMenCount>=100 && cycTime==3";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.put("courseAmout",1);
        engine.put("coursePayMenCount",5);
        engine.put("cycTime",10);
        engine.put("appointedDate","2017-03-09");
        Object result = engine.eval(expr);
        System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);

    }

    @Test
    public void r() throws ParseException {

        // 多个表达式中的变量名不可重复

        Equation condition = EquationParser.parse("sin(a)");

        Equation condition2 = EquationParser.parse("b + c");
        Expression expr = condition2.getExpression();

        ExpressionCombiner combiner = new ExpressionCombiner();
        Expression exprResult = combiner.replace(condition, "a", expr);

        exprResult.getVariable("b").setValue(1);
        exprResult.getVariable("c").setValue(2);

        System.out.println(condition.eval());
    }


}
