import java.util.Iterator;
import java.util.List;

public class FaultCondition_null_FollowUp
{

    public FaultCondition_null_FollowUp()
    {
    }

    public static Boolean execute(Object aobj[])
    {
        Object obj = aobj[0];
        return execute2(obj);
    }

    private static Boolean execute2(Object obj)
    {
        List list = (List)obj;
        int i = 0;
        Long long1 = null;
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Long long2 = (Long)iterator.next();
            if(long1 == null)
            {
                long1 = long2;
            } else
            {
                long l = long2.longValue() - long1.longValue();
                if(l > 0x4d3f6400L)
                    i++;
            }
        } while(true);
        if(i > 0)
            return Boolean.valueOf(true);
        else
            return Boolean.valueOf(false);
    }
}