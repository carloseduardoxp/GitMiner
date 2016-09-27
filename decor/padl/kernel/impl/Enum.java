package padl.kernel.impl;

import padl.kernel.IEntityMarker;
import padl.kernel.IEnum;

public class Enum extends AbstractClass implements IEntityMarker, IEnum {
	private static final long serialVersionUID = 7309059468835692115L;

	public Enum(final char[] anID, final char[] aName,String localPath) {
		super(anID, aName,localPath);
	}
}
