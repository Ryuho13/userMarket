package model;

public class UserInfo {
    private Integer id;
    private Integer uId;
    private String nickname;
    private Integer regionId;
    private String addrDetail;
    private String profileImg;
    
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	
	public Integer getuId() { return uId; }
	public void setuId(Integer uId) { this.uId = uId; }
	
	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }
	
	public Integer getRegionId() { return regionId; }
	public void setRegionId(Integer regionId) { this.regionId = regionId; }
	
	public String getAddrDetail() { return addrDetail; }
	public void setAddrDetail(String addrDetail) { this.addrDetail = addrDetail; }
	
	public String getProfileImg() { return profileImg; }
	public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
	
	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", uId=" + uId + ", nickname=" + nickname + ", regionId=" + regionId
				+ ", addrDetail=" + addrDetail + ", profileImg=" + profileImg + "]";
	}
}