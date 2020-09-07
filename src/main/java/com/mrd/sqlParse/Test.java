package com.mrd.sqlParse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class Test {
    static Formatter formatter = new Formatter(System.out);

    public static void main(String[] args) throws JsonProcessingException {
        Sql4Json sql4Json = new Sql4Json();
        ObjectMapper objectMapper = new ObjectMapper();
        TestData testData = new TestData();
        testData.setA(111);
        testData.setD("hello json sql");
        testData.setB(new TestData1(3.14));
        testData.setE(new TestData(123, "456"));
        String jsonString = objectMapper.writeValueAsString(testData);
        System.out.println("===========================================start=================================================");
        System.out.println("json string: " + jsonString);
        Map<String, Boolean> data = generateTestData(jsonString);
        data.forEach((sql, value) -> {
            try {
                boolean b = sql4Json.hasResult(sql, jsonString);
                System.out.println("————————————————————————————————————————————————————————————————————————————————————");
                formatter.format("%-50s %-20s %-20s\n", "sql:" + sql, "hasResult: " + b, "correct:" + value.equals(b));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        System.out.println("===========================================end=================================================");
    }

    private static Map<String, Boolean> generateTestData(String jsonString) throws JsonProcessingException {
        Map<String, Boolean> map = new HashMap<>();
        String sql0 = "a = 111";
        String sql1 = "a = 111 and d like \"%hello\"";
        String sql2 = "a = 111 and d like \"hello%\"";
        String sql3 = "a = 1 and d like \"hello%\"";
        String sql4 = "a > 1 and d like \"%hello%\"";
        String sql5 = "a >=111 and d = \"hello json sql\" ";
        String sql6 = "a < 120 and b.c > 3";
        String sql7 = "e.a = 123";
        String sql8 = "a = 1 or d like \"hello%\"";
        String sql9 = "a = 111 and d like \"hello%\" or b.c <= 1";
        map.put(sql0, true);
        map.put(sql1, false);
        map.put(sql2, true);
        map.put(sql3, false);
        map.put(sql4, true);
        map.put(sql5, true);
        map.put(sql6, true);
        map.put(sql7, true);
        map.put(sql8, true);
        map.put(sql9, true);
        return map;
    }
}
