package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.dto.ProductDTO;
import lk.techmart.core.service.HomeService;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Path("/home")
public class HomeController {

    @EJB
    private HomeService homeService;

    @Path("/products")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts(){
        ServiceResponse<List<ProductDTO>> response = homeService.getHomeProducts();
        return Response.status(
                response.isSuccess()? Response.Status.OK : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }
}
