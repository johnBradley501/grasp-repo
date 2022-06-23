package uk.ac.kcl.cch.jb.sparql.builder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.sparqlbuilder.constraint.Expression;
import org.eclipse.rdf4j.sparqlbuilder.constraint.Expressions;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;
import org.eclipse.rdf4j.sparqlbuilder.rdf.RdfLiteral;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import uk.ac.kcl.cch.jb.sparql.model.SelectVarItem;

public class ExpressionMaker {
	
	// https://rdf4j.org/javadoc/3.2.0/org/eclipse/rdf4j/sparqlbuilder/constraint/Expressions.html
	
	public static ExpressionMaker instance = new ExpressionMaker();
	
	public static final int ABS = 0;
	public static final int AVG = 1;
	public static final int BOUND = 2;
	public static final int CEIL = 3;
	public static final int COUNT = 4;
	public static final int EQUALS = 5;
	public static final int GT = 6;
	public static final int GTE = 7;
	public static final int LT = 8;
	public static final int LTE = 9;
	public static final int MAX = 10;
	public static final int MIN = 11;
	public static final int MINUS = 12;
	public static final int NOT = 13;
	public static final int NOTEQ = 14;
	public static final int PLUS = 15;
	public static final int REGEXP = 16;
	public static final int REGEXPI = 17;
	public static final int SAMPLE = 18;
	public static final int STR = 19;
	public static final int SUM = 20;
	public static final int STRSTARTS = 21;
	public static final int STRENDS = 22;
	public static final int CONTAINS = 23;
	public static final int STRBEFORE = 24;
	public static final int STRAFTER = 25;
	public static final int STRLANG = 26;
	
	public static final String[] ExpressionNames = {"abs","avg","bound","ceil","count","=",">",">=","<","<=","max","min","-","not","¬=","+","RE","REi","sample","str","sum",
			"starts", "ends", "contains", "before", "after", "lang"};

	private static JSONObject typeData = null;
	private static Map<String, int[]> valArrays = new HashMap<String, int[]>();
	private static Map<String, String[]> nameArrays = new HashMap<String, String[]>();
	private static Map<String, String> typeChecks = new HashMap<String, String> ();
	private static Map<String, int[]> aggValArrays = new HashMap<String, int[]>();
	private static Map<String, String[]> aggNameArrays = new HashMap<String, String[]>();

	
	private ExpressionMaker() {
		
	}
	
	private int[] jArray2ints(JSONArray jArray, boolean emptyAllowed) {
		int offset = emptyAllowed? 2:0;
		int[] rslt = new int[jArray.length()+offset];
		int cnt = offset;
		for(Object item: jArray) {
			rslt[cnt++] = (Integer)item;
		}
		if(emptyAllowed) {
			rslt[0] = SelectVarItem.NOAGG;
			rslt[1] = SelectVarItem.GROUPBY;
		}
		return rslt;
	}
	
