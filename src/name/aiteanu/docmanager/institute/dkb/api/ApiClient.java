package name.aiteanu.docmanager.institute.dkb.api;

import java.io.InputStream;
import java.util.Set;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.openqa.selenium.Cookie;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient {
	
	BasicCookieStore cookieStore = new BasicCookieStore();
	
	public ApiClient(Set<Cookie> cookies) {
        for (Cookie cookie : cookies) {
        	BasicClientCookie basicCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
        	basicCookie.setDomain("banking.dkb.de");
        	//basicCookie.setDomain(cookie.getDomain());
        	//basicCookie.setPath(cookie.getPath());
        	cookieStore.addCookie(basicCookie);
		}
	}
	
	public GetDocumentsResponse getDocuments() throws Exception {
		try {
            // URL of the API
            String apiUrl = "https://banking.dkb.de/api/documentstorage/documents?page%5Blimit%5D=200&page%5Boffset%5D=0&sort=-creationDate";
            
            // Create HttpClient and make GET request
            CloseableHttpClient httpClient = HttpClientBuilder.create()
        		.setDefaultCookieStore(cookieStore)
        		.build();
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(apiUrl)
        		.addHeader("Accept", "application/vnd.api+json")
                .build();
            
            GetDocumentsResponse documents = httpClient.execute(httpGet, response -> {
                //System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final HttpEntity entity = response.getEntity();
                InputStream responseStream = entity.getContent();
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                GetDocumentsResponse documents2 = mapper.readValue(responseStream, GetDocumentsResponse.class);
                return documents2;
            });
            return documents;
        } catch (Exception e) {
            throw e;
        }
	}
	
	public byte[] getDocumentBinary(String id) throws Exception {
		try {
            // URL of the API
            String apiUrl = "https://banking.dkb.de/api/documentstorage/documents/" + id;
            
            // Create HttpClient and make GET request
            CloseableHttpClient httpClient = HttpClientBuilder.create()
        		.setDefaultCookieStore(cookieStore)
        		.build();
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(apiUrl)
        		.addHeader("Accept", "application/pdf")
                .build();
            
            byte[] data = httpClient.execute(httpGet, response -> {
                //System.out.println(response.getCode() + " " + response.getReasonPhrase());
            	if (response.getCode() == 200) {
	                final HttpEntity entity = response.getEntity();
	                return EntityUtils.toByteArray(entity);
            	} else {
            		throw new HttpResponseException(response.getCode(), response.getReasonPhrase());
            	}
            });
            
            return data;
        } catch (Exception e) {
            throw e;
        }
	}
}
