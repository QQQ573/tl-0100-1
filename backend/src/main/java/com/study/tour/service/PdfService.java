package com.study.tour.service;

import java.io.ByteArrayOutputStream;

public interface PdfService {
    ByteArrayOutputStream generateAgreement(Long orderId);
    ByteArrayOutputStream generateInsuranceCertificate(Long orderId);
    ByteArrayOutputStream generateInsuranceBadge(Long orderId);
}
