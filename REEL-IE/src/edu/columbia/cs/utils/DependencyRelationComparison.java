package edu.columbia.cs.utils;
import java.util.HashMap;



public class DependencyRelationComparison {
	private static Node<String> tree;
	private static HashMap<String,Node<String>> map = new HashMap<String,Node<String>>();
	
	static{
		Node<String> dep = new Node<String>(null,"dep");
		map.put("dep", dep);
		
			Node<String> aux = new Node<String>(dep,"aux");
			dep.addChild(aux);
			map.put("aux", aux);
		
				Node<String> auxpass = new Node<String>(aux,"auxpass");
				aux.addChild(auxpass);
				map.put("auxpass", auxpass);
		
				Node<String> cop = new Node<String>(aux,"cop");
				aux.addChild(cop);
				map.put("cop", cop);
		
			Node<String> conj = new Node<String>(dep,"conj");
			dep.addChild(conj);
			map.put("conj", conj);
			
			Node<String> cc = new Node<String>(dep,"cc");
			dep.addChild(cc);
			map.put("cc", cc);
			
			Node<String> arg = new Node<String>(dep,"arg");
			dep.addChild(arg);
			map.put("arg", arg);
			
				Node<String> subj = new Node<String>(arg,"subj");
				arg.addChild(subj);
				map.put("subj", subj);
				
					Node<String> nsubj = new Node<String>(subj,"nsubj");
					subj.addChild(nsubj);
					map.put("nsubj", nsubj);
					
						Node<String> nsubjpass = new Node<String>(nsubj,"nsubjpass");
						nsubj.addChild(nsubjpass);
						map.put("nsubjpass", nsubjpass);
					
					Node<String> csubj = new Node<String>(subj,"csubj");
					subj.addChild(csubj);
					map.put("csubj", csubj);
					
						Node<String> csubjpass = new Node<String>(csubj,"csubjpass");
						csubj.addChild(csubjpass);
						map.put("csubjpass", csubjpass);
				
				Node<String> comp = new Node<String>(arg,"comp");
				arg.addChild(comp);
				map.put("comp", comp);
				
					Node<String> obj = new Node<String>(comp,"obj");
					comp.addChild(obj);
					map.put("obj", obj);
					
						Node<String> dobj = new Node<String>(obj,"dobj");
						obj.addChild(dobj);
						map.put("dobj", dobj);
						
						Node<String> iobj = new Node<String>(obj,"iobj");
						obj.addChild(iobj);
						map.put("iobj", iobj);
						
						Node<String> pobj = new Node<String>(obj,"pobj");
						obj.addChild(pobj);
						map.put("pobj", pobj);
				
					Node<String> attr = new Node<String>(comp,"attr");
					comp.addChild(attr);
					map.put("attr", attr);
					
					Node<String> ccomp = new Node<String>(comp,"ccomp");
					comp.addChild(ccomp);
					map.put("ccomp", ccomp);
					
					Node<String> xcomp = new Node<String>(comp,"xcomp");
					comp.addChild(xcomp);
					map.put("xcomp", xcomp);
					
					Node<String> compl = new Node<String>(comp,"compl");
					comp.addChild(compl);
					map.put("compl", compl);
					
					Node<String> complm = new Node<String>(comp,"complm");
					comp.addChild(complm);
					map.put("complm", complm);
					
					Node<String> mark = new Node<String>(comp,"mark");
					comp.addChild(mark);
					map.put("mark", mark);
					
					Node<String> rel = new Node<String>(comp,"rel");
					comp.addChild(rel);
					map.put("rel", rel);
					
					Node<String> acomp = new Node<String>(comp,"acomp");
					comp.addChild(acomp);
					map.put("acomp", acomp);
					
					Node<String> pcomp = new Node<String>(comp,"pcomp");
					comp.addChild(pcomp);
					map.put("pcomp", pcomp);
					
				Node<String> agent = new Node<String>(arg,"agent");
				arg.addChild(agent);
				map.put("agent", agent);
				
		Node<String> ref = new Node<String>(dep,"ref");
		dep.addChild(ref);
		map.put("ref", ref);
		
		Node<String> expl = new Node<String>(dep,"expl");
		dep.addChild(expl);
		map.put("expl", expl);
		
		Node<String> mod = new Node<String>(dep,"mod");
		dep.addChild(mod);
		map.put("mod", mod);
		
			Node<String> advcl = new Node<String>(mod,"advcl");
			mod.addChild(advcl);
			map.put("advcl", advcl);
			
			Node<String> purpcl = new Node<String>(mod,"purpcl");
			mod.addChild(purpcl);
			map.put("purpcl", purpcl);
			
			Node<String> tmod = new Node<String>(mod,"tmod");
			mod.addChild(tmod);
			map.put("tmod", tmod);
			
			Node<String> rcmod = new Node<String>(mod,"rcmod");
			mod.addChild(rcmod);
			map.put("rcmod", rcmod);
			
			Node<String> amod = new Node<String>(mod,"amod");
			mod.addChild(amod);
			map.put("amod", amod);
			
			Node<String> infmod = new Node<String>(mod,"infmod");
			mod.addChild(infmod);
			map.put("infmod", infmod);
			
			Node<String> partmod = new Node<String>(mod,"partmod");
			mod.addChild(partmod);
			map.put("partmod", partmod);
			
			Node<String> num = new Node<String>(mod,"num");
			mod.addChild(num);
			map.put("num", num);
			
			Node<String> number = new Node<String>(mod,"number");
			mod.addChild(number);
			map.put("number", number);
			
			Node<String> appos = new Node<String>(mod,"appos");
			mod.addChild(appos);
			map.put("appos", appos);
			
			Node<String> nn = new Node<String>(mod,"nn");
			mod.addChild(nn);
			map.put("nn", nn);
			
			Node<String> abbrev = new Node<String>(mod,"abbrev");
			mod.addChild(abbrev);
			map.put("abbrev", abbrev);
			
			Node<String> advmod = new Node<String>(mod,"advmod");
			mod.addChild(advmod);
			map.put("advmod", advmod);
			
				Node<String> neg = new Node<String>(advmod,"neg");
				advmod.addChild(neg);
				map.put("neg", neg);
			
			Node<String> poss = new Node<String>(mod,"poss");
			mod.addChild(poss);
			map.put("poss", poss);
			
			Node<String> possessive = new Node<String>(mod,"possessive");
			mod.addChild(possessive);
			map.put("possessive", possessive);
			
			Node<String> prt = new Node<String>(mod,"prt");
			mod.addChild(prt);
			map.put("prt", prt);
			
			Node<String> det = new Node<String>(mod,"det");
			mod.addChild(det);
			map.put("det", det);
			
			Node<String> prep = new Node<String>(mod,"prep");
			mod.addChild(prep);
			map.put("prep", prep);
			
			Node<String> predet = new Node<String>(mod,"predet");
			mod.addChild(predet);
			map.put("predet", predet);
			
			Node<String> preconj = new Node<String>(mod,"preconj");
			mod.addChild(preconj);
			map.put("preconj", preconj);
			
			Node<String> quantmod = new Node<String>(mod,"quantmod");
			mod.addChild(quantmod);
			map.put("quantmod", quantmod);
			
			Node<String> measure = new Node<String>(mod,"measure");
			mod.addChild(measure);
			map.put("measure", measure);
			
		Node<String> parataxis = new Node<String>(dep,"parataxis");
		dep.addChild(parataxis);
		map.put("parataxis", parataxis);
		
		Node<String> punct = new Node<String>(dep,"punct");
		dep.addChild(punct);
		map.put("punct", punct);
			
		Node<String> sdep = new Node<String>(dep,"sdep");
		dep.addChild(sdep);
		map.put("sdep", sdep);
		
			Node<String> xsubj = new Node<String>(sdep,"xsubj");
			sdep.addChild(xsubj);
			map.put("xsubj", xsubj);
	}
	
	public static int computeDistanceBetweenLinks(String class1, String class2){
		if(class1.equals(class2)){
			return 1;
		}
		return 0;
	}
	
	private static int auxBottomDistance(Node<String> n1, String class2, int dist){
		if(n1.getData().equals(class2)){
			return dist;
		}
		
		for(Node<String> child : n1.getChildren()){
			int distance = auxBottomDistance(child,class2,dist+1);
			if(distance!=-1){
				return distance;
			}
		}
		return -1;
	}
	
	public static void main(String[] args){
		System.out.println(computeDistanceBetweenLinks("nsubjpass","neg"));
	}
}
