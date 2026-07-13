package lk.techmart.web.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.annotation.Admin;
import lk.techmart.core.dto.InventoryTransactionEvent;
import lk.techmart.core.dto.StockDataDTO;
import lk.techmart.core.service.InventoryService;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Path("/inventory")
@Admin
public class InventoryController {

    @EJB
    private InventoryService inventoryService;

    @Path("/receive")
    @POST
    public Response receiveStock(InventoryTransactionEvent dto) {
        ServiceResponse<Void> response = inventoryService.receiveStock(dto);
        return Response.ok().entity(response).build();
    }

    @Path("/get-all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInventoryData(
            @QueryParam("sort") @DefaultValue("qty") String sort,
            @QueryParam("order") @DefaultValue("asc") String order
    ){
        ServiceResponse<List<StockDataDTO>> response = inventoryService.getInventoryData(sort, order);
        return Response.ok().entity(response).build();
    }

    @Path("/get{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInventoryDataById(@PathParam("id") int id){
        ServiceResponse<StockDataDTO> response = inventoryService.getInventoryDataById(id);
        return Response.ok().entity(response).build();
    }
}
