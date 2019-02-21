import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Main
{

	public static void main(String[] args)
	{
		Set<Permutation> everyPermutation = new HashSet<>();
		Set<Permutation> involutivePermutation = new HashSet<>();
		Set<Permutation> fixedPointPermutation = new HashSet<>();
		Set<Permutation> validPermutation = new HashSet<>();
		
		PrintStream console = System.out;
		System.out.println("Algo started.");
		
		
		try
		{
			PrintStream log = new PrintStream(new File("log.txt"));
			System.setOut(log);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		
		//generating every permutation
		for(int i =0;i<4;i++)
		{
			for(int j = 0;j<4;j++)
			{
				if(j !=i)
				{
					for(int k = 0;k<4;k++)
					{
						if(k !=i && k !=j)
						{
							for(int l = 0;l<4;l++)
							{
								if(l !=i && l != j && l != k)
								{
									Permutation p = new Permutation(i,j,k,l);
									everyPermutation.add(p);
									if(p.isInvolutive())
									{
										involutivePermutation.add(p);
									}
									if(p.hasFixedPoint())
									{
										fixedPointPermutation.add(p);
									}
								}
							}
						}
					}
				}
			}
		}
		
		validPermutation.addAll(everyPermutation);
		System.out.println("We have " + everyPermutation.size() + " permutations in total.");
		
		
		Iterator<Permutation> it1 = validPermutation.iterator();
		Map<Permutation,Set<Pair<Permutation,Permutation>>> mapTriplet = new HashMap<>(); //triplet that the key is in
		Map<Permutation,Set<Triplet>> mapPermSetTriplet = new HashMap<>();
		

		//listing all valid triplets
		Set<Triplet> validTriplet = new HashSet<>();
		while(it1.hasNext())
		{
			Permutation p1 = it1.next();
			Iterator<Permutation> it2 = validPermutation.iterator();
			while(it2.hasNext())
			{
				Permutation p2 = it2.next();
				Permutation p3 = p1.compose(p2);
				p3 = p3.inverse();
				validTriplet.add(new Triplet(p1, p2, p3));
				if(!mapTriplet.containsKey(p3))
				{
					mapTriplet.put(p3, new HashSet<>());
				}
				mapTriplet.get(p3).add(new Pair<>(p1,p2));
				if(!mapPermSetTriplet.containsKey(p1))
				{
					mapPermSetTriplet.put(p1, new HashSet<>());
				}
				mapPermSetTriplet.get(p1).add(new Triplet(p1, p2, p3));
				
				if(!mapPermSetTriplet.containsKey(p2))
				{
					mapPermSetTriplet.put(p2, new HashSet<>());
				}
				mapPermSetTriplet.get(p2).add(new Triplet(p1, p2, p3));
				
				
				if(!mapPermSetTriplet.containsKey(p3))
				{
					mapPermSetTriplet.put(p3, new HashSet<>());
				}
				mapPermSetTriplet.get(p3).add(new Triplet(p1, p2, p3));
			}
		}
		
		System.out.println(validTriplet.size() + " are valid.");

		System.out.println(validTriplet);
		
		
		//check that if a triplet is valid the two other that represent the same triplet are valid too
		Iterator<Triplet> it;
		it = validTriplet.iterator();
		while(it.hasNext())
		{
			Triplet tripletTest = it.next();
			Triplet tripletTest2 = new Triplet(tripletTest._p3, tripletTest._p1, tripletTest._p2);
			Triplet tripletTest3 = new Triplet(tripletTest._p2, tripletTest._p3, tripletTest._p1);
			if(!validTriplet.contains(tripletTest2) || !validTriplet.contains(tripletTest3))
				System.out.println("not ok");
		}
		
		
		//list potential switch
		//Set<Pair<Permutation,Pair<Pair<Permutation,Permutation>,Pair<Permutation,Permutation>>>> setPotentialSwitch = new HashSet<>();
		Set<Pair<Permutation,Permutation>> setGoodSwitch = new HashSet<>();
		Set<Pair<Permutation,Permutation>> setSuperGoodSwitch = new HashSet<>();
		
		Map<Pair<Pair<Permutation,Permutation>, Permutation>,Pair<Permutation,Permutation>> mapSuperGoodSwitch = new HashMap<>();
		
		//First-order switch mean that the unchanged permutation is the third in the triplet
		//second-order switch means that the unchanged permutation is the second one
		Iterator<Permutation> itPerm1 = validPermutation.iterator();
		while(itPerm1.hasNext())
		{
			Permutation p1 = itPerm1.next();
			Iterator<Permutation> itPerm2 = validPermutation.iterator();
			while(itPerm2.hasNext())
			{
				Permutation p2 = itPerm2.next();
				if(!p1.equals(p2))
				{
					
					Pair<Permutation,Permutation> permSwitch = new Pair<>(p1,p2);
					System.out.println("--------------Checking permutation:" + permSwitch + " -----------------------");
					Set<Triplet> setTriplet = mapPermSetTriplet.get(p1);
					boolean isGood = true;
					boolean isSuperGood = true;
					Iterator<Triplet> itTri = setTriplet.iterator();
					while(itTri.hasNext())
					{
						Triplet triplet = itTri.next();
						boolean switchFirstOrderPermitted = false;
						boolean switchSecondOrderPermitted = false;
						if(!p1.equals(triplet._p3))
						{
							Pair<Permutation,Permutation> otherPerm = null;
							
							//first order switch
							if(p1.equals(triplet._p1))
							{
								Set<Pair<Permutation,Permutation>> setFirstOrderPossiblePair = mapTriplet.get(triplet._p3);
								if(setFirstOrderPossiblePair.size() != 1)
								{
									Pair<Permutation,Permutation> pair1 = null;
									Pair<Permutation,Permutation> pair2 = null;
									Iterator<Pair<Permutation,Permutation>> itFirstOrderPair = setFirstOrderPossiblePair.iterator();
									while(itFirstOrderPair.hasNext())
									{
										Pair<Permutation,Permutation> pair = itFirstOrderPair.next();
										if(pair.first().equals(permSwitch.first()))
										{
											pair1 = pair;
										}
										if(pair.first().equals(permSwitch.second()))
										{
											pair2 = pair;
										}
										
									}
									if(pair1 != null && pair2 != null)
									{
										switchFirstOrderPermitted = true;
										Pair<Permutation,Permutation> permSwitch2 = new Pair<>(pair1.second(),pair2.second());
										otherPerm = permSwitch2;
										
										System.out.println("First order switch permitted with:" + permSwitch2+ " for triplet:" + triplet);
										
										///TODO: remove that
										//setPotentialSwitch.add(new Pair<>(triplet._p3,new Pair<>(permSwitch,permSwitch2)));
										//setPotentialSwitch.add(new Pair<>(triplet._p3,new Pair<>(permSwitch2,permSwitch)));
									}
									else
									{
										System.out.println("First order switch NOT permitted because no matching pair with triplet:" + triplet);
									}
							
								}
								else
								{
									System.out.println("First order switch NOT permitted because triplet:" + triplet + " is unswitchable");
								}
							}
							//second order switch
							else if(p1.equals(triplet._p2))
							{
								Set<Pair<Permutation,Permutation>> setSecondOrderPossiblePair = mapTriplet.get(triplet._p2);
								if(setSecondOrderPossiblePair.size() != 1)
								{
									Pair<Permutation,Permutation> pair1 = null;
									Pair<Permutation,Permutation> pair2 = null;
									Iterator<Pair<Permutation,Permutation>> itSecondOrderPair = setSecondOrderPossiblePair.iterator();
									while(itSecondOrderPair.hasNext())
									{
										Pair<Permutation,Permutation> pair = itSecondOrderPair.next();
										if(pair.second().equals(permSwitch.first()))
										{
											pair1 = pair;
										}
										if(pair.second().equals(permSwitch.second()))
										{
											pair2 = pair;
										}
										
									}
									if(pair1 != null && pair2 != null)
									{
										switchSecondOrderPermitted = true;
										Pair<Permutation,Permutation> permSwitch2 = new Pair<>(pair1.first(),pair2.first());
										otherPerm = permSwitch2;
										
										System.out.println("Second order switch permitted with:" + permSwitch2 + " for triplet:" + triplet);
										
										///TODO remove that
										//setPotentialSwitch.add(new Pair<>(triplet._p2,new Pair<>(permSwitch,permSwitch2)));
										//setPotentialSwitch.add(new Pair<>(triplet._p2,new Pair<>(permSwitch2,permSwitch)));
									}
									else
									{
										System.out.println("Second order switch NOT permitted because no matching pair with triplet:" + triplet);
									}
							
								}
								else
								{
									System.out.println("Second order switch NOT permitted because triplet:" + triplet + " is unswitchable");
								}
							}
							
							if(switchFirstOrderPermitted || switchSecondOrderPermitted)
							{
								if(switchFirstOrderPermitted && switchSecondOrderPermitted)
								{
									isSuperGood = false;
								}
							}
							else
							{
								
								isGood = false;
								isSuperGood = false;
							}
							
							Pair<Pair<Permutation,Permutation>,Permutation> pairSwitchTriplet;
							if(switchFirstOrderPermitted)
							{
								pairSwitchTriplet= new Pair<>(permSwitch,triplet._p3);
							}
							else
							{
								pairSwitchTriplet= new Pair<>(permSwitch,triplet._p2);
							}
							if(mapSuperGoodSwitch.containsKey(pairSwitchTriplet))
							{
								System.out.println("----------------NOT SUPER GOOD----------------------");
							}
							else
							{
								mapSuperGoodSwitch.put(pairSwitchTriplet, otherPerm);
							}
						}
					}
					
					if(isGood)
					{
						setGoodSwitch.add(permSwitch);
						if(isSuperGood)
						{
							setSuperGoodSwitch.add(permSwitch);
						}
					}
				}
			}
		}
		
		int totalPermSwitchNb = validPermutation.size()*(validPermutation.size()-1);
		
		System.out.println("Among " + totalPermSwitchNb + " switch: " + setGoodSwitch.size() + " are good; and " + setSuperGoodSwitch.size() +  " are super good.");
		
		System.out.println("-------------------------List of the super-good switch pairs:--------------------------------");
		
		for(Pair<Pair<Permutation,Permutation>,Permutation> key : mapSuperGoodSwitch.keySet())
		{
			
			System.out.println("Switch:" + key.first() + " for triplet:" + key.second() + " pairs with switch:" + mapSuperGoodSwitch.get(key));
			
		}
		
		
		//construction of the graph of the switch
		Map<Pair<Permutation,Permutation>, Integer> mapVertexId = new HashMap<>();
		Map<Pair<Integer,Integer>,Permutation> mapEdgeLabel = new HashMap<>();
		
		int nextIdVertex = 0;
		
		for(Pair<Pair<Permutation,Permutation>,Permutation> key : mapSuperGoodSwitch.keySet())
		{
			int idVertex1,idVertex2;
			Pair<Permutation,Permutation> permSwitch1 = key.first();
			if(!mapVertexId.containsKey(permSwitch1))
			{
				mapVertexId.put(permSwitch1, nextIdVertex);
				idVertex1 = nextIdVertex;
				nextIdVertex++;
			}
			else
			{
				idVertex1 = mapVertexId.get(permSwitch1);
			}
			
			Pair<Permutation,Permutation> permSwitch2 = mapSuperGoodSwitch.get(key);
			if(!mapVertexId.containsKey(permSwitch2))
			{
				mapVertexId.put(permSwitch2, nextIdVertex);
				idVertex2 = nextIdVertex;
				nextIdVertex++;
			}
			else
			{
				idVertex2 = mapVertexId.get(permSwitch2);
			}
			
			Pair<Integer,Integer> pairSwitch = new Pair<>(idVertex1,idVertex2);
			if(!mapEdgeLabel.containsKey(pairSwitch))
			{
				pairSwitch = new Pair<>(pairSwitch.second(),pairSwitch.first());
				if(!mapEdgeLabel.containsKey(pairSwitch))
				{
					mapEdgeLabel.put(pairSwitch, key.second());
				}
			}
		}
		
		System.out.println("The graph is composed of: " + mapVertexId.size() + " vertices; and " + mapEdgeLabel.size() + " edges.");
		
		
		//writing to .dot file
		List<String> lines = new ArrayList<>();
		String header = "strict graph G{";
		lines.add(header);
		for(Pair<Permutation, Permutation> permSwitch : mapVertexId.keySet())
		{
		
			String vertexLine = "" + mapVertexId.get(permSwitch) + "[label=\"" + permSwitch + "\"];";
			lines.add(vertexLine);
		
		}
		
		for(Pair<Integer,Integer> switchPair : mapEdgeLabel.keySet())
		{
			String edgeLine = "" + switchPair.first() + " -- " + switchPair.second() + "[label=\"" + mapEdgeLabel.get(switchPair)  + "\"];";
			lines.add(edgeLine);
		}
		
		
		String end = "}";
		lines.add(end);
		Path file = Paths.get("switch_graph.dot");
		try
		{
			Files.write(file, lines, Charset.forName("UTF-8"));
			System.out.println("Graph saved as .dot format in \"switch_graph.dot\"");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.setOut(console);
		
		System.out.println("Algo finished.");
		
		
	}
}
