<!doctype html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<body>
<h1>Hello, <sec:authentication property="principal.displayName" /></h1>
<a href="j_spring_security_logout">Logout</a>
</body>
