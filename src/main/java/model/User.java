package model;

public class User {
    private Integer id;
    private String accountId;
    private String pw;
    private String name;
    private String phn;
    private String em;
    
    
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
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
	
	@Override
	public String toString() {
		return "User [id=" + id + ", accountId=" + accountId + ", pw=" + pw + ", name=" + name + ", phn=" + phn
				+ ", em=" + em + "]";
	}
}
