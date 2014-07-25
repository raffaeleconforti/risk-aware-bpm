package org.yawlfoundation.yawl.riskPrediction.DatabaseInterface;

public class CacheKey {

	String a, b, c;
	
	public CacheKey(String a, String b, String c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CacheKey) {
			CacheKey cache = (CacheKey) o;
			boolean checkA = false;
			boolean checkB = false;
			boolean checkC = false;
			
			if(a == null && cache.a == null) {
				checkA = true;
			}else if(a != null && cache.a != null) {
				if(a.equals(cache.a)) checkA = true;
			}
			
			if(b == null && cache.b == null) {
				checkB = true;
			}else if(b != null && cache.b != null) {
				if(b.equals(cache.b)) checkB = true;
			}
			
			if(c == null && cache.c == null) {
				checkC = true;
			}else if(c != null && cache.c != null) {
				if(c.equals(cache.c)) checkC = true;
			}
			
			return (checkA && checkB && checkC);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		sb.append(a);
		sb.append(b);
		sb.append(c);
		return sb.toString().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(a);
		sb.append(b);
		sb.append(c);
		return sb.toString();
	}
	
}
