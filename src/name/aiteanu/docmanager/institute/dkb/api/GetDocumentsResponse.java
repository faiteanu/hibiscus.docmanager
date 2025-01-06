package name.aiteanu.docmanager.institute.dkb.api;

import java.util.List;

public class GetDocumentsResponse {
	private List<DocumentInfo> data;
	
	
	public List<DocumentInfo> getData() {
		return data;
	}

	public void setData(List<DocumentInfo> documents) {
		this.data = documents;
	}
	
}
