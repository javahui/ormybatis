package cn.bblink.common.ormybatis.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import cn.bblink.common.ormybatis.bean.OperationExpressionBean;
import cn.bblink.common.ormybatis.bean.OrderByBean;

/**
 * Where语句构造器
 * 
 * @author donghui
 */
public class WhereBuilder {
	private List<OperationExpressionBean> operExpressList = new ArrayList<OperationExpressionBean>();
	private List<OrderByBean> orderByList = new ArrayList<OrderByBean>();
	private Integer limitIndex = 0;
	private Integer limitSize = null;
	
	public WhereBuilder() {}
	
	public WhereBuilder(String field, Object value) {
		this.eq(field, value);
	}
	
	/**
	 * 等于 (=) 默认(查询条件isNotEmpty)
	 */
	public WhereBuilder eq(String field, Object value) {
		return this.setOperationExpression(field, "=", value);
	}
	
	/**
	 * required = true 不要为空的查询条件
	 */
	public WhereBuilder eq(String field, Object value, boolean required) {
		return this.setOperationExpression(field, "=", value, required);
	}
	
	/**
	 * 等于 (=)
	 */
	public WhereBuilder eq(Map<String, Object> paramMap) {
		if (MapUtils.isNotEmpty(paramMap)) {
			for (String key : paramMap.keySet()) {
				this.eq(key, paramMap.get(key));
			}
		}
		return this;
	}
	
	/**
	 * 大于(>)
	 */
	public WhereBuilder gt(String field, Object value) {
		return this.setOperationExpression(field, ">", value);
	}
	
	/**
	 * 大于等于(>=) 
	 */
	public WhereBuilder gte(String field, Object value) {
		return this.setOperationExpression(field, ">=", value);
	}
	
	/**
	 * 小于(<) 
	 */
	public WhereBuilder lt(String field, Object value) {
		return this.setOperationExpression(field, "<", value);
	}
	
	/**
	 * 小于等于(<=) 
	 */
	public WhereBuilder lte(String field, Object value) {
		return this.setOperationExpression(field, "<=", value);
	}
	
	/**
	 * like查询(% + value + %)
	 */
	public WhereBuilder like(String field, Object value) {
		return this.setOperationExpression(field, "like", value);
	}
	
	/**
	 * like查询(value + %)
	 */
	public WhereBuilder likeLeft(String field, Object value) {
		return this.setOperationExpression(field, "like_left", value);
	}
	
	/**
	 * like查询(% + value)
	 */
	public WhereBuilder likeRight(String field, Object value) {
		return this.setOperationExpression(field, "like_right", value);
	}
	
	/**
	 * 精确到date日期(天)的比较,日期字符串形式yyyy-MM-dd
	 * <pre>
	 * AND DATE_FORMAT(field, '%Y-%m-%d') >= startDate
	 * AND DATE_FORMAT(field, '%Y-%m-%d') <= endDate
	 * </pre>
	 * @param field 
	 * @param startDate 开始日期(yyyy-MM-dd)
	 * @param endDate 结束日期(yyyy-MM-dd)
	 */
	public WhereBuilder betweenDate(String field, String startDate, String endDate) {
		startDate = StringUtils.defaultIfBlank(startDate, "1970-01-01");
		endDate = StringUtils.defaultIfBlank(endDate, "2050-12-12");
		field = "DATE_FORMAT(" + field + ", '%Y-%m-%d')";
		return this.gte(field, startDate).lte(field, endDate);
	}
	
	/**
	 *  IN (xxx,yyy)
	 */
	public WhereBuilder in(String field, Object... values) {
		return this.setOperationExpression(field, "in", values);
	}
	
	/**
	 *  IN (xxx,yyy)
	 */
	public WhereBuilder in(String field, List values) {
		return this.setOperationExpression(field, "in", values);
	}

	public WhereBuilder isNull(String field) {
		return this.setOperationExpression(field, "IS", "NULL");
	}

	public WhereBuilder isNotNull(String field) {
		return this.setOperationExpression(field, "IS NOT", "NULL");
	}
	
	/**
	 * LIMIT limitSize
	 */
	public WhereBuilder limit(int limitSize) {
		this.limitSize = limitSize;
		return this;
	}
	/**
	 * LIMIT limitIndex, limitSize
	 */
	public WhereBuilder limit(int limitIndex, int limitSize) {
		this.limitIndex = ((limitIndex < 0) ? 0 : limitIndex);
		this.limitSize = limitSize;
		return this;
	}
	
	/**
	 * order by field_name desc
	 */
	public WhereBuilder orderDesc(String field) {
		orderByList.add(new OrderByBean(field, "DESC"));
		return this;
	}
	
	/**
	 * order by field_name asc
	 */
	public WhereBuilder orderAsc(String field) {
		orderByList.add(new OrderByBean(field, "ASC"));
		return this;
	}
	
	/**
	 * update时 field_name = field_name + 1
	 */
	public WhereBuilder incr(String field) {
		return this.incr(field, 1);
	}
	
	/**
	 * update时 field_name = field_name + num
	 */
	public WhereBuilder incr(String field, Number num) {
		return this.setOperationExpression(field, "incr", num);
	}
	
	/**
	 * 判断查询条件lis不为空
	 * @return
	 */
	public boolean isEmptyOperExpressList() {
		return CollectionUtils.isEmpty(this.operExpressList);
	}
	
	private WhereBuilder setOperationExpression(String field, String oper, Object value){
		return this.setOperationExpression(field, oper, value, false);
	}
	
    private WhereBuilder setOperationExpression(String field, String oper, Object value, boolean required){
    	if (!required) {
    		if (value == null) {
    			return this;
    		}
        	if (value instanceof String) {
        		if (StringUtils.isBlank((String)value)) {
        			return this;
        		}
        	}
        	if(value.getClass().isArray() ) {
        		if (ArrayUtils.isEmpty((Object[])value)) {
        			return this;
        		}
        	}
        	if (value instanceof Collection) {
        		if (CollectionUtils.isEmpty((Collection)value)) {
        			return this;
        		}
        	}
    		if (value instanceof Map) {
    			if (MapUtils.isEmpty((Map)value)) {
    				return this;
    			}
    		}
		}
    	OperationExpressionBean bean = new OperationExpressionBean(field, oper, value);
    	operExpressList.add(bean);
    	return this;
    }
    
    public List<OperationExpressionBean> getOperExpressList() {return operExpressList;}
	public void setOperExpressList(List<OperationExpressionBean> operExpressList) {this.operExpressList = operExpressList;}
	
	public List<OrderByBean> getOrderByList() {return orderByList;}
	public void setOrderByList(List<OrderByBean> orderByList) {this.orderByList = orderByList;}
	
	public Integer getLimitIndex() {return limitIndex;}
	public void setLimitIndex(Integer limitIndex) {this.limitIndex = limitIndex;}
	
	public Integer getLimitSize() {return limitSize;}
	public void setLimitSize(Integer limitSize) {this.limitSize = limitSize;}
}