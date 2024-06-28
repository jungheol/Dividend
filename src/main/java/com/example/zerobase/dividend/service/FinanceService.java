package com.example.zerobase.dividend.service;

import com.example.zerobase.dividend.model.Company;
import com.example.zerobase.dividend.model.Dividend;
import com.example.zerobase.dividend.model.ScrapResult;
import com.example.zerobase.dividend.persist.CompanyRepository;
import com.example.zerobase.dividend.persist.DividendRepository;
import com.example.zerobase.dividend.persist.entity.CompanyEntity;
import com.example.zerobase.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapResult getDividendByCompanyName(String companyName) {
        // 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 조회된 회사 Id로 배당금 정보 조회
        List<DividendEntity> dividendEntityList = this.dividendRepository.findAllByCompanyId(company.getId());

        // 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();
        for (var entity : dividendEntityList) {
            dividends.add(Dividend.builder()
                                    .date(entity.getDate())
                                    .dividend(entity.getDividend())
                                    .build());
        }

//        List<Dividend> dividends = dividendEntityList.stream()
//                                                        .map(e -> Dividend.builder()
//                                                                .date(e.getDate())
//                                                                .dividend(e.getDividend())
//                                                                .build())
//                                                        .collect(Collectors.toList());

        return new ScrapResult(Company.builder()
                                        .ticker(company.getTicker())
                                        .name(company.getName())
                                        .build(),
                                dividends);
    }
}
