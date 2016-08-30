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
package padl.event;

import java.util.List;
import padl.kernel.IAbstractLevelModel;

public final class IdentificationEvent implements IEvent {
	private final String recognizedConstituentName;

	public IdentificationEvent(final String recognizedConstituentName) {
		this.recognizedConstituentName = recognizedConstituentName;
	}
	public String getConstituentName() {
		return this.recognizedConstituentName;
	}
	public IAbstractLevelModel getAbstractModel() {
		return null;
	}
	public List getSubmittedConstituents() {
		return null;
	}
}
