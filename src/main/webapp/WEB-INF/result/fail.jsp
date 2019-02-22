<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>闲时小饮-操作失败</title>
    <link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
    <link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
    <script type="text/javascript" src="../js/jquery-3.2.1.min.js"></script>
</head>

<body onload="load();">
<div class="login_title">
    <h1>闲时小饮-操作失败</h1>
</div>
<div style="text-align: center" >
    <h3 class="text-danger">错误：${data.msg}</h3>
    <p>网页将在<span id="time">5</span>秒后跳转，也可以点击此处返回 <a href="/admin/user_list.do">首页</a></p>
</div>
</body>
<script type="text/javascript">
    function load() {
        console.log("1");
        var count = 5;
        var t1 = self.setInterval(function () {
            count = count-1;
            document.getElementById('time').innerHTML=count;
            if (count==1){
                self.clearTimeout(t1);
                self.history.go(-1);
            }
        },1000)
    }
</script>
</html>
