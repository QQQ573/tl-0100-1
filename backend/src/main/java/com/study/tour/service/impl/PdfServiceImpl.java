package com.study.tour.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.study.tour.entity.Order;
import com.study.tour.entity.OrderItem;
import com.study.tour.entity.Student;
import com.study.tour.repository.OrderItemRepository;
import com.study.tour.repository.OrderRepository;
import com.study.tour.repository.StudentRepository;
import com.study.tour.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StudentRepository studentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

    @Override
    public ByteArrayOutputStream generateAgreement(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        Student student = studentRepository.findById(order.getStudentId()).orElse(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc, PageSize.A4);

            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
            document.setFont(font);

            Paragraph title = new Paragraph("暑期研学出行电子协议")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            Paragraph subTitle = new Paragraph("协议编号: " + order.getOutTradeNo())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(15);
            document.add(subTitle);

            document.add(new Paragraph("甲方（学员及监护人）：").setBold().setFontSize(12));
            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.addCell(new Cell().add(new Paragraph("学员姓名").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(student != null ? student.getName() : "").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("学员身份证号").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(maskIdCard(student != null ? student.getIdCard() : "")).setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("监护人姓名").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(order.getGuardianName() != null ? order.getGuardianName() : "").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("监护人电话").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(order.getGuardianPhone() != null ? order.getGuardianPhone() : "").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("开营日期").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(order.getCampStartDate() != null ? order.getCampStartDate().format(DATE_FORMATTER) : "").setFontSize(10)));
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("乙方（研学机构）：").setBold().setFontSize(12));
            document.add(new Paragraph("机构名称：暑期研学教育科技有限公司").setFontSize(10));
            document.add(new Paragraph("联系电话：400-888-8888").setFontSize(10));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("一、服务内容").setBold().setFontSize(12).setMarginTop(10));
            Table productTable = new Table(3);
            productTable.setWidth(UnitValue.createPercentValue(100));
            productTable.addCell(new Cell().add(new Paragraph("服务项目").setFontSize(10).setBold()));
            productTable.addCell(new Cell().add(new Paragraph("单价").setFontSize(10).setBold()));
            productTable.addCell(new Cell().add(new Paragraph("数量").setFontSize(10).setBold()));
            for (OrderItem item : items) {
                productTable.addCell(new Cell().add(new Paragraph(item.getProductName()).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph("¥" + item.getPrice()).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(item.getQuantity().toString()).setFontSize(10)));
            }
            document.add(productTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("二、费用说明").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("本协议总费用：人民币 ¥" + order.getTotalAmount() + " 元（大写：" + 
                    convertToChineseAmount(order.getTotalAmount().doubleValue()) + "）").setFontSize(10));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("三、双方权利与义务").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("1. 甲方应按时支付相关费用，并确保所提供信息真实有效。").setFontSize(10));
            document.add(new Paragraph("2. 乙方应按照协议约定提供研学服务及相关保障。").setFontSize(10));
            document.add(new Paragraph("3. 甲方如需退订，需在开营前提出，且无私募补差在途。").setFontSize(10));
            document.add(new Paragraph("4. 乙方应为甲方学员购买出行保险，保障学员出行安全。").setFontSize(10));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("四、其他约定").setBold().setFontSize(12).setMarginTop(10));
            document.add(new Paragraph("1. 本协议自双方签字（电子签）之日起生效。").setFontSize(10));
            document.add(new Paragraph("2. 本协议一式两份，甲乙双方各执一份，具有同等法律效力。").setFontSize(10));
            document.add(new Paragraph("3. 因本协议产生的争议，双方应友好协商解决。").setFontSize(10));
            document.add(new Paragraph("\n\n"));

            Table signTable = new Table(2);
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(new Cell().add(new Paragraph("甲方签字（电子）：").setFontSize(10)));
            signTable.addCell(new Cell().add(new Paragraph("乙方签字（电子）：").setFontSize(10)));
            signTable.addCell(new Cell().add(new Paragraph("日期：" + (order.getPaidAt() != null ? order.getPaidAt().format(DATE_FORMATTER) : "")).setFontSize(10)));
            signTable.addCell(new Cell().add(new Paragraph("日期：" + (order.getPaidAt() != null ? order.getPaidAt().format(DATE_FORMATTER) : "")).setFontSize(10)));
            document.add(signTable);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("生成协议PDF失败: " + e.getMessage(), e);
        }
        return baos;
    }

    @Override
    public ByteArrayOutputStream generateInsuranceCertificate(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        Student student = studentRepository.findById(order.getStudentId()).orElse(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc, PageSize.A4);

            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
            document.setFont(font);

            Paragraph title = new Paragraph("暑期研学出行保险凭证")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            Paragraph certNo = new Paragraph("保单号: INS" + order.getOutTradeNo())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(certNo);

            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(createHeaderCell("被保险人信息"));
            infoTable.addCell(createHeaderCell(""));

            infoTable.addCell(new Cell().add(new Paragraph("被保险人姓名").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(student != null ? student.getName() : "").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("身份证号").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(maskIdCard(student != null ? student.getIdCard() : "")).setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("联系电话").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(order.getGuardianPhone() != null ? order.getGuardianPhone() : "").setFontSize(10)));

            infoTable.addCell(createHeaderCell("保险信息"));
            infoTable.addCell(createHeaderCell(""));

            infoTable.addCell(new Cell().add(new Paragraph("保险产品").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("暑期研学出行综合险").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("保险金额").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("人民币 50 万元").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("保险期间").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(
                    order.getCampStartDate() != null ? order.getCampStartDate().format(DATE_FORMATTER) : "" 
                    + " 至 " + 
                    (order.getCampStartDate() != null ? order.getCampStartDate().plusDays(7).format(DATE_FORMATTER) : "")
            ).setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("生效日期").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph(
                    order.getPaidAt() != null ? order.getPaidAt().format(DATE_FORMATTER) : ""
            ).setFontSize(10)));

            infoTable.addCell(createHeaderCell("保障范围"));
            infoTable.addCell(createHeaderCell(""));

            infoTable.addCell(new Cell().add(new Paragraph("意外身故/伤残").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("50万元").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("意外医疗费用").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("5万元").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("紧急救援服务").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("10万元").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("行程取消损失").setFontSize(10)));
            infoTable.addCell(new Cell().add(new Paragraph("5000元").setFontSize(10)));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("特别说明：")
                    .setBold()
                    .setFontSize(10)
                    .setMarginTop(10));
            document.add(new Paragraph("1. 本保险凭证仅作为投保成功的证明，具体保险责任以保险合同条款为准。").setFontSize(9));
            document.add(new Paragraph("2. 保险期间从开营日零时起至研学活动结束日二十四时止。").setFontSize(9));
            document.add(new Paragraph("3. 出险后请及时拨打客服热线 95500 报案。").setFontSize(9));
            document.add(new Paragraph("\n"));

            Paragraph footer = new Paragraph("承保公司：暑期研学保险有限公司")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(30);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("生成保险凭证PDF失败: " + e.getMessage(), e);
        }
        return baos;
    }

    @Override
    public ByteArrayOutputStream generateInsuranceBadge(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        Student student = studentRepository.findById(order.getStudentId()).orElse(null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc, new PageSize(300, 200));

            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
            document.setFont(font);

            Paragraph badgeTitle = new Paragraph("✓ 已保障")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 153, 76))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(badgeTitle);

            Paragraph name = new Paragraph(student != null ? student.getName() : "同学")
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(name);

            Paragraph desc = new Paragraph("暑期研学出行险")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5);
            document.add(desc);

            Paragraph amount = new Paragraph("保额 ¥500,000")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(new DeviceRgb(204, 0, 0))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(amount);

            Paragraph certNo = new Paragraph("保单号: INS" + order.getOutTradeNo())
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(15)
                    .setFontColor(ColorConstants.GRAY);
            document.add(certNo);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("生成保险徽章失败: " + e.getMessage(), e);
        }
        return baos;
    }

    private Cell createHeaderCell(String text) {
        Cell cell = new Cell(1, 2);
        cell.setBackgroundColor(new DeviceRgb(240, 248, 255));
        cell.add(new Paragraph(text).setFontSize(11).setBold().setFontColor(new DeviceRgb(0, 102, 204)));
        return cell;
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    private String convertToChineseAmount(double amount) {
        String[] units = {"", "拾", "佰", "仟", "万", "拾万", "佰万", "仟万", "亿"};
        String[] digits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        
        long num = (long) Math.round(amount * 100);
        long integerPart = num / 100;
        int decimalPart = (int) (num % 100);

        StringBuilder result = new StringBuilder();
        
        String intStr = String.valueOf(integerPart);
        for (int i = 0; i < intStr.length(); i++) {
            int digit = intStr.charAt(i) - '0';
            int unitIndex = intStr.length() - i - 1;
            if (digit != 0) {
                result.append(digits[digit]).append(units[unitIndex]);
            } else if (result.length() > 0 && !result.substring(result.length() - 1).equals("零")) {
                result.append("零");
            }
        }
        
        if (result.length() == 0) {
            result.append("零");
        }
        result.append("元");

        if (decimalPart > 0) {
            int jiao = decimalPart / 10;
            int fen = decimalPart % 10;
            if (jiao > 0) {
                result.append(digits[jiao]).append("角");
            }
            if (fen > 0) {
                result.append(digits[fen]).append("分");
            }
        } else {
            result.append("整");
        }

        return result.toString();
    }
}
