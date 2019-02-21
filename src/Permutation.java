import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;


public class Permutation
{
	private static final String[] _name = new String[]{"1","2","-1","-2"};

	//creation as identity
	public Permutation()
	{
		_value = new int[4];
		
		for(int i =0;i<4;i++)
		{
			_value[i] = i;
		}
	}
	
	public Permutation(int x0,int x1,int x2,int x3)
	{
		this();
		_value[0] = x0;
		_value[1] = x1;
		_value[2] = x2;
		_value[3] = x3;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_value);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permutation other = (Permutation) obj;
		if (!Arrays.equals(_value, other._value))
			return false;
		return true;
	}
	
	public Permutation compose(Permutation other)
	{
		Permutation composed = new Permutation();
		for(int i = 0;i<4;i++)
		{
			composed._value[i] = this._value[other._value[i]];
		}
		
		return composed;
	}
	
	public Permutation inverse()
	{
		Permutation inverse = new Permutation();
		for(int i =0;i<4;i++)
		{
			for(int j = 0;j<4;j++)
			{
				if(_value[j] == i)
				{
					inverse._value[i] = j;
				}
			}
		}
		
		return inverse;
	}
	
	public boolean hasFixedPoint()
	{
		for(int i =0;i<4;i++)
		{
			if(_value[i] == i)
				return true;
		}
		return false;
	}
		
	public boolean isInvolutive()
	{
		for(int i =0;i<4;i++)
		{
			if(_value[_value[i]] != i)
				return false;
		}
		return true;
	}

	public String toString()
	{
		String str = "(";
		for(int i =0;i<3;i++)
		{
			str += _name[_value[i]] + ",";
		}
		str += _name[_value[3]] + ")";
		return str;
	}
	
	
	
	
	//function to test if the 3-uplet is valid for a certain triangle
	public static boolean validForPPP(Permutation p1,Permutation p2, Permutation p3)
	{
		for(int i=0;i<4;i++)
		{
			if(p1._value[i] == i || p2._value[i] == i || p3._value[i] == i)
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean validForMMP(Permutation p1,Permutation p2,Permutation p3)
	{
		for(int i=0;i<4;i++)
		{
			if(p1._value[i] == ((i+2)%4) || p2._value[i] == ((i+2)%4)  || p3._value[i] == i)
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean validForMPP(Permutation p1, Permutation p2, Permutation p3)
	{
		for(int i=0;i<4;i++)
		{
			if(p1._value[i] == ((i+2)%4) || p2._value[i] == i  || p3._value[i] == i)
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean validForMMM(Permutation p1,Permutation p2, Permutation p3)
	{
		for(int i=0;i<4;i++)
		{
			if(p1._value[i] == ((i+2)%4) || p2._value[i] == ((i+2)%4)  || p3._value[i] == ((i+2)%4))
			{
				return false;
			}
		}
		return true;
	}
	// getting the set of all subset of size k composed with elements of
		// original set
		public static <V> List<List<V>> subsets(List<V> set, int k, V start) {
			// System.out.println("set of size:" + k + ":: from : " + start);
			// set of size :k, composed of element of :set, starting at element
			// :start.
			List<List<V>> subsets = new Vector<>();
			// k=0 => returning singleton "emptyset"
			if (k <= 0) {
				subsets.add(new Vector<>());
				return subsets;
			}
			// iterating on the list until finding element start
			ListIterator<V> it = set.listIterator();
			V v = null;
			while (it.hasNext()) {
				v = it.next();
				if (v == start)
					break;

			}
			// backing one step to retrieve the iterator of element start (not so
			// nice but
			// didn't find out how to do it a better way: this is the reason for
			// using List Interface)
			it.previous();
			while (it.hasNext()) {
				v = it.next();
				// recursion: getting the k-1 sized subsets starting at start+1
				List<List<V>> smallerSubsets = subsets(set, k - 1, v);
				// adding element start to every subsets of size k-1 => all subsets
				// are of size k
				for (List<V> s : smallerSubsets) {
					// check for unicity of element in the set(not so nice either,
					// but
					// the Vector class does not implement Set Interface)
					if (!s.contains(v)) {
						s.add(v);
						subsets.add(s);
					}
				}
			}
			return subsets;
		}
		
		// Returns a list of subsets of size n from the set S
		public static <V> Set<Set<V>> allSubsetsOfSizeN(Set<V> S, int k) {
			// Converting Set -> List
			ArrayList<V> vertices = new ArrayList<V>();
			for (V v : S) {
				vertices.add(v);
			}
			Set<Set<V>> subsets = new HashSet<Set<V>>();
			List<List<V>> result = subsets(vertices, k, vertices.get(0));

			HashSet<V> tmp;
			for (List<V> list : result) {
				tmp = new HashSet<V>();
				for (V v : list) {
					tmp.add(v);
				}
				subsets.add(new HashSet<V>(tmp));
			}

			return subsets;
		}
		
		//desc to string function
		public static String toString(Set<Permutation[]> listTriplet)
		{
			String str = "[";
			Iterator<Permutation[]> it = listTriplet.iterator();
			while(it.hasNext())
			{
				Permutation[] triplet = it.next();
				str += Permutation.toString(triplet);
				if(it.hasNext())
					str += ",";
			}
			
			str += "]";
			
			return str;
		}
		
		
		public static String toString(Permutation[] triplet)
		{
			String str = "[" + triplet[0] + "," + triplet[1] + "," + triplet[2] + "]";
			return str;
		}
		
	//the values of the permutations, correspond, in the order to: -2,-1,1,2
	private int[] _value;

}
