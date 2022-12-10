/**
 * 
 */
package com.iotcore.core.model.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jmgarcia
 *
 */
public class MetricsQuery implements Metrics {

	private static final long serialVersionUID = 6719679696883225169L;
	private static final Logger LOG = LoggerFactory.getLogger(MetricsQuery.class);
	
	private static final String PARAM_FMT = "{%s}";
	private static final String DBNAME_PARAM = "{DB_NAME}";
	private static final String TABLE_PARAM = "{TABLE_NAME}";
	private static final String CONDITIONAL_WHERE_PARAM = "{COND_WHERE_CLAUSES}";
	
	private static final String DB_DEFAULT = "IoTCloud";
	
	public static String databaseDefault = DB_DEFAULT;
	
	private String name;
	private String description;
	private String version;
	private String sql;
	private String sourceDatabase;
	private String sourceTable;
	private List<WhereCondition> conditionalWhereClauses;
	private Map<String, Object> parameterValues;
	private List<String> resultColumns;
	private Map<String, String> columnType;
	
	
	
	/**
	 * 
	 */
	public MetricsQuery() {
		parameterValues = new HashMap<String, Object>();
		setWhereConditions(new ArrayList<WhereCondition>());
	}
	
	
	/**
	 * 
	 */
	public MetricsQuery(String querySql, String[] parameterNames) {
		this.setSql(querySql);
		this.parameterValues = new HashMap<String, Object>();
		if (parameterNames != null) {
			for (String param:parameterNames) {
				this.parameterValues.put(param, null);
			}
		}
		setWhereConditions(new ArrayList<WhereCondition>());
	}


	/**
	 * @return
	 */
	public String getFinalSql() {
		
		StringBuffer buff = new StringBuffer(sql);
		
		buff = replaceParam(buff, DBNAME_PARAM,  getSourceDatabase());
		buff = replaceParam(buff, TABLE_PARAM, getSourceTable());
		
		// Replace query parameters
		for (String pName:parameterValues.keySet()) {
			Object value = parameterValues.get(pName);
			if (value != null) {
				String param = String.format(PARAM_FMT, pName);
				String valStr = paramValueOf(value);
				buff = replaceParam(buff, param, valStr);
			}
		}
		
		// Replace conditional where clauses
		StringBuffer whereClauses = new StringBuffer("");
		for (WhereCondition con:conditionalWhereClauses) {
			String sql = con.getFinalSql();
			whereClauses.append(sql);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Add query condition: {}", sql);
			}
		}
		buff = replaceParam(buff, CONDITIONAL_WHERE_PARAM, whereClauses.toString());
		
