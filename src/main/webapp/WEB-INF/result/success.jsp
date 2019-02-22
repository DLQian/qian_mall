<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>闲时小饮-操作成功</title>
    <link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
    <link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
    <script type="text/javascript" src="../js/jquery-3.2.1.min.js"></script>
</head>

<body onload="load();">
<div  style="text-align: center" >
    <div class="login_title">
        <h1>闲时小饮-操作成功</h1>
    </div>
    <div>
        <p>网页将在<span id="time">5</span>秒后跳转，也可以点击此处返回 <a href="/admin/user_list.do">首页</a></p>
    </div>
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
