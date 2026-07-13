package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.*;
import lk.techmart.core.entity.Admin;
import lk.techmart.core.entity.Order;
import lk.techmart.core.entity.OrderItem;
import lk.techmart.core.entity.Status;
import lk.techmart.core.service.AdminService;
import lk.techmart.core.util.ServiceResponse;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AdminServiceBean implements AdminService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Inject
    private Event<String> loggerEvent;

    @Inject
    private ActiveAdminRegistry activeAdminRegistry;

    @Override
    public ServiceResponse<AdminSessionDTO> adminLogin(AdminDTO request) {
        try{
            if(request == null){
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Invalid request").build();
            }
            if(request.getUsername() == null || request.getUsername().isBlank()){
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Please enter your username").build();
            }
            if(request.getPassword() == null || request.getPassword().isBlank()){
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Please enter your password").build();
            }
            Admin admin = entityManager.createQuery(
                            "SELECT a FROM Admin a " +
                                    "WHERE a.username = :username", Admin.class)
                    .setParameter("username", request.getUsername().trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if(admin == null){
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Invalid credentials").build();
            }
            if (!admin.getPassword().equals(request.getPassword())) {
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Invalid credentials").build();
            }
            if (!"ADMIN".equals(admin.getRole().getName())){
                return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Forbidden request").build();
            }

            boolean loginAllowed = activeAdminRegistry.registerAdmin(admin.getId());
            if (!loginAllowed) {
                return ServiceResponse.<AdminSessionDTO>builder()
                        .success(false)
                        .message("This admin account is already logged in on another device.")
                        .build();
            }

            AdminSessionDTO adminSessionDTO = AdminSessionDTO.builder().id(admin.getId()).username(admin.getUsername()).roleName(admin.getRole().getName()).build();
            return ServiceResponse.<AdminSessionDTO>builder().success(true).message("Login Successful").data(adminSessionDTO).build();
        } catch (Exception e) {
            loggerEvent.fire(AdminServiceBean.class.getName()+": Admin Login failed " + e);
            return ServiceResponse.<AdminSessionDTO>builder().success(false).message("Login failed due to server error.").build();
        }

    }

    @Override
    public ServiceResponse<AdminDashboardDTO> getDashboardData(LocalDate startDate, LocalDate endDate) {
        try{
            long totalProductUnit = ((Number) entityManager.createQuery("SELECT COALESCE(SUM(s.qty),0) " +
                    "FROM Stock s WHERE s.status=true", Long.class).getSingleResult()).longValue();

            long totalSales = ((Number) entityManager.createQuery("SELECT COUNT(o) FROM Order o " +
                            "WHERE o.status.value = :status", Long.class)
                    .setParameter("status", Status.Type.COMPLETED.name())
                    .getSingleResult()).longValue();

            long activeUsers = ((Number) entityManager.createQuery("SELECT COUNT(u) FROM User u " +
                    "WHERE u.active = true", Long.class).getSingleResult()).longValue();


            List<Order> orders = entityManager.createQuery(
                            "SELECT DISTINCT o FROM Order o " +
                                    "JOIN FETCH o.orderItems oi " +
                                    "JOIN FETCH oi.stock s " +
                                    "WHERE o.status.value =:status " +
                                    "AND o.createdAt >= :start " +
                                    "AND o.createdAt < :end", Order.class)
                    .setParameter("status", Status.Type.COMPLETED.name())
                    .setParameter("start", startDate.atStartOfDay())
                    .setParameter("end", endDate.plusDays(1).atStartOfDay())
                    .getResultList();

            Map<LocalDate,SalesChartDTO> salesMap = new LinkedHashMap<>();

            for(Order order : orders){
                LocalDate date = order.getCreatedAt().toLocalDate();
                SalesChartDTO chart = salesMap.getOrDefault(
                        date,
                        SalesChartDTO.builder()
                                .date(date)
                                .label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT,Locale.ENGLISH))
                                .orderCount(0)
                                .salesSum(0)
                                .build()
                );
                chart.setOrderCount(chart.getOrderCount()+1);
                double orderTotal = 0;
                for(OrderItem item: order.getOrderItems()){
                    orderTotal += item.getQty() * item.getStock().getPrice();
                }
                chart.setSalesSum(chart.getSalesSum()+orderTotal);
                salesMap.put(date,chart);
            }

            List<SalesChartDTO> chartList = new ArrayList<>();
            LocalDate current = startDate;

            while(!current.isAfter(endDate)){
                chartList.add(
                        salesMap.getOrDefault(
                                current,
                                SalesChartDTO.builder()
                                        .date(current)
                                        .label(current.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                                        .orderCount(0)
                                        .salesSum(0)
                                        .build()
                        )
                );
                current = current.plusDays(1);
            }
            AdminDashboardDTO dashboard = AdminDashboardDTO.builder()
                    .totalProductUnits(totalProductUnit)
                    .totalSales(totalSales)
                    .activeUsers(activeUsers)
                    .salesChart(chartList)
                    .build();

            return ServiceResponse.<AdminDashboardDTO>builder()
                    .success(true)
                    .message("Dashboard data loaded successfully.")
                    .data(dashboard)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<AdminDashboardDTO>builder().success(false).message("admin data loading failed").build();
        }
    }

    @Override
    public ServiceResponse<List<AdminOrderSummeryDTO>> getOrderSummery() {
        List<Order> orders = entityManager.createQuery("SELECT o FROM Order o", Order.class).getResultList();

        List<AdminOrderSummeryDTO> dtoList = orders.stream()
                .map(this::mapToDTO)
                .toList();

        return ServiceResponse.<List<AdminOrderSummeryDTO>>builder()
                .success(true)
                .message("Successfully loaded")
                .data(dtoList)
                .build();
    }

    private AdminOrderSummeryDTO mapToDTO(Order o) {

        long performance = 0;

        if (o.getStartTime() != null && o.getEndTime() != null) {
            performance = o.getEndTime() - o.getStartTime();
        }

        return AdminOrderSummeryDTO.builder()
                .orderId(o.getId())
                .status(o.getStatus() != null ? o.getStatus().getValue() : null)
                .userId(o.getUser() != null ? o.getUser().getId() : 0)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .performance(performance)
                .build();
    }
}
