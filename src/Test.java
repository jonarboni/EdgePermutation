import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test
{

	public Test()
	{
	}
	
	public static void main(String[] args)
	{
		Integer[] a = new Integer[] {1,2};
		Integer[] b = new Integer[] {1,2};
		
		if(Arrays.equals(a, b))
			System.out.println("ok1");
		Set<Integer[]> set = new HashSet<>();
		set.add(a);
		if(set.contains(b))
			System.out.println("ok");
		else
			System.out.println("pas ok");
		
		
		
	}
}


