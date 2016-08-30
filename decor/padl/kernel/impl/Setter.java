/*******************************************************************************
 * Copyright (c) 2001-2014 Yann-Ga�l Gu�h�neuc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Yann-Ga�l Gu�h�neuc and others, see in file; API and its implementation
 ******************************************************************************/
package padl.kernel.impl;

import java.util.Iterator;
import padl.kernel.IConstituent;
import padl.kernel.IMethod;
import padl.kernel.ISetter;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2005/08/05
 */
class Setter extends Method implements ISetter {
	private static final long serialVersionUID = 1314084068977253108L;
	private int cardinality;
	public Setter(final IMethod aMethod) {
		super(aMethod.getID());

		this.setAbstract(aMethod.isAbstract());
		if (!aMethod.isAbstract()) {
			this.setCodeLines(aMethod.getCodeLines());
		}
		this.setComment(aMethod.getComment());
		this.setDisplayName(aMethod.getDisplayName());
		this.setName(aMethod.getName());
		this.setPrivate(aMethod.isPrivate());
		this.setPublic(aMethod.isPublic());
		this.setReturnType(aMethod.getReturnType());
		this.setStatic(aMethod.isStatic());
		this.setVisibility(aMethod.getVisibility());
		this.setWeight(aMethod.getWeight());

		final Iterator iterator = aMethod.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			// Yann 2007/11/14: Clone?
			// Why did I clone the constituent in the method
			// before "moving" them into the getter?
			//	constituent.startCloneSession();
			//	constituent.performCloneSession();
			//	final IConstituent clone = constituent.getClone();
			//	constituent.endCloneSession();
			//	this.addConstituent(clone);
			this.addConstituent(constituent);
		}
	}
	public Setter(final char[] anID) {
		super(anID);
	}
	public int getCardinality() {
		return this.cardinality;
	}
	public void setCardinality(final int aCardinality) {
		this.cardinality = aCardinality;
	}
}
