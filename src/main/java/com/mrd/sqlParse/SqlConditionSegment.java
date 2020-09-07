package com.mrd.sqlParse;

import lombok.Data;

@Data
public class SqlConditionSegment {
    private String key;
    private String value;
    private String operate;
    private String preConnector;
    private SqlConditionSegment nextCondition;
}
