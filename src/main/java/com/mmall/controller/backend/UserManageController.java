package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView login(String username , String password , HttpSession session){
        ModelAndView successResult = new ModelAndView("back/home/index");
        ModelAndView failResult = new ModelAndView("result/fail");
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN){
                //管理员登录
                session.setAttribute(Const.CURRENT_USER,user);
                return successResult.addObject("data",response);
            }else {
                return failResult.addObject("data",ServerResponse.createByErrorMessage("不是管理员，无法登录"));
            }
        }
        return failResult.addObject("data",response);
    }

    @RequestMapping(value = "dologin.do",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView doLogin(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            ModelAndView result = new ModelAndView("back/login/login");
            return  result;
        }
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return new ModelAndView("back/home/index").addObject("data",ServerResponse.createBySuccess(user));
        } else {
            return new ModelAndView("result/fail").addObject("data",ServerResponse.createByErrorMessage("不是管理员，无法登录"));
        }
    }

    @RequestMapping(value = "user_list.do",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView userList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("back/login/login");
        if (currentUser==null){
            return failResult.addObject("data",ServerResponse.createByErrorMessage("用户未登录"));
        }
        ModelAndView successResult = new ModelAndView("back/user/list");

        successResult.addObject("data",currentUser);
        successResult.addObject("page",iUserService.userList(pageNum,pageSize));
        return successResult;
    }

    @RequestMapping(value = "doupdate.do",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView doUpdate(Integer userId){
        ModelAndView result = new ModelAndView("back/user/update");
        if (userId == null ){
            return result;
        }else {
            result.addObject("user",iUserService.getInformation(userId));
            return result;
        }
    }

    @RequestMapping(value = "updateUserInfo.do" , method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView updateUserInfo(HttpSession session,User user,Integer userId ){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (currentUser==null){
            return failResult.addObject("data",ServerResponse.createByErrorMessage("用户未登录"));
        }
        ModelAndView successResult = new ModelAndView("result/success");
        if (userId == null){
            ServerResponse serverResponse = iUserService.register(user);
            if (serverResponse.isSuccess()){
                return successResult;
            }else
            {
                failResult.addObject("data",serverResponse);
                return failResult;
            }
        }
        user.setId(userId);
        ServerResponse<User> result = iUserService.updateInfor(user);
//        if (result.isSuccess()){
//            session.setAttribute(Const.CURRENT_USER,result.getData());
//        }
        successResult.addObject("data",result);
        return successResult;
    }

    @RequestMapping(value = "search_user.do" , method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView searchUser(HttpSession session,String username,@RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize ){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        ModelAndView failResult = new ModelAndView("result/fail");
        if (currentUser==null){
            return failResult.addObject("data",ServerResponse.createByErrorMessage("用户未登录"));
        }
        ModelAndView successResult = new ModelAndView("back/user/list");
        successResult.addObject("data",currentUser);
        successResult.addObject("page",iUserService.searchUser(username,pageNum,pageSize));
        return successResult;
    }


}
