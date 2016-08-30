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
package util.io;

import java.io.IOException;
import java.io.Writer;

/**
 * @author 	Yann-Ga�l Gu�h�neuc
 */
public class NullWriter extends Writer {
	public void close() throws IOException {
	}
	public void flush() throws IOException {
	}
	public void write(final char[] cbuf, final int off, final int len)
			throws IOException {
	}
}
