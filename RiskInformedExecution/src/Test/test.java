import java.util.*;import java.math.*;public class Consequence_null_Activities {public static Double execute(Object[] o) {Object newClaim = o[0];
Object closeFile = o[1];
Object incomingCorr = o[2];
Object conductFile = o[3];
Object contactCust = o[4];
Object processAddInfo = o[5];
Object genPay = o[6];
Object authPay = o[7];
Object FollowUptimes = o[8];
return execute2(newClaim, closeFile, incomingCorr, conductFile, contactCust, processAddInfo, genPay, authPay, FollowUptimes);}private static Double execute2(Object newClaim, Object closeFile, Object incomingCorr, Object conductFile, Object contactCust, Object processAddInfo, Object genPay, Object authPay, Object FollowUptimes) {Long a1 = Long.parseLong((String) newClaim);
          	Long a2 = Long.parseLong((String) incomingCorr);
          	Long a3 = Long.parseLong((String) conductFile);
          	Long a4 = Long.parseLong((String) contactCust);
          	Long a5 = Long.parseLong((String) processAddInfo);
          	Long a6 = Long.parseLong((String) genPay);
          	Long a7 = Long.parseLong((String) authPay);
          	Long a8 = Long.parseLong((String) FollowUptimes);
          	Long a9 = Long.parseLong((String) closeFile);
          	int min = 0;
          	if(a1 > 0)  min++;
          	if(a2 > 0)  min++;
          	if(a3 > 0)  min++;
          	if(a4 > 0)  min++;
          	if(a5 > 0)  min++;
          	if(a6 > 0)  min++;
          	if(a7 > 0)  min++;
          	if(a8 > 0)  min++;
          	if(a9 > 0)  min++;
          	long total = a1+a2+a3+a4+a5+a6+a7+a8+a9;
          	double res = total-min;
          	if(total > min) {
          		if(total < 40) return res;
          		else return 40.0;
          	}
          	return 0.0;}}