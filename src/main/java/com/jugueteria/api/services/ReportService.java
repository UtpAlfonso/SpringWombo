package com.jugueteria.api.services;
import com.jugueteria.api.dto.response.SalesReportResponse;
import java.time.LocalDate;
public interface ReportService {
    SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate);
}