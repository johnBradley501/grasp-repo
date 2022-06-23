package uk.ac.kcl.cch.jb.sparql.utils;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.kcl.cch.jb.sparql.model.RDFServer;
import uk.ac.kcl.cch.jb.sparql.model.SPARQLQuery;

public class QueryRunner {

	private CloseableHttpClient httpclient;
	private URL endpoint;

	public QueryRunner(SPARQLQuery query) {
		this.endpoint = query.getEndpoint();
		httpclient = HttpClients.createDefault();
	}
	
	public JSONObject run(String query)	{
		// HttpPost httpPost = new HttpPost(server.getSparqlEngine().toString());
		RequestBuilder reqbuilder = RequestBuilder.post();
		RequestBuilder reqbuilder2 = reqbuilder.setUri(endpoint.toString()).addHeader("Accept", "application/sparql-results+json"); //   application/json
		HttpUriRequest httppost = reqbuilder2.addParameter("query", query).build();
		
		
		JSONObject rslt = null;
		CloseableHttpResponse httpresponse = null;
		try {
			// ResponseHandler<String> responseHandler = new ResponseHandler();
			httpresponse = httpclient.execute(httppost);
			int status = httpresponse.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				//rslt = new JSONObject(httpresponse.getEntity().getContent());
				//String json = EntityUtils.toString(httpresponse.getEntity(), "UTF-8");
				rslt = new JSONObject(EntityUtils.toString(httpresponse.getEntity(), "UTF-8"));
				//System.out.println("result: "+json);
			} else {
				rslt = new JSONObject();
				rslt.put("failed", status);
				rslt.put("reason", httpresponse.getStatusLine().getReasonPhrase());
			}
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpresponse.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rslt;
	}
	public void dispose() {
		try {
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
