package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCacha;
import com.mmall.pojo.User;
import com.mmall.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView login(String username , String password , HttpSession session){
        ModelAndView failResult = new ModelAndView("result/profail");
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
            ModelAndView successResult = new ModelAndView("pro/home/index");
            return successResult.addObject("data",response);
        }
        failResult.addObject("data",response);
        return failResult;
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do" , method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        ModelAndView result = new ModelAndView("pro/home/index");
        return result;
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 检查校验用户名邮箱
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str , String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取登录用户的信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getCurrentUserInfo.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getCurrentUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录，未能获取到用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 获取密码问题
     * @param username
     * @return
     */
    @RequestMapping(value = "getPasswordQuestion.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getPasswordQuestion (String username){
        return iUserService.passwordQuestion(username);
    }

    /**
     * 验证答案密码是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "checkQuestion.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkQuestion(String username,String question,String answer){
        return  iUserService.checkQuestion(username,question,answer);
    }

    /**
     * 忘记密码 - 修改密码
     * @param username
     * @param newPassword
     * @param paramToken
     * @return
     */
    @RequestMapping(value = "resetPassword.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String username,String newPassword,String paramToken){
         username = TokenCacha.getKey("username");
        return iUserService.resetPassword(username,newPassword,paramToken);
    }

    /**
     * 通过旧密码 - 修改密码
     * @param session
     * @param newPassword
     * @param oldPassword
     * @return
     */
    @RequestMapping(value = "resetPasswordByOldPassword.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPasswordByOldPassword(HttpSession session,String newPassword,String oldPassword){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPasswordByOldPassword(newPassword,oldPassword,user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateUserInfo.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user ){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> result = iUserService.updateInfor(user);
        if (result.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,result.getData());
        }
        return result;
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getInformation.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation (HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请进行登录");
        }
        return iUserService.getInformation(currentUser.getId());

    }


    @RequestMapping(value = "to_register.do")
    @ResponseBody
    public ModelAndView toRegister (){
        ModelAndView resulte = new ModelAndView("pro/login/register");
        return resulte;
    }

    @RequestMapping(value = "tologin.do")
    @ResponseBody
    public ModelAndView toLogin (){
        ModelAndView resulte = new ModelAndView("pro/login/login");
        return resulte;
    }
    @RequestMapping(value = "toindex.do")
    @ResponseBody
    public ModelAndView toIndex (HttpSession session){
        ModelAndView resulte = new ModelAndView("pro/home/index");
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = ServerResponse.createBySuccess(user) ;
        resulte.addObject("data",response);
        return resulte;
    }

}
