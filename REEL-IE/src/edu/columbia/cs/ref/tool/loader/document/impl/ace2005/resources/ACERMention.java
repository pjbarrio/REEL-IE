package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;

import edu.columbia.cs.ref.model.entity.Entity;

/**
 * Internal representation of a relationship mention in ACE 2005.
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
public class ACERMention {

	ACERelation m_relation;

	String m_strID;
	ACEEMention m_arg1;
	ACEEMention m_arg2;

	protected int m_nInc;

	public ACERMention()
	{
		m_nInc = 0;
	}

	public boolean include(ACERMention rm)
	{
		ACEEMention arg11, arg12, arg21, arg22;

		if (m_arg1.m_nBeg < m_arg2.m_nBeg) {
			arg11 = m_arg1;
			arg12 = m_arg2;
		}
		else {
			arg11 = m_arg2;
			arg12 = m_arg1;
		}

		if (rm.m_arg1.m_nBeg < rm.m_arg2.m_nBeg) {
			arg21 = rm.m_arg1;
			arg22 = rm.m_arg2;
		}
		else {
			arg21 = rm.m_arg2;
			arg22 = rm.m_arg1;
		}

		return arg11.m_nBeg + arg11.m_nLen <= arg21.m_nBeg &&
		arg22.m_nBeg + arg22.m_nLen <= arg12.m_nBeg;
	}


	public boolean overlap(ACERMention rm)
	{
		ACEEMention arg11, arg12, arg21, arg22;

		if (m_arg1.m_nBeg < m_arg2.m_nBeg) {
			arg11 = m_arg1;
			arg12 = m_arg2;
		}
		else {
			arg11 = m_arg2;
			arg12 = m_arg1;
		}

		if (rm.m_arg1.m_nBeg < rm.m_arg2.m_nBeg) {
			arg21 = rm.m_arg1;
			arg22 = rm.m_arg2;
		}
		else {
			arg21 = rm.m_arg2;
			arg22 = rm.m_arg1;
		}


		return (arg11.m_nBeg > arg21.m_nBeg + arg21.m_nLen &&
				arg11.m_nBeg + arg11.m_nLen < arg22.m_nBeg &&
				arg12.m_nBeg > arg22.m_nBeg + arg22.m_nLen) ||
				(arg21.m_nBeg > arg11.m_nBeg + arg11.m_nLen &&
						arg21.m_nBeg + arg21.m_nLen < arg12.m_nBeg &&
						arg22.m_nBeg > arg12.m_nBeg + arg12.m_nLen);
	}

	public String getId(){
		return m_strID;
	}

	public ACEEMention getArg1(){
		return m_arg1;
	}

	public ACEEMention getArg2(){
		return m_arg2;
	}
}
