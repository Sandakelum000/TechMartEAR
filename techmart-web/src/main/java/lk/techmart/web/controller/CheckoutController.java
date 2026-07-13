package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.annotation.IsUser;
import lk.techmart.core.dto.CheckoutDataDTO;
import lk.techmart.core.dto.CheckoutRequestDTO;
import lk.techmart.core.dto.PayHereDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.service.CheckoutService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.web.util.SessionUtil;

@Path("/checkouts")
@IsUser
public class CheckoutController {

    @EJB
    private CheckoutService checkoutService;

    @Path("/user-checkout")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userCheckout(CheckoutRequestDTO requestData, @Context HttpServletRequest request){
        UserSessionDTO sessionUser = SessionUtil.getSessionUser(request);

        ServiceResponse<PayHereDTO> response = checkoutService.processCheckout(requestData, sessionUser);

       if(!response.isSuccess()){
           return Response.status(Response.Status.BAD_REQUEST)
                   .entity(response)
                   .build();
       }
       return Response.ok(response).build();
    }


    @IsUser
    @Path("/user-checkout-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserCheckoutData(@Context HttpServletRequest request) {
        UserSessionDTO sessionUser = SessionUtil.getSessionUser(request);

        ServiceResponse<CheckoutDataDTO> response = checkoutService.getCheckoutData(sessionUser);
        if(response.isSuccess()){
            return Response.status(Response.Status.OK)
                    .entity(response)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
