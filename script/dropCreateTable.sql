-- MySQL dump 10.10
--
-- Host: localhost    Database: relatorios
-- ------------------------------------------------------
-- Server version	5.0.18-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


 -- DROP DATABASE IF EXISTS `db_gitMiner`;

 -- CREATE DATABASE `db_gitMiner`;
 
 -- USE db_gitMiner;

DROP TABLE IF EXISTS `TB_DETECTED_SMELL`;
DROP TABLE IF EXISTS `TB_CLASS_COMMIT_CHANGE`;
DROP TABLE IF EXISTS `TB_COMMIT_CHANGE`;
DROP TABLE IF EXISTS `TB_COMMIT`;
DROP TABLE IF EXISTS `TB_CLASS`;
DROP TABLE IF EXISTS `TB_BRANCH`;
DROP TABLE IF EXISTS `TB_PROJECT`;

--
-- Table structure for table `TB_PROJECT`
--

CREATE TABLE `TB_PROJECT` (
  `CD_PROJECT` int(11) NOT NULL auto_increment,
  `NM_PROJECT` varchar(255) NOT NULL,
  `DS_URL` varchar(255) NOT NULL,
  PRIMARY KEY  (`CD_PROJECT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `TB_BRANCH`
--

CREATE TABLE `TB_BRANCH` (
  `CD_BRANCH` int(11) NOT NULL auto_increment,
  `NM_BRANCH` varchar(255) NOT NULL,
  `CD_PROJECT` int(11) NOT NULL,
  PRIMARY KEY  (`CD_BRANCH`),
  KEY `ProjectIDBranch` (`CD_PROJECT`),
  CONSTRAINT `fk_PROJECT` FOREIGN KEY (`CD_PROJECT`) REFERENCES `TB_PROJECT` (`CD_PROJECT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `TB_CLASS`
--

CREATE TABLE `TB_CLASS` (
  `CD_CLASS` int(11) NOT NULL auto_increment,
  `NM_CLASS` varchar(255) default NULL,
  `CD_BRANCH` int(11) NOT NULL,
  PRIMARY KEY  (`CD_CLASS`),
  KEY `BranchIDClass` (`CD_BRANCH`),
  KEY `ClassName` (`NM_CLASS`),
  CONSTRAINT `fk_branch_class` FOREIGN KEY (`CD_BRANCH`) REFERENCES `TB_BRANCH` (`CD_BRANCH`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



--
-- Table structure for table `TB_COMMIT`
--
CREATE TABLE `TB_COMMIT` (
   `DS_HASH` char(40) NOT NULL,   
   `DT_COMMIT` datetime NOT NULL,
   `NM_AUTHOR` varchar(255) DEFAULT NULL,   
   `DS_EMAIL_AUTHOR` varchar(255) DEFAULT NULL,
   `DS_SUBJECT` TEXT DEFAULT NULL,
   `CD_BRANCH` int(11) NOT NULL,
    PRIMARY KEY (`DS_HASH`),
    KEY `BranchIDCommit` (`CD_BRANCH`),
    CONSTRAINT `fk_branch_commit` FOREIGN KEY (`CD_BRANCH`) REFERENCES `TB_BRANCH` (`CD_BRANCH`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `TB_COMMIT_CHANGE`
--
CREATE TABLE `TB_COMMIT_CHANGE` (  
   `CD_COMMIT_CHANGE` int(11) NOT NULL AUTO_INCREMENT,
   `DS_MODIFICATION_TYPE` varchar(50) DEFAULT NULL,
   `NM_FILE` varchar(255) NOT NULL,      
   `DS_HASH` char(40) NOT NULL,
   PRIMARY KEY (`CD_COMMIT_CHANGE`),
   KEY `CommitIDCommitChange` (`DS_HASH`),   
   KEY `IDFileName` (`NM_FILE`),   
   CONSTRAINT `fk_commit_commitChange` FOREIGN KEY (`DS_HASH`) REFERENCES `TB_COMMIT` (`DS_HASH`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `TB_CLASS_COMMIT_CHANGE`
--

CREATE TABLE `TB_CLASS_COMMIT_CHANGE` (
   `CD_COMMIT_CHANGE` int(11) NOT NULL,
   `CD_CLASS` int(11) NOT NULL,
   `ACAIC` decimal(7,2),
   `ACMIC` decimal(7,2),
   `AID` decimal(7,2),
   `ANA` decimal(7,2),
   `CAM` decimal(7,2),
   `CBO` decimal(7,2),
   `CIS` decimal(7,2),
   `CLD` decimal(7,2),
   `CP` decimal(7,2),
   `DAM` decimal(7,2),
   `DCAEC` decimal(7,2),
   `DCC` decimal(7,2),
   `DCMEC` decimal(7,2),
   `DIT` decimal(7,2),
   `DSC` decimal(7,2),
   `EIC` decimal(7,2),
   `EIP` decimal(7,2),
   `ICH` decimal(7,2),
   `IR` decimal(7,2),
   `LCOM` decimal(7,2),
   `LOC` decimal(7,2),
   `McCabe` decimal(7,2),
   `MFA` decimal(7,2),
   `MOA` decimal(7,2),
   `NAD` decimal(7,2),
   `NCM` decimal(7,2),
   `NCP` decimal(7,2),
   `NMA` decimal(7,2),
   `NMD` decimal(7,2),
   `NMI` decimal(7,2),
   `NMO` decimal(7,2),
   `NOA` decimal(7,2),
   `NOC` decimal(7,2),
   `NOD` decimal(7,2),
   `NOF` decimal(7,2),
   `NOH` decimal(7,2),
   `NOM` decimal(7,2),
   `NOP` decimal(7,2),
   `NOPM` decimal(7,2),
   `NOTC` decimal(7,2),
   `NOTI` decimal(7,2),
   `NPrM` decimal(7,2),
   `OneWay` decimal(7,2),
   `ng` decimal(7,2),
   `PIIR` decimal(7,2),
   `PP` decimal(7,2),
   `REIP` decimal(7,2),
   `RFC` decimal(7,2),
   `RFP` decimal(7,2),
   `RPII` decimal(7,2),
   `RRFP` decimal(7,2),
   `RRTP` decimal(7,2),
   `RTP` decimal(7,2),
   `SIX` decimal(7,2),
   `WMC` decimal(7,2),
   PRIMARY KEY (`CD_COMMIT_CHANGE`,`CD_CLASS`),
   CONSTRAINT `fk_class_commitChange` FOREIGN KEY (`CD_CLASS`) REFERENCES `TB_CLASS` (`CD_CLASS`),
   CONSTRAINT `fk_commitChange_class` FOREIGN KEY (`CD_COMMIT_CHANGE`) REFERENCES `TB_COMMIT_CHANGE` (`CD_COMMIT_CHANGE`)
);

--
-- Table structure for table `TB_DETECTED_SMELL`
--
CREATE TABLE `TB_DETECTED_SMELL` (  
   `CD_DETECTED_SMELL` int(11) NOT NULL AUTO_INCREMENT,
   `NM_DETECTED_SMELL` varchar(255) NOT NULL,
   `DS_OBSERVACAO` varchar(255) DEFAULT NULL,
   `CD_COMMIT_CHANGE` int(11) NOT NULL,
   `CD_CLASS` int(11) NOT NULL,
   PRIMARY KEY (`CD_DETECTED_SMELL`),
   KEY `CommitChangeIDDetectedSmell` (`CD_COMMIT_CHANGE`),
   CONSTRAINT `fk_commitChange_DetectedSmell` FOREIGN KEY (`CD_COMMIT_CHANGE`) REFERENCES 
              `TB_CLASS_COMMIT_CHANGE` (`CD_COMMIT_CHANGE`),
   CONSTRAINT `fk_class_DetectedSmell` FOREIGN KEY (`CD_CLASS`) REFERENCES 
              `TB_CLASS_COMMIT_CHANGE` (`CD_CLASS`)              
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
