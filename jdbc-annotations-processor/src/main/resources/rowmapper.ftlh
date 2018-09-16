<#assign FieldUtil=statics['fr.nduheron.socle.jdbc.annotations.utils.FieldUtil']>

package ${mapper.packageName};

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;
<#if mapper.entity.converters?has_content >
import org.springframework.beans.factory.annotation.Autowired;
</#if>
<#list mapper.entity.converters as converter>
import ${converter.fullClassName};
</#list>
import ${mapper.entity.fullClassName};

@Component
public class ${mapper.className}Impl implements ${mapper.className} {

	<#list mapper.entity.converters as converter>
	@Autowired
	private ${converter.className} ${converter.fieldName};
	</#list>
	
	@Override
	public ${mapper.entity.className} mapRow(ResultSet rs, int rowNum) throws SQLException {
		${mapper.entity.className} result = new ${mapper.entity.className}();
		
  		<#list mapper.entity.fields as field>
  			<#if field.parameters?has_content>
  				<#if field.nulSafe>
	  				<#list field.parameters as param>
  		${param.type} ${FieldUtil.getVariableName(field, param)} = ${FieldUtil.getValue(param, 'rs')};
	  				</#list>
	  	boolean is${field.name}Null = ${FieldUtil.getNullableCondition(field)?no_esc};
	  	if (!is${field.name}Null) {
		  	result.set${field.name}(${FieldUtil.getConstructorValue(field, 'rs')});
		}
	  			<#else> 
	  	result.set${field.name}(${FieldUtil.getConstructorValue(field, 'rs')});	  	
	  			</#if>
  			<#else> 
	  	result.set${field.name}(${FieldUtil.getValue(field, 'rs')});
			</#if>
  		</#list>
  		
		return result;
	}

}