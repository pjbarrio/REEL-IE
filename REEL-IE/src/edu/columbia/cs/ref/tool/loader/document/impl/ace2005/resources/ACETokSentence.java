package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;
import java.io.*;
import java.util.*;

/**
 * Internal representation of a tokenized sentence in ACE 2005.
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
public class ACETokSentence {

  int m_nSent;

  String[] m_vecTokens;
  String[] m_vecPOS;

  LinkedList m_lstEM;
  LinkedList m_lstRM;
  
  public ACETokSentence(int nSent, Vector vecTokens, Vector vecPOS)
  {
    m_nSent = nSent;

    m_vecTokens = (String[]) vecTokens.toArray(new String[0]);
    m_vecPOS = (String[]) vecPOS.toArray(new String[0]);

    m_lstEM = new LinkedList();
    m_lstRM = new LinkedList();
  }

  public int length()
  {
    return m_vecTokens.length;
  }

  public void addEMention(ACEEMention aceEM)
  {
    m_lstEM.add(aceEM);
  }

  public void addRMention(ACERMention aceRM)
  {
    m_lstRM.add(aceRM);
  }

  public void displayRM()
  {
    ListIterator it = m_lstRM.listIterator();
    while (it.hasNext()) {
      ACERMention aceRM = (ACERMention) it.next();
      String strArg1 = getEMText(aceRM.m_arg1);
      String strArg2 = getEMText(aceRM.m_arg2);

      System.out.println(m_nSent + ": (" + aceRM.m_relation.m_strType 
			 + ", " + aceRM.m_relation.m_strSType + ") "
			 + strArg1 + " --> " + strArg2
			 + ": " + aceRM.m_arg1.m_strType + " "
			 + aceRM.m_arg1.m_entity.m_strType + " "
			 + aceRM.m_arg2.m_strType + " "
			 + aceRM.m_arg2.m_entity.m_strType);
    }
  }

  public String getEMText(ACEEMention aceEM)
  {
    String strText = "";
    for (int i = aceEM.m_nHBeg; i < aceEM.m_nHBeg + aceEM.m_nHLen; i++)
      strText += m_vecTokens[i] + " ";

    return strText.trim();
  }


  public void writeAnnotations(LinkedList lstAnno, PrintWriter pw)
  {
    // initialize out vector
    LinkedList[] vecOut = new LinkedList[m_vecTokens.length];
    for (int i = 0; i < vecOut.length; i++) {
      vecOut[i] = new LinkedList();
      vecOut[i].add(m_vecTokens[i]);
    }

    // augment out vector with annotations
    ListIterator it = lstAnno.listIterator();
    while (it.hasNext()) {
      ACEEMention em = (ACEEMention) it.next();
      vecOut[em.m_nBeg].addFirst(em.getBegin());
      vecOut[em.m_nBeg + em.m_nLen - 1].addLast(em.getEnd());
    }

    // create output string
    StringBuffer sbOut = new StringBuffer();
    for (int j = 0; j < vecOut.length; j++) {
      ListIterator lit = vecOut[j].listIterator();
      while (lit.hasNext()) {
        sbOut.append(lit.next());
        sbOut.append(" ");
      }
    }

    pw.println(sbOut);
  }


}
