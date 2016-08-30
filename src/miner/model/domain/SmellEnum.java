/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miner.model.domain;

import miner.util.Log;
import miner.util.exception.ValidationException;

/**
 *
 * @author carloseduardoxp
 */
public enum SmellEnum {

    AntiSingleton,
    BaseClassKnowsDerivedClass,
    BaseClassShouldBeAbstract,
    Blob,
    ClassDataShouldBePrivate,
    ComplexClass,
    FunctionalDecomposition,
    LargeClass,
    LazyClass,
    LongMethod,
    LongParameterList,
    ManyFieldAttributesButNotComplex,
    MessageChains,
    RefusedParentBequest,
    SpaghettiCode,
    SpeculativeGenerality,
    SwissArmyKnife,
    TraditionBreaker;
    
    public static SmellEnum getSmellName(String name) throws ValidationException {
        for (SmellEnum smell : SmellEnum.values()) {
            if (smell.toString().equals(name)) {
                return smell;
            }
        }
        Log.writeLog("Cant find smell "+name+" in SmellEnum");
        throw new ValidationException("Cant find smell "+name+" in SmellEnum");
    }

    public static String getNomeSmells() {
        String retorno = "";
        for (SmellEnum smell : SmellEnum.values()) {
            retorno += "@ATTRIBUTE " + smell.toString() + " {y,n}\n";
        }
        return retorno;
    }

}
