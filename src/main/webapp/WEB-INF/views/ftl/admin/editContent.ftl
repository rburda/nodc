<#macro isChecked value>
	<#if ( value ) >checked=checked</#if>
</#macro>

<#macro readonly value>
	<#if ("hotel_name"?matches(value))>readonly=readonly</#if>
</#macro>

<!DOCTYPE html>
<html lang="en">
  <head>
		<#include "/admin/includes/admin_header.ftl">
  </head>

  <body>

		<#include "/admin/includes/admin_navbar.ftl">


    <div class="container">
      <form class="form-horizontal" action="/admin/saveContent" method="post" commandName="wrapper">	
      	<input type="hidden" name="tableName" readonly="readonly" value="${contentEditor.tableName}" />
      	<input type="hidden" name="name" readonly="readonly" value="${contentEditor.name}" />		
				<div class="container">
					<div class="span8">
						<h2>Edit ${contentEditor.name}</h2>
						<fieldset>
							<#assign keys = model["contentEditor"].attributes?keys>
							<#list keys as attrName>
						   	<div class="control-group">
						    </div>
						    <div class="control-group">
						    	<label class="control-label" for="${attrName}"><strong>${attrName}</strong></label>
						    	<div class="controls">
										<textarea id="${attrName}" <@readonly value=attrName/> style="width: 1000px" rows="8" name='attributes["${attrName}"]'>${contentEditor.attributes[attrName]!""}</textarea>
										<label style="font-size:16px;color:green" class="checkbox">
						     			<span class="add-on"><i class="icon-pencil"></i></span>
						     			<input id="chk${attrName}" name='overrideStatus["${attrName}"]' type=checkbox <@readonly value=attrName/> <@isChecked value=contentEditor.overrideStatus[attrName]!false/> /> 
						     			Prevent Content Update
						     		</label>
								 	</div>
						   </div>							
							</#list>	
						   	<div class="form-actions">
									<input type="submit" class="btn btn-primary" value="Save"/>
									<a class="btn btn-danger" href="/admin/editMaster">Cancel</a>
								</div>
						 </fieldset>
					</div>
				</div>
			</form>
    </div> <!-- /container -->

    <#include "/admin/includes/admin_footer.ftl">
  </body>
</html>
