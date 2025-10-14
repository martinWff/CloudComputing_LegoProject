//package cc.data;

import java.util.Arrays;

/**
 * Represents a User, as returned to the clients
 * 
 * NOTE: array of house ids is shown as an example of how to store a list of elements and 
 * handle the empty list.
 */
public class User {
	private String id;
	private String name;
	private String pwd;
	private String photoId;
	private String[] legoIds;
	public User(String id, String name, String pwd, String photoId, String[] legoIds) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
		this.legoIds = legoIds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public String[] getLegoIds() {
		return legoIds == null ? new String[0] : legoIds ;
	}
	public void setLegoIds(String[] legoIds) {
		this.legoIds = legoIds;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + ", legoIds="
				+ Arrays.toString(legoIds) + "]";
	}

}
