package conversor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/conversor")
public class PaginaInicial {
	
	private static final String UPLOAD_FOLDER = "/tmp/sambatech/";
	@Context
	private UriInfo context;

	@POST
	@Path("/uploadvideo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadVideo(@FormDataParam("file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException, URISyntaxException, InterruptedException {

		if (uploadedInputStream == null || fileDetail == null)
			return Response.status(400).entity("[CONVERSOR] Dados inválidos").build();
		try {
			createFolderIfNotExists(UPLOAD_FOLDER);
		} catch (SecurityException se) {
			return Response.status(500).entity("[CONVERSOR] Não foi possível criar o diretório no servidor").build();
		}
		String uploadedFileLocation = UPLOAD_FOLDER + fileDetail.getFileName();
		try {
			saveToFile(uploadedInputStream, uploadedFileLocation);
		} catch (IOException e) {
			return Response.status(500).entity("[CONVERSOR] Não foi possível salvar o arquivo").build();
		}
		
		/*Rotina para fazer o Upload para o Amazon S3*/
		S3 s3 = new S3();
		s3.upload("lucasmaiasilva", fileDetail.getFileName(), uploadedFileLocation);
		
		/*Rotina para enviar para o Zencoder*/
		RestClient cli = new RestClient();
		cli.post(fileDetail.getFileName(),fileDetail.getFileName()+".mp4");
		
		/*Baixa o arquivo do amazon s3 para o disco local*/
		s3.download(fileDetail.getFileName()+".mp4");
		
		/*Redirecionamento para a pagina do player*/
		URL url = new URL("http://52.67.43.9/video.html"); 
		URI uri = url.toURI();
	    return Response.seeOther(uri).build();

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
