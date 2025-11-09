package model;

public class UserProfile {
	
    private int id;
    private String accountId;
    private String name;
    private String phn;
    private String em;

    private String nickname;
    private Integer regionId;
    private String addrDetail;
    private String profileImg;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhn() {
		return phn;
	}
	public void setPhn(String phn) {
		this.phn = phn;
	}
	public String getEm() {
		return em;
	}
	public void setEm(String em) {
		this.em = em;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	public String getAddrDetail() {
		return addrDetail;
	}
	public void setAddrDetail(String addrDetail) {
		this.addrDetail = addrDetail;
	}
	public String getProfileImg() {
		return profileImg;
	}
	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}
	
	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", accountId=" + accountId + ", name=" + name + ", phn=" + phn + ", em=" + em
				+ ", nickname=" + nickname + ", regionId=" + regionId + ", addrDetail=" + addrDetail + ", profileImg="
				+ profileImg + "]";
	}

    
    
}
