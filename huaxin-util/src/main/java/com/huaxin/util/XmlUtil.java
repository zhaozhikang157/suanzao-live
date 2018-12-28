package com.huaxin.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * 解析xml字符串
 * @author bhj
 *
 */
public class XmlUtil {
	private static Logger log = LoggerFactory.getLogger(XmlUtil.class);
	
	private Document doc = null;

	public XmlUtil(String xmlStr) {
		try {
			this.doc = DocumentHelper.parseText(xmlStr);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public XmlUtil(InputStream xml) {
		SAXReader reader = new SAXReader();
		try {
			this.doc = reader.read(xml);
		} catch (DocumentException e) {
			log.error("", e);
		}
	}
	
	/**
	 * @param visitElement:节点名称
	 * @return 节点值
	 */
	public String getChildText(String visitElement){
		Element ele = getChildElement(visitElement);
		if(ele != null){
			return ele.getTextTrim();
		}
		return null;
	}
	
	
	/**
	 * @param visitElement:属性所在的节点名称
	 * @param attribute 属性
	 * @return 属性值
	 */
	public String getAttributeValue(String visitElement,String attribute){
		log.info("param is [visitElement :"+visitElement+",attribute : "+attribute+"]");
		Element el = getChildElement(visitElement);
		if(el == null){
			return null;
		}
		String attributeValue = el.attributeValue(attribute);
		return attributeValue;
	}
	
	/**
	 * 
	 * @param visitElement:节点名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Element getChildElement(String visitElement){
		Element ele = findElements(getRootElement().elements(), visitElement);
		return ele;
	}
	
	/**
	 * 
	 * @param elements:节点集合
	 * @param visitElement:需要查找的节点名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Element findElements(List<Element> elements,String visitElement){
		log.info("param is [visitElement :"+visitElement+"]");
		if(visitElement == null || "".equals(visitElement)){
			return null;
		}
		for(Element el : elements){
			List<Element> nodes = el.elements();
			int size = nodes.size();
			if(size > 0){
				Element result = findElements(nodes, visitElement);
				return result;
			}else{
				if(el.getName().equalsIgnoreCase(visitElement)){
					return el;
				}
			}
		}
		log.info("not find the node :"+visitElement);
		return null;
	}
	
	
	public Element getRootElement(){
		return this.doc.getRootElement();
	}
	
	/**
	 * 获取根节点下的所有子节点
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Element> findAllElements(){
		List<Element> elements = getRootElement().elements();
		return elements;
	}
	
	public String toString(){
		if(this.doc == null){
			return "";
		}
		return this.doc.asXML();
	}

}
