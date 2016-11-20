package conversor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/conversor")
public class PaginaInicial {

	@GET
	@Path("/upload")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response upload() throws IOException {
		//UploadS3 s3 = new UploadS3();
		//s3.upload();
		return Response.status(Status.CREATED).entity("nao sei se foi").build();
	}

	@GET
	@Path("/saudacao")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response saudacao() {
		Saudacao sau = new Saudacao("Boa NoiTE 3");
		return Response.status(Status.CREATED).entity(sau).build();
	}

	@GET
	@Path("/saudacao/{name}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response saudacao(@PathParam("name") String name) {
		Person p = new Person(name);
		return Response.status(Status.CREATED).entity(p).build();
	}

	private static final String UPLOAD_FOLDER = "/tmp/sambatech/";
	@Context
	private UriInfo context;

	@POST
	@Path("/uploadvideo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadVideo(@FormDataParam("file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
		// check if all form parameters are provided
		if (uploadedInputStream == null || fileDetail == null)
			return Response.status(400).entity("Invalid form data").build();
		// create our destination folder, if it not exists
		try {
			createFolderIfNotExists(UPLOAD_FOLDER);
		} catch (SecurityException se) {
			return Response.status(500).entity("Can not create destination folder on server").build();
		}
		String uploadedFileLocation = UPLOAD_FOLDER + fileDetail.getFileName();
		try {
			saveToFile(uploadedInputStream, uploadedFileLocation);
		} catch (IOException e) {
			return Response.status(500).entity("Can not save file").build();
		}
		/*Rotina para fazer o Upload para o Amazon S3*/
		UploadS3 s3 = new UploadS3();
		s3.upload("lucasmaiasilva", fileDetail.getFileName(), uploadedFileLocation);
		return Response.status(200).entity("File saved to " + uploadedFileLocation).build();
	}

	private void saveToFile(InputStream inStream, String target) throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}

	private void createFolderIfNotExists(String dirName) throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}

}
