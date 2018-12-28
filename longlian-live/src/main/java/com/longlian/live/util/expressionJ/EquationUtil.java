package com.longlian.live.util.expressionJ;

import org.da.expressionj.expr.parser.EquationParser;
import org.da.expressionj.expr.parser.ParseException;
import org.da.expressionj.functions.FunctionsDefinitions;
import org.da.expressionj.model.Equation;
import org.da.expressionj.model.Variable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 */
public class EquationUtil {

    private final Map<String, Equation> equations;
    private static FunctionsDefinitions def;

    public EquationUtil() {
        equations = new HashMap<String , Equation>();
        def = FunctionsDefinitions.getInstance();
    }

    /**
     * 注册函数
     */
    public void register(Class<? extends Function> clz) throws IllegalAccessException, InstantiationException {
        Function func = clz.newInstance();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            def.addFunction(method.getName(), func, method);
        }
    }

    /**
     * 释放注册的函数
     */
    public static void reset() {
        def.reset();
    }

    /**
     * 根据表达式语句获得方程式对象
     */
    public Equation getEquation(String expr) throws ParseException {
        Equation equation = equations.get(expr);
        if (equation == null) {
            equation = EquationParser.parse(expr);
            equations.put(expr, equation);
        }
        return equation;
    }

    /**
     * @param expr 表达式语句
     * @param o    参与运算的键值对列表,键必须与表达式中参数名相同
     */
    public Object evaluate(String expr, Map<String, Object> o) throws ParseException {
        Equation equation = getEquation(expr);
        Map<String, Variable> vars = equation.getVariables();
        for (String key : vars.keySet()) {
            vars.get(key).setValue(o.get(key));
        }
        return equation.eval();
    }

}
