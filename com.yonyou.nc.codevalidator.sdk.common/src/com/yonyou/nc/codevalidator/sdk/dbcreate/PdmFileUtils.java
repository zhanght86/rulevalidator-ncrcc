package com.yonyou.nc.codevalidator.sdk.dbcreate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.yonyou.nc.codevalidator.sdk.dbcreate.PdmFile.ViewInfo;
import com.yonyou.nc.codevalidator.sdk.log.Logger;

/**
 * PDM文件解析工具类，用来检查pdm文件
 * 
 * @author mazhqa
 * @since V2.2
 */
public class PdmFileUtils {

	/** PDM文件类型 */
	private static final String PD_FILE_TYPE = "PDM_DATA_MODEL_XML";
	/** PDM数据库类型版本 */
	private static final String DB_SQLSERVER_2005 = "Microsoft SQL Server 2005";
	/** PD版本 */
	private static final String PD_VERSION = "12.0.0.1700";
	/** TRUE的字符串表示 */
	private static final String TRUE_VALUE = "1";

	/**
	 * 校验PDM文件类型、数据库类型版本、PD版本信息。
	 * 
	 * @param pdmFile
	 *            待校验的PDM文件
	 * @throws PdmParseException - pdm格式错误时，抛出此错误
	 */
	@SuppressWarnings("resource")
	public static void validatePdm(File pdmFile) throws PdmParseException {
		BufferedReader br = null;
		boolean success = false;
		String fileName = pdmFile.getAbsolutePath();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(pdmFile), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				int indexOfPdStartTag = -1;
				// Current line may not only contain '<?PowerDesigner' tag and
				// the order of attributes is random.
				if ((indexOfPdStartTag = line.indexOf("<?PowerDesigner ")) != -1) {
					int indexOfPdEndTag = line.indexOf(">", indexOfPdStartTag);
					String pdContent = line.substring(indexOfPdStartTag + "<?PowerDesigner ".length(), indexOfPdEndTag);
					Pattern pattern = Pattern.compile("signature=\"([^\"]*)\"");
					Matcher matcher = pattern.matcher(pdContent);
					if (!matcher.find() || !PD_FILE_TYPE.equalsIgnoreCase(matcher.group(1))) {
						throw new PdmParseException("PDM(" + fileName + ")文件格式不对, 目前支持的文件类型为" + PD_FILE_TYPE);
					}
					pattern = Pattern.compile("Target=\"([^\"]*)\"");
					matcher = pattern.matcher(pdContent);
					if (!matcher.find() || !DB_SQLSERVER_2005.equalsIgnoreCase(matcher.group(1))) {
						throw new PdmParseException("PDM(" + fileName + ")文件中当前数据库类型不符, 目前支持的数据库类型为"
								+ DB_SQLSERVER_2005);
					}
					pattern = Pattern.compile("version=\"([^\"]*)\"");
					matcher = pattern.matcher(pdContent);
					if (!matcher.find() || !PD_VERSION.equalsIgnoreCase(matcher.group(1))) {
						throw new PdmParseException("PDM(" + fileName + ")文件中PowerDesigner版本不对, 目前支持的版本为"
								+ PD_VERSION);
					}
					success = true;
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			String errorMsg = "PDM(" + fileName + ")编码失败。";
			Logger.error(errorMsg, e);
			throw new PdmParseException(errorMsg);
		} catch (FileNotFoundException e) {
			String errorMsg = "PDM(" + fileName + ")不存在。";
			Logger.error(errorMsg);
			throw new PdmParseException(errorMsg);
		} catch (IOException e) {
			String errorMsg = "PDM(" + fileName + ")读取失败。";
			Logger.error(errorMsg, e);
			throw new PdmParseException(errorMsg);
		} finally {
			IOUtils.closeQuietly(br);
		}
		if (!success) {
			throw new PdmParseException("PDM(" + fileName + ")中文件有误。无法解析出数据库类型、版本等信息。");
		}
	}

