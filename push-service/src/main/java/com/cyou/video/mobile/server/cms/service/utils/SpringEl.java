package com.cyou.video.mobile.server.cms.service.utils;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpringEl {

  public static Object getFieldValue(String field, Object obj) {
    try {
      ExpressionParser parser = new SpelExpressionParser();
      Expression exp = parser.parseExpression(field);
      return exp.getValue(obj);
    }
    catch(Exception e) {
      // TODO: handle exception
      return null;
    }
  }
  public static Object condition(String condition, Object obj) {
    ExpressionParser parser = new SpelExpressionParser();
    Expression exp = parser.parseExpression(condition);
    return exp.getValue(obj);
  }
}
