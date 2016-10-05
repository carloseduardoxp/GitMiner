package miner.model.domain;

public enum Metric {
	
	 ACAIC("Ancestor class-attribute import coupling of an entity."),
	 ACMIC("Ancestors class-method import coupling of an entity."),
	 AID("Average inheritance depth of an entity. Uses a recursive algorithm to calculate it."),
	 ANA("Average number of classes from which a class inherits information."),
	 CAM("Relateness among methods of the class based upon the parameter list of the methods. The metrics is computed using the summation of the intersection of parameters of a method with the maximum independant set of all parameter types in the class."),
	 CBO("Coupling Between Objects of an entity."),
	 CIS("Number of public methods in an entity."),
	 CLD("Class-to-leaf depth of an entity."),
	 CP("Number of packages that depend on the package containing entity."),
	 DAM("Ratio of the number of private and protected attributes to the total number of attributes declared in an entity."),
	 DCAEC("Descendants class-attribute export coupling of an entity."),
	 DCC("Number of classes that a class is directly related to (by attribute declarations and message passing)"),
	 DCMEC("Descendants class-method export coupling of an entity"),
	 DIT("Depth of inheritance tree of an entity. Uses a recursive algorithm to calculate it"),
	 DSC("Total number of entities in a design"),
	 EIC("Number of inheritance relationships in which superclasses are in external packages"),
	 EIP("Number of inheritance relationships where the superclass is in the package containing entity and the subclass is in another package"),
	 ICHClass("Complexity of an entity as the sum of the complexities of its declared and inherited methods"),
	 IR("Inheritance ratio"),
	 LCOM1("Lack of cohesion in methods of an entity"),
	 LCOM2("Lack of cohesion in methods of an entity"),
	 LCOM5("Lack of cohesion in methods of an entity"),
	 LOC("Number of lines of code of all the methods of an entity"),
	 McCabe("McCabe Complexity: Number of points of decision"),
	 MFA("Ratio of the number of methods inherited by an entity to the number of methods accessible by member methods of the entity"),
	 MOA("Data declarations whose types are user-defined entities"),
	 NAD("Number of attributes declared by an entity"),
	 NCM("Number of changed methods of in an entity"),
	 NCP("Number of packages containing an entity"),
	 NMA("Number of new methods declared by an entity"),
	 NMD("Number of methods declared by an entity"),
	 NMI("Number of methods inherited of an entity. Constructors or not considered as method, they are not counted in the result of the metric"),
	 NMO("Number of methods overridden by an entity"),
	 NOA("Number of ancestors of an entity"),
	 NOC("Number of children of an entity"),
	 NOD("Number of descendents of an entity"),
	 NOF("Number of attributes declared by an entity"),
	 NOH("Number of class hierarchies in the model"),
	 NOM("Number of all methods defined in an entity"),
	 NOP("Number of parents of an entity"),
	 NOPM("Number of methods that can exhibit polymorphic behavior. (A method can exhibit polymorphic behaviour if it is overridden by one or more descendent classes"),
	 NOTC("Number of invocations of JUnit assert methods that occur in the code of a test case"),
	 NOTI("Highest number of transitive invocation among methods of a class. See the Law of Demeter for a definition"),
	 NPrM("Number protected members of an entity"),
	 PIIR("Number of inheritance relationships existing between entities in the package containing the entity"),
	 PP("Number of provider packages of the package containing the entity"),
	 REIP("EIP divided by the sum of PIIR and EIP"),
	 RFC("Response for class: a count of the number of methods of an entity and the number of methods of other entities that are invoked by the methods of the entity"),
	 RFP("Number of entities referenced from the entities belonging to other packages than the package containing the entity."),
	 RPII("PIIR divided by the sum of PIIR and EIP"),
	 RRFP("RFP divided by the sum of RFP and the number of internal class references"),
	 RRTP("RTP divided by the sum of RTP and the number of internal class references"),
	 RTP("Number of entities referenced by entities in the package containing the entity wrt. to entities in other packages"),
	 SIX("Specialisation indeX of an entity"),
	 WMC("Weight of an entity as the number of method invocations in each method. (Default constructors are considered even if not explicitely declared.)");
       
         
     private String description;
     
     private Metric(String description) {
         this.description = description;
     }
     
     @Override
     public String toString() {
         return this.name()+" - "+description;
     }

}
