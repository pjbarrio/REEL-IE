package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;
import java.util.*;

/**
 * Internal representation of a relationship in ACE 2005.
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
public class ACERelation {

	String m_strID;
	boolean m_bExplicit;

	String m_strType;
	String m_strSType;

	ACEEntity m_arg1;
	ACEEntity m_arg2;

	LinkedList<ACERMention> m_lstMentions;

	public ACERelation()
	{
		m_lstMentions = new LinkedList();
		m_bExplicit = true;
	}

	public void addMention(ACERMention rmention)
	{
		m_lstMentions.add(rmention);
		rmention.m_relation = this;
	}

	public boolean isExplicit()
	{
		return m_bExplicit;
	}

	public String getId(){
		return m_strID;
	}

	public String getType(){
		return m_strType;
	}

	public String getSubType(){
		return m_strSType;
	}
	
	public List<ACERMention> getMentions(){
		return m_lstMentions;
	}

}
