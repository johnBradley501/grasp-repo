package uk.ac.kcl.cch.jb.sparql.model;

public class ModifierComponent {
	
	public static final int DISTINCT_COMPONENT = 0;
	public static final int LIMIT_COMPONENT = 1;
	public static final int OFFSET_COMPONENT = 2;
	public static final int PADDING_COMPONENT = 3;
	
	private SelectClauseModifier modifier;
	private int type;
	
	public ModifierComponent(int type, SelectClauseModifier modifier) {
		this.modifier = modifier;
		this.type = type;
	}
	
	public SelectClauseModifier getModifier() {return modifier;}
	public int getType() {return type;}

}
