package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.services.FilerService;
import com.mmall.services.ICategoryService;
import com.mmall.services.IProductService;
import com.mmall.services.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("admin/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private FilerService filerService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 保存或更新商品信息
     *
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "product_save.do",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView productSave(HttpSession session,Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (user == null) {
            ServerResponse response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
            return failResult.addObject("data",response);
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            ServerResponse response = iProductService.saveOrUpdate(product);
            ModelAndView successResult = new ModelAndView("result/success");
            successResult.addObject("data",response);
            return successResult;
        } else {
            return failResult.addObject(ServerResponse.createByErrorMessage("无权限进行操作"));
        }
    }

    /**
     * 更改商品销售状态
     *
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_status.do")
    @ResponseBody
    public ServerResponse setStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return iProductService.setStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }

    /**
     * 获取商品详情接口
     *
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("product_detail.do")
    @ResponseBody
    public ModelAndView productDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (user == null) {
            return failResult.addObject("data",ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作"));
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            ModelAndView successResult = new ModelAndView("back/product/update");

            //获取商品分类信息
            ServerResponse category = iCategoryService.selectDeepCategory(0);
            successResult.addObject("category",category);
            if (productId != null ){
                ServerResponse product = iProductService.backProductDetail(productId);
                successResult.addObject("product",product);
                successResult.addObject("data",user);
                return successResult;
            }
            return successResult;
        } else {
            return failResult.addObject("data",ServerResponse.createByErrorMessage("无权限进行操作"));
        }
    }

    /**
     * 商品简要数据展示
     *
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ModelAndView getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (user == null) {
            return failResult.addObject("data",ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作"));
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            ModelAndView successResult = new ModelAndView("back/product/list");
            ServerResponse page = iProductService.getProductList(pageNum, pageSize);
            successResult.addObject("page",page);
            successResult.addObject("data",user);
            return successResult;
        } else {
            return failResult.addObject("data",ServerResponse.createByErrorMessage("无权限进行操作"));
        }
    }

    /**
     * 搜索商品
     *
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            return iProductService.backSearchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限进行操作");
        }
    }


    /**
     * springmvc上传
     *
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file,Integer productId ,HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (user == null) {
            return failResult.addObject("data",ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作"));
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
//            String path = request.getSession().getServletContext().getRealPath("upload");
            String path = PropertiesUtil.getProperty("picture.goods");
            String targetFileName = filerService.upload(file, path);
            String url = path +"/"+ targetFileName;

            ServerResponse response = iProductService.inputImage(productId,targetFileName);
            if (response.isSuccess()){
                ModelAndView successResult = new ModelAndView("result/success");
                Map fileMap = Maps.newHashMap();
                fileMap.put("uri", targetFileName);
                fileMap.put("url", url);
                return successResult.addObject("data",ServerResponse.createBySuccess("操作成功",fileMap));
            }
            return failResult.addObject("data",response);

        }
        else {
            return failResult.addObject(ServerResponse.createByErrorMessage("无权限进行操作"));
        }
    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()) {
            String path = PropertiesUtil.getProperty("picture.goods");
            String targetFileName = filerService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }


    }


    @RequestMapping(value = "toupload.do", method = RequestMethod.GET)
    public ModelAndView uploadPic(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (user == null) {
            return failResult.addObject("data",ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录后再进行操作"));
        }
        ModelAndView successResult = new ModelAndView("back/product/inputImage");
        successResult.addObject("data",user);
        successResult.addObject("productId",productId);
        return  successResult;
    }
}
