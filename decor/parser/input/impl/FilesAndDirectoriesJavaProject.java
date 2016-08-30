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
package parser.input.impl;

import java.util.ArrayList;
import java.util.List;
import parser.reader.NamedReader;
import parser.reader.NamedReaderType;

public class FilesAndDirectoriesJavaProject extends FileSystemJavaProject {

	private final NamedReader[] namedReaders;
	public FilesAndDirectoriesJavaProject(
		final List<String> classPaths,
		final List<String> sourceFilesAndDirectories,
		final List<String> aListOfFilesAndDiecrtories) throws Exception {

		super(classPaths, sourceFilesAndDirectories);
		this.namedReaders =
			this.buildSourcepathEntries(aListOfFilesAndDiecrtories);

	}

	protected NamedReader[] buildCompilationUnitList() {
		final List<NamedReader> compilationUnits = new ArrayList<NamedReader>();

		for (final NamedReader src : this.namedReaders) {
			compilationUnits.addAll(this.findReadersByType(
				src,
				NamedReaderType.JavaFile));
		}
		return compilationUnits.toArray(new NamedReader[0]);
	}

	protected List<NamedReader> findReadersByType(
		final NamedReader parent,
		final NamedReaderType type) {

		final List<NamedReader> readersFound = new ArrayList<NamedReader>();

		if (parent.getType() == NamedReaderType.JarFile) {
			readersFound.add(parent);
		}
		else if (parent.getType() == NamedReaderType.Directory) {
			final NamedReader[] elements = parent.read();
			if (elements != null) {
				for (final NamedReader element : elements) {
					if (element != null) {
						if (element.getType() == NamedReaderType.Directory) {
							readersFound.addAll(this.findReadersByType(
								element,
								type));
						}
						else if (element.getType() == type) {
							readersFound.add(element);
						}
					}
				}
			}
		}
		else if (parent.getType() == NamedReaderType.JavaFile) {
			readersFound.add(parent);
		}
		return readersFound;
	}
}
