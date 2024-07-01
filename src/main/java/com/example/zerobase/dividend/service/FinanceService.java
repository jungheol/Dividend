package com.example.zerobase.dividend.service;

import com.example.zerobase.dividend.model.Company;
import com.example.zerobase.dividend.model.Dividend;
import com.example.zerobase.dividend.model.ScrapResult;
import com.example.zerobase.dividend.model.constants.CacheKey;
import com.example.zerobase.dividend.persist.CompanyRepository;
import com.example.zerobase.dividend.persist.DividendRepository;
import com.example.zerobase.dividend.persist.entity.CompanyEntity;
import com.example.zerobase.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 조회된 회사 Id로 배당금 정보 조회
        List<DividendEntity> dividendEntityList = this.dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = dividendEntityList.stream()
                                                        .map(e -> new Dividend(e.getDate(), e.getDividend()))
                                                        .collect(Collectors.toList());

        return new ScrapResult(new Company(company.getTicker(), company.getName()),
                                dividends);
    }
}
