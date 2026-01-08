package com.example.KpiDemo.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.KpiDemo.dto.KpiDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class KpiService {

    private RestTemplate restTemplate = new RestTemplate();

    //인증키 부분은 알아서 작성. application.properties.example 참조
    @Value("${kpi.cert-key}")
    private String certKey;

    @Value("${kpi.url-lv2}")
    private String urlLv2;

    @Value("${kpi.url-lv3}")
    private String urlLv3;

    // 증가율 계산
    public double calculateRate(int beVal, int nowVal) {
        return ((double)(nowVal - beVal) / beVal) * 100;
    }

    // 메인 전송
    public void sendKpi(KpiDto kpiDto) {
        System.out.println("CERT_KEY: " + certKey);

        LocalDateTime now = LocalDateTime.now();
        String trsDttm = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String ocrDttm = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 증가율 계산
        double actualRate = calculateRate(kpiDto.getBe_val(), kpiDto.getNow_val());

        HttpHeaders headers = JsonHeader();

        // Lv2 데이터 생성
        JSONObject lv2 = buildLv2(
                ocrDttm,
                trsDttm,
                actualRate,
                kpiDto.getTarget_rate()
        );

        post(urlLv2, lv2, headers);

        // Lv3 데이터 생성
        JSONObject lv3 = buildLv3(
                ocrDttm,
                trsDttm,
                kpiDto.getNow_val()
        );

        post(urlLv3, lv3, headers);
    }

    // Lv2 (증가율)
    private JSONObject buildLv2(String ocrDttm, String trsDttm, double actualRate, double targetRate) {
        JSONObject param = new JSONObject()
                .put("kpiCertKey", certKey)
                .put("ocrDttm", ocrDttm)
                .put("kpiFldCd", "P")
                .put("kpiDtlCd", "B")
                .put("kpiDtlNm", "일 생산량 증가율")
                .put("achrt", Double.toString(actualRate))
                .put("targetAchrt", targetRate)
                .put("trsDttm", trsDttm);

        return new JSONObject()
                .put("KPILEVEL2", new JSONArray().put(param));
    }

    // Lv3 (실제 생산량)
    private JSONObject buildLv3(String ocrDttm, String trsDttm, int production) {
        JSONObject param = new JSONObject()
                .put("kpiCertKey", certKey)
                .put("ocrDttm", ocrDttm)
                .put("kpiFldCd", "P")
                .put("kpiDtlCd", "B")
                .put("kpiDtlNm", "일 생산량")
                .put("msmtVl", production)
                .put("unt", "EA")
                .put("trsDttm", trsDttm);

        return new JSONObject().put("KPILEVEL3", new JSONArray().put(param));
    }

    // HTTP 헤더 생성
    public HttpHeaders JsonHeader() {
        HttpHeaders headers = new HttpHeaders();

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);

        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    // HTTP POST 전송
    private void post(String url, JSONObject body, HttpHeaders headers) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("POST: " + url);
            System.out.println("Response => " + response.getBody());
        } catch (Exception e) {
            System.out.println("POST 실패: " + url);
            System.out.println("에러: " + e.getMessage());
        }
    }
}