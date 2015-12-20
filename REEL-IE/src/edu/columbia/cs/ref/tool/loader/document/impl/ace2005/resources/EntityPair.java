package edu.columbia.cs.ref.tool.loader.document.impl.ace2005.resources;

/**
 * Internal representation of a span representing an entity in ACE 2005.
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
public class EntityPair {

  static public class Labels {
    public static final int NONE = 0;
    public static final int SOC_SYM = 1;
  }

  String m_strID;

  // index of left entity
  int m_nLeft;
  // index of right entity 
  int m_nRight;
  
  int m_nTrueLabel;
  int m_nInfLabel;

  // confidence for inferred label
  double m_conf;


  public EntityPair(String strID, int nLeft, int nRight)
  {
    m_strID = m_strID;

    m_nLeft = nLeft;
    m_nRight = nRight;

    m_nTrueLabel = m_nInfLabel = -1;
    m_conf = 0.0;
  }


}
