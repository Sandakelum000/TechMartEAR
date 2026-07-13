package lk.techmart.web.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lk.techmart.core.util.AppConfig;

import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/files")
public class FileController {

    @GET
    @Path("{path: .+}")
    public Response getFIle(@PathParam("path") String path){
        try{
            java.nio.file.Path file = Paths.get(AppConfig.UPLOAD_DIR, path);
            if(!Files.exists(file)){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            String type = Files.probeContentType(file);
            return Response.ok(file.toFile(),type).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
