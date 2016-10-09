package miner.model.domain;

import java.util.List;

public class ClassCommitChange {

	private Class javaClass;

	private CommitChange commitChange;

	private Double ACAIC;
	private Double ACMIC;
	private Double AID;
	private Double ANA;
	private Double CAM;
	private Double CBO;
	private Double CIS;
	private Double CLD;
	private Double CP;
	private Double DAM;
	private Double DCAEC;
	private Double DCC;
	private Double DCMEC;
	private Double DIT;
	private Double DSC;
	private Double EIC;
	private Double EIP;
	private Double ICH;
	private Double IR;
	private Double LCOM1;
	private Double LCOM2;
	private Double LCOM5;
	private Double LOC;
	private Double McCabe;
	private Double MFA;
	private Double MOA;
	private Double NAD;
	private Double NCM;
	private Double NCP;
	private Double NMA;
	private Double NMD;
	private Double NMI;
	private Double NMO;
	private Double NOA;
	private Double NOC;
	private Double NOD;
	private Double NOF;
	private Double NOH;
	private Double NOM;
	private Double NOP;
	private Double NOPM;
	private Double NOTC;
	private Double NOTI;
	private Double NPrM;
	private Double PIIR;
	private Double PP;
	private Double REIP;
	private Double RFC;
	private Double RFP;
	private Double RPII;	
	private Double RRFP;
	private Double RRTP;
	private Double RTP;
	private Double SIX;
	private Double WMC;
	
	private List<DetectedSmell> smells;
	
	public ClassCommitChange(Class javaClass, CommitChange commitChange) {
		super();
		this.javaClass = javaClass;
		this.commitChange = commitChange;
	}
	
	public ClassCommitChange(Class javaClass) {
		super();
		this.javaClass = javaClass;
	}
	
	public Class getJavaClass() {
		return javaClass;
	}
	public void setJavaClass(Class javaClass) {
		this.javaClass = javaClass;
	}
	public CommitChange getCommitChange() {
		return commitChange;
	}
	public void setCommitChange(CommitChange commitChange) {
		this.commitChange = commitChange;
	}
	public Double getACAIC() {
		return ACAIC;
	}
	public void setACAIC(Double aCAIC) {
		ACAIC = aCAIC;
	}
	public Double getACMIC() {
		return ACMIC;
	}
	public void setACMIC(Double aCMIC) {
		ACMIC = aCMIC;
	}
	public Double getAID() {
		return AID;
	}
	public void setAID(Double aID) {
		AID = aID;
	}
	public Double getANA() {
		return ANA;
	}
	public void setANA(Double aNA) {
		ANA = aNA;
	}
	public Double getCAM() {
		return CAM;
	}
	public void setCAM(Double cAM) {
		CAM = cAM;
	}
	public Double getCBO() {
		return CBO;
	}
	public void setCBO(Double cBO) {
		CBO = cBO;
	}
	public Double getCIS() {
		return CIS;
	}
	public void setCIS(Double cIS) {
		CIS = cIS;
	}
	public Double getCLD() {
		return CLD;
	}
	public void setCLD(Double cLD) {
		CLD = cLD;
	}
	public Double getCP() {
		return CP;
	}
	public void setCP(Double cP) {
		CP = cP;
	}
	public Double getDAM() {
		return DAM;
	}
	public void setDAM(Double dAM) {
		DAM = dAM;
	}
	public Double getDCAEC() {
		return DCAEC;
	}
	public void setDCAEC(Double dCAEC) {
		DCAEC = dCAEC;
	}
	public Double getDCC() {
		return DCC;
	}
	public void setDCC(Double dCC) {
		DCC = dCC;
	}
	public Double getDCMEC() {
		return DCMEC;
	}
	public void setDCMEC(Double dCMEC) {
		DCMEC = dCMEC;
	}
	public Double getDIT() {
		return DIT;
	}
	public void setDIT(Double dIT) {
		DIT = dIT;
	}
	public Double getDSC() {
		return DSC;
	}
	public void setDSC(Double dSC) {
		DSC = dSC;
	}
	public Double getEIC() {
		return EIC;
	}
	public void setEIC(Double eIC) {
		EIC = eIC;
	}
	public Double getEIP() {
		return EIP;
	}
	public void setEIP(Double eIP) {
		EIP = eIP;
	}
	public Double getICH() {
		return ICH;
	}
	public void setICH(Double iCH) {
		ICH = iCH;
	}
	public Double getIR() {
		return IR;
	}
	public void setIR(Double iR) {
		IR = iR;
	}
	public Double getLCOM1() {
		return LCOM1;
	}

	public void setLCOM1(Double lCOM1) {
		LCOM1 = lCOM1;
	}

	public Double getLCOM2() {
		return LCOM2;
	}

	public void setLCOM2(Double lCOM2) {
		LCOM2 = lCOM2;
	}

	public Double getLCOM5() {
		return LCOM5;
	}

	public void setLCOM5(Double lCOM5) {
		LCOM5 = lCOM5;
	}

