<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<title>闲时小饮-后台商品管理详情</title>
<link rel = "stylesheet"  type = "text/css" href ="../../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../../css/mystyle.css"/>
<body>
<div class="my-contain" >
    <div class="login_title">
        <h1>闲时小饮-后台商品管理</h1>
    </div>
    <form class="form-horizontal" action="/admin/product/product_save.do" method="post">
        <input type="hidden" name="id" value="${product.data.id}">
        <div class="form-group">
            <label for="categoryId" class="col-sm-4 control-label">分类</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="categoryId" name="categoryId" placeholder="分类" value="<c:if test="${not empty product.data.categoryId}">${product.data.categoryId}</c:if>" required="required">
            </div>
        </div>
        <div class="form-group">
            <label for="name" class="col-sm-4 control-label">名称</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="name" name="name" placeholder="名称" required="required" value="<c:if test="${not empty product.data.name}">${product.data.name}</c:if>">
            </div>
        </div>
        <div class="form-group">
            <label for="subtitle" class="col-sm-4 control-label">副标题</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="subtitle" name="subtitle" placeholder="副标题" required="required" value="<c:if test="${not empty product.data.subtitle}">${product.data.subtitle}</c:if>">
            </div>
        </div>
        <div class="form-group">
            <label for="price" class="col-sm-4 control-label">价格</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="price" name="price" placeholder="价格" required="required" value="<c:if test="${not empty product.data.price}">${product.data.price}</c:if>">
            </div>
        </div>
        <div class="form-group">
            <label for="status" class="col-sm-4 control-label">商品状态</label>
            <div class="col-sm-4">
                <select class="form-control" id="status" name="status" placeholder="商品状态" required="required">
                    <option value="<c:if test="${product.data.status == 1}">1"selected="selected</c:if>">在售</option>
                    <option value="<c:if test="${product.data.status == 2}">2"selected="selected</c:if>">下架</option>
                    <option value="<c:if test="${product.data.status == 3}">3"selected="selected</c:if>">删除</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="detail" class="col-sm-4 control-label">详情描述</label>
            <div class="col-sm-4">
                <textarea class="form-control" cols="30" rows="4" id="detail" name="detail" placeholder="详情描述" value="<c:if test="${not empty product.data.detail}">${product.data.detail}</c:if>"></textarea>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-4">
                <button type="submit" class="btn btn-default">确定</button>
                <span class="btn btn-default pull-right"><a href="/admin/user_list.do">返回首页</a></span>
            </div>
        </div>
    </form>
</div>
</body>
</html>

