package org.yawlfoundation.yawl.sensors.language;

import javax.servlet.jsp.el.ELException;

import org.apache.commons.el.ExpressionEvaluatorImpl;

public class ExpressionTester {
	
	public static final String[] brackets= new String[] {"(" , ")"};
	public static final String[] mathops= new String[] {"+" , "-" , "*" , "/"};
	public static final String[] compops= new String[] {"<" , "<=" , "==" , "!=" , ">=" , ">"};
	public static final String[] boolops1= new String[] {"&&" , "||"};
	public static final String[] boolops2= new String[] {"&" , "|"};
	public static final String[] bools= new String[] {"false" , "true"};
		
	public static ExpressionEvaluatorImpl exp1 = new ExpressionEvaluatorImpl();
	public static YExpression exp2 = new YExpression();
	
	public static void main(String[] args) {
		boolean different = false;
		int i = 0;
		int j = 0;
		long iTot = 0;
		long jTot = 0;
		int count = 0;
		while(!different && count<20000) {
			count++;
			String[] res = generateExpression();
			
			System.out.println(res[0]);
			System.out.println(res[1]);
			try {
				exp1.parseExpression(res[0], Boolean.class, null);
				
				long t1 = System.nanoTime();
				Boolean result1 = (Boolean) exp1.evaluate("${"+res[0]+"}", Boolean.class, null, null);
				t1 = System.nanoTime()-t1;
//					System.out.println(result1);
				
				long t2 = System.nanoTime();
				Boolean result2 = (Boolean) exp2.booleanEvaluation("("+res[1]+")", null, null, null);
				t2 = System.nanoTime()-t2;
//					System.out.println(result2);
				
				if(t1-t2 > 0) {
					i++;
					iTot+=(t1-t2);
				}else if(t2-t1 > 0) {
					j++;
					jTot+=(t2-t1);
				}
				if(result1!=result2) {
					different = true;
				}
			} catch (ELException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(i + " " + j + " " + iTot + " " + jTot + " " + count);
	}
	
	public static String[] generateExpression() {
		String res1 = "";
		String res2 = "";
		
		boolean first = true;
		while(first || Math.random()>0.2) {
			if(first) first = false;
			else {
				int boolop = (int) Math.round((boolops1.length-1)*Math.random());
				res1 += boolops1[boolop];
				res2 += boolops2[boolop];
			}
			
			boolean bracket = false;
			if(Math.random() > 0.5) {
				bracket = true;
				res1 += "!(";
				res2 += "!(";
			}

			if(Math.random() > 0.5) {
				if(Math.random() > 0.6) {
					String[] res = createBrackets();
					res1 += res[0];
					res2 += res[1];
				}else {
					Double a = 100*Math.random();
					res1 += a;
					res2 += a;
				}
				
				int compop = (int) Math.round((compops.length-1)*Math.random());
				res1 += compops[compop];
				res2 += compops[compop];
				
				if(Math.random() > 0.6) {
					String[] res = createBrackets();
					res1 += res[0];
					res2 += res[1];
				}else {
					Double a = 100*Math.random();
					res1 += a;
					res2 += a;
				}
			}else {
				int bool = (int) Math.round((bools.length-1)*Math.random());;
				res1 += bools[bool];
				res2 += bools[bool];
			}
			
			if(bracket) {
				res1 += ")";
				res2 += ")";
			}
			int boolop = (int) Math.round((boolops1.length-1)*Math.random());
			res1 += boolops1[boolop];
			res2 += boolops2[boolop];
			bracket = false;
			if(Math.random() > 0.5) {
				bracket = true;
				res1 += "!(";
				res2 += "!(";
			}
			
			if(Math.random() > 0.5) {
				if(Math.random() > 0.6) {
					String[] res = createBrackets();
					res1 += res[0];
					res2 += res[1];
				}else {
					Double a = 100*Math.random();
					res1 += a;
					res2 += a;
				}
				
				int compop = (int) Math.round((compops.length-1)*Math.random());
				res1 += compops[compop];
				res2 += compops[compop];
				
				if(Math.random() > 0.6) {
					String[] res = createBrackets();
					res1 += res[0];
					res2 += res[1];
				}else {
					Double a = 100*Math.random();
					res1 += a;
					res2 += a;
				}
			}else {
				int bool = (int) Math.round((bools.length-1)*Math.random());;
				res1 += bools[bool];
				res2 += bools[bool];
			}
				
			if(bracket) {
				res1 += ")";
				res2 += ")";
			}
		}
		
		return new String[] {res1, res2};
	}
	
	public static String[] createBrackets() {
		String res1 = "(";
		String res2 = "(";
		Double a = null;
		
		if(Math.random() > 0.2) {
			a = 100*Math.random();
			res1 += a;
			res2 += a;
		}else {
			String[] res = createBrackets();
			res1 += res[0];
			res2 += res[1];
		}
		
		while(Math.random() > 0.2) {
			int mathop = (int) Math.round((mathops.length-1)*Math.random());
			res1 += mathops[mathop];
			res2 += mathops[mathop];
			
			if(Math.random() > 0.2) {
				a = 100*Math.random();
				res1 += a;
				res2 += a;
			}else {
				String[] res = createBrackets();
				res1 += res[0];
				res2 += res[1];
			}
		}
		
		res1 += ")";
		res2 += ")";
		
		return new String[] {res1, res2};
	}
}
