package conversor;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/conversor")
public class PaginaInicial {

	
	@GET
	@Path("/saudacao")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response saudacao() {
	  Saudacao sau = new Saudacao("Boa NoiTE 3");	
	  return Response.status(Status.CREATED).entity(sau).build();
	}
	
	@GET
	@Path("/saudacao/{name}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response saudacao(@PathParam("name") String name) {
		Person p = new Person(name);
		return Response.status(Status.CREATED).entity(p).build();
	}

}
