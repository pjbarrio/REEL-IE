package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;
import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.filter.*;
import org.jdom.input.*;
import org.jdom.output.*;

import edu.columbia.cs.ref.model.Segment;

/**
 * Internal representation of the ACE 2005 Document.
 * 
 * <br>
 * <br>
 * 
 * This class is an adaptation of the class created by Razvan Bunescu used to load ACE 2003.
 * The original class can be found in http://ace.cs.ohio.edu/~razvan/code/ssk_re.tar.gz
 * 
 * <br>
 * <br>
 * 
 * This class should not be used to load documents to our framework. It is only for internal use.
 * 
 * @author      Pablo Barrio
 * @author		Goncalo Simoes
 * @version     0.1
 * @since       2011-09-27
 */
public class ACEDocument {

	String m_strId;
	String m_strDir;

	Vector<ACEEntity> m_vecEntities;
	Vector<ACERelation> m_vecRelations;

	List<Segment> m_strText;

	public ACEDocument()
	{
		m_vecEntities = new Vector();
		m_vecRelations = new Vector();

		m_strText = new ArrayList<Segment>();
	}

	public void load(String strFile)
	{
		loadXML(strFile + ".apf.xml");
		loadSGM(strFile + ".sgm");
	}


	public void removeRepetitions()
	{
		for (int i = 0; i < m_vecEntities.size(); i++) {
			ACEEntity e1 = (ACEEntity) m_vecEntities.get(i);
			ListIterator it1 = e1.m_lstMentions.listIterator();
			while (it1.hasNext()) {
				ACEEMention em1 = (ACEEMention) it1.next();
				for (int j = i + 1; j < m_vecEntities.size(); j++) {
					ACEEntity e2 = (ACEEntity) m_vecEntities.get(j);
					ListIterator it2 = e2.m_lstMentions.listIterator();
					while (it2.hasNext()) {
						ACEEMention em2 = (ACEEMention) it2.next();
						if (em1.compareTo(em2) == 0) {
							it2.remove();
							// replace em2 with em1 in all relations
							for (int k = 0; k < m_vecRelations.size(); k++) {
								ACERelation r = (ACERelation) m_vecRelations.get(k);
								ListIterator itr = r.m_lstMentions.listIterator();
								while (itr.hasNext()) {
									ACERMention rm = (ACERMention) itr.next();
									if (rm.m_arg1 == em2)
										rm.m_arg1 = em1;
									if (rm.m_arg2 == em2)
										rm.m_arg2 = em1;
								}
							}
						}
					}
				}
			}
		}
	}


	public void loadXML(String strXMLFile)
	{
		try {
			SAXBuilder builder = new SAXBuilder();
			builder.setExpandEntities(true);
			Document doc = builder.build(strXMLFile);

			Element rootElement = doc.getRootElement();
			TreeMap mapEntities = new TreeMap();
			TreeMap mapEMentions = new TreeMap();
			process(rootElement, mapEntities, mapEMentions);
		}
		catch (JDOMException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}   
	}


