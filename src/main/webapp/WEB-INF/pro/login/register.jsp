<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>闲时小饮-注册</title>
    <link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
    <link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
    <script type="text/javascript" src="../js/jquery-3.2.1.min.js"></script>
</head>

<body>
    <div class="login_title">
        <h1>闲时小饮-注册</h1>
    </div>
    <form class="form-horizontal" action="/user/register.do" method="post">
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
                <span id="is_same1"></span>
            </div>
        </div>
        <div class="form-group">
            <label for="passwordTwo" class="col-sm-4 control-label">再次输入密码</label>
            <div class="col-sm-4">
                <input type="password" class="form-control" id="passwordTwo" name="passwordTwo" placeholder="密码" required="required" onblur="onblurs()">
                <span id="is_same2"></span>
            </div>
        </div>
        <div class="form-group">
            <label for="email" class="col-sm-4 control-label">邮箱</label>
            <div class="col-sm-4">
                <input type="email" class="form-control" id="email" name="email" placeholder="邮箱" required="required">
            </div>
        </div>
        <div class="form-group">
            <label for="phone" class="col-sm-4 control-label">电话</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="phone" name="phone" placeholder="电话" required="required">
            </div>
        </div>
        <div class="form-group">
            <label for="question" class="col-sm-4 control-label">密保问题</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="question" name="question" placeholder="密保问题" required="required">
            </div>
        </div>
        <div class="form-group">
            <label for="answer" class="col-sm-4 control-label">密保答案</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" id="answer" name="answer" placeholder="密保答案" required="required">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-8">
                <button type="submit" class="btn btn-default">注册</button>
                <button class="btn btn-default"><a style="text-decoration:none;" href="#" onClick="javascript :history.back(-1);">返回</a></button>
            </div>
        </div>

    </form>
</body>

<script type="text/javascript">
    function onblurs(){
        var password = $("#password").val();
        var passwordTwo = $("#passwordTwo").val();
        console.log(password,passwordTwo);
        if (password === passwordTwo){
            $("#is_same1").html("<span class='text-primary'>两次密码输入相同</span>");
            $("#is_same2").html("<span class='text-primary'>两次密码输入相同</span>");

        }
        else{
            $("#is_same1").html("<span class='text-danger'>两次密码输入不同</span>");
            $("#is_same2").html("<span class='text-danger'>两次密码输入不同</span>");
        }
    }
</script>
</html>
