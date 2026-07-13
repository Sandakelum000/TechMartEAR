package lk.techmart.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.annotation.Admin;
import lk.techmart.core.dto.FileItem;
import lk.techmart.core.dto.ProductDTO;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.service.FileStorageService;
import lk.techmart.core.service.ProductService;
import lk.techmart.core.util.AppConfig;
import lk.techmart.core.util.ServiceResponse;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/products")
@Admin
public class ProductController {

    @EJB
    private ProductService productService;

    @EJB
    private FileStorageService storageService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProductAttributes(){
        ServiceResponse<ProductResponseDTO> response = productService.getAllProductAttribute();
        return Response.ok().entity(response).build();
    }

    @Path("/save-product")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveProduct(ProductDTO request){
        ServiceResponse<Integer> response = productService.addNewProduct(request);
        return Response.status(
                response.isSuccess()
                        ? Response.Status.CREATED
                        : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }

    @Path("/{productId}/upload-images")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProductImages(
            @PathParam("productId") int productId,
            @FormDataParam("images[]") FormDataBodyPart bodyParts) throws IOException {

        if (bodyParts == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No images provided")
                    .build();
        }

        if (!productService.isProductExists(productId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product not found")
                    .build();
        }
        List<String> uploadUrls = new ArrayList<>();

        for(BodyPart part: bodyParts.getParent().getBodyParts()){
            String fileName = part.getContentDisposition().getFileName();

            try (InputStream inputStream = part.getEntityAs(InputStream.class)) {
                byte[] fileData = inputStream.readAllBytes();

                FileItem fileItem =
                        storageService.saveFile(
                                fileData,
                                fileName,
                                "products/" + productId
                        );
                uploadUrls.add(fileItem.getUrl());
            }
        }
        ServiceResponse<Void> response = productService.addProductImages(productId, uploadUrls);
        return Response.status(
                response.isSuccess()
                        ? Response.Status.CREATED
                        : Response.Status.BAD_REQUEST
        ).entity(response).build();
    }

}
