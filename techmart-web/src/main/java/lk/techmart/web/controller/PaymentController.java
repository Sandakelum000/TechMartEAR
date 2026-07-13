package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lk.techmart.core.annotation.Console;
import lk.techmart.core.annotation.IsUser;
import lk.techmart.core.dto.PaymentHistoryDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.messaging.MessageBrokerType;
import lk.techmart.core.service.CheckoutMessageService;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.service.PaymentService;
import lk.techmart.core.util.AppUtil;
import lk.techmart.core.util.PayHereUtil;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.ejb.jms.config.SystemConfigService;
import lk.techmart.ejb.jms.factory.CheckoutMessageFactory;
import lk.techmart.web.util.SessionUtil;

import java.net.URI;
import java.util.List;

@Path("/payments")
public class PaymentController {

//    @EJB
//    private CheckoutMessageService checkoutMessageService;

    @EJB
    private CheckoutMessageFactory messageFactory;

    @EJB
    private SystemConfigService configService;

    @EJB
    private PaymentService paymentService;

    @Inject
    @Console
    private Event<String> logEvent;

    @Path("/return")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response paymentSuccess(@QueryParam("orderId") String orderId) {
        return Response.seeOther(URI.create("http://localhost:8080/techmart" + "/invoice.html?orderId=" + orderId)).build();
    }

    @Path("/cancel")
    @GET
    public Response paymentCancel() {
        System.out.println("Payment canceled");
        return Response.ok().build();
    }


    @Path("/notify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response paymentNotify(
            @FormParam("merchant_id") String merchantId,
            @FormParam("order_id") String orderId,
            @FormParam("payhere_amount") String payHereAmount,
            @FormParam("payhere_currency") String payHereCurrency,
            @FormParam("status_code") String statusCode,
            @FormParam("md5sig") String md5Sig
    ) {

        System.out.println("notify calling");

        System.out.println("orderId = " + orderId);
        System.out.println("statusCode = " + statusCode);

        if (orderId == null || statusCode == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing Form Parameters").build();
        }

        MultivaluedMap<String, String> formMap = new MultivaluedHashMap<>();
        formMap.add("merchant_id", merchantId);
        formMap.add("order_id", orderId);
        formMap.add("payhere_amount", payHereAmount);
        formMap.add("payhere_currency", payHereCurrency);
        formMap.add("status_code", statusCode);
        formMap.add("md5sig", md5Sig);

        if (!PayHereUtil.validateNotify(formMap)) {
            System.out.println("SIGNATURE FAILED");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("INVALID SIGNATURE").build();
        }

        System.out.println("SENDING JMS: " + orderId + " status=" + statusCode);

        MessageBrokerType brokerType = configService.getActiveBroker();
        messageFactory.getProducer(brokerType)
                .sendPaymentEvent(orderId,Integer.parseInt(statusCode));

        logEvent.fire("SENDING ACTIVEMQ: " + orderId + " status=" + statusCode);
        return Response.ok().build();
    }


    @Path("get-all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPaymentDetails(@Context HttpServletRequest request){
        UserSessionDTO user = SessionUtil.getSessionUser(request);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ServiceResponse.builder()
                            .success(false)
                            .message("Please login first")
                            .build())
                    .build();
        }
        ServiceResponse<List<PaymentHistoryDTO>> response = paymentService.userPaymentHistory(user);
        return Response.ok().entity(response).build();
    }


}