	public int[] getExpressionValList(String type) {
		if(valArrays.containsKey(type))return valArrays.get(type);
		if(typeData == null) loadTypeData();
		if(!typeData.has(type))type="undefinedType";
		JSONArray jArray = null;
		try {
			jArray = typeData.getJSONArray(type).getJSONArray(1);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		int[] rslt = jArray2ints(jArray, false);
		valArrays.put(type,  rslt);
		return rslt;
	}
	
	public String[] getExpressionNameList(String type) {
		if(nameArrays.containsKey(type))return nameArrays.get(type);
		int[] numbs = getExpressionValList(type);
		String[] rslt = new String[numbs.length];
		int cnt = 0;
		for (int item: numbs) {
			rslt[cnt++] = ExpressionMaker.ExpressionNames[item];
		}
		nameArrays.put(type,  rslt);
		return rslt;
	}
	
	public int[] getAggregateValLists(String type) {
		if(aggValArrays.containsKey(type))return aggValArrays.get(type);
		if(typeData == null) loadTypeData();
		if(!typeData.has(type))type="undefinedType";
		JSONArray jArray = null;
		try {
			jArray = typeData.getJSONArray(type).getJSONArray(2);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		int[] rslt = jArray2ints(jArray, true);
		aggValArrays.put(type,  rslt);
		return rslt;
		
	}
	
	public String[] getAggregateNameList(String type) {
		if(aggNameArrays.containsKey(type))return aggNameArrays.get(type);
		int[] numbs = getAggregateValLists(type);
		String[] rslt = new String[numbs.length];
		rslt[0] = "--";
		rslt[1] = "group by";
		int cnt = 0;
		for (int item: numbs) {
			if(item >= 0)rslt[cnt] = ExpressionMaker.ExpressionNames[item];
			cnt++;
		}
		aggNameArrays.put(type,  rslt);
		return rslt;
	}

	
	public String getCheckType(String type) {
		if(typeChecks.containsKey(type))return typeChecks.get(type);
		if(typeData == null) loadTypeData();
		if(!typeData.has(type))type="undefinedType";
		String rslt = "";
		try {
			rslt = typeData.getJSONArray(type).getString(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		typeChecks.put(type, rslt);
		return rslt;
	}
	
	private void loadTypeData() {
		// https://stackoverflow.com/questions/12292926/what-is-the-difference-between-getresourceasstream-with-and-without-getclassload
		InputStream inputStream = getClass().getResourceAsStream("ExpressionMappings.json");
		InputStreamReader reader = new InputStreamReader(inputStream);
		try {
			typeData = new JSONObject(new JSONTokener(reader));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}


	public Expression getExpression(int type, Variable var) {
		if(type == ABS)return Expressions.abs(var);
		if(type == AVG)return Expressions.avg(var);
		if(type == BOUND)return Expressions.bound(var);
		if(type == CEIL)return Expressions.ceil(var);
		if(type == COUNT)return Expressions.count(var);
		if(type == MAX)return Expressions.max(var);
		if(type == MIN)return Expressions.min(var);
		if(type == MINUS)return Expressions.minus(var);
		if(type == NOT)return Expressions.not(var);
		if(type == PLUS)return Expressions.plus(var);
		if(type == SAMPLE)return Expressions.sample(var);
		if(type == STR)return Expressions.str(var);
		return null;
	}
	
	public Expression getExpression(int type, Variable var, int numb) {
		RdfLiteral lit = Rdf.literalOf(numb);
		if(type == EQUALS)return Expressions.equals(var,  lit);
		if(type == GT)return Expressions.gt(var,  lit);
		if(type == GTE)return Expressions.gte(var,  lit);
		if(type == LT)return Expressions.lt(var,  lit);
		if(type == LTE)return Expressions.lte(var,  lit);
		if(type == NOTEQ)return Expressions.notEquals(var,  lit);
		return null;
	}
	
	public Expression getExpression(int type, Variable var, float numb) {
		RdfLiteral lit = Rdf.literalOf(numb);
		if(type == EQUALS)return Expressions.equals(var,  lit);
		if(type == GT)return Expressions.gt(var,  lit);
		if(type == GTE)return Expressions.gte(var,  lit);
		if(type == LT)return Expressions.lt(var,  lit);
		if(type == LTE)return Expressions.lte(var,  lit);
		if(type == NOTEQ)return Expressions.notEquals(var,  lit);
		return null;
	}
	
	public Expression getExpression(int type, Variable var, boolean val) {
		RdfLiteral lit = Rdf.literalOf(val);
		if(type == EQUALS)return Expressions.equals(var,  lit);
		if(type == NOTEQ)return Expressions.notEquals(var,  lit);
		return null;
	}
	
	public Expression getExpression(int type, Variable var, String val) {
		RdfLiteral lit = Rdf.literalOf(val);
		if(type == EQUALS)return Expressions.equals(var,  lit);
		if(type == GT)return Expressions.gt(var,  lit);
		if(type == GTE)return Expressions.gte(var,  lit);
		if(type == LT)return Expressions.lt(var,  lit);
		if(type == LTE)return Expressions.lte(var,  lit);
		if(type == NOTEQ)return Expressions.notEquals(var,  lit);
		if(type == REGEXP)return Expressions.regex(var,  val);
		if(type == REGEXPI)return Expressions.regex(var,  val, "i");
		if(type == STRSTARTS)return Expressions.function(org.eclipse.rdf4j.sparqlbuilder.constraint.SparqlFunction.STRSTARTS, var,  lit);
		if(type == STRENDS)return Expressions.function(org.eclipse.rdf4j.sparqlbuilder.constraint.SparqlFunction.STRENDS, var,  lit);
		if(type == CONTAINS)return Expressions.function(org.eclipse.rdf4j.sparqlbuilder.constraint.SparqlFunction.CONTAINS, var,  lit);
		if(type == STRLANG)return Expressions.function(org.eclipse.rdf4j.sparqlbuilder.constraint.SparqlFunction.LANG, var,  lit);
		return null;
	}
}
