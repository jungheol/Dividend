package com.example.zerobase.dividend.scheduler;

import com.example.zerobase.dividend.model.Company;
import com.example.zerobase.dividend.model.ScrapResult;
import com.example.zerobase.dividend.persist.CompanyRepository;
import com.example.zerobase.dividend.persist.DividendRepository;
import com.example.zerobase.dividend.persist.entity.CompanyEntity;
import com.example.zerobase.dividend.persist.entity.DividendEntity;
import com.example.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    // 일정 주기마다 수행
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        // 저장된 회사 목록 조회
        List< CompanyEntity> companyEntityList = this.companyRepository.findAll();
        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companyEntityList) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapResult scrapResult = this.yahooFinanceScraper.scrap(Company.builder()
                                                                            .name(company.getName())
                                                                            .ticker(company.getTicker())
                                                                            .build());

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값을 저장
            scrapResult.getDividendList().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 존재하지 않는 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists){
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
