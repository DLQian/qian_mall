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
    <form class="form-horizontal" action="/admin/product/upload.do" method="post" enctype="multipart/form-data" >
        <input type="hidden" name="productId" value="${productId}">
        <div class="form-group">
            <label for="upload_file" class="col-sm-4 control-label">主图片</label>
            <div class="col-sm-4">
                <input type="file"  id="upload_file" name="upload_file" placeholder="主图片" value="<c:if test="${not empty product.data.mainImage}">${product.data.mainImage  }</c:if>">
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

