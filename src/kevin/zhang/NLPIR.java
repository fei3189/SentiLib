package kevin.zhang;
import java.io.*;

public class NLPIR {
    public static native boolean  NLPIR_Init(byte[] sDataPath,int encoding);
		public static native boolean  NLPIR_Exit();
		public native   int NLPIR_ImportUserDict(byte[] sPath);
		public native float NLPIR_GetUniProb(byte[] sWord);
		public native boolean NLPIR_IsWord(byte[] sWord);
		public native   byte[] NLPIR_ParagraphProcess(byte[] sSrc,int bPOSTagged);
		public native   boolean NLPIR_FileProcess(byte[] sSrcFilename,byte[] sDestFilename,int bPOSTagged);
		public native   byte[] nativeProcAPara(byte[] src);
		public native int NLPIR_AddUserWord(byte[] sWord);//add by qp 2008.11.10
		public native int NLPIR_SaveTheUsrDic();
		public native int NLPIR_DelUsrWord(byte[] sWord);
	 
	 	public native int NLPIR_SetPOSmap(int nPOSmap);
		public native int NLPIR_GetElemLength(int nIndex);
		
		public native  byte[] NLPIR_GetKeyWords( byte[] sLine,int nMaxKeyLimit,boolean bWeightOut);
		public native  byte[] NLPIR_GetFileKeyWords( byte[] sFilename,int nMaxKeyLimit,boolean bWeightOut);
		public native  byte[] NLPIR_GetNewWords( byte[]sLine,int nMaxKeyLimit,boolean bWeightOut);
		public native  byte[] NLPIR_GetFileNewWords( byte[] sFilename,int nMaxKeyLimit,boolean bWeightOut);

/*********************************************************************
*
*  ���º���Ϊ2013�汾ר������´ʷ��ֵĹ�̣�һ�㽨���ѻ�ʵ�֣��������ߴ���
*  �´�ʶ����ɺ����Զ����뵽�ִ�ϵͳ�У��������
*  ������NLPIR_NWI(New Word Identification)��ͷ
*********************************************************************/
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_Start
*
*  Description: �����´�ʶ��

*
*  Parameters : None
*  Returns    : boolean, true:success, false:fail
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native boolean NLPIR_NWI_Start();//New Word Indentification Start
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_AddFile
*
*  Description: ���´�ʶ��ϵͳ����Ӵ�ʶ���´ʵ��ı��ļ�
*				��Ҫ������NLPIR_NWI_Start()֮�󣬲���Ч
*
*  Parameters : byte[]sFilename���ļ���
*  Returns    : boolean, true:success, false:fail
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native int  NLPIR_NWI_AddFile(byte[]sFilename);
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_AddMem
*
*  Description: ���´�ʶ��ϵͳ�����һ�δ�ʶ���´ʵ��ڴ�
*				��Ҫ������NLPIR_NWI_Start()֮�󣬲���Ч
*
*  Parameters : byte[]sFilename���ļ���
*  Returns    : boolean, true:success, false:fail
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native boolean NLPIR_NWI_AddMem(byte[]sText);
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_Complete
*
*  Description: �´�ʶ��������ݽ���
*				��Ҫ������NLPIR_NWI_Start()֮�󣬲���Ч
*
*  Parameters : None
*  Returns    : boolean, true:success, false:fail
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native boolean NLPIR_NWI_Complete();//�´�
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_GetResult
*
*  Description: ��ȡ�´�ʶ��Ľ��
*				��Ҫ������NLPIR_NWI_Complete()֮�󣬲���Ч
*
*  Parameters : bWeightOut���Ƿ���Ҫ���ÿ���´ʵ�Ȩ�ز���
*
*  Returns    : �����ʽΪ
*				���´�1�� ��Ȩ��1�� ���´�2�� ��Ȩ��2�� ... 
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native byte[] NLPIR_NWI_GetResult(boolean bWeightOut);//����´�ʶ����
/*********************************************************************
*
*  Func Name  : NLPIR_NWI_Result2UserDict
*
*  Description: ���´�ʶ�����뵽�û��ʵ���
*				��Ҫ������NLPIR_NWI_Complete()֮�󣬲���Ч
*				�����Ҫ���´ʽ�����ñ��棬������ִ��NLPIR_SaveTheUsrDic
*  Parameters : None
*  Returns    : boolean, true:success, false:fail
*
*  Author     : Kevin Zhang
*  History    : 
*              1.create 2012/11/23
*********************************************************************/
public native int  NLPIR_NWI_Result2UserDict();//�´�ʶ����תΪ�û��ʵ�,�����´ʽ����Ŀ

    /* Use static intializer */
    static {
			System.loadLibrary("NLPIR_JNI");
    }
}


