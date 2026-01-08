package com.example.KpiDemo.controller;

import com.example.KpiDemo.dto.KpiDto;
import com.example.KpiDemo.dto.KpiResponseDto;
import com.example.KpiDemo.service.KpiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @PostMapping("/kpi")
    public ResponseEntity<KpiResponseDto> sendKpi(@RequestBody KpiDto kpiDto) {

        kpiService.sendKpi(kpiDto);

        KpiResponseDto responseDto = new KpiResponseDto(
                kpiDto.getBe_val(),
                kpiDto.getNow_val(),
                kpiDto.getTarget_rate(),
                0.0,
                true,
                "전송 완료"
        );

        return ResponseEntity.ok(responseDto);
    }
}