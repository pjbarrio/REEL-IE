package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;
import java.util.*;

/**
 * Internal representation of an entity in ACE 2005.
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
public class ACEEntity {

	String m_strID;
	String m_strType;
	String m_strSubType;
	boolean m_bGeneric;

	LinkedList<ACEEMention> m_lstMentions;

	// entity attributes
	HashMap<String,String> m_mapAtt;

	public ACEEntity()
	{
		m_lstMentions = new LinkedList<ACEEMention>();
		m_mapAtt = new HashMap<String,String>();
	}

	public void addMention(ACEEMention emention)
	{
		emention.m_entity = this;
		m_lstMentions.add(emention);
	}

	public String getAttribute(String strKey)
	{
		return (String) m_mapAtt.get(strKey);
	}

	public void setAttribute(String strKey, String strVal)
	{
		m_mapAtt.put(strKey, strVal);
	}

	public String getId(){
		return m_strID;
	}

	public String getType(){
		return m_strType;
	}

	public String getSubtype(){
		return m_strSubType;
	}
	
	public List<ACEEMention> getMentions(){
		return m_lstMentions;
	}
}
