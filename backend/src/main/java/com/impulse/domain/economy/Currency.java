package com.impulse.economy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency {
    @Id
    private String id;
    private String name;
    private String type;
    private boolean transferable;
    private boolean purchasable;
    private String description;
    // getters y setters
}