	/**
	 * 解析PDM文件，提取出：表(列、主键、外键)、索引、视图信息。
	 * 
	 * @param pdmFile
	 *            待分析的PDM文件
	 * @param parseReference
	 *            是否解析外键引用
	 * @return {@link PdmFile}
	 * @throws PdmParseException 
	 */
	public static PdmFile parsePdm(File pdmFile, boolean parseReference) throws PdmParseException {
		long start = System.currentTimeMillis();
		String fileName = pdmFile.getAbsolutePath();
		Document dom = getDocument(pdmFile);
		Element rootEle = dom.getDocumentElement();
		PdmFile pdm = new PdmFile();
		pdm.setVersion(PD_VERSION);
		Element modelEle = (Element) DomUtils.findNestedChild(rootEle, "o:RootObject/c:Children/o:Model");
		List<Node> nameNodes = DomUtils.findNestedChilds(modelEle, "a:Name");
		if (nameNodes.size() != 1) {
			throw new PdmParseException("PDM(" + fileName + ")文件中包含非法数量的PDM");
		} else {
			pdm.setPdmDesc(nameNodes.get(0).getFirstChild().getNodeValue());
		}
		pdm.setPdmName(DomUtils.findChildContent(modelEle, "a:Code"));

		// 1.表(含主键约束)、索引
		Map<String, Table> idTableMap = new LinkedHashMap<String, Table>();
		Map<String, Column> idColumnMap = new HashMap<String, Column>();
		Map<String, Index> idIndexMap = new LinkedHashMap<String, Index>();
		parseTables(modelEle, fileName, idTableMap, idColumnMap, idIndexMap);
		pdm.getTables().addAll(idTableMap.values());
		pdm.getIndexs().addAll(idIndexMap.values());

		// 2.外键约束
		if (parseReference) {
			List<FkConstraint> fkConstraints = parseReferences(modelEle, pdmFile.getAbsolutePath(), idTableMap,
					idColumnMap);
			pdm.getFkConstraints().addAll(fkConstraints);
		}

		// 3.视图
		List<ViewInfo> viewInfos = parseViews(modelEle, fileName);
		pdm.getViews().addAll(viewInfos);

		Logger.debug("Total millis cost at parsing pdm(" + fileName + "): " + (System.currentTimeMillis() - start));
		return pdm;
	}

