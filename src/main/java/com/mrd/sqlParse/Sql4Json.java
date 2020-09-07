package com.mrd.sqlParse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Locale;

public class Sql4Json {
    public boolean hasResult(String sql, String jsonString) throws JsonProcessingException {
        Boolean result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        SqlConditionSegment sqlConditionSegment = SqlUtil.parseSqlCondition(sql);
        while (sqlConditionSegment != null) {
            String key = sqlConditionSegment.getKey();
            String value = sqlConditionSegment.getValue();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            String operate = sqlConditionSegment.getOperate();
            String preConnector = sqlConditionSegment.getPreConnector();
            JsonNode temp = jsonNode;
            if (jsonNode.has(key)) {
                temp = temp.get(key);
            } else {
                // 说明是嵌套的
                String[] split = key.split("\\.");
                for (String k : split) {
                    if (temp.has(k)) {
                        temp = temp.get(k);
                    } else {
                        throw new IllegalArgumentException(String.format(Locale.ENGLISH, "sql condition error: invalid %s", k));
                    }
                }
            }

            if (operate.equals(SqlUtil.OPERATE_EQ)) {
                result = dealEqualsResult(result, value, operate, preConnector, temp);
            } else if (operate.equals(SqlUtil.OPERATE_LIKE)) {
                result = dealStringLikeResult(result, value, operate, preConnector, temp);
            } else if (temp.getNodeType() == JsonNodeType.NUMBER) {
                result = dealNumberResult(result, value, operate, preConnector, temp);
            }
            sqlConditionSegment = sqlConditionSegment.getNextCondition();
        }
        return result == null ? false : result;
    }

    /**
     * 处理等于的条件
     *
     * @param result
     * @param value
     * @param operate
     * @param preConnector
     * @param temp
     * @return
     */
    private Boolean dealEqualsResult(Boolean result, String value, String operate, String preConnector, JsonNode temp) {
        boolean equals = false;
        switch (temp.getNodeType()) {
            case NUMBER:
                equals = Double.parseDouble(value) == (temp.doubleValue());
                break;
            case STRING:
                equals = temp.asText().equals(value);
                break;
            case BOOLEAN:
                equals = Boolean.valueOf(value).equals(temp.asBoolean());
        }
        result = getResult(result, operate, preConnector, equals);
        return result;
    }

    /**
     * 处理like的条件
     *
     * @param result
     * @param value
     * @param operate
     * @param preConnector
     * @param temp
     * @return
     */
    private Boolean dealStringLikeResult(Boolean result, String value, String operate, String preConnector, JsonNode temp) {
        String jsonValue = temp.asText();
        int i = value.indexOf(SqlUtil.OPERATE_WILDCARD);
        int j = value.lastIndexOf(SqlUtil.OPERATE_WILDCARD);
        if (i == j) {
            if (i == 0) {
                result = getResult(result, operate, preConnector, jsonValue.endsWith(value.substring(i + 1)));
            } else if (i == value.length() - 1) {
                result = getResult(result, operate, preConnector, jsonValue.startsWith(value.substring(0, i)));
            }
        } else if (j - i == value.length() - 1) {
            result = getResult(result, operate, preConnector, jsonValue.contains(value.substring(i + 1, j)));
        } else {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "sql condition error: invalid condition %s", value));
        }
        return result;
    }

    /**
     * 比较，>,<,>=,<=,目前只作用于数字，日期范围暂未考虑
     *
     * @param result
     * @param value
     * @param operate
     * @param preConnector
     * @param temp
     * @return
     */
    private Boolean dealNumberResult(Boolean result, String value, String operate, String preConnector, JsonNode temp) {
        switch (operate) {
            case SqlUtil.OPERATE_GT:
                result = getResult(result, operate, preConnector, Double.parseDouble(value) < (temp.doubleValue()));
                break;
            case SqlUtil.OPERATE_LT:
                result = getResult(result, operate, preConnector, Double.parseDouble(value) > (temp.doubleValue()));
                break;
            case SqlUtil.OPERATE_GTE:
                result = getResult(result, operate, preConnector, Double.parseDouble(value) <= (temp.doubleValue()));
                break;
            case SqlUtil.OPERATE_LTE:
                result = getResult(result, operate, preConnector, Double.parseDouble(value) >= (temp.doubleValue()));
                break;
        }
        return result;
    }

    private Boolean getResult(Boolean result, String operate, String preConnector, boolean equals) {
        if (preConnector == null) {
            result = equals;
        } else if (SqlUtil.CONDITION_AND.equals(preConnector)) {
            result = result == null ? equals : result && equals;
        } else if (SqlUtil.CONDITION_OR.equals(preConnector)) {
            result = result == null ? equals : result || equals;
        } else {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "sql condition error: invalid %s", operate));
        }
        return result;
    }

}
