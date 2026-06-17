package com.study.tour.controller;

import com.study.tour.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF生成", description = "PDF相关接口")
@CrossOrigin(origins = "*")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/agreement/{orderId}")
    @Operation(summary = "生成电子协议PDF")
    public ResponseEntity<byte[]> generateAgreement(@PathVariable Long orderId) {
        ByteArrayOutputStream baos = pdfService.generateAgreement(orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "agreement_" + orderId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }

    @GetMapping("/insurance/{orderId}")
    @Operation(summary = "生成保险凭证PDF")
    public ResponseEntity<byte[]> generateInsuranceCertificate(@PathVariable Long orderId) {
        ByteArrayOutputStream baos = pdfService.generateInsuranceCertificate(orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "insurance_" + orderId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }

    @GetMapping("/insurance-badge/{orderId}")
    @Operation(summary = "生成保险徽章PDF")
    public ResponseEntity<byte[]> generateInsuranceBadge(@PathVariable Long orderId) {
        ByteArrayOutputStream baos = pdfService.generateInsuranceBadge(orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }
}
