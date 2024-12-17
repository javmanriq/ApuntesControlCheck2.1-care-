<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="Provided Cares">
    <jsp:attribute name="customScript">
    </jsp:attribute>
    <jsp:body>
        <h2>
        	INVALID CARE!
    	</h2>
        <p>Either the chosen care is incompatible with the  pet type, or 
            you have provided a previous care in this visit that  discourages the application of the chosen care.</p>    
    </jsp:body>
</petclinic:layout>
