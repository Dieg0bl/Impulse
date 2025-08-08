package com.impulse.domain.monetizacion;

import jakarta.persistence.*;

@Entity
@Table(name="plans")
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=32)
    private String code;
    @Column(nullable=false, length=80)
    private String name;
    @Column(nullable=false)
    private Integer priceCents;
    @Column(nullable=false, length=8)
    private String currency;
    @Column(nullable=false, length=16)
    private String period; // MONTH, YEAR
    @Column(nullable=false)
    private Boolean active = true;

    public Plan(){}
    public Plan(String code,String name,Integer priceCents,String currency,String period){
        this.code=code;this.name=name;this.priceCents=priceCents;this.currency=currency;this.period=period;this.active=true;
    }
    public Long getId(){return id;}
    public String getCode(){return code;}
    public String getName(){return name;}
    public Integer getPriceCents(){return priceCents;}
    public String getCurrency(){return currency;}
    public String getPeriod(){return period;}
    public Boolean getActive(){return active;}
    public void setActive(Boolean active){this.active=active;}
}
