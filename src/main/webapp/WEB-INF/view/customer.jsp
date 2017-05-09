<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="BASE" value="${pageContext.request.contextPath}"/>

<html>
<head>
   <title>客户管理</title>
   <script type="application/javascript">
           $(document).ready(function () {
               $("#myDiv").load("hello.jsp");
           });
       </script>
</head>
<body>

<h1>客户列表</h1>

  <table>
     <tr>
        <th>名</th>
        <th>联系人</th>
        <th>电话</th>
        <th>邮箱</th>
        <th>操作</th>
     </tr>
     <c:forEach var="customer" items="${customerList}">
     <tr>
        <td>${customer.name}</td>
        <td>${customer.contact}</td>
        <td>${customer.telephone}</td>
        <td>${customer.email}</td>
        <td>
           <a href="${BASE}/customer_edit?id=${customer.id}">编辑</a>
           <a href="${BASE}/customer_delete?id=${customer.id}">删除</a>
        </td>
     </tr>
     </c:forEach>
  </table>
  <section id="main-content">
          <section class="wrapper site-min-height">

              <div class="row mt">
                  <div class="col-lg-12">

                      <div class="showback">
                          <div class="site-index">
                              <div class="jumbotron">
                                  <div id="myDiv"></div>
                              </div>
                          </div>
                      </div>
                  </div>

              </div>
          </section>
</body>
</html>