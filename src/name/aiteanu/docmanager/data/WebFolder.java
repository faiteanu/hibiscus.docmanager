package name.aiteanu.docmanager.data;

public class WebFolder {
	private String name;
	private String relativeUrl;
	
	public WebFolder() {}
	
	public WebFolder(String name, String relativeUrl) {
		this.name = name;
		this.relativeUrl = relativeUrl;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelativeUrl() {
		return relativeUrl;
	}
	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}
	
}
