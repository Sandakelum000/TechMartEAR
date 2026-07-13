package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.util.ServiceResponse;

@Path("/orders")
public class OrderController {

    @EJB
    private OrderService orderService;

    @Path("/verify-order")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyOrder(@QueryParam("orderId")String orderId){
        ServiceResponse<Void> response = orderService.verifyOrderDetails(orderId);

        return Response.status(
                response.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST
        ).entity(response).build();

    }
}
