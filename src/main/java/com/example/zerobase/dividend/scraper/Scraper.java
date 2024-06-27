package com.example.zerobase.dividend.scraper;

import com.example.zerobase.dividend.model.Company;
import com.example.zerobase.dividend.model.ScrapResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapResult scrap(Company company);
}
