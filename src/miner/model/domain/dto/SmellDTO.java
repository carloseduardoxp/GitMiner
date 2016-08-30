/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.domain.dto;

/**
 *
 * @author carloseduardoxp
 */
public class SmellDTO {
    
    private Integer id;
    
    private Integer codigoBranch;
    
    private String nomeBranch;
    
    private String nomeArquivo;
    
    private String hashCommit;
    
    private Integer codigoCommit;
    
    private String tipoModificacao;
    
    private String nomeSmell;

    public SmellDTO(Integer id,Integer codigoBranch, String nomeBranch, String nomeArquivo, String hashCommit, Integer codigoCommit, String tipoModificacao, String nomeSmell) {
        this.id = id;
        this.codigoBranch = codigoBranch;
        this.nomeBranch = nomeBranch;
        this.nomeArquivo = nomeArquivo;
        this.hashCommit = hashCommit;
        this.codigoCommit = codigoCommit;
        this.tipoModificacao = tipoModificacao;
        this.nomeSmell = nomeSmell;
    }

    public SmellDTO() {
    }

    public Integer getCodigoBranch() {
        return codigoBranch;
    }

    public void setCodigoBranch(Integer codigoBranch) {
        this.codigoBranch = codigoBranch;
    }

    public String getNomeBranch() {
        return nomeBranch;
    }

    public void setNomeBranch(String nomeBranch) {
        this.nomeBranch = nomeBranch;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getHashCommit() {
        return hashCommit;
    }

    public void setHashCommit(String hashCommit) {
        this.hashCommit = hashCommit;
    }

    public Integer getCodigoCommit() {
        return codigoCommit;
    }

    public void setCodigoCommit(Integer codigoCommit) {
        this.codigoCommit = codigoCommit;
    }

    public String getTipoModificacao() {
        return tipoModificacao;
    }

    public void setTipoModificacao(String tipoModificacao) {
        this.tipoModificacao = tipoModificacao;
    }

    public String getNomeSmell() {
        return nomeSmell;
    }

    public void setNomeSmell(String nomeSmell) {
        this.nomeSmell = nomeSmell;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SmellDTO{" + "id=" + id + ", codigoBranch=" + codigoBranch + ", nomeBranch=" + nomeBranch + ", nomeArquivo=" + nomeArquivo + ", hashCommit=" + hashCommit + ", codigoCommit=" + codigoCommit + ", tipoModificacao=" + tipoModificacao + ", nomeSmell=" + nomeSmell + '}';
    }
    
    
            
    
}
