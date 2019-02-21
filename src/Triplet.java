
public class Triplet
{

	public Triplet(Permutation p1  ,Permutation p2,Permutation p3)
	{
		_p1 = p1;
		_p2 = p2;
		_p3 = p3;
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_p1 == null) ? 0 : _p1.hashCode());
		result = prime * result + ((_p2 == null) ? 0 : _p2.hashCode());
		result = prime * result + ((_p3 == null) ? 0 : _p3.hashCode());
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
		Triplet other = (Triplet) obj;
		if (_p1 == null)
		{
			if (other._p1 != null)
				return false;
		} else if (!_p1.equals(other._p1))
			return false;
		if (_p2 == null)
		{
			if (other._p2 != null)
				return false;
		} else if (!_p2.equals(other._p2))
			return false;
		if (_p3 == null)
		{
			if (other._p3 != null)
				return false;
		} else if (!_p3.equals(other._p3))
			return false;
		return true;
	}

	//to test if the three permutations are valid for triangle colorinng 
	public boolean isValid()
	{
		if(_p1.compose(_p2).compose(_p3).equals(new Permutation())) //if the composed permutation is identity then it is a valid triplet
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		String str = "[" + _p1.toString() + ";" + _p2.toString() + ";" + _p3.toString() + "]";
		return str;
	}


	public Permutation _p1;
	public Permutation _p2;
	public Permutation _p3;

}
