<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<title>闲时小饮-首页</title>
<link rel = "stylesheet"  type = "text/css" href ="css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="css/mystyle.css"/>
<script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>

<body>
<div>
    <nav class="navbar navbar-default back-nav">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">

                <a class="navbar-brand" href="/user/toindex.do">首页</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <c:if test="${not empty data.data.username}">
                    <li><a href="">欢迎 <span class="text-primary">${data.data.username}</span> 登录 </a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">登录<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/user/logout.do">注销</a></li>
                        </ul>
                    </li>
                    </c:if>
                    <c:if test="${empty data.data.username}">
                        <li><a href="/user/tologin.do"> 登录 </a></li>
                    </c:if>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>

    <div id="myCarousel" class="carousel slide back-carousel-div">
        <!-- 轮播（Carousel）指标 -->
        <ol class="carousel-indicators">
            <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#myCarousel" data-slide-to="1"></li>
            <li data-target="#myCarousel" data-slide-to="2"></li>
        </ol>
        <!-- 轮播（Carousel）项目 -->
        <div class="carousel-inner back-carousel-div" >
            <div class="item active">
                <img class=" back-carousel-img"  src="./image/carousel1.jpg" alt="First slide">
            </div>
            <div class="item">
                <img class=" back-carousel-img"  src="./image/carousel2.jpg" alt="Second slide">
            </div>
            <div class="item">
                <img class="back-carousel-img"  src="./image/carousel3.jpg" alt="Third slide">
            </div>
        </div>
        <!-- 轮播（Carousel）导航 -->
        <a class="carousel-control left" href="#myCarousel" data-slide="prev"> <span _ngcontent-c3="" aria-hidden="true"></span></a>
        <a class="carousel-control right" href="#myCarousel" data-slide="next"></a>
    </div>
    <div class="my-contain">
        <h2>闲时小饮-后台首页</h2>
    </div>
</div>

</body>
</html>
