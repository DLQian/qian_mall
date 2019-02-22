<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<html>
<title>闲时小饮-后台首页</title>
<link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
<script type="text/javascript" src="../js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../js/bootstrap.min.js"></script>

<body>
<div>
    <nav class="navbar navbar-default back-nav">
        <div class="container-fluid">
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#">欢迎 <span class="text-primary">${data.data.username}</span> 登录</a></li>
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
            <li role="presentation" id="nav-tab1" onclick="myclick(this);"><a href="/admin/user_list.do">用户管理</a></li>
            <li role="presentation" id="nav-tab2" onclick="myclick(this);"><a href="/admin/product/get_product_list.do">商品管理</a></li>
            <li role="presentation" id="nav-tab3" onclick="myclick(this);"><a href="#">订单管理</a></li>
            <li role="presentation" id="nav-tab4" onclick="myclick(this);"><a href="#">分类管理</a></li>
            <li role="presentation" id="nav-tab5" onclick="myclick(this);"><a href="#">地址管理</a></li>
            <ol class="breadcrumb pull-right back-nav-tabs-ol">
                <li><a href="active">首页</a></li>
            </ol>
        </ul>
        <h1 class="my-contain" style="margin-top: 10%;text-align: center">欢迎登录闲时小饮后台</h1>
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
