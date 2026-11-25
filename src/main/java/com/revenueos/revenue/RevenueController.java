package com.revenueos.revenue;

import com.revenueos.revenue.dto.MonthlyRevenuePoint;
import com.revenueos.revenue.dto.RevenueSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/revenue")
@Tag(name = "Revenue")
@PreAuthorize("hasAuthority('REVENUE_VIEW')")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/summary")
    @Operation(operationId = "revenueSummary", summary = "Company revenue roll-up (invoiced, collected, outstanding, profit)")
    public RevenueSummaryResponse summary() {
        return revenueService.summary();
    }

    @GetMapping("/monthly")
    @Operation(operationId = "monthlyRevenue", summary = "Invoiced vs collected by month")
    public List<MonthlyRevenuePoint> monthly(@RequestParam(defaultValue = "6") int months) {
        return revenueService.monthly(months);
    }
}
