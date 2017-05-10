<!-- XSL - это стандарт XML, описывающий язык для преобразования XML-документа
	из одного вида в другой (не обязательно XML).
	
	Данный XSL документ выполняет преобразование входного XML-документа (см. zooinventory.xml) в HTML-формат.
	
	Пример применения XSL в Java API: XSLTransform.java
	-->
<xsl:stylesheet	
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<!-- Тег "xsl:template" выбирает элемент, соответствующий XPath-выражению
		 в атрибуте match, в данном случае мы выбираем корневой элемент "/inventory": -->
	<xsl:template match="/inventory">
		<!-- Внутри тега "xsl:template" мы можем поместить любой синтаксически верный XML-текст
			 (или воспользоваться выражениями CDATA, чтобы вывести что угодно).
			 Выводим простую HTML-страничку, представляющую содержимое элемента
			 "inventory" в виде таблицы: -->
		<html><head><title>Zoo Inventory</title></head>
		<body><h1>Zoo Inventory</h1>
		<table border="1" width="100%">
		<tr><td><b>Name</b></td><td><b>Species</b></td>
		<td><b>Habitat</b></td><td><b>Temperament</b></td>
		<td><b>Diet</b></td></tr>
			<!-- Тег "xsl:apply-templates" делегирует входной документ следующему по порядку XSL-шаблону
				 (если бы мы его не поставили, то текущий шаблон вобрал бы в себя весь документ,
				 и последующие шаблоны не получили бы никаких входных данных): -->
			<xsl:apply-templates/><!-- Process Inventory -->
		</table>
		</body>
		</html>
	</xsl:template>
	
	<!-- Выполняем проход по всем элементам "animal" внутри "inventory": -->
	<xsl:template match="inventory/animal">
			<tr><td><xsl:value-of select="name"/></td>
		    <td><xsl:value-of select="species"/></td>
			<td><xsl:value-of select="habitat"/></td>
			<td><xsl:value-of select="temperament"/></td> 
			<td><xsl:apply-templates select="food|foodRecipe"/>
				<!-- Process Food,FoodRecipe--></td></tr>
	</xsl:template>

	<!-- Для элемента foodRecipe мы выводим отдельную HTML-табличку: -->
	<xsl:template match="foodRecipe">
		<table>
		<tr><td><em><xsl:value-of select="name"/></em></td></tr>
		<xsl:for-each select="ingredient">
			<tr><td><xsl:value-of select="."/></td></tr>
		</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>
