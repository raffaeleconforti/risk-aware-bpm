package org.yawlfoundation.yawl.sensors.language;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class YExpressionSolver {
	
	private String currToken;
	private StringTokenizer st;
	public static final String CONTAINS = "}";
	public static final String ISCONTAINED = "{";
	public static final String DOT = ".";
	public static final String PARTA = "(";
	public static final String PARTC = ")";
	public static final String PARQA = "[";
	public static final String PARQC = "]";
	public static final String SUM = "+";
	public static final String SUB = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String EXP = "^";
	public static final String MOD = "%";
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String NOT = "!";
	public static final String MINOR = "<";
	public static final String MINOREQUAL = "<=";
	public static final String EQUAL = "==";
	public static final String SINGLEEQUAL = "=";
	public static final String NOTEQUAL = "!=";
	public static final String GREATEREQUAL = ">=";
	public static final String GREATER = ">";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	private final DateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	
	
}
