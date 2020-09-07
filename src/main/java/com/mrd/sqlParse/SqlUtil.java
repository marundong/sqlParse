package com.mrd.sqlParse;

import java.util.Locale;
import java.util.Set;

public class SqlUtil {
    public static final String CONDITION_AND = " and ";
    public static final String CONDITION_OR = " or ";
    public static final int CONNECTOR_AND = 1;
    public static final int CONNECTOR_OR = 0;
    public static final String OPERATE_EQ = "=";
    public static final String OPERATE_GT = ">";
    public static final String OPERATE_GTE = ">=";
    public static final String OPERATE_LT = "<";
    public static final String OPERATE_LTE = "<=";
    public static final String OPERATE_LIKE = " like ";
    public static final String OPERATE_WILDCARD = "%";
    public static final String DOT = ".";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";

    static final String[] OPERATES = {
            OPERATE_EQ,
            OPERATE_GT,
            OPERATE_LT,
            OPERATE_LIKE,
            OPERATE_WILDCARD};
    private static final String[] SEPARATES = {CONDITION_AND, CONDITION_OR};

    /**
     * 不考虑“()”连接条件的情况，也不考虑字符串条件中有AND OR 的情况
     *
     * @param sql
     * @return SqlConditionSegment
     */
    public static SqlConditionSegment parseSqlCondition(String sql) {
        SubstringData andOrCondition = getAndOrCondition(sql);
        if (andOrCondition == null) {
            // 沒有and，or连接条件
            SqlConditionSegment operateSegment = getOperateSegment(sql);
            operateSegment.setNextCondition(null);
            operateSegment.setPreConnector(null);
            return operateSegment;
        }
        String leftCondition = andOrCondition.getLeft();
        SqlConditionSegment operateSegment = parseSqlCondition(leftCondition);

        SqlConditionSegment rightCondition = parseSqlCondition(andOrCondition.getRight());
        rightCondition.setPreConnector(andOrCondition.getSeparator());
        operateSegment.setNextCondition(rightCondition);

        return operateSegment;
    }

    private static SqlConditionSegment getOperateSegment(String sql) {
        SqlConditionSegment sqlConditionSegment = new SqlConditionSegment();
        SubstringData operateCondition = getOperateCondition(sql);
        sqlConditionSegment.setOperate(operateCondition.getSeparator());
        sqlConditionSegment.setKey(operateCondition.getLeft().trim());
        sqlConditionSegment.setValue(operateCondition.getRight().trim());
        return sqlConditionSegment;
    }

    public static void checkField(Set<String> dataKeys, String field) {
        if (!field.contains(DOT) && !dataKeys.contains(field)) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "sql field %s is invalid", field));
        }
    }

    private static SubstringData getAndOrCondition(String str) {
        return getSubstringDataTrim(str, SEPARATES);
    }

    private static SubstringData getOperateCondition(String str) {
        SubstringData gte = getSubstringDataTrim(str, OPERATE_GTE);
        if (gte != null) {
            return gte;
        } else {
            SubstringData lte = getSubstringDataTrim(str, OPERATE_LTE);
            if (lte != null) {
                return lte;
            }
        }
        return getSubstringDataTrim(str, OPERATES);
    }

    private static SubstringData getSubstringDataTrim(String str, String... separates) {

        for (String separate : separates) {
            String trimSql = str.trim();
            int i = trimSql.indexOf(separate);
            if (i > 0) {
                String left = trimSql.substring(0, i);
                String right = trimSql.substring(i + separate.length());
                return new SubstringData(left, separate, right);
            }
        }
        return null;
    }
}
