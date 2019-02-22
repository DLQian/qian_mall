<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<title>闲时小饮-后台用户信息修改</title>
<link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
<body>
<div class="my-contain" >
    <div class="login_title">
        <h1>闲时小饮-用户信息修改</h1>
    </div>
    <form class="form-horizontal" action="/admin/updateUserInfo.do" method="post">
        <input type="hidden" name="userId" value="${user.data.id}">
        <div class="form-group">
            <label for="username" class="col-sm-4 control-label">用户名</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="username" name="username" placeholder="用户名" value="<c:if test="${not empty user.data.username}">${user.data.username}</c:if>" required="required" <c:if test="${not empty user.data.username}">readonly</c:if> >
            </div>
        </div>
        <div class="form-group">
            <label for="email" class="col-sm-4 control-label">邮箱</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="email" name="email" placeholder="邮箱" required="required" value="<c:if test="${not empty user.data.email}">${user.data.email}</c:if>" <c:if test="${not empty user.data.username}">readonly</c:if>>
            </div>
        </div>
        <div class="form-group">
            <label for="phone" class="col-sm-4 control-label">电话</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="phone" name="phone" placeholder="电话" required="required" value="<c:if test="${not empty user.data.phone}">${user.data.phone}</c:if>">
            </div>
        </div>
        <div class="form-group">
        <label for="question" class="col-sm-4 control-label">问题</label>
        <div class="col-sm-4">
            <input type="text" class="form-control" id="question" name="question" placeholder="问题" required="required" value="<c:if test="${not empty user.data.question}">${user.data.question}</c:if>">
        </div>
        </div>
        <div class="form-group">
            <label for="answer" class="col-sm-4 control-label">答案</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="answer" name="answer" placeholder="答案" required="required" value="<c:if test="${not empty user.data.answer}">${user.data.answer}</c:if>">
            </div>
        </div>
        <div class="form-group">
            <label for="role" class="col-sm-4 control-label">角色</label>
            <div class="col-sm-4">
                <select class="form-control" id="role" name="role" placeholder="角色" required="required">
                    <option value="<c:if test="${empty user.data.role}">100</c:if>" <c:if test="${empty user.data.role}">selected="selected"</c:if>>请选择</option>
                    <option value="<c:if test="${user.data.role == 1}">1"selected="selected</c:if>">管理员</option>
                    <option value="<c:if test="${user.data.role == 0}">0"selected="selected</c:if>">普通用户</option>

                </select>
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

