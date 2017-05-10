<!-- В данном файле приведен пример использования XSL-форматирования в упрощенной форме.
	
	Данный XSL документ выполняет преобразование входного XML-документа (см. zooinventory.xml) в HTML-формат
	
	Пример применения XSL в Java API: XSLTransform.java
	-->
<!DOCTYPE html>
<html xsl:version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xi="http://www.w3.org/2001/XInclude">
	<head>
    	<title>XSL Testing</title>
  	</head>
  	<body>
  		<h1 align="center">Выражения XPath и соответствующие им выборки</h1>
  		<hr></hr>
  		
  		<h2>Синтаксис выражений, связанный с узлами документа:</h2>
  		
    	<h3 align="center">"/inventory/animal" <span style="font-weight:normal;">(Все узлы "animal" в корневом элементе "inventory")</span></h3>
    	<!-- Выводим значения элементов выборки в виде HTML-списка: -->
    	<xsl:for-each select="/inventory/animal"> <li><xsl:value-of select="."/></li> </xsl:for-each>
		
		<h3 align="center">"/inventory/animal/food" <span style="font-weight:normal;">(Все узлы "food" внутри "animal" в корневом элементе "inventory")</span></h3>
    	<xsl:for-each select="/inventory/animal/food"> <li><xsl:value-of select="."/></li> </xsl:for-each>
		
		<h3 align="center">"//name" <span style="font-weight:normal;">(Все узлы "name" где-либо в документе)</span></h3>
    	<xsl:for-each select="//name"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
    	<h3 align="center">"//@animalClass" <span style="font-weight:normal;">(Все атрибуты "animalClass" где-либо в документе)</span></h3>
    	<xsl:for-each select="//@animalClass"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"//animal/*" <span style="font-weight:normal;">(Все дочерние элементы узлов "animal" где-либо в документе)</span></h3>
    	<xsl:for-each select="//animal/*"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"/inventory/animal/." <span style="font-weight:normal;">(Текущий узел (все узлы внутри "animal" в корневом элементе "inventory"))</span></h3>
    	<xsl:for-each select="/inventory/animal/."> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"/inventory/animal/.." <span style="font-weight:normal;">(На один узел вверх (родительский узел "inventory"))</span></h3>
    	<xsl:for-each select="/inventory/animal/.."> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
    	
    	<h2>Использование предикатов:</h2>
    	
    	<h3 align="center">"/inventory/animal[1]" <span style="font-weight:normal;">(Первый элемент из дочерних узлов "animal" в корневом элементе "inventory")</span></h3>
    	<xsl:for-each select="/inventory/animal[1]"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">'//animal[@animalClass="mammal"]' <span style="font-weight:normal;">(Все узлы "animal" с значением атрибута "animalClass" = "mammal")</span></h3>
    	<xsl:for-each select='//animal[@animalClass="mammal"]'> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">'//animal[name="Cocoa"]' <span style="font-weight:normal;">(Все узлы "animal" с дочерним узлом "name", равным тексту "Cocoa")</span></h3>
    	<xsl:for-each select='//animal[name="Cocoa"]'> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"//animal[weight > 35]/name" <span style="font-weight:normal;">(Все узлы "name" внутри "animal", чей узел "weight" в виде числа больше 35)</span></h3>
    	<xsl:for-each select="//animal[weight > 35]/name"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"//animal[weight>20 and weight&lt;44]" <span style="font-weight:normal;">(Все узлы "animal", чей узел "weight" больше 20 и меньше 44)</span></h3>
    	<xsl:for-each select="//animal[weight>20 and weight&lt;44]"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
    	<h2>Использование функций:</h2>
    	
    	<h3 align="center">"/inventory/comment()" <span style="font-weight:normal;">(Все коментарии корневого узла "inventory")</span></h3>
    	<xsl:for-each select="/inventory/comment()"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"/inventory/animal[last()]" <span style="font-weight:normal;">(Последний узел "animal" в корневом элементе "inventory")</span></h3>
    	<xsl:for-each select="/inventory/animal[last()]"> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">"//foodRecipe[count(ingredient)>2]/.." <span style="font-weight:normal;">(Все родители узла "foodRecipe" кол-во "ingredient" которого больше 2)</span></h3>
    	<xsl:for-each select="//foodRecipe[count(ingredient)>2]/.."> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">'//animal[starts-with(name,"C")]' <span style="font-weight:normal;">(Все узлы "animal", чей элемент "name" начинается со строки "C")</span></h3>
    	<xsl:for-each select='//animal[starts-with(name,"C")]'> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
		<h3 align="center">'//animal[contains(habitat,"Africa")]' <span style="font-weight:normal;">(Все узлы "animal", чей элемент "habitat" содержит строку "Africa")</span></h3>
    	<xsl:for-each select='//animal[contains(habitat,"Africa")]'> <li><xsl:value-of select="."/></li> </xsl:for-each>
    	
  	</body>
</html>


