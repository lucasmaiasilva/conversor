package conversor;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/conversor")
public class PaginaInicial {
	
	@GET
	@Path("/saudacao")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public void saudacao(){
		System.out.println("Boa noite");
	}

}