	public void loadSGM(String strSGMFile)
	{
		try {
			StringBuffer buf=new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(strSGMFile));
			String strLine = br.readLine();
			boolean inside=false;
			int currentIndex=0;
			while (strLine != null) {
				int i = 0;
				while (i < strLine.length()) {
					char c = strLine.charAt(i);
					if (c == '<'){
						inside=true;;
					}else if(c == '>'){
						inside =false;
						i++;
						String sectionValue = buf.toString();
						int sectionOffset = currentIndex;
						m_strText.add(new Segment(sectionValue,sectionOffset));
						
						currentIndex+=buf.toString().length();
						buf=new StringBuffer();
						continue;
					}
						
					if(!inside){
						buf.append(c);
					}
						
					i++;
				}
				if(!inside){
					buf.append('\n');
				}
				strLine = br.readLine();
			}
			String sectionValue = buf.toString();
			int sectionOffset = currentIndex;
			m_strText.add(new Segment(sectionValue,sectionOffset));
			//System.out.println(m_strText);
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}   
	}


	public void process(Element elem, 
			TreeMap mapEntities, TreeMap mapEMentions)
	{
		String strName = elem.getName();
		if (strName.equals("document"))
			m_strId = elem.getAttribute("DOCID").getValue();

		if (strName.equals("entity"))
			processEntity(elem, mapEntities, mapEMentions);
		else if (strName.equals("relation"))
			processRelation(elem, mapEntities, mapEMentions);
		else {
			List lstChildren = elem.getChildren();
			ListIterator it = lstChildren.listIterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				process(el, mapEntities, mapEMentions);
			}
		}
	}


	public void processEntity(Element elem, 
			TreeMap mapEntities, TreeMap mapEMentions)
	{
		// process entity
		ACEEntity entity = new ACEEntity();
		entity.m_strID = elem.getAttribute("ID").getValue();
		entity.m_strType = elem.getAttribute("TYPE").getValue();
		entity.m_strSubType = elem.getAttribute("SUBTYPE").getValue();
		m_vecEntities.add(entity);
		mapEntities.put(entity.m_strID, entity);

		// process entity mentions
		List lstChildren = elem.getChildren();
		ListIterator it = lstChildren.listIterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			String strName = el.getName();
			if (strName.equals("entity_type")) {
				System.out.println(el.getTextTrim());
				entity.m_strType = el.getTextTrim();
				entity.m_bGeneric = el.getAttribute("GENERIC").equals("TRUE");
			}
			else if (strName.equals("entity_mention")) {
				ACEEMention emention = new ACEEMention();
				emention.m_strID = el.getAttribute("ID").getValue();
				emention.m_strType = el.getAttribute("TYPE").getValue();
				setAttributes(emention, el);
				mapEMentions.put(emention.m_strID, emention);
				entity.addMention(emention);
			}
		}
	}


	protected void setAttributes(ACEEMention em, Element el)
	{
		List lst1 = el.getChildren();
		ListIterator it1 = lst1.listIterator();
		while (it1.hasNext()) {
			Element el1 = (Element) it1.next();
			if (el1.getName().equals("extent")) {
				List lst2 = el1.getChildren();
				ListIterator it2 = lst2.listIterator();
				while (it2.hasNext()) {
					Element el2 = (Element) it2.next();
					if (el2.getName().equals("charseq")) {
						em.m_nBeg = Integer.valueOf(el2.getAttributeValue("START")).intValue();
						em.m_nLen = Integer.valueOf(el2.getAttributeValue("END")).intValue() - 
						em.m_nBeg + 1;
					}
				}
			}
			else if (el1.getName().equals("head")) {
				List lst2 = el1.getChildren();
				ListIterator it2 = lst2.listIterator();
				while (it2.hasNext()) {
					Element el2 = (Element) it2.next();
					if (el2.getName().equals("charseq")) {
						em.m_nHBeg = Integer.valueOf(el2.getAttributeValue("START")).intValue();
						em.m_nHLen = Integer.valueOf(el2.getAttributeValue("END")).intValue() - 
						em.m_nHBeg + 1;
					}
				}
			}
		}
	}


	public void processRelation(Element elem, 
			TreeMap mapEntities, TreeMap mapEMentions)
	{
		// process relation
		ACERelation relation = new ACERelation();
		Attribute attrID = elem.getAttribute("ID");
		relation.m_strID = attrID.getValue();
		relation.m_strType = elem.getAttribute("TYPE").getValue();
		Attribute st = elem.getAttribute("SUBTYPE");
		if(st!=null){
			relation.m_strSType = st.getValue();
		}else{
			relation.m_strSType = relation.m_strType;
		}
		//String strClass = elem.getAttribute("CLASS").getValue();
		//relation.m_bExplicit = strClass.equals("EXPLICIT");
		m_vecRelations.add(relation);

		// process relation arguments & mentions
		List lstChildren = elem.getChildren();
		ListIterator it = lstChildren.listIterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			String strName = el.getName();
			if (strName.equals("relation_argument")) { // process arguments
				String role = el.getAttribute("ROLE").getValue();
				if(role.startsWith("Arg-")){
					String strID = el.getAttribute("REFID").getValue();
					ACEEntity entArg = (ACEEntity) mapEntities.get(strID);
					int nArg = Integer.parseInt(role.substring(4));
					if (nArg == 1)
						relation.m_arg1 = entArg;
					else if (nArg == 2)
						relation.m_arg2 = entArg;
					else
						assert false;
				}
			}
			else if (el.getName().equals("relation_mention")) {
				ACERMention rmention = new ACERMention();
				rmention.m_strID = el.getAttribute("ID").getValue();
				ListIterator argit = el.getChildren().listIterator();
				while (argit.hasNext()) {
					Element elArg = (Element) argit.next();
					if (elArg.getName().equals("relation_mention_argument")) {
						ACEEMention emArg = (ACEEMention) 
						mapEMentions.get(elArg.getAttribute("REFID").getValue());
						String role = elArg.getAttribute("ROLE").getValue();
						if(role.startsWith("Arg-")){
							int nArg = Integer.parseInt(role.substring(4));
							if (nArg == 1)
								rmention.m_arg1 = emArg;
							else if (nArg == 2)
								rmention.m_arg2 = emArg;
							else
								assert false;
						}
					}
				}

				relation.addMention(rmention);
			}
		}
	}	


	protected ACEEMention getOtherEMention(ACERMention rm, ACEEMention em)
	{
		if (rm.m_arg1 == em)
			return rm.m_arg2;
		else
			return rm.m_arg1;
	}


	


	protected TreeMap getEntityRelations()
	{
		TreeMap mapEM = new TreeMap();
		for (int i = 0; i < m_vecRelations.size(); i++) {
			ACERelation relation = (ACERelation) m_vecRelations.get(i);
			if (relation.isExplicit()) {
				ListIterator it = relation.m_lstMentions.listIterator();
				while (it.hasNext()) {
					ACERMention rm = (ACERMention) it.next();
					addRelation(mapEM, rm.m_arg1, rm);
					addRelation(mapEM, rm.m_arg2, rm);
				}
			}
		}

		return mapEM;
	}


	protected void addRelation(TreeMap mapEM, ACEEMention em1, ACERMention rm)
	{
		LinkedList lstRM = (LinkedList) mapEM.get(em1);
		if (lstRM == null) {
			lstRM = new LinkedList();
			mapEM.put(em1, lstRM);
		}

		lstRM.add(rm);
	}


	public boolean overlap()
	{
		boolean bOverlap = false;

		for (int i = 0; i < m_vecRelations.size(); i++) {
			ACERelation r1 = (ACERelation) m_vecRelations.get(i);
			if (r1.isExplicit()) {
				ListIterator it1 = r1.m_lstMentions.listIterator();
				while (it1.hasNext()) {
					ACERMention rm1 = (ACERMention) it1.next();
					for (int j = i; j < m_vecRelations.size(); j++) {
						ACERelation r2 = (ACERelation) m_vecRelations.get(j);
						if (r2.isExplicit()) {
							ListIterator it2 = r2.m_lstMentions.listIterator();
							while (it2.hasNext()) {
								ACERMention rm2 = (ACERMention) it2.next();
								if (rm1.overlap(rm2)) {
									System.out.println("  Overlap between " + rm1.m_strID +
											" and " + rm2.m_strID);
									bOverlap = true;
								}
							}
						}
					}
				}
			}
		}

		return bOverlap;
	}


	public void include()
	{
		for (int i = 0; i < m_vecRelations.size(); i++) {
			ACERelation r1 = (ACERelation) m_vecRelations.get(i);
			if (r1.isExplicit()) {
				ListIterator it1 = r1.m_lstMentions.listIterator();
				while (it1.hasNext()) {
					ACERMention rm1 = (ACERMention) it1.next();
					for (int j = i; j < m_vecRelations.size(); j++) {
						ACERelation r2 = (ACERelation) m_vecRelations.get(j);
						if (r2.isExplicit()) {
							ListIterator it2 = r2.m_lstMentions.listIterator();
							while (it2.hasNext()) {
								ACERMention rm2 = (ACERMention) it2.next();
								if (rm1.include(rm2)) 
									rm1.m_nInc++;
								else if (rm2.include(rm1))
									rm2.m_nInc++;
							}
						}
					}
				}
			}
		}
	}


	public static void main_include(String[] args) 
	{
		if (args.length != 2) {
			System.out.println("Usage: java ACEDocument <corpus dir> <corpus file>!"); 
			return;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(args[1]));
			String strLine = br.readLine();
			while (strLine != null) {
				System.out.println("Document " + strLine + ":");

				String strFile = args[0] + "/" + strLine.trim();
				ACEDocument aceDoc = new ACEDocument();
				aceDoc.load(strFile);

				aceDoc.include();
				// display includes
				for (int i = 0; i < aceDoc.m_vecRelations.size(); i++) {
					ACERelation r1 = (ACERelation) aceDoc.m_vecRelations.get(i);
					if (r1.isExplicit()) {
						ListIterator it1 = r1.m_lstMentions.listIterator();
						while (it1.hasNext()) {
							ACERMention rm1 = (ACERMention) it1.next();
							System.out.println("Relation " + rm1.m_strID + 
									" includes: " + rm1.m_nInc);
						}
					}
				}

				/*
	// display degrees
	TreeMap mapEM = aceDoc.computeDegrees();
	Set entries = mapEM.entrySet();
	Iterator it = entries.iterator();
	while (it.hasNext()) {
	  Map.Entry entry = (Map.Entry) it.next();
	  LinkedList lstRels = (LinkedList) entry.getValue();
	  System.out.println("    Entity mention " + entry.getKey() + 
			     " has degree: " + lstRels.size());
	}
				 */

				strLine = br.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Segment> getSegments() {
		return m_strText;
	}

	public void setSegments(List<Segment> mStrText) {
		m_strText = mStrText;
	}
	
	public Vector<ACEEntity> getEntities(){
		return m_vecEntities;
		 
	}
	
	public Vector<ACERelation> getRelationships(){
		return m_vecRelations;
	}
}
