package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.annotation.IsUser;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.UserDTO;
import lk.techmart.core.dto.UserSessionDTO;
import lk.techmart.core.service.CartAsyncService;
import lk.techmart.core.service.CartService;
import lk.techmart.core.service.UserService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.web.session.CartSession;
import lk.techmart.web.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

@Path("/users")
public class UserController {

    @EJB
    private UserService userService;

    @EJB
    private CartAsyncService cartAsyncService;

    @Inject
    private CartSession cartSession;

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(UserDTO userDTO) {
        ServiceResponse<Void> response = userService.registerUser(userDTO);
        return Response.ok().entity(response).build();
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(UserDTO userDTO, @Context HttpServletRequest request) {

        ServiceResponse<UserSessionDTO> response = userService.userLogin(userDTO);

        if (response.isSuccess()) {
            UserSessionDTO user = response.getData();
            SessionUtil.setSessionUser(request, user);

            //cart async
            List<CartItemDTO> sessionCart = new ArrayList<>(cartSession.getItems());
            cartAsyncService.mergeCarts(user, sessionCart);
            cartSession.clear();
        }
        return Response.status(Response.Status.OK)
                .entity(ServiceResponse.builder()
                        .success(response.isSuccess())
                        .message(response.getMessage())
                        .build())
                .build();
    }

    @IsUser
    @Path("/logout")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {

        boolean success = SessionUtil.invalidate(request);
        if(success){
            return Response.ok(
                    ServiceResponse.builder()
                            .success(true)
                            .message("Successfully Logout")
                            .build()
            ).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
