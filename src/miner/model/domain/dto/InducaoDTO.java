/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.domain.dto;

import java.util.Objects;

/**
 *
 * @author carloseduardoxp
 */
public class InducaoDTO {
    
    private String smellOrigem;
    
    private String smellDestino;
    
    private Integer quantidade;
    
    private boolean marcadoParaIncrementar;

    public InducaoDTO(String smellOrigem, String smellDestino) {
        this.smellOrigem = smellOrigem;
        this.smellDestino = smellDestino;
        this.quantidade = 0;
        this.marcadoParaIncrementar = false;
    }

    public String getSmellOrigem() {
        return smellOrigem;
    }

    public String getSmellDestino() {
        return smellDestino;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    
    public void addQuantidade() {
        this.quantidade++;
    }

    public boolean isMarcadoParaIncrementar() {
        return marcadoParaIncrementar;
    }

    public void setMarcadoParaIncrementar(boolean marcadoParaIncrementar) {
        this.marcadoParaIncrementar = marcadoParaIncrementar;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.smellOrigem);
        hash = 89 * hash + Objects.hashCode(this.smellDestino);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InducaoDTO other = (InducaoDTO) obj;
        if (this.smellOrigem == null ? other.smellOrigem != null : !this.smellOrigem.equals(other.smellOrigem)) {
            return false;
        }
        return (this.smellDestino == null ? other.smellDestino == null : this.smellDestino.equals(other.smellDestino));
    }

    @Override
    public String toString() {
        return smellOrigem+","+smellDestino+","+quantidade;
    }

     
}