	/**
	 * 解析表信息。
	 * 
	 * @param modelEle
	 * @param xPath
	 * @param pdmFileName
	 * @param idTableMap
	 * @param idColumnMap
	 * @param idIndexMap
	 * @throws PdmParseException 
	 * @throws XPathExpressionException
	 */
	private static void parseTables(Element modelEle, String pdmFileName, Map<String, Table> idTableMap,
			Map<String, Column> idColumnMap, Map<String, Index> idIndexMap) throws PdmParseException {
		long start = System.currentTimeMillis();
		// 0.表集合
		List<Node> tableNodes = DomUtils.findNestedChilds(modelEle, "c:Tables/o:Table");
		if (tableNodes.isEmpty()) {
			throw new PdmParseException("PDM(" + pdmFileName + ")中没有找到表，请检查PDM中是否在顶层定义了package。");
		}

		for (Node tableNode : tableNodes) {
			// 1.表
			String tableId = ((Element) tableNode).getAttribute("Id");
			Table table = new Table();
			table.setDesc(DomUtils.findChildContent(tableNode, "a:Name"));
			table.setName(DomUtils.findChildContent(tableNode, "a:Code"));
			// 2.表列集合
			List<Node> colNodes = DomUtils.findNestedChilds(tableNode, "c:Columns/o:Column");
			if (colNodes.isEmpty()) {
				Logger.warn("PDM(" + pdmFileName + ")中表(" + table.getName() + ")未定义列。");
				continue;
			} else {
				boolean success = true;
				for (Node colNode : colNodes) {
					String colId = ((Element) colNode).getAttribute("Id");
					Column col = new Column();
					col.setDesc(DomUtils.findChildContent(colNode, "a:Name"));
					col.setName(DomUtils.findChildContent(colNode, "a:Code"));
					if (StringUtils.equalsIgnoreCase(col.getName(), "ts")
							|| StringUtils.equalsIgnoreCase(col.getName(), "dr")) {
						Logger.warn(new StringBuilder("PDM(").append(pdmFileName).append(")中表(")
								.append(table.getName()).append(")的列(").append(col.getName()).append(")无需设置。")
								.toString());
						continue;
					}
					col.setTypeName(DomUtils.findChildContent(colNode, "a:DataType"));
					if (StringUtils.isBlank(col.getTypeName())) {
						success = false;
						String msg = new StringBuilder("PDM(").append(pdmFileName).append(")中表(")
								.append(table.getName()).append(")的列(").append(col.getName()).append(")未设置数据类型。")
								.toString();
						Logger.error(msg);
						continue;
					}
					String length = DomUtils.findChildContent(colNode, "a:Length");
					if (StringUtils.isNotBlank(length)) {
						col.setLength(Integer.valueOf(length));
					}
					String precision = DomUtils.findChildContent(colNode, "a:Precision");
					if (StringUtils.isNotBlank(precision)) {
						col.setPrecise(Integer.valueOf(precision));
					}
					col.setNullable(!TRUE_VALUE.equals(DomUtils.findChildContent(colNode, "a:Mandatory")));
					String defaultValue = DomUtils.findChildContent(colNode, "a:DefaultValue");
					if (StringUtils.isNotBlank(defaultValue)) {
						if (col.getTypeName() != null && col.getTypeName().toLowerCase().lastIndexOf("char") != -1) {
							if (defaultValue.lastIndexOf("'") == -1) {
								defaultValue = "'" + defaultValue + "'";
							}
						}
						col.setDefaultValue(defaultValue);
					}

					String stereoType = DomUtils.findChildContent(colNode, "a:Stereotype");
					if (StringUtils.isNotBlank(stereoType)) {
						col.setStereotype(stereoType);
					}
					table.getAllColumns().add(col);
					idColumnMap.put(colId, col);
				}
				if (success) {
					idTableMap.put(tableId, table);
				} else {
					continue;
				}
			}

			// 3.key集合

			// 4.索引集合
			List<Node> indexNodes = DomUtils.findNestedChilds(tableNode, "c:Indexes/o:Index");
			for (Node indexNode : indexNodes) {
				String indexName = DomUtils.findChildContent(indexNode, "a:Code");
				List<Node> indexColNodes = DomUtils.findNestedChilds(indexNode,
						"c:IndexColumns/o:IndexColumn/c:Column/o:Column");
				if (indexColNodes.isEmpty()) {
					Logger.warn(new StringBuilder("PDM(").append(pdmFileName).append(")中表(").append(table.getName())
							.append(")的索引(").append(indexName).append(")未定义列。").toString());
					continue;
				}
				String indexId = ((Element) indexNode).getAttribute("Id");
				Index index = new Index();
				index.setTable(table);
				index.setUnique(TRUE_VALUE.equals(DomUtils.findChildContent(indexNode, "a:Unique")));
				index.setName(indexName);
				index.setDesc(DomUtils.findChildContent(indexNode, "a:Name"));
				for (Node indexColNode : indexColNodes) {
					Element indexColEle = (Element) indexColNode;
					index.getColumns().add(idColumnMap.get(indexColEle.getAttribute("Ref")));
				}
				idIndexMap.put(indexId, index);
			}

			// 5.主键
			String primaryKeyRef = DomUtils.findNestedChildAttr(tableNode, "c:PrimaryKey/o:Key", "Ref");
			if (StringUtils.isBlank(primaryKeyRef)) {
				Logger.warn("PDM(" + pdmFileName + ")中表(" + table.getName() + ")无主键。");
			} else {
				// 据primaryKeyRef查找主键列
				Element okeyEle = (Element) DomUtils.findNestedChild(tableNode, "c:Keys/o:Key", "Id", primaryKeyRef);
				if (okeyEle != null) {
					List<Node> pkColNodes = DomUtils.findNestedChilds(okeyEle, "c:Key.Columns/o:Column");
					if (!pkColNodes.isEmpty()) {
						PkConstraint pkConstraint = new PkConstraint();
						String constraintName = DomUtils.findChildContent(okeyEle, "a:ConstraintName");
						if (StringUtils.isBlank(constraintName)) {
							constraintName = "pk_" + table.getName();
						}
						pkConstraint.setName(constraintName.toLowerCase());
						boolean success = true;
						for (Node pkColNode : pkColNodes) {
							String colRef = ((Element) pkColNode).getAttribute("Ref");
							Column col = idColumnMap.get(colRef);
							if (col == null) {
								success = false;
								break;
							}
							pkConstraint.getColumns().add(col);
						}
						if (success) {
							table.setPkConstraint(pkConstraint);
						} else {
							Logger.warn("PDM(" + pdmFileName + ")中表(" + table.getName() + ")无主键。");
						}
					}

				}
			}

			// 6.聚集作用对象(主键或索引)
			String clusteredIndexId = DomUtils.findNestedChildAttr(tableNode, "c:ClusterObject/o:Index", "Ref");
			if (StringUtils.isNotBlank(clusteredIndexId)) {
				Index index = idIndexMap.get(clusteredIndexId);
				if (index != null) {
					index.setClustered(true);
				}
			}
		}
		Logger.debug(new StringBuilder("Millis cost at parsing (").append(pdmFileName).append(") with ")
				.append(tableNodes.size()).append(" tables: ").append((System.currentTimeMillis() - start)).toString());
	}

