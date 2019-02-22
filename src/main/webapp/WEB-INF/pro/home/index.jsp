<%@ page language="java"  contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<link rel = "stylesheet"  type = "text/css" href ="../css/bootstrap.min.css"/>
<link rel = "stylesheet"  type = "text/css" href ="../css/mystyle.css"/>
<script type="text/javascript" src="../js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../js/bootstrap.min.js"></script>
<title>闲时小饮-后台首页</title>

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
                    <li><a href="#">欢迎 <span class="text-primary">${data.data.username}</span> 登录 </a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">登录<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="/user/logout.do">注销</a></li>
                        </ul>
                    </li>
                    </c:if>
                    <c:if test="${empty data.data.username}">
                        <li><a href="/user/tologin.do"> ${data.data.username} 登录 </a></li>
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
                <img class=" back-carousel-img"  src="../image/carousel1.jpg" alt="First slide">
            </div>
            <div class="item">
                <img class=" back-carousel-img"  src="../image/carousel2.jpg" alt="Second slide">
            </div>
            <div class="item">
                <img class="back-carousel-img"  src="../image/carousel3.jpg" alt="Third slide">
            </div>
        </div>
        <!-- 轮播（Carousel）导航 -->
        <a class="carousel-control left" href="#myCarousel" data-slide="prev"> <span _ngcontent-c3="" aria-hidden="true"></span></a>
        <a class="carousel-control right" href="#myCarousel" data-slide="next"></a>

    </div>

    <div class="my-contain text-center" >
        <h2 style="margin-top: 50px">闲时小饮
            <div style="margin-top: 20px"><small>闲来无事贪一杯，不知恬淡，能交三两好友。</small><small>时忙却想小酌饮，苦中有乐，不知三两时辰。</small></div>
            <div style="margin-top: 20px"><small>小童常问何处有，苦思不解，叹息三两白昼。</small><small>饮一杯淡奶茶香，酣畅淋漓，回味三两春秋。</small></div>
        </h2>

        <ul class="list-inline">
            <li class="general">
                <div class="thumbnail">
                    <a href="" class="ad-click-event">
                        <img src="../image/pro_index_product.jpg" alt="Admin Theme">
                    </a>

                    <div class="caption">
                        <h3>商品</h3>
                        <p><b class="badge bg-danger">热款</b> 全新登场，全新亮相</p>
                        <p>
                            <strong>2月新春</strong> 值得期待的饮品等待你的品尝
                        </p>

                        <p>
                            <a href="/product/to_product.do" class="btn btn-primary ad-click-event" role="button">进入</a>
                        </p>
                        <p><i class="fa fa-shopping-cart margin-r5"></i> 进入畅快淋漓的世界</p>
                    </div>
                </div>
            </li>
            <li class="general">
                <div class="thumbnail">
                    <a href="" class="ad-click-event">
                        <img src="../image/pro_index_product.jpg" alt="Admin Theme">
                    </a>

                    <div class="caption">
                        <h3>购物车</h3>
                        <p><b class="badge bg-danger">所购</b> 多种多样，有你所购</p>
                        <p>
                            <strong>2月新春</strong> 值得期待的饮品等待你的品尝
                        </p>

                        <p>
                            <a href="/cart/find.do" class="btn btn-primary ad-click-event" role="button">进入</a>

                        </p>
                        <p><i class="fa fa-shopping-cart margin-r5"></i> 进入多彩缤纷的世界</p>
                    </div>
                </div>

            </li>
            <li class="general">
                <div class="thumbnail">
                    <a href="" class="ad-click-event">
                        <img src="../image/pro_index_product.jpg" alt="Admin Theme">
                    </a>

                    <div class="caption">
                        <h3>订单</h3>
                        <p><b class="badge bg-danger">订单</b> 购我所爱，看我所购</p>
                        <p>
                            <strong>2月新春</strong> 品味过去的所喜爱的甜蜜生活
                        </p>

                        <p>
                            <a href="/order/list_order.do" class="btn btn-primary ad-click-event" role="button">进入</a>
                        </p>
                        <p><i class="fa fa-shopping-cart margin-r5"></i> 进入享受自我的世界</p>
                    </div>
                </div>

            </li>
        </ul>


    </div>



</div>

</body>
</html>
