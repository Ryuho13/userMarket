package model;

public class SiggArea {
    private int id;
    private String name;
    private int sidoAreaId;
    
    public SiggArea() {}

    public SiggArea(int id, String name, int sidoAreaId) {
        this.id = id;
        this.name = name;
        this.sidoAreaId = sidoAreaId;
    }

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