	/**
	 * 解析外键引用。
	 * 
	 * @param modelEle
	 * @param xPath
	 * @param pdmFileName
	 * @param idTableMap
	 * @param idColumnMap
	 * @return
	 * @throws XPathExpressionException
	 */
	private static List<FkConstraint> parseReferences(Element modelEle, String pdmFileName,
			Map<String, Table> idTableMap, Map<String, Column> idColumnMap) {
		long startMillis = System.currentTimeMillis();
		List<FkConstraint> fkConstraints = new ArrayList<FkConstraint>();
		List<Node> referenceNodes = DomUtils.findNestedChilds(modelEle, "c:References/o:Reference");
		if (referenceNodes.isEmpty()) {
			Logger.debug("PDM(" + pdmFileName + ")中未配置外键引用。");
			return fkConstraints;
		}

		for (Node referenceNode : referenceNodes) {
			String referName = DomUtils.findChildContent(referenceNode, "a:Code"), referDesc = DomUtils.findChildContent(
					referenceNode, "a:Name");
			String mainTableId = DomUtils.findNestedChildAttr(referenceNode, "c:Object1/o:Table", "Ref");
			String subTableId = DomUtils.findNestedChildAttr(referenceNode, "c:Object2/o:Table", "Ref");
			Table mainTable = idTableMap.get(mainTableId);
			Table subTable = idTableMap.get(subTableId);
			if (mainTable == null || subTable == null) {
				Logger.warn(new StringBuilder("PDM(").append(pdmFileName).append(")中外键引用(").append(referDesc)
						.append(")引用关联的表无效。").toString());
				continue;
			}
			Node referenceJoinNode = DomUtils.findNestedChild(referenceNode, "c:Joins/o:ReferenceJoin");
			if (referenceJoinNode != null) {
				String mainTableColId = DomUtils.findNestedChildAttr(referenceJoinNode, "c:Object1/o:Column", "Ref");
				String subTableColId = DomUtils.findNestedChildAttr(referenceJoinNode, "c:Object2/o:Column", "Ref");
				;
				Column mainTableCol = idColumnMap.get(mainTableColId);
				Column subTableCol = idColumnMap.get(subTableColId);

				if (mainTableCol != null || subTableCol != null) {
					FkConstraint fkConstraint = new FkConstraint();
					fkConstraint.setTable(subTable);
					fkConstraint.setRefTable(mainTable);
					fkConstraint.getColumns().add(subTableCol);
					fkConstraint.getRefColumns().add(mainTableCol);
					fkConstraint.setName(referName);
					fkConstraints.add(fkConstraint);
					subTable.getFkConstraints().add(fkConstraint);
				} else {
					Logger.warn(new StringBuilder("PDM(").append(pdmFileName).append(")中外键引用(").append(referDesc)
							.append(")引用关联的表列无效。").toString());
				}
			} else {
				Logger.warn(new StringBuilder("PDM(").append(pdmFileName).append(")中外键引用(").append(referDesc)
						.append(")引用关联的表列无效。").toString());
			}
		}

		Logger.debug("Millis cost at parsing " + referenceNodes.size() + " references is: "
				+ (System.currentTimeMillis() - startMillis));
		return fkConstraints;
	}

