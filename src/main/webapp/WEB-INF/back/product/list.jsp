<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<title>闲时小饮-后台商品管理</title>
<link rel = "stylesheet"  type = "text/css" href ="../../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../../css/mystyle.css"/>
<script type="text/javascript" src="../../js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../../js/bootstrap.min.js"></script>

<body>
<div>
    <nav class="navbar navbar-default back-nav">
        <div class="container-fluid">
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#">欢迎 <span class="text-primary">${data.username}</span> 登录</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">登录<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/user/logout.do">注销</a></li>
                        </ul>
                    </li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>

    <div class="my-contain">
        <h2>闲时小饮-后台</h2>
        <ul class="nav nav-tabs">
            <li role="presentation"  id="nav-tab1" onclick="myclick(this);"><a href="/admin/user_list.do">用户管理</a></li>
            <li role="presentation" class="active" id="nav-tab2" onclick="myclick(this);"><a href="/admin/product/get_product_list.do">商品管理</a></li>
            <li role="presentation" id="nav-tab3" onclick="myclick(this);"><a href="/admin/order/list_order.do">订单管理</a></li>
            <li role="presentation" id="nav-tab4" onclick="myclick(this);"><a href="">分类管理</a></li>
            <ol class="breadcrumb pull-right back-nav-tabs-ol">
                <li><a href="#">首页</a></li>
                <li class="active">商品管理</li>
            </ol>
        </ul>
        <row>
            <span class="btn btn-info pull-left back-form-inline-margin "><a href="/admin/product/product_detail.do">添加商品</a></span>
            <form class="form-inline pull-right back-form-inline-margin" action="/admin/product/search_product.do">
                <div class="form-group">
                    <label for="productId">商品Id：</label>
                    <input type="text" class="form-control" id="productId" name="productId" placeholder="商品Id">
                </div>
                <div class="form-group">
                    <label for="productName">商品名称：</label>
                    <input type="text" class="form-control" id="productName" name="productName" placeholder="商品名称">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
        </row>

        <table class="table table-hover text-">
            <thead>
            <tr>
                <th>ID</th>
                <th>分类</th>
                <th>名称</th>
                <th>描述</th>
                <th>价格</th>
                <th>状态</th>
                <th>操作</th>

            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty page.data.size}">
            <c:forEach var="item" items="${page.data.list}">
                <tr>
                    <td>${item.id}</td>
                    <td>${item.categoryId}</td>
                    <td>${item.name}</td>
                    <td>${item.subtitle}</td>
                    <td>${item.price}</td>
                    <td>${item.status}</td>
                    <td>
                        <button class="btn btn-sm btn-info"><a href="/admin/product/product_detail.do?productId=${item.id}">详情</a></button>
                        <button class="btn btn-sm btn-info"><a href="/admin/product/toupload.do?productId=${item.id}">图片</a></button>
                    </td>

                </tr>
            </c:forEach>
            </c:if>
            <c:if test="${empty page.data.size}">
                <tr>暂无数据</tr>
            </c:if>
            </tbody>
        </table>
        <nav aria-label="Page navigation" class="pull-right">
            <ul class="pagination" >
                <li>
                    <a href="/admin/user_list.do?pageName=${page.data.firstPage}" aria-label="Previous" <c:if test="${page.data.isFirstPage}">onclick="return false;" </c:if>>
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach items="${page.data.navigatepageNums}" var="pageNum">
                    <li><a href="/admin/user_list.do?pageNum=${pageNum}">${pageNum}</a></li>
                </c:forEach>
                <li>
                    <a href="/admin/user_list.do?pageName=${page.data.lastPage}" aria-label="Next" <c:if test="${page.data.isLastPage}">onclick="return false;"</c:if>>
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</div>
</body>
<script>
    function myclick(obj){
        $("#nav-tab1").removeClass("active");
        $("#nav-tab2").removeClass("active");
        $("#nav-tab3").removeClass("active");
        $("#nav-tab4").removeClass("active");
        $("#nav-tab5").removeClass("active");
        $(obj).addClass("active");
    }
</script>
</html>
