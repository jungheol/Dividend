package com.example.zerobase.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapResult {

    private Company company;

    private List<Dividend> dividendList;

    public ScrapResult() {
        this.dividendList = new ArrayList<>();
    }

}