	/**
	 * 解析视图。
	 * 
	 * @param modelEle
	 * @param xPath
	 * @param pdmFileName
	 * @return
	 * @throws XPathExpressionException
	 */
	@SuppressWarnings("unchecked")
	private static List<ViewInfo> parseViews(Element modelEle, String pdmFileName) {
		long startMillis = System.currentTimeMillis();
		List<Node> viewNodes = DomUtils.findNestedChilds(modelEle, "c:Views/o:View");
		if (viewNodes.isEmpty()) {
			Logger.debug("PDM(" + pdmFileName + ")中未配置视图。");
			return Collections.EMPTY_LIST;
		}

		List<ViewInfo> viewInfos = new ArrayList<ViewInfo>();
		for (Node viewNode : viewNodes) {
			String viewName = DomUtils.findChildContent(viewNode, "a:Code"), viewDesc = DomUtils.findChildContent(
					viewNode, "a:Name");
			List<Node> viewColNodes = DomUtils.findNestedChilds(viewNode, "c:Columns/o:ViewColumn");
			if (viewColNodes.isEmpty()) {
				Logger.warn("PDM(" + pdmFileName + ")中视图(" + viewDesc + ")未设置查询列。");
			} else {
				// boolean success = true;
				// for(Node viewColNode : viewColNodes){
				// String colName = DomUtil.findChildContent(viewColNode,
				// "a:Code");
				//
				// List<Node> viewColInnerNodes =
				// DomUtil.findNestedChilds(viewColNode,
				// "c:ViewColumn.Columns/o:Column");
				// if(viewColInnerNodes.isEmpty()){
				// success = false;
				// Logger.warn(new
				// StringBuilder("PDM(").append(pdmFileName).append(")中视图(")
				// .append(viewDesc).append(")中列(").append(colName).append(")无效。").toString());
				// }
				// }
				// if(!success){
				// continue;
				// }
				String sqlQuery = DomUtils.findChildContent(viewNode, "a:View.SQLQuery");
				if (StringUtils.isNotBlank(sqlQuery)) {
					sqlQuery = sqlQuery.replaceAll("&#39;", "'");
					ViewInfo viewInfo = new ViewInfo();
					viewInfo.setName(viewName);
					viewInfo.setDesc(viewDesc);
					viewInfo.setSql(sqlQuery);
					viewInfo.setDesc(DomUtils.findChildContent(viewNode, "Name"));
					viewInfos.add(viewInfo);
				}
			}
		}
		Logger.debug("Millis cost at parsing " + viewNodes.size() + " view is: "
				+ (System.currentTimeMillis() - startMillis));
		return viewInfos;
	}

	private static Document getDocument(File file) throws PdmParseException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(file);
		} catch (Exception e) {
			Logger.error("解析文件" + file.getPath() + "为xml出错。", e);
			throw new PdmParseException("解析文件" + file.getPath() + "为xml出错。");
		}
	}

	static enum CaseType {
		TOLOWER, TOUPPER, RESERVE
	}

}
