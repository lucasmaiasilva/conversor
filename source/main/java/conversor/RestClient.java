package conversor;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.ClientProtocolException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestClient {

	private String output;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void get() throws ClientProtocolException, IOException {

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		WebResource service = client.resource(UriBuilder.fromUri("http://52.67.200.126:8080").build());
		setOutput(service.path("/conversor/").path("/servicos/conversor/saudacao").accept(MediaType.APPLICATION_JSON)
				.get(String.class));
	}

	public void post(String file_name, String new_file_name) {

		Client client = Client.create();

		WebResource webResource = client.resource("https://app.zencoder.com/api/v2/jobs");

		String zencode = System.getenv("zencode");

		String input = "{\"api_key\": \"" + zencode + "\", "
				+ "\"input\":\"https://s3-sa-east-1.amazonaws.com/lucasmaiasilva/"+file_name+"\", \"outputs\": [ { \"url\": "
				+ "\"s3://s3-sa-east-1.amazonaws.com/lucasmaiasilva/"+new_file_name+"\", \"width\": \"1280\", \"height\":"
				+ "\"720\"} ] }";

		// POST method
		ClientResponse response = webResource.accept("application/json").type("application/json")
				.post(ClientResponse.class, input);

		// check response status code
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		// display response
		String output = response.getEntity(String.class);
		System.out.println("Output from Server .... ");
		System.out.println(output + "\n");

	}

}
