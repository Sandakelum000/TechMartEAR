package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.AdminDTO;
import lk.techmart.core.dto.AdminDashboardDTO;
import lk.techmart.core.dto.AdminOrderSummeryDTO;
import lk.techmart.core.dto.AdminSessionDTO;
import lk.techmart.core.util.ServiceResponse;

import java.time.LocalDate;
import java.util.List;

@Remote
public interface AdminService {
    ServiceResponse<AdminSessionDTO> adminLogin(AdminDTO request);
    ServiceResponse<AdminDashboardDTO> getDashboardData(LocalDate startDate, LocalDate endDate);
    ServiceResponse<List<AdminOrderSummeryDTO>> getOrderSummery();
}
