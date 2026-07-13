package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.dto.SearchRequestDTO;
import lk.techmart.core.service.AdvanceSearchService;
import lk.techmart.core.util.ServiceResponse;

@Path("/advanced-search")
public class AdvanceSearchController {

    @EJB
    private AdvanceSearchService advanceSearchService;

    @Path("/all-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAdvancedSearchData(){
        ServiceResponse<ProductResponseDTO> response = advanceSearchService.getAllProducts();
        return Response.ok().entity(response).build();
    }

    @Path("/filter")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAdvancedSearchData(SearchRequestDTO request){
        ServiceResponse<ProductResponseDTO> response = advanceSearchService.advanceSearch(request);
        return Response.ok().entity(response).build();
    }

}
