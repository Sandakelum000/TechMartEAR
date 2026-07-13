package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.annotation.Admin;
import lk.techmart.core.dto.AdminDTO;
import lk.techmart.core.dto.AdminDashboardDTO;
import lk.techmart.core.dto.AdminOrderSummeryDTO;
import lk.techmart.core.dto.AdminSessionDTO;
import lk.techmart.core.service.AdminService;
import lk.techmart.core.util.ServiceResponse;

import java.time.LocalDate;
import java.util.List;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(value = MediaType.APPLICATION_JSON)
public class AdminController {
    @EJB
    private AdminService adminService;

    @Inject
    private lk.techmart.ejb.beans.ActiveAdminRegistry activeAdminRegistry;

    @POST
    @Path("/login")
    public Response adminLogin(AdminDTO adminDTO, @Context HttpServletRequest request) {
        ServiceResponse<AdminSessionDTO> response = adminService.adminLogin(adminDTO);
        if (!response.isSuccess()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("admin",response.getData());
        return Response.ok(
                ServiceResponse.builder()
                        .success(true)
                        .message(response.getMessage())
                        .build()
        ).build();
    }

    @POST
    @Path("/logout")
    @Admin
    public Response adminLogout(@Context HttpServletRequest request){
        HttpSession httpSession = request.getSession(false);
        if(httpSession != null){
            AdminSessionDTO adminSession = (AdminSessionDTO) httpSession.getAttribute("admin");
            if (adminSession != null) {
                activeAdminRegistry.removeAdmin(adminSession.getId());
            }
            httpSession.invalidate();
        }
        return Response.ok(
                ServiceResponse.builder()
                        .success(true)
                        .message("Logout successful")
                        .build()
        ).build();
    }

    @Path("/dashboard")
    @GET
    @Admin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardData(
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @Context HttpServletRequest request
    ){
        try{
            if(startDate == null || endDate == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ServiceResponse.builder()
                                .success(false)
                                .message("startDate and endDate are required (yyyy-MM-dd)")
                                .build())
                        .build();
            }
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if(end.isBefore(start)){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ServiceResponse.builder()
                                .success(false)
                                .message("endDate cannot be before startDate")
                                .build())
                        .build();
            }
            ServiceResponse<AdminDashboardDTO> response = adminService.getDashboardData(start, end);
            if(!response.isSuccess()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(response)
                        .build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ServiceResponse.builder()
                            .success(false)
                            .message("Dashboard loading failed")
                            .build())
                    .build();
        }
    }

    @Path("/order-summery")
    @GET
    @Admin
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderSummery(){
        ServiceResponse<List<AdminOrderSummeryDTO>> response = adminService.getOrderSummery();
        if(!response.isSuccess()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        }
        return Response.ok(response).build();
    }

}
