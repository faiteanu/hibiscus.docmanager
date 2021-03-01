package name.aiteanu.docmanager.synchronize;

import java.util.ArrayList;
import java.util.List;

import name.aiteanu.docmanager.rmi.Document;

public class SyncResult {
	private List<Document> documents = new ArrayList<>();
	private List<Exception> errors = new ArrayList<>();
	
	public List<Document> getDocuments() {
		return documents;
	}
	public List<Exception> getErrors() {
		return errors;
	}
		
}
