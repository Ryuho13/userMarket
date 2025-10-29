package dto;

public class Member {
	private Long id;
	private String email;
	private String password;
	private String nickname;
	private String regionCode;

	public Member() {
	}

	public Member(Long id, String email, String password, String nickname, String regionCode) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.regionCode = regionCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	@Override
	public String toString() {
		return "Member{id=" + id + ", email='" + email + "', nickname='" + nickname + "', regionCode='" + regionCode
				+ "'}";
	}
}
