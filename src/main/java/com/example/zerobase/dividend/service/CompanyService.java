package com.example.zerobase.dividend.service;

import com.example.zerobase.dividend.model.Company;
import com.example.zerobase.dividend.model.ScrapResult;
import com.example.zerobase.dividend.persist.CompanyRepository;
import com.example.zerobase.dividend.persist.DividendRepository;
import com.example.zerobase.dividend.persist.entity.CompanyEntity;
import com.example.zerobase.dividend.persist.entity.DividendEntity;
import com.example.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;
    private final CompanyRepository companyRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapResult scrapResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapResult.getDividendList().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .toList();
        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }
}