		return buff.toString();
	}
	
	/**
	 * @param name the name to set
	 */
	public MetricsQuery withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @param description the description to set
	 */
	public MetricsQuery withDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @param version the version to set
	 */
	public MetricsQuery withVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * @param sql the sql to set
	 */
	public MetricsQuery withSql(String sql) {
		this.sql = sql;
		return this;
	}
	
	/**
	 * @param parameterValues the parameterNames to set
	 */
	public MetricsQuery withParameters(List<String> parameterNames) {
		for (String param:parameterNames) {
			withParameterValue(param, null);
		}
		return this;
	}
	

	/**
	 * @param parameterValues the queryParameters to set
	 */
	public MetricsQuery withParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
		return this;
	}
	
	/**
	 * @param parameterValues the queryParameters to set
	 */
	public MetricsQuery withParameterValue(String parameter, Object value) {
		parameterValues.put(parameter, value);
		return this;
	}

	/**
	 * @param resultColumns the resultColumns to set
	 */
	public MetricsQuery withResultColumns(List<String> resultColumns) {
		this.resultColumns = resultColumns;
		return this;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public MetricsQuery withColumnType(Map<String, String> columnType) {
		this.columnType = columnType;
		return this;
	}



	/**
	 * @param sourceDatabase the sourceDatabase to set
	 */
	public MetricsQuery withSourceDatabase(String sourceDatabase) {
		this.sourceDatabase = sourceDatabase;
		return this;
	}



	/**
	 * @param sourceTable the sourceTable to set
	 */
	public MetricsQuery withSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
		return this;
	}
	
	/**
	 * @param conditionalWhereClauses the conditionalWhereClauses to set
	 */
	public MetricsQuery withConditionalWhereClauses(List<WhereCondition> whereConditions) {
		this.conditionalWhereClauses.addAll(whereConditions);
		return this;
	}

	/**
	 * @param whereCondition
	 * @return
	 */
	public MetricsQuery withConditionalWhereClause(WhereCondition whereCondition) {
		this.conditionalWhereClauses.add(whereCondition);
		return this;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}



	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}



	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}



	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}



	/**
	 * @return the sourceDatabase
	 */
	public String getSourceDatabase() {
		if (sourceDatabase == null) {
			sourceDatabase = databaseDefault;
		}
		return sourceDatabase;
	}



	/**
	 * @param sourceDatabase the sourceDatabase to set
	 */
	public void setSourceDatabase(String sourceDatabase) {
		this.sourceDatabase = sourceDatabase;
	}



	/**
	 * @return the sourceTable
	 */
	public String getSourceTable() {
		return sourceTable;
	}



	/**
	 * @param sourceTable the sourceTable to set
	 */
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}



	/**
	 * @return the parameterValues
	 */
	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}


	/**
	 * @param parameterValues the parameterValues to set
	 */
	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}



	/**
	 * @return the resultColumns
	 */
	public List<String> getResultColumns() {
		return resultColumns;
	}



	/**
	 * @param resultColumns the resultColumns to set
	 */
	public void setResultColumns(List<String> resultColumns) {
		this.resultColumns = resultColumns;
	}



	/**
	 * @return the columnType
	 */
	public Map<String, String> getColumnType() {
		return columnType;
	}



	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(Map<String, String> columnType) {
		this.columnType = columnType;
	}
	
	
	
	/**
	 * @return the conditionalWhereClauses
	 */
	public List<WhereCondition> getConditionalWhereClauses() {
		return conditionalWhereClauses;
	}


	/**
	 * @param conditionalWhereClauses the conditionalWhereClauses to set
	 */
	public void setWhereConditions(List<WhereCondition> whereConditions) {
		this.conditionalWhereClauses = whereConditions;
	}


	/**
	 * @param src
	 * @param param
	 * @param value
	 * @return
	 */
	private static StringBuffer replaceParam(StringBuffer src, String param, String value) {
		int l = param.length();
		int r = 0;
		while ((r = src.indexOf(param, r)) >= 0) {
			src.delete(r, r + l);
			src.insert(r, value);
		}
		return src;
	}


	

	private static String paramValueOf(Object value) {
		if (value instanceof String) {
			return (String)value;
		}
		else if  (value instanceof Date) {
			return DATE_FORMAT.format(value);
		}
		else if (value instanceof Object[]) {
			return paramValueOf((Object[])value);
		}
		else {
			return value.toString();
		}
	}
	
	private static String paramValueOf(Object [] value) {
		StringBuffer buff = new StringBuffer("'");
		String sep1 = "";
		String sep2 = "', '";
		String sep = sep1;
		for(Object val:value) {
			buff.append(sep);
			buff.append(paramValueOf(val));
			sep = sep2;
				
		}
		return buff.append('\'').toString();
	}
	
	
	/**
	 * Conditional clause for where conditions inside queries
	 *
	 */
	public static class WhereCondition {
		
		public static WhereCondition andParamNotNullCondition(MetricsQuery parent, String param, String sql) {
			return new WhereCondition(parent, 
					AppendOperation.AND, 
					param, 
					ActivationCondition.NOT_NULL, 
					sql);
		}
		
		public enum AppendOperation {
			AND,
			OR;
		}
		
		public enum ActivationCondition {
			IS_NULL,
			NOT_NULL,
			EQUALS,
			NOT_EQUALS;
		}
		
		private MetricsQuery parent;
		private String activationParam = "";
		private ActivationCondition activationCondition = ActivationCondition.NOT_NULL;
		private Object activationValue = null;
		private AppendOperation appendOperation = AppendOperation.AND;
		private String conditionSql;
		
		/**
		 * @param parent
		 */
		public WhereCondition(MetricsQuery parent) {
			this.parent = parent;
		}
		
		/**
		 * @param parent
		 * @param op
		 * @param param
		 * @param condition
		 * @param sql
		 */
		public WhereCondition(MetricsQuery parent, AppendOperation op, String param, ActivationCondition condition, String sql) {
			this.parent = parent;
			this.appendOperation = op;
			this.activationParam = param;
			this.conditionSql = sql;
		}
		
		
		/**
		 * @return
		 */
		public String getFinalSql() {
			Object paramValue =  (parent.getParameterValues().get(activationParam));
			boolean activate = false;
			switch (activationCondition) {
			case EQUALS:
				activate = paramValue.equals(activationValue);
				break;
			case IS_NULL:
				activate = paramValue == null;
				break;
			case NOT_EQUALS:
				activate = !paramValue.equals(activationValue);
				break;
			case NOT_NULL:
				activate = paramValue != null;
				break;
			default:
				break;
			}
			
			if (activate) {
				return appendOperation.name() + " " + conditionSql + " ";
			}
			else {
				return "";
			}
			
		}

		
		
		/**
		 * @param activationParam the activationParam to set
		 */
		public WhereCondition withActivationParam(String activationParam) {
			this.activationParam = activationParam;
			return this;
		}

		/**
		 * @param activationCondition the activationCondition to set
		 */
		public WhereCondition withActivationCondition(ActivationCondition activationCondition) {
			this.activationCondition = activationCondition;
			return this;
		}

		/**
		 * @param activationValue the activationValue to set
		 */
		public WhereCondition withActivationValue(Object activationValue) {
			this.activationValue = activationValue;
			return this;
		}

		/**
		 * @param appendOperation the appendOperation to set
		 */
		public WhereCondition withAppendOperation(AppendOperation appendOperation) {
			this.appendOperation = appendOperation;
			return this;
		}

		/**
		 * @param conditionSql the conditionSql to set
		 */
		public WhereCondition withConditionSql(String conditionSql) {
			this.conditionSql = conditionSql;
			return this;
		}
		

		/**
		 * @return the activationParam
		 */
		public String getActivationParam() {
			return activationParam;
		}

		/**
		 * @param activationParam the activationParam to set
		 */
		public void setActivationParam(String activationParam) {
			this.activationParam = activationParam;
		}

		/**
		 * @return the activationCondition
		 */
		public ActivationCondition getActivationCondition() {
			return activationCondition;
		}

		/**
		 * @param activationCondition the activationCondition to set
		 */
		public void setActivationCondition(ActivationCondition activationCondition) {
			this.activationCondition = activationCondition;
		}

		/**
		 * @return the activationValue
		 */
		public Object getActivationValue() {
			return activationValue;
		}

		/**
		 * @param activationValue the activationValue to set
		 */
		public void setActivationValue(Object activationValue) {
			this.activationValue = activationValue;
		}

		/**
		 * @return the appendOperation
		 */
		public AppendOperation getAppendOperation() {
			return appendOperation;
		}

		/**
		 * @param appendOperation the appendOperation to set
		 */
		public void setAppendOperation(AppendOperation appendOperation) {
			this.appendOperation = appendOperation;
		}

		/**
		 * @return the conditionSql
		 */
		public String getConditionSql() {
			return conditionSql;
		}

		/**
		 * @param conditionSql the conditionSql to set
		 */
		public void setConditionSql(String conditionSql) {
			this.conditionSql = conditionSql;
		}

		
	}

	
	
	
}