	public Double getLOC() {
		return LOC;
	}
	public void setLOC(Double lOC) {
		LOC = lOC;
	}
	public Double getMcCabe() {
		return McCabe;
	}
	public void setMcCabe(Double mcCabe) {
		McCabe = mcCabe;
	}
	public Double getMFA() {
		return MFA;
	}
	public void setMFA(Double mFA) {
		MFA = mFA;
	}
	public Double getMOA() {
		return MOA;
	}
	public void setMOA(Double mOA) {
		MOA = mOA;
	}
	public Double getNAD() {
		return NAD;
	}
	public void setNAD(Double nAD) {
		NAD = nAD;
	}
	public Double getNCM() {
		return NCM;
	}
	public void setNCM(Double nCM) {
		NCM = nCM;
	}
	public Double getNCP() {
		return NCP;
	}
	public void setNCP(Double nCP) {
		NCP = nCP;
	}
	public Double getNMA() {
		return NMA;
	}
	public void setNMA(Double nMA) {
		NMA = nMA;
	}
	public Double getNMD() {
		return NMD;
	}
	public void setNMD(Double nMD) {
		NMD = nMD;
	}
	public Double getNMI() {
		return NMI;
	}
	public void setNMI(Double nMI) {
		NMI = nMI;
	}
	public Double getNMO() {
		return NMO;
	}
	public void setNMO(Double nMO) {
		NMO = nMO;
	}
	public Double getNOA() {
		return NOA;
	}
	public void setNOA(Double nOA) {
		NOA = nOA;
	}
	public Double getNOC() {
		return NOC;
	}
	public void setNOC(Double nOC) {
		NOC = nOC;
	}
	public Double getNOD() {
		return NOD;
	}
	public void setNOD(Double nOD) {
		NOD = nOD;
	}
	public Double getNOF() {
		return NOF;
	}
	public void setNOF(Double nOF) {
		NOF = nOF;
	}
	public Double getNOH() {
		return NOH;
	}
	public void setNOH(Double nOH) {
		NOH = nOH;
	}
	public Double getNOM() {
		return NOM;
	}
	public void setNOM(Double nOM) {
		NOM = nOM;
	}
	public Double getNOP() {
		return NOP;
	}
	public void setNOP(Double nOP) {
		NOP = nOP;
	}
	public Double getNOPM() {
		return NOPM;
	}
	public void setNOPM(Double nOPM) {
		NOPM = nOPM;
	}
	public Double getNOTC() {
		return NOTC;
	}
	public void setNOTC(Double nOTC) {
		NOTC = nOTC;
	}
	public Double getNOTI() {
		return NOTI;
	}
	public void setNOTI(Double nOTI) {
		NOTI = nOTI;
	}
	public Double getNPrM() {
		return NPrM;
	}
	public void setNPrM(Double nPrM) {
		NPrM = nPrM;
	}

	public Double getPIIR() {
		return PIIR;
	}
	public void setPIIR(Double pIIR) {
		PIIR = pIIR;
	}
	public Double getPP() {
		return PP;
	}
	public void setPP(Double pP) {
		PP = pP;
	}
	public Double getREIP() {
		return REIP;
	}
	public void setREIP(Double rEIP) {
		REIP = rEIP;
	}
	public Double getRFC() {
		return RFC;
	}
	public void setRFC(Double rFC) {
		RFC = rFC;
	}
	public Double getRFP() {
		return RFP;
	}
	public void setRFP(Double rFP) {
		RFP = rFP;
	}
	public Double getRPII() {
		return RPII;
	}
	public void setRPII(Double rPII) {
		RPII = rPII;
	}
	public Double getRRFP() {
		return RRFP;
	}
	public void setRRFP(Double rRFP) {
		RRFP = rRFP;
	}
	public Double getRRTP() {
		return RRTP;
	}
	public void setRRTP(Double rRTP) {
		RRTP = rRTP;
	}
	public Double getRTP() {
		return RTP;
	}
	public void setRTP(Double rTP) {
		RTP = rTP;
	}
	public Double getSIX() {
		return SIX;
	}
	public void setSIX(Double sIX) {
		SIX = sIX;
	}
	public Double getWMC() {
		return WMC;
	}
	public void setWMC(Double wMC) {
		WMC = wMC;
	}
	
	public List<DetectedSmell> getSmells() {
        return smells;
    }

    public void setSmells(List<DetectedSmell> smells) {
        this.smells = smells;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commitChange == null) ? 0 : commitChange.hashCode());
		result = prime * result + ((javaClass == null) ? 0 : javaClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassCommitChange other = (ClassCommitChange) obj;
		if (commitChange == null) {
			if (other.commitChange != null)
				return false;
		} else if (!commitChange.equals(other.commitChange))
			return false;
		if (javaClass == null) {
			if (other.javaClass != null)
				return false;
		} else if (!javaClass.equals(other.javaClass))
			return false;
		return true;
	}
	
    

}
