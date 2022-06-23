package uk.ac.kcl.cch.jb.sparql.dnd;

import org.eclipse.gef.dnd.SimpleObjectTransfer;

public class TransferHandler {

	
	public static final SimpleObjectTransfer TRANSFER = new SimpleObjectTransfer() {
		private final String TYPE_NAME = "org.eclipse.gef.clipboard.transfer"; //$NON-NLS-1$
		private final int TYPE_ID = registerType(TYPE_NAME);
		protected int[] getTypeIds() {
			return new int[] {TYPE_ID};
		}
		protected String[] getTypeNames() {
			return new String[] {TYPE_NAME};
		}
	};
	
	public static Object currentObject = null;
	
	public static void setCurrentObject(Object thing) {
		currentObject = thing;
	}
	
	public static Object getCurrentObject() {
		return currentObject;
	}

}
