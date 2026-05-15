package com.example.gymerp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.example.gymerp.dto.EmpDto;
import com.example.gymerp.dto.ProductDto;
import com.example.gymerp.dto.ServiceDto;
import com.example.gymerp.service.ModalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ModalController {

    private final ModalService modalService;

    /* =========================================================  [서비스 상품 선택 모달]   ========================================================= */
    
    
    @GetMapping("/modals/services")
    public Map<String, Object> getServiceModalList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) List<String> categoryCodes // 🔹 카테고리 코드 필터 추가
    ) {
        // DTO 생성 및 파라미터 세팅
        ServiceDto dto = new ServiceDto();
        dto.setKeyword(keyword);
        dto.setCategoryCodes(categoryCodes);

        // 페이징 계산 (MySQL)
        int offset = (page - 1) * limit;  // OFFSET
        int size = limit;                  // LIMIT 개수
        dto.setStartRowNum(offset);
        dto.setEndRowNum(size);

        // Service 호출
        List<ServiceDto> list = modalService.getServiceModalList(dto);
        int totalCount = modalService.getServiceModalCount(dto);

        // 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("limit", limit);

        return result;
    }

    /* =========================================================  [서비스 상품 선택 모달 끝]   ========================================================= */


    /* =========================================================  [실물 상품 선택 모달]   ========================================================= */
    
    
    @GetMapping("/modals/products")
    public Map<String, Object> getProductModalList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        // 1. Service를 통해 실물 상품 목록과 전체 개수 조회
        List<ProductDto> list = modalService.getProductModalList(keyword, page, limit);
        int totalCount = modalService.getProductModalCount(keyword);

        // 2. 결과를 Map에 담아 반환
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("limit", limit);

        return result;
    }

    /* =========================================================  [실물 상품 선택 모달 끝]   ========================================================= */


    /* =========================================================  [직원 선택 모달]   ========================================================= */
    
    @GetMapping("/modals/employees")
    public Map<String, Object> getEmployeeModalList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        List<EmpDto> list = modalService.getEmployeeModalList(keyword, page, limit);
        int totalCount = modalService.getEmployeeModalCount(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("limit", limit);

        return result;
    }

    /* =========================================================  [직원 선택 모달 끝]   ========================================================= */

}