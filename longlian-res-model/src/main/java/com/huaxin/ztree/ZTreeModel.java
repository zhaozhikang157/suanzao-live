package com.huaxin.ztree;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * ZTree模型数据对象
 * @author syl
 *  
 */
public class ZTreeModel implements Serializable{
	
	/*文件夹*/
	public static final String FILE_FOLDER = "file_folder";
	
	/*共享文件夹*/
	public static final String FILE_FOLDER_SHARE = "file_folder_share";
	
	/*在线人员*/
	public static final String USER_ONLINE = "person_online_node";
	
	/*不在线人员*/
	public static final String USER_OFFLINE = "person_offline_node";

	/**
	 * 构造函数
	 */
	public ZTreeModel(){
		
	}
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param isParent
	 * @param pId
	 * @param open
	 * @param iconSkin
	 * @param title
	 */
	public ZTreeModel(String id, String name, boolean isParent, String pId, boolean open, String iconSkin, String title)
	{
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.iconSkin = iconSkin;
		this.open = open;
		this.title = title;
		this.isParent = isParent;
	}

	
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param isParent
	 * @param pId
	 * @param open
	 * @param iconSkin
	 * @param title
	 * @param onRight 是否允许右击
	 */
	public ZTreeModel(String id, String name, boolean isParent, String pId, boolean open, String iconSkin, String title, boolean onRight)
	{
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.iconSkin = iconSkin;
		this.open = open;
		this.title = title;
		this.isParent = isParent;
		this.onRight = onRight;
	}
	
	
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param isParent
	 * @param pId
	 * @param open
	 * @param iconSkin
	 * @param title
	 */
	public ZTreeModel(String id, String name, boolean isParent, String pId, boolean open, String iconSkin, String title, boolean onRight, boolean isCheck)
	{
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.iconSkin = iconSkin;
		this.open = open;
		this.title = title;
		this.isParent = isParent;
		this.onRight = onRight;
		this.checked = isCheck;
	}
	
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param isParent
	 * @param pId
	 * @param open
	 * @param iconSkin
	 * @param title
	 */
	public ZTreeModel(String id, String name, boolean isParent, String pId, boolean open, String iconSkin, String title, boolean onRight, boolean isCheck, boolean noCheck)
	{
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.iconSkin = iconSkin;
		this.open = open;
		this.title = title;
		this.isParent = isParent;
		this.onRight = onRight;
		this.checked = isCheck;
		this.nocheck = noCheck;
	}
	
	/**
	 * 构造函数
	 * @param id
	 * @param name
	 * @param isParent
	 * @param pId
	 * @param open
	 * @param iconSkin
	 * @param title
	 * @param onRight 是否允许右击 
	 * @param extend1 扩展字段1
	 * @param extend2 扩展字段2  
	 */
	public ZTreeModel(String id, String name, boolean isParent, String pId, boolean open, String iconSkin, String title, boolean onRight, String extend1, String extend2)
	{
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.iconSkin = iconSkin;
		this.open = open;
		this.title = title;
		this.isParent = isParent;
		this.onRight = onRight;
		this.extend1 = extend1;
		this.extend2 = extend2;
	}
	private String id;
	private String pId;
	private String name;
	private String iconSkin;
	private Map<String,Object> params = new HashMap<String,Object>();//额外附加参数
	
	private boolean open;
	private boolean checked;
	private boolean disabled;
	
	private boolean nocheck;
	
	private String title;//title
	
	private boolean onRight = false;
	
	private String extend1;//扩展字段1
	
	private String extend2;//扩展字段2
	
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	private boolean isParent;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIconSkin() {
		return iconSkin;
	}
	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}
	public boolean isParent() {
		return isParent;
	}
	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}
	public void setParams(Map<String,Object> params) {
		this.params = params;
	}
	public Map<String,Object> getParams() {
		return params;
	}
	public String getTitle() {
			if (title == null) return name;
			title = title.trim();
			if(title.length() < 1)	 return name;
			return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}
	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public boolean isOnRight() {
		return onRight;
	}
	public void setOnRight(boolean onRight) {
		onRight = onRight;
	}
	public String getExtend1() {
		return extend1;
	}
	public String getExtend2() {
		return extend2;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public boolean isNocheck() {
		return nocheck;
	}
	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}


}
