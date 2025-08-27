package com.impulse.domain.reto;

public class PermisoDTO {
    private String tipo;
    private boolean valor;

    public PermisoDTO() {}
    public PermisoDTO(String tipo, boolean valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isValor() { return valor; }
    public void setValor(boolean valor) { this.valor = valor; }
}
