scala2.11.12包下载,并依赖与项目
Flink1.7.2
spark2.2


删除spark的log
删除flink的冲突包


  <groupId>org.apache.calcite</groupId>
      <artifactId>calcite-core</artifactId>
      <version>1.16.0</version>
      <scope>compile</scope>
	  
	  
	  shift+F9 
	  
修改项目下 .idea\workspace.xml，找到标签
 <component name="PropertiesComponent">
在标签里加一行 即可
<property name="dynamic.classpath" value="true" />	  