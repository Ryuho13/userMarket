package model;

public class SiggArea {
    private int id;
    private String name;
    private int sidoAreaId;

    // ✅ 기본 생성자 (JSP에서 EL 접근시 필요할 수도 있음)
    public SiggArea() {}

    // ✅ 3개 필드를 모두 받는 생성자
    public SiggArea(int id, String name, int sidoAreaId) {
        this.id = id;
        this.name = name;
        this.sidoAreaId = sidoAreaId;
    }

    // ✅ getter / setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSidoAreaId() {
        return sidoAreaId;
    }

    public void setSidoAreaId(int sidoAreaId) {
        this.sidoAreaId = sidoAreaId;
    }
}
