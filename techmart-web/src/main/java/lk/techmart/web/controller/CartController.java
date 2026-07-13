package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.CartRequestDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.service.CartService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.web.session.CartSession;
import lk.techmart.web.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

@Path("/carts")
public class CartController {

    @EJB
    private CartService cartService;

    @Inject
    private CartSession cartSession;

    @Path("/add-to-cart")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(CartRequestDTO requestDTO ,@Context HttpServletRequest request) {

        UserSessionDTO user = SessionUtil.getSessionUser(request);
        if (user == null) { // guest user

            ServiceResponse<List<CartItemDTO>> response = cartService.addToCartGuest(requestDTO, cartSession.getItems());
            if (response.isSuccess()) {
                //update session
                cartSession.setItems(response.getData());
            }
            return Response.status(
                    response.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST
            ).entity(ServiceResponse.builder()
                    .success(response.isSuccess())
                    .message(response.getMessage())
                    .data(null)
                    .build()).build();
        }
        // logged user
        ServiceResponse<Void> response = cartService.addToCartUser(requestDTO, user);
        return Response.status(
                response.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }

    @Path("/all-carts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllCarts(@Context HttpServletRequest request) {
        UserSessionDTO user = SessionUtil.getSessionUser(request);

        ServiceResponse<List<CartItemDTO>> response = cartService.getAllUserCarts(user, cartSession.getItems());

        return Response.status(
                response.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }

    @Path("/remove-cart/{cartId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCartItem(@PathParam("cartId") String cartId, @Context HttpServletRequest request){
        UserSessionDTO user = SessionUtil.getSessionUser(request);

        ServiceResponse<List<CartItemDTO>> response = cartService.deleteCartItem(cartId, user, cartSession.getItems());
        //reupdate sessionCart
        if (user == null && response.getData() != null) {
            cartSession.setItems(response.getData());
        }
        return Response.status(
                response.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }
}
