package com.mmall.services;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str ,String type);

    ServerResponse<String> passwordQuestion(String username);

    ServerResponse<String> checkQuestion(String username,String question,String answer);

    ServerResponse<String> resetPassword (String username,String newPassword,String paramToken);

    ServerResponse<String> resetPasswordByOldPassword(String newPassword,String oldPassword,User user);

    ServerResponse<User> updateInfor(User user);

    ServerResponse<User> getInformation (Integer userId);

    ServerResponse<String> checkAdmin(Integer userId);

    ServerResponse<PageInfo> userList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchUser(String username,Integer pageNum,Integer pageSize);
}
