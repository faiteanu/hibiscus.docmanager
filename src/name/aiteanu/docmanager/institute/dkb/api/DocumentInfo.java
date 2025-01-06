package name.aiteanu.docmanager.institute.dkb.api;

public class DocumentInfo {
    private String id;
    private String type;
    //private Links links;
    private Attributes attributes;
    //private Relationships relationships;
    
    // Getters and setters
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Attributes getAttributes() {
		return attributes;
	}
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

}
