<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<title>闲时小饮-后台登录</title>
<link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
<body>
<div class="my-contain" >
    <div class="login_title">
        <h1>闲时小饮-后台登录</h1>
    </div>
    <form class="form-horizontal" action="/admin/login.do" method="post">
        <div class="form-group">
            <label for="username" class="col-sm-4 control-label">用户名</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="username" name="username" placeholder="用户名" required="required">
            </div>
        </div>
        <div class="form-group">
            <label for="password" class="col-sm-4 control-label">密码</label>
            <div class="col-sm-4">
                <input type="password" class="form-control" id="password" name="password" placeholder="密码" required="required">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-8">
                <button type="submit" class="btn btn-default">登录</button>
            </div>
        </div>
    </form>
</div>

</body>
</html>

