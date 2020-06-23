package ykk.xc.com.bswms.bean;

import java.io.Serializable;

/**
 * 结构表
 * @author Administrator
 *
 */
public class ICItemStructure implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String fnumber;			// 代码
	private String fname; 			// 名称

	public ICItemStructure() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFnumber() {
		return fnumber;
	}
	public void setFnumber(String fnumber) {
		this.fnumber = fnumber;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
}