package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.services.ICategoryService;
import com.mmall.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加商品分类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */

    @RequestMapping(value = "add_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0")Integer parentId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
    }

    /**
     * 更新分类信息
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategory(HttpSession session,Integer categoryId,String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()){
            return iCategoryService.setCategory(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
    }

    /**
     * 获取下一子节点的所有分类
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_next_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()){
            return iCategoryService.getNextChildCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
    }

    /**
     * 寻找当前节点下的所有子节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDeepCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录后再进行操作");
        }
        if (iUserService.checkAdmin(user.getId()).isSuccess()){
           return iCategoryService.selectDeepCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
    }
}
