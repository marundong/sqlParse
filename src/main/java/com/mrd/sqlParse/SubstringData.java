package com.mrd.sqlParse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubstringData {
    private String left;
    private String separator;
    private String right;

    public SubstringData(String left, String separator, String right) {
        this.left = left;
        this.separator = separator;
        this.right = right;
    }
}
