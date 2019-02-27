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
									boolean validP = p.validForPEdge();
									boolean validM = p.validForMEdge();
									if((validP || validM) /*&& !(validP && validM)*/)
									{
										validPermutation.add(p);
									}
									
								}
							}
						}
					}
				}
			}
		}

		System.out.println("We have " + everyPermutation.size() + " permutations in total; " + validPermutation.size() + " are valid among them.");
		
		
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
				Triplet t1,t2,t3;
				t1 = new Triplet(p1, p2, p3);
				t2 = new Triplet(p2, p3, p1);
				t3 = new Triplet(p3, p1, p2);
				validTriplet.add(t1);
				validTriplet.add(t2);
				validTriplet.add(t3);
				if(!mapTriplet.containsKey(p3))
				{
					mapTriplet.put(p3, new HashSet<>());
				}
				mapTriplet.get(p3).add(new Pair<>(p1,p2));
				
				
				if(!mapPermSetTriplet.containsKey(p1))
				{
					mapPermSetTriplet.put(p1, new HashSet<>());
				}
				mapPermSetTriplet.get(p1).add(t1);
				mapPermSetTriplet.get(p1).add(t2);
				mapPermSetTriplet.get(p1).add(t3);
				
				if(!mapPermSetTriplet.containsKey(p2))
				{
					mapPermSetTriplet.put(p2, new HashSet<>());
				}
				mapPermSetTriplet.get(p2).add(t1);
				mapPermSetTriplet.get(p2).add(t2);
				mapPermSetTriplet.get(p2).add(t3);
				
				
				if(!mapPermSetTriplet.containsKey(p3))
				{
					mapPermSetTriplet.put(p3, new HashSet<>());
				}
				mapPermSetTriplet.get(p3).add(t1);
				mapPermSetTriplet.get(p3).add(t2);
				mapPermSetTriplet.get(p3).add(t3);
			}
		}
		

		
		
		//check that if a triplet is valid the two other that represent the same triplet are valid too
		Iterator<Triplet> it;
		it = validTriplet.iterator();
		Set<Triplet> validTripletFinal = new HashSet<>();
		while(it.hasNext())
		{
			Triplet tripletTest = it.next();
			validTripletFinal.add(tripletTest);
			Triplet tripletTest2 = new Triplet(tripletTest._p3, tripletTest._p1, tripletTest._p2);
			Triplet tripletTest3 = new Triplet(tripletTest._p2, tripletTest._p3, tripletTest._p1);
			if(!validTriplet.contains(tripletTest2))
			{
				validTripletFinal.add(tripletTest2);
			}
				//System.out.println("-----------------ERROR: " + tripletTest + " is a valid triplet: not " + tripletTest2 + " --------------");
			if(!validTriplet.contains(tripletTest3))
			{
				validTripletFinal.add(tripletTest3);
			}
				//System.out.println("-----------------ERROR: " + tripletTest + " is a valid triplet: not " + tripletTest3 + " --------------");
		}
		
		System.out.println(validTripletFinal.size() + "triplets are valid.");

		System.out.println(validTripletFinal);
		
		validTriplet = validTripletFinal;
		
		
		//list potential switch
		//Set<Pair<Permutation,Pair<Pair<Permutation,Permutation>,Pair<Permutation,Permutation>>>> setPotentialSwitch = new HashSet<>();
		Set<Pair<Permutation,Permutation>> setGoodSwitch = new HashSet<>();
		Set<Pair<Permutation,Permutation>> setNotSuperGoodSwitch = new HashSet<>();
		Map<Permutation,Set<Triplet>> mapPermSetTripletRemaining = new HashMap<>();
		Map<Pair<Permutation,Permutation>, Map<Permutation,Pair<Permutation,Permutation>>> mapSwitch= new HashMap<>();
		
		Map<Pair<Pair<Permutation,Permutation>, Permutation>,Pair<Permutation,Permutation>> mapSuperGoodSwitch = new HashMap<>();
		
		Map<Pair<Permutation,Permutation>,Set<Triplet>> mapSwitchSetTripletOk = new HashMap<>();
		
		
		
		
		Iterator<Permutation> i1,i2,i3,i4;
		i1 = validPermutation.iterator();
		while(i1.hasNext())
		{
			Permutation p1 = i1.next();
			i2 = validPermutation.iterator();
			while(i2.hasNext())
			{
				Permutation p2 = i2.next();
				if(!p1.equals(p2))
				{
					mapSwitchSetTripletOk.put(new Pair<>(p1,p2), new HashSet<>());
					mapSwitch.put(new Pair<>(p1,p2), new HashMap<>());
				}
				
			}
		}
		i1 = validPermutation.iterator();
		while(i1.hasNext())
		{
			Permutation p1 = i1.next();
			i2 = validPermutation.iterator();
			while(i2.hasNext())
			{
				Permutation p2 = i2.next();
				i3 = validPermutation.iterator();
				while(i3.hasNext())
				{
					Permutation p3 = i3.next();
					
					if(!p1.equals(p3))
					{
						i4 = validPermutation.iterator();
						while(i4.hasNext())
						{
							Permutation p4 = i4.next();
							Permutation prod1 = p1.compose(p2);
							Permutation prod2 = p3.compose(p4);
							if(prod1.equals(prod2))
							{
								Permutation thirdPerm = prod1.inverse();
								Pair<Permutation,Permutation> switchPerm = new Pair<>(p1,p3);
								Pair<Permutation,Permutation> otherSwitch = new Pair<>(p2,p4);
								Map<Permutation,Pair<Permutation,Permutation>> mapOtherSwitch;
								Triplet t1,t2,t3; //the same triplet but with different order on the permutation
								t1 = new Triplet(p1, p2, thirdPerm);
								t2 = new Triplet(thirdPerm, p1, p2);
								t3 = new Triplet(p2, thirdPerm, p1);
								Set<Triplet> setTripletOk;
								
								//the corresponding list of triplets for the first member switch
								mapOtherSwitch = mapSwitch.get(switchPerm);
								setTripletOk = mapSwitchSetTripletOk.get(switchPerm);
								if(mapOtherSwitch.containsKey(thirdPerm) && !mapOtherSwitch.get(thirdPerm).equals(otherSwitch))
								{
									setNotSuperGoodSwitch.add(switchPerm);
								}
								else
								{
									mapOtherSwitch.put(thirdPerm,otherSwitch);
								}
								
								setTripletOk.add(t1);
								setTripletOk.add(t2);
								setTripletOk.add(t3);
								
								//the corresponding list of triplet for the second memeber of the switch
								mapOtherSwitch = mapSwitch.get(otherSwitch);
								setTripletOk = mapSwitchSetTripletOk.get(otherSwitch);
								if(mapOtherSwitch.containsKey(thirdPerm) && !mapOtherSwitch.get(thirdPerm).equals(switchPerm))
								{
									setNotSuperGoodSwitch.add(otherSwitch);
								}
								else
								{
									mapOtherSwitch.put(thirdPerm,switchPerm);
								}
								setTripletOk.add(t1);
								setTripletOk.add(t2);
								setTripletOk.add(t3);
							}
						}
						
					}
					
				}
			}
		}
		
		System.out.println("We have:" + mapSwitch.size() + " possible switch.");
		
		
		
		//check for good switchs
		for(Entry<Pair<Permutation,Permutation>,Map<Permutation,Pair<Permutation,Permutation>>> entry : mapSwitch.entrySet())
		{
			Pair<Permutation,Permutation> switchPerm = entry.getKey();
			Set<Triplet> setTripletInvolved = mapPermSetTriplet.get(switchPerm.first());
			Set<Triplet> setTripletOk = mapSwitchSetTripletOk.get(switchPerm);
			Map<Permutation,Pair<Permutation,Permutation>> mapOtherSwitch = mapSwitch.get(switchPerm);
			/*System.out.println("For switch:" + switchPerm + " we have:" + setTripletInvolved.size() + " triplet involved with:" + switchPerm.first());
			System.out.println("Among them:" + setTripletOk.size() +  " are ok.");*/
			if(setTripletInvolved.size() == setTripletOk.size())
			{
				System.out.println("The switch:" + switchPerm + " is good.");
				setGoodSwitch.add(switchPerm);
			}
			else
			{
				Set<Triplet> setTripletNotOk = new HashSet<>();
				setTripletNotOk.addAll(setTripletInvolved);
				setTripletNotOk.removeAll(setTripletOk);
				System.out.println("The switch: " + switchPerm + " is INVOLVED IN " + setTripletInvolved.size() + " triplets:");
				System.out.println(setTripletInvolved);
				System.out.println("The switch: " + switchPerm + " is OK for " + setTripletOk.size() + " triplets:");
				System.out.println(setTripletOk);
				System.out.println("The switch: " + switchPerm + " is NOT OK for " + setTripletNotOk.size() + " triplets:");
				System.out.println(setTripletNotOk);
			}
			
		}
		
		Set<Pair<Permutation,Permutation>> setSuperGoodSwitch = new HashSet<>();
		setSuperGoodSwitch.addAll(setGoodSwitch);
		setSuperGoodSwitch.removeAll(setNotSuperGoodSwitch);
		
		System.out.println("We have:" + mapSwitch.size() + " possible switch.");
		System.out.println("And we have:" + setGoodSwitch.size() + " good switch among them.");
		System.out.println("But " + setNotSuperGoodSwitch.size() + " are not super good.");
		System.out.println("Finaly we have:" + setSuperGoodSwitch.size() + " super good switch.");
		
		System.setOut(console);
		
		
		
		
		
		
		//First-order switch mean that the unchanged permutation is the third in the triplet
		//second-order switch means that the unchanged permutation is the second one
		/*Iterator<Permutation> itPerm1 = validPermutation.iterator();
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
								Set<Pair<Permutation,Permutation>> setSecondOrderPossiblePair = mapTriplet.get(triplet._p3);
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
							if(otherPerm== null || (mapSuperGoodSwitch.containsKey(pairSwitchTriplet) && !otherPerm.equals(mapSuperGoodSwitch.get(pairSwitchTriplet))))
							{
								System.out.println("----------------"+ pairSwitchTriplet + "NOT SUPER GOOD----------------------");
								if(otherPerm != null)
									System.out.println("Switch:" + pairSwitchTriplet + ":" + otherPerm + " is not super good because:" + mapSuperGoodSwitch.get(pairSwitchTriplet) + " is already a swicth.");
								else
									System.out.println("---------------------NOT GOOD--------------------");
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
		*/
		
		
		System.out.println("Algo finished.");
		
		
	}
}
