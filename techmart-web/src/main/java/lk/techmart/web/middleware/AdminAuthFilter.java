package lk.techmart.web.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lk.techmart.core.annotation.Admin;
import lk.techmart.core.util.ServiceResponse;

@Provider
@Admin
public class AdminAuthFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext ctx) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            ctx.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(
                                    ServiceResponse.builder()
                                            .success(false)
                                            .message("Admin authentication required.")
                                            .build()
                            )
                            .build()
            );
        }
    }
}
