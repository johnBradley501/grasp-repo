package uk.ac.kcl.cch.jb.sparql.utils;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.cch.jb.sparql.model.ClassComponent;
import uk.ac.kcl.cch.jb.sparql.model.INamedComponent;
import uk.ac.kcl.cch.jb.sparql.model.VariableComponent;

public class VarNameManager {
	
	private Map<String, Integer> lastCount = new HashMap<String, Integer>();
	
	public void addName(INamedComponent comp) {
		String name = comp.getName();
		String parts[] = name.split("_");
		String lastPart = parts[parts.length-1];
		int count = 0;
		if(lastPart.matches("^[0-9]+$")) {
			count = Integer.parseInt(lastPart);
			name = name.substring(0,name.length()-lastPart.length()-1);
		}
		if(lastCount.containsKey(name)) {
			if(lastCount.get(name) < count)lastCount.put(name, count);
		} else {
			lastCount.put(name,  count);
		}
	}
	
	public String proposeName(INamedComponent comp) {
		return generateName(comp.getBasisForName());
	}
	
	//public String proposeName(ClassComponent cc) {
	//	return generateName(cc.getDisplayURI());
	//}
	
	//public String proposeName(VariableComponent vc) {
	//	if(vc.getMyProperty() == null)return generateName("var");
	//	return generateName(vc.getMyProperty().getDisplayURI());
	//}

	private String generateName(String dName) {
		String interim = "var";
		if(dName.matches("^[^:]*:[^:/]+$")) {
			String[]dNameParts = dName.split(":");
			interim = dNameParts[1].toLowerCase();
		}
		return buildNewname(interim);	
	}
	
	private String buildNewname(String name) {
		int index = 1;
		if(lastCount.containsKey(name)) {
			index = lastCount.get(name) + 1;
			lastCount.put(name, index);
		} else lastCount.put(name, index);
		String rslt = name;
		if(index > 1)rslt = rslt+"_"+index;
		return rslt;
	}

}
