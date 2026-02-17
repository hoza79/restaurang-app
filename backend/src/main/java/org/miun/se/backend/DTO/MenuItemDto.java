package org.miun.se.backend.DTO;

public class MenuItemDto {

    private String name;
    private Integer menuItemId;
    private String description;
    private Double price;
    private Boolean available;



    public MenuItemDto(){}

    public MenuItemDto(String name, Integer menuItemId, String description, Double price, Boolean available){
        this.name = name;
        this.menuItemId = menuItemId;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public String getName(){ return name;}
    public Integer getMenuItemId(){ return menuItemId ;}
    public String getDescription(){ return description ;}
    public Double getPrice(){return price; }
    public Boolean getAvailable() {return available;}

    public void setName(String name){ this.name = name;}
    public void setMenuItemId(Integer menuItemId){ this.menuItemId = menuItemId; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price){ this.price = price;}
    public void setAvailable(Boolean available){ this.available = available; }

}
